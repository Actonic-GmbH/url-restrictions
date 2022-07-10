require(["jquery", "url-restrictions/domReady", "url-restrictions/rest", "url-restrictions/flags", "url-restrictions/aui-select"], function ($, domReady, ConfRest, flag, CoreAuiSelect) {

    domReady(function () {
        new Configuration().init();
    });

    function Configuration() {
        var isDropDown = true;
        this.init = function () {
            getCustomEvents();
        };

        function getCustomEvents() {
            $("#add-rule-dialog-show-btn").on("click", function () {
                $("#add-rule-dialog").remove();
                $("#rule-dialog-placeholder").html(ACTONIC.UC.CONFIGURATION.showDialog(
                    {
                        action: 'add',
                        ruleId: "",
                        pattern: "",
                        restrictionType: "byName",
                        responseType: "forbidden",
                        responseValue: ""
                    }
                ));
                AJS.dialog2("#add-rule-dialog").show();
                CoreAuiSelect.initUserSelector("restrictionValue", null, false);
                getDialogEvents();
            });

            $("#checker-submit-button").on("click", function (event) {
                event.preventDefault();
                var formData = $("#checker-form").serialize();
                ConfRest.submitChecker(
                    formData,
                    function (data) {
                        log.debug(data);

                        if (data.result === "SKIP") {
                            flag.showFlag(
                                "No rules matches",
                                '<p>No rules to match these url, user or IP</p>',
                                "info");
                        }
                        if (data.result === "FORBIDDEN") {
                            flag.showFlag(
                                "Match found, 403 Forbidden will be shown",
                                '<p>Rule ID = ' + data.ruleModel.id + '</p>' +
                                '<p>pattern = ' + data.ruleModel.pattern + '</p>' +
                                '<p>restrictionType = ' + data.ruleModel.restrictionType + '</p>' +
                                '<p>restrictionValue = ' + data.ruleModel.restrictionValue + '</p>' +
                                '<p>responseType = ' + data.ruleModel.responseType + '</p>',
                                "warning");
                        }
                        if (data.result === "REDIRECT") {
                            flag.showFlag(
                                "Match found, redirect will be applied",
                                '<p>Rule ID = ' + data.ruleModel.id + '</p>' +
                                '<p>pattern = ' + data.ruleModel.pattern + '</p>' +
                                '<p>restrictionType = ' + data.ruleModel.restrictionType + '</p>' +
                                '<p>restrictionValue = ' + data.ruleModel.restrictionValue + '</p>' +
                                '<p>responseType = ' + data.ruleModel.responseType + '</p>' +
                                '<p>responseValue = ' + data.ruleModel.responseValue + '</p>',
                                "warning");
                        }
                    },
                    function (jqXHR, textStatus, errorThrown) {
                        log.error(jqXHR);
                        log.error(textStatus);

                        flag.showFlag(
                            "Error checking rules",
                            '<p> ' + jqXHR.responseText + '</p>',
                            "error");
                    }
                );

                setAllCookies();
            });

            $(".edit-rule-button").on("click", function () {
                var ruleId = $(this).attr("ruleId");
                ConfRest.getRuleById(ruleId,
                    function (data) {
                        $("#add-rule-dialog").remove();
                        $("#rule-dialog-placeholder").html(ACTONIC.UC.CONFIGURATION.showDialog(
                            {
                                action: 'edit',
                                ruleId: data.id,
                                pattern: data.pattern,
                                restrictionType: data.restrictionType,
                                responseType: data.responseType,
                                responseValue: data.responseValue
                            }
                        ));
                        var selectorId = "restrictionValue";
                        CoreAuiSelect.initUserSelector(selectorId, data.restrictionValue, false);
                        AJS.dialog2("#add-rule-dialog").show();
                        getDialogEvents();
                    },
                    function () {
                        log.error(jqXHR.responseText);

                        flag.showFlag(
                            "Error getting rule",
                            '<p> ' + jqXHR.responseText + '</p>',
                            "error");
                    });
            });

            $(".delete-rule-button").on("click", function () {
                var ruleId = $(this).attr("ruleId");
                ConfRest.deleteRuleById(ruleId,
                    function (data) {
                        log.debug(data);

                        location.reload();
                    },
                    function () {
                        log.error(jqXHR);
                        flag.showFlag(
                            "Error deleting rule",
                            '<p> ' + jqXHR.responseText + '</p>',
                            "error");
                    });
            });

            $("#rules-table tr").not(':first').hover(
                function () {
                    $(this).css("background", "lavender");
                },
                function () {
                    $(this).css("background", "");
                }
            );
        }

        function getDialogEvents() {
            $("#restrictionType").off("change");
            $("#restrictionType").on("change", function (event) {
                event.preventDefault();
                var selectedRestrictionType = $(this).val();
                var selectorId = "restrictionValue";
                if(selectedRestrictionType == "byName") {
                    isDropDown = true;
                    CoreAuiSelect.initUserSelector(selectorId, null, false);
                } else if(selectedRestrictionType == "byIp") {
                    isDropDown = false;
                    CoreAuiSelect.setDefaultSetting(selectorId, "");
                } else if(selectedRestrictionType == "byMask") {
                    isDropDown = false;
                    CoreAuiSelect.setDefaultSetting(selectorId, "");
                } else if(selectedRestrictionType == "byGroup") {
                    isDropDown = true;
                    CoreAuiSelect.initGroupSelector(selectorId, null, false);
                }
            });
            $("#dialog-submit-button").off("click");
            $("#dialog-submit-button").on("click", function (event) {
                event.preventDefault();
                var action = $("input[name='action']").val();
                var formData = $("#save-rule-form").serialize();
                if(isDropDown) {
                    formData += "&restrictionValue=" + $("#restrictionValue").val();
                }

                log.debug(formData);

                if (action == "add") {
                    ConfRest.addRule(
                        formData,
                        function (data) {
                            if (data.type && data.type === "ok") {
                                $("#add-rule-dialog").remove();
                                location.reload();
                            }
                            if (data.type && data.type === "confirm") {
                                var result = confirm(data.text + "\r\nDo you really want to add this rule?");
                                if (result == true) {
                                    formData += "&confirmed=yes";
                                    ConfRest.editRule(formData, function () {
                                        $("#add-rule-dialog").remove();
                                        location.reload();
                                    });
                                }
                            }
                        },
                        function (jqXHR, textStatus, errorThrown) {
                            flag.showFlag(
                                "Error",
                                '<p>' + jqXHR.responseText + '</p>',
                                "error");
                        });
                } else if (action == "edit") {
                    ConfRest.editRule(
                        formData,
                        function (data) {
                            if (data.type && data.type === "ok") {
                                $("#add-rule-dialog").remove();
                                location.reload();
                            }
                            if (data.type && data.type === "confirm") {
                                var result = confirm(data.text + "\r\nDo you really want to add this rule?");
                                if (result == true) {
                                    formData += "&confirmed=yes";
                                    ConfRest.editRule(formData, function () {
                                        $("#add-rule-dialog").remove();
                                        location.reload();
                                    });
                                }
                            }
                        },
                        function (jqXHR, textStatus, errorThrown) {
                            flag.showFlag(
                                "Error",
                                '<p>' + jqXHR.responseText + '</p>',
                                "error");
                        });
                }
            });

            $("#dialog-close-button").off("click");
            $("#dialog-close-button").on("click", function () {
                AJS.dialog2("#add-rule-dialog").hide();
                $("#add-rule-dialog").remove();
            });
        }

        function getAllCookies() {
            $("input[name='url']").val(CookieUtils.getCookie("url"));
            $("input[name='userName']").val(CookieUtils.getCookie("userName"));
            $("input[name='ip']").val(CookieUtils.getCookie("ip"));
        }

        function setAllCookies() {
            CookieUtils.setCookie("url", $("input[name='url']").val());
            CookieUtils.setCookie("userName", $("input[name='userName']").val());
            CookieUtils.setCookie("ip", $("input[name='ip']").val());
        }
    }
});
