
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
var app  = express();                         //express instance

// Function definition
function REST(){
    var self = this;
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

    let logBuffer = [];
    const MAX_LOGS = 1;

    const middleware = (req, res, next) => {
        // Add log entry to buffer
        logBuffer.push({
            timestamp: new Date(),
            ip: req.ip,
            api: req.path,
            uuid: req.headers['x-request-id'],
            meta_data: JSON.stringify(req.body),
            status: 'pending'
        });

        // If buffer reaches 1000 logs, bulk insert to database
        if (logBuffer.length >= MAX_LOGS) {
            const logsToInsert = logBuffer;
            logBuffer = []; // Reset buffer

            // Bulk insert logs
            const sql = 'INSERT INTO logger (timestamp, ip, api, uuid, meta_data, status) VALUES ?';
            const values = logsToInsert.map(log => [
                log.timestamp,
                log.ip,
                log.api, 
                log.uuid,
                log.meta_data,
                log.status
            ]);

            connection.query(sql, [values], function(err) {
                if (err) console.error('Error bulk inserting logs:', err);
            });
        }

        console.log("Middleware intercepted request:", req.body);
        next();
    };

    var router = express.Router();
    app.use('/api', [middleware, router], (req, res) => {
        console.log("Final Server Handler received:", req.body);
        res.json({ response: "Processed by Server", receivedData: req.body });
    });
    
    var rest_router = new rest(router,connection);
    self.startServer();
}

// If we get here, we are ready to start the server. Basically a listen() on 
// port 3000. I hardwired it in this example, you can change it if you like.
// I guess making it a variable would be better (javascript doesn't have #define).

REST.prototype.startServer = function() {
      app.listen(3000,function(){
          console.log("Server Started at Port 3000.");
      });
}

// We land here if we can't connect to mysql

REST.prototype.stop = function(err) {
    console.log("Issue connecting with mysql and/or connecting to the database.\n" + err);
    process.exit(1);
}

// Instantiation

new REST();
