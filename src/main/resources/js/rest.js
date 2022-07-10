define("url-restrictions/rest", ["jquery"], function ($) {

    function Rest() {
        this.addRule = function(formData, successCallback, errorCallback) {
            $.ajax({
                type: "PUT",
                url: AJS.contextPath() + "/rest/url-restriction-rule/1.0/rule",
                data: formData,
                success: function (data) {
                    if (typeof successCallback === "function") successCallback(data);
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    if (typeof errorCallback === "function") errorCallback(jqXHR, textStatus, errorThrown);
                }
            });
        };

        this.editRule = function(formData, successCallback, errorCallback) {
            $.ajax({
                type: "POST",
                url: AJS.contextPath() + "/rest/url-restriction-rule/1.0/rule",
                data: formData,
                success: function (data) {
                    if (typeof successCallback === "function") successCallback(data);
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    if (typeof errorCallback === "function") errorCallback(jqXHR, textStatus, errorThrown);
                }
            });
        };

        this.getRuleById = function(ruleId, successCallback, errorCallback) {
            $.ajax({
                type: "GET",
                url: AJS.contextPath() + "/rest/url-restriction-rule/1.0/rule/" + ruleId,
                success: function (data) {
                    if (typeof successCallback === "function") successCallback(data);
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    if (typeof errorCallback === "function") errorCallback(jqXHR);
                }
            });
        };

        this.deleteRuleById = function(ruleId, successCallback, errorCallback) {
            $.ajax({
                type: "DELETE",
                url: AJS.contextPath() + "/rest/url-restriction-rule/1.0/rule/" + ruleId,
                success: function (data) {
                    if (typeof successCallback === "function") successCallback(data);
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    if (typeof errorCallback === "function") errorCallback(jqXHR);
                }
            });
        };

        this.submitChecker = function(formData, successCallback, errorCallback) {
            $.ajax({
                type: "GET",
                url: AJS.contextPath() + "/rest/url-restriction-rule/1.0/checker/check",
                data: formData,
                success: function (data) {
                    if (typeof successCallback === "function") successCallback(data);
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    if (typeof errorCallback === "function") errorCallback(jqXHR, textStatus, errorThrown);
                }
            });
        };
    }

    return new Rest();
});
