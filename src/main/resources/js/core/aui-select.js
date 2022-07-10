define("url-restrictions/aui-select", ["jquery"], function ($) {

        function CoreAuiSelect() {
            var _this = this;
            this.setDefaultSetting = function(selectorId, initValue) {
                var parent = $("#" + selectorId).parent();
                $("#" + selectorId).remove();
                $("#s2id_" + selectorId).remove();
                $(parent).append("<input type=\"text\" class=\"text medium-field\" id=\"" + selectorId + "\" name=\"" + selectorId + "\">");
                $("#" + selectorId).val(initValue);
            };
            this.initUserSelector = function (selectorId, currentUserName, multiple) {
                var parent = $("#" + selectorId).parent();
                $("#" + selectorId).remove();
                $("#s2id_" + selectorId).remove();
                $(parent).prepend("<input type=\"text\" class=\"text\" id=\"" + selectorId + "\">");

                var placeholder = multiple ? "Select Users" : "Select User";

                var initialValues;
                var initialNames;
                if (currentUserName) {
                    if (Array.isArray(currentUserName)) {
                        initialValues = [];
                        initialNames = [];
                        currentUserName.forEach(function (userName) {
                            initialValues.push({id: userName, text: userName});
                            initialNames.push(userName);
                        });
                    } else {
                        initialValues = {id: currentUserName, text: currentUserName};
                        initialNames = currentUserName;
                    }
                }
                AJS.$("#" + selectorId).auiSelect2({
                    placeholder: placeholder,
                    allowClear: true,
                    multiple: multiple,
                    ajax: {
                        type: "GET",
                        cache: true,
                        dataType: 'json',
                        url: AJS.contextPath() + "/rest/api/2/user/picker",
                        data: function (term) {
                            return {
                                query: term,
                                maxResults: 100
                            };
                        },
                        results: function (data) {
                            var users = data.users;
                            var results = [];
                            $.each(users, function (index, item) {
                                results.push({id: item.name, text: item.displayName});
                            });
                            return {
                                results: results
                            };
                        }
                    },
                    initSelection: function (element, callback) {
                        if (currentUserName) {
                            callback(initialValues);
                        }
                    }
                });

                if (currentUserName) {
                    $("#" + selectorId).val(initialNames).trigger("change");
                }
            };

            this.initGroupSelector = function (selectorId, currentGroupNames, multiple) {
                var parent = $("#" + selectorId).parent();
                $("#" + selectorId).remove();
                $("#s2id_" + selectorId).remove();
                $(parent).prepend("<input type=\"text\" class=\"text\" id=\"" + selectorId + "\">");

                if (!multiple) multiple = false;

                var placeholder = multiple ? "Select Groups" : "Select Group";

                var initialValues = [];
                if (currentGroupNames) {
                    if (Array.isArray(currentGroupNames)) {
                        currentGroupNames.forEach(function (currentGroupName) {
                            initialValues.push({id: currentGroupName, text: currentGroupName});
                        });
                    } else {
                        if (multiple) {
                            initialValues.push({id: currentGroupNames, text: currentGroupNames});
                        } else {
                            initialValues = {id: currentGroupNames, text: currentGroupNames};
                        }
                    }
                }

                AJS.$("#" + selectorId).auiSelect2({
                    placeholder: placeholder,
                    allowClear: true,
                    multiple: multiple,
                    ajax: {
                        type: "GET",
                        cache: true,
                        dataType: "json",
                        url: AJS.contextPath() + "/rest/api/2/groups/picker",
                        data: function (term) {
                            return {
                                query: term,
                                maxResults: 100
                            };
                        },
                        results: function (data) {
                            var groups = data.groups;
                            var results = [];
                            $.each(groups, function (index, item) {
                                results.push({id: item.name, text: item.name});
                            });
                            return {
                                results: results
                            };
                        }
                    },
                    initSelection: function (element, callback) {
                        if (currentGroupNames) {
                            callback(initialValues);
                        }
                    }
                });

                if (currentGroupNames) {
                    $("#" + selectorId).val(initialValues).trigger("change");
                }
            }
        }

        return new CoreAuiSelect();
    }
);
