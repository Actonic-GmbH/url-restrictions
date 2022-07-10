package proficient.developer.plugins.jira.urlrestrictions.webwork;

import com.atlassian.jira.web.action.ActionViewDataMappings;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import proficient.developer.plugins.jira.urlrestrictions.ao.RuleEntity;
import proficient.developer.plugins.jira.urlrestrictions.manager.URConfigurationManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigWebwork extends JiraWebActionSupport {
    private final Map data = new HashMap();
    private final URConfigurationManager urConfigurationManager;
    private String pattern;
    private String restrictionType;
    private String restrictionValue;
    private String responseType;
    private String responseValue;
    private String action;
    private int ruleId;

    public ConfigWebwork(URConfigurationManager urConfigurationManager) {
        this.urConfigurationManager = urConfigurationManager;
    }

    @ActionViewDataMappings({"input", "success", "error"})
    public Map getData() {
        return data;
    }
    @Override
    public String doExecute() throws Exception {
        if ("add".equals(action)) {
            urConfigurationManager.addRule(pattern, restrictionType, restrictionValue, responseType, responseValue);
        }
        if ("edit".equals(action)) {
            urConfigurationManager.editRule(ruleId, pattern, restrictionType, restrictionValue, responseType, responseValue);
        }

        List<RuleEntity> ruleEntities = urConfigurationManager.getRuleEntities();
        data.put("ruleEntities", ruleEntities);

        return INPUT;
    }

    public void setRuleId(int ruleId) {
        this.ruleId = ruleId;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public void setRestrictionType(String restrictionType) {
        this.restrictionType = restrictionType;
    }

    public void setRestrictionValue(String restrictionValue) {
        this.restrictionValue = restrictionValue;
    }

    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }

    public void setResponseValue(String responseValue) {
        this.responseValue = responseValue;
    }
}
