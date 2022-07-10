(function (log, undefined) {
    var LEVEL = {
        DEBUG: 'DEBUG',
        INFO: 'INFO',
        WARN: 'WARN',
        ERROR: 'ERROR'
    };

    log.currentLevel = LEVEL.WARN;

    log.debug = function (msg) {
        if (this.currentLevel === LEVEL.DEBUG) console.log(msg);
    };
    log.info = function (msg) {
        if (this.currentLevel === LEVEL.DEBUG || this.currentLevel === LEVEL.INFO) console.info(msg);
    };
    log.warn = function (msg) {
        if (this.currentLevel === LEVEL.DEBUG || this.currentLevel === LEVEL.INFO || this.currentLevel === LEVEL.WARN) console.warn(msg);
    };
    log.error = function (msg) {
        if (this.currentLevel === LEVEL.DEBUG || this.currentLevel === LEVEL.INFO || this.currentLevel === LEVEL.WARN || this.currentLevel === LEVEL.ERROR) console.error(msg);
    };
}(window.log = window.log || {}));