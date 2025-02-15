const fs = require('fs');
const path = require('path');

// Create Logger class
function Logger() {
    // Create logs directory if it doesn't exist
    const logsDir = path.join(__dirname, 'logs');
    if (!fs.existsSync(logsDir)) {
        fs.mkdirSync(logsDir);
        console.log("Logs directory created at:", logsDir);
    }
    
    this.logPath = path.join(logsDir, 'logs.txt');
    
    // Create log file if it doesn't exist
    if (!fs.existsSync(this.logPath)) {
        fs.writeFileSync(this.logPath, '', 'utf8');
        console.log("Log file created at:", this.logPath);
    }
    
    console.log("Logger Initialized with path:", this.logPath);
}

// Add methods to prototype
Logger.prototype.logRequest = function(req) {
    console.log("Logging Request");
    const userId = req.headers['x-user-id'] || 'anonymous';
    const logEntry = {
        timestamp: new Date(),
        ip: req.ip,
        api: req.path,
        uuid: userId,
        meta_data: JSON.stringify(req.body),
        status: 'pending'
    };

    this.writeToLog(logEntry);
};

Logger.prototype.logResponse = function(req, status) {
    console.log("Logging Response");
    const userId = req.headers['x-user-id'] || 'anonymous';
    const logStatus = {
        timestamp: new Date(),
        ip: req.ip,
        api: req.path,
        uuid: userId,
        meta_data: JSON.stringify(req.body),
        status: status
    };

    this.writeToLog(logStatus);
};

Logger.prototype.writeToLog = function(data) {
    console.log("Writing to log");
    fs.appendFile(
        this.logPath,
        JSON.stringify(data) + '\n',
        (err) => {
            if (err) console.error('Error writing to log file:', err);
        }
    );
};

// Export constructor function
module.exports = Logger;