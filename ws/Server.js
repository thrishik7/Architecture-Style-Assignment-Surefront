/******************************************************************************************************************
* File: Server.js
* Course: 17655
* Project: Assignment A3
* Copyright: Copyright (c) 2018 Carnegie Mellon University
* Versions:
*   1.0 February 2018 - Initial implementation of assignment 3 for 2018 architectures course (Lattanze).
*
* Description: This module is the server for a simple restful webservices application built on a Node server with a
* MySQL database. This module basically connects to the database, sets up the express body parser instance (for 
* parsing URL paramters), configures and starts the server.  
*
* Parameters: 
*   router - this holds is the URL from the client and allows us to map what is in the URL to a REST operation
*   connection - this is the connection to the database
*
* Internal Methods: 
*   REST.prototype.connectMysql - connects to the ws_orderinfo database
*   REST.prototype.configureExpress - configures the express framework for the server
*   REST.prototype.startServer - starts the server
*   REST.prototype.stop - error handler
* 
* External Dependencies: mysql, express/body-parser, REST.js
*
******************************************************************************************************************/
var mysqlConfig = require('./config/mysql.config.json')
var express = require("express");             //express is a Node.js web application framework 
var mysql   = require("mysql");               //Database
var bodyParser  = require("body-parser");     //Javascript parser utility
var rest = require("./REST.js");              //REST services/handler module
var app  = express();    

const fs = require('fs');
const path = require('path');

// Create Logger class
function Logger() {
    // Create logs directory if it doesn't exist
    const logsDir = path.join('.', 'logs');
    if (!fs.existsSync(logsDir)) {
        fs.mkdirSync(logsDir);
        console.log("Logs directory created at:", logsDir);
    }
    
    this.logPath = path.join(logsDir, 'log.txt');
    
    // Create log file if it doesn't exist
    if (!fs.existsSync(this.logPath)) {
        fs.writeFileSync(this.logPath, '', 'utf8');
        console.log("Log file created at:", this.logPath);
    }
    
    console.log("Logger Initialized with path:", this.logPath);
}

// Add methods to prototype
Logger.prototype.logRequest = function(req) {
    const timestamp = new Date().toISOString()
    const logEntry = {
        timestamp: timestamp,
        level: 'INFO',
        ip: req.ip,
        api: req.path,
        session_token: req.headers['x-session-token'] || 'anonymous',
        meta_data: JSON.stringify(req.body) || 'null',
        status: 'pending',
        error: 'null',
    };
    this.writeToLog(logEntry);
};

Logger.prototype.logResponse = function(req, res) {
    const timestamp = new Date().toISOString()
    const logEntry = {
        timestamp: timestamp,
        level: res.statusCode >= 400 ? 'ERROR' : 'INFO',
        ip: req.ip,
        api: req.path,
        session_token: req.headers['x-session-token'] || 'anonymous',
        meta_data: JSON.stringify(req.body) || 'null',
        status: res.statusCode || 'unknown',
        error: res.error || 'null',
    };
    this.writeToLog(logEntry);
};

Logger.prototype.writeToLog = function(data) {
    console.log("Writing to log");
    const logString = `${data.timestamp} [${data.level}] ip=${data.ip} | api=${data.api} | session=${data.session_token} | meta=${data.meta_data} | status=${data.status} | error=${data.error}\n`;
    try {
        fs.appendFile(
            this.logPath,
            logString,
            (err) => {
                if (err) console.error('Error writing to log file:', err);
            }
        );
    } catch (err) {
        console.error('Error writing to log file:', err);
    }
};

// Function definition
function REST(){
    var self = this;
    console.log('Initializing REST...'); // Debug log
    self.connectMysql();
};

// Here we connect to the database. Of course you will put your own user and password information 
// in the "user" and "password" variables. Note that we also create a connection pool.
// Note that I hardwared the server to the ws_orderinfo name. You will have to provide your own
// password... you will probably use the same user. If not, you will have to change that as well.

REST.prototype.connectMysql = function() {
    var self = this;
    var pool = mysql.createPool(mysqlConfig);

    // Here make the connection to the ws_ordersinfo database

    pool.getConnection(function(err,connection) {
        if(err) {
          self.stop(err);
        } else {
          self.configureExpress(connection);
        }
    });
}

// Here is where we configure express and the body parser so the server
// process can get parsed URLs. You really shouldn't have to tinker with this.
REST.prototype.configureExpress = function(connection) {
    var self = this;
    app.use(bodyParser.urlencoded({ extended: true }));
    app.use(bodyParser.json());
    app.use(bodyParser.text());

    // Create a logger instance
    const logger = new Logger();

    var router = express.Router();  // Move router creation before middleware

    const middleware = async (req, res, next) => {
        console.log("Middleware Intercepted");
        logger.logRequest(req);

        // Skip session validation for login and register endpoints
        if (req.path === '/users/login' || req.path === '/users/register') {
            const originalSend = res.send;
            res.send = function(...args) {
                logger.logResponse(req, res);
                originalSend.apply(res, args);
            };
            return next();
        }

        // Session validation
        const sessionToken = req.headers['x-session-token'];
        if (sessionToken && sessionToken !== 'undefined') {
            const query = 'SELECT * FROM sessions WHERE session_token = ? AND valid_until > NOW()';
            connection.query(query, [sessionToken], (error, results) => {
                if (error) {
                    console.error('Session validation error:', error);
                    res.status(500).json({ error: 'Internal server error' });
                    return;
                }

                if (results.length === 0) {
                    res.status(401).json({ error: 'Invalid or expired session' });
                    return;
                }

                // Add user info to request object
                req.session = results[0];
                
                // Continue with original middleware functionality
                const originalSend = res.send;
                res.send = function(...args) {
                    logger.logResponse(req, res);
                    originalSend.apply(res, args);
                };
                next();
            });
        } else {
            res.status(401).json({ error: 'No session token provided' });
            return;
        }
    };

    var router = express.Router();
    app.use('/api', [middleware, router]);
    var rest_router = new rest(router,connection);
    self.startServer();
}

REST.prototype.startServer = function() {
      app.listen(3000,function(){
          console.log("Server Started at Port 3000.");
      });
}

REST.prototype.stop = function(err) {
    console.log("Issue connecting with mysql and/or connecting to the database.\n" + err);
    process.exit(1);
}

new REST();
