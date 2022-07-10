package proficient.developer.plugins.jira.urlrestrictions.model;

import proficient.developer.plugins.jira.urlrestrictions.ao.RuleEntity;

/**
 *
 */
public class RuleModel {
    private int id;
    private String pattern;
    private String restrictionType;
    private String responseType;
    private String restrictionValue;
    private String responseValue;

    /* */
    public RuleModel(int id, String pattern, String restrictionType, String responseType, String restrictionValue, String responseValue) {
        this.id = id;
        this.pattern = pattern;
        this.restrictionType = restrictionType;
        this.responseType = responseType;
        this.restrictionValue = restrictionValue;
        this.responseValue = responseValue;
    }

    /* */
    public RuleModel(RuleEntity ruleEntity) {
        this.id = ruleEntity.getID();
        this.pattern = ruleEntity.getPattern();
        this.restrictionType = ruleEntity.getRestrictionType();
        this.responseType = ruleEntity.getResponseType();
        this.restrictionValue = ruleEntity.getRestrictionValue();
        this.responseValue = ruleEntity.getResponseValue();
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRestrictionType() {
        return restrictionType;
    }

    public void setRestrictionType(String restrictionType) {
        this.restrictionType = restrictionType;
    }

    public String getResponseType() {
        return responseType;
    }

    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getRestrictionValue() {
        return restrictionValue;
    }

    public void setRestrictionValue(String restrictionValue) {
        this.restrictionValue = restrictionValue;
    }

    public String getResponseValue() {
        return responseValue;
    }

    public void setResponseValue(String responseValue) {
        this.responseValue = responseValue;
    }

    @Override
    public String toString() {
        return "RuleModel{" +
                "id=" + id +
                ", pattern='" + pattern + '\'' +
                ", restrictionType='" + restrictionType + '\'' +
                ", responseType='" + responseType + '\'' +
                ", restrictionValue='" + restrictionValue + '\'' +
                ", responseValue='" + responseValue + '\'' +
                '}';
    }
}

