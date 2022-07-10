package proficient.developer.plugins.jira.urlrestrictions.model;

/**
 *
 */
public class RulesWorkResultModel {
    private Result result;
    private String redirectTo;
    private RuleModel ruleModel;

    /* */
    public RulesWorkResultModel(Result result) {
        this.result = result;
    }

    /* */
    public RulesWorkResultModel(RuleModel ruleModel, Result result) {
        this.ruleModel = ruleModel;
        this.result = result;
    }

    public Result getResult() {
        return result;
    }

    public String getRedirectTo() {
        return redirectTo;
    }

    public void setRedirectTo(String redirectTo) {
        this.redirectTo = redirectTo;
    }

    public enum Result {
        SKIP, REDIRECT, FORBIDDEN
    }
}
