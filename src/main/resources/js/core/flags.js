define("url-restrictions/flags", ["url-restriction/safe-jquery"], function ($) {

    function Flags() {
        var _this = this;
        var ajsVersion = parseFloat(AJS.version.substr(0, 3));

        this.showFlag = function (title, body, type) {
            if (!type) type = "info"; // success, info, warning, error

            if (ajsVersion < 5.9) {
                require(['aui/flag'], function (flag) {
                    flag({
                        type: type,
                        title: title,
                        body: body,
                        close: 'auto'
                    });
                });
            } else {
                //version gt or eq 5.9
                AJS.flag({
                    type: type,
                    title: title,
                    body: body,
                    close: 'auto'
                });
            }
        }

        this.show = function (config) { //title, body, type, close
            if (!config.type) config.type = "info"; // success, info, warning, error
            if (!config.close) config.close = "auto"; // auto, manual

            if (ajsVersion < 5.9) {
                require(['aui/flag'], function (flag) {
                    flag(config);
                });
            } else {
                AJS.flag(config);
            }
        }

        this.simpleSuccess = function (message) {
            var config = {
                type: "success",
                body: message,
                close: 'auto'
            }
            _this.show(config);
        }
        this.simpleWarning = function (message) {
            var config = {
                type: "warning",
                body: message,
                close: 'auto'
            }
            _this.show(config);
        }
    }

    return new Flags();
});

