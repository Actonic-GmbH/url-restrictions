package proficient.developer.plugins.jira.urlrestrictions.service;

import com.atlassian.jira.security.groups.GroupManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.common.base.CharMatcher;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.net.InetAddresses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import proficient.developer.plugins.jira.urlrestrictions.ao.RuleEntity;
import proficient.developer.plugins.jira.urlrestrictions.exception.ConfirmationNeededException;
import proficient.developer.plugins.jira.urlrestrictions.exception.WrongRuleParameterException;
import proficient.developer.plugins.jira.urlrestrictions.manager.URConfigurationManager;
import proficient.developer.plugins.jira.urlrestrictions.model.RuleModel;
import proficient.developer.plugins.jira.urlrestrictions.model.RulesWorkResultModel;
import proficient.developer.plugins.jira.urlrestrictions.util.CIDRUtils;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class RuleValidatorService {

    private final String ADD_MODE = "add";
    public static final List<String> RESTRICTION_TYPES = Lists.newArrayList(
            "byName", "byIp", "byMask", "byGroup"
    );
    public static final List<String> RESPONSE_TYPE = Lists.newArrayList(
            "forbidden", "redirect"
    );
    public static final HashMap<String, String> FORBIDDEN_PATTERNS = new HashMap<String, String>() {{
        put("ur-configuration.jspa", "You will lost access to this configuration");
        put("url-restriction-rule", "You will lost access to this configuration");
    }};
    public static final HashMap<String, String> WARNING_PATTERNS = new HashMap<String, String>() {{
        put("upm", "It may affect a lot of functions");
        put("admin", "It may affect a lot of functions");
        put("secure", "It may affect a lot of functions");
        put("rest", "It may affect a lot of functions");
    }};
    private final URConfigurationManager urConfigurationManager;
    @ComponentImport
    private final GroupManager groupManager;
    @ComponentImport
    private final UserManager userManager;

    @Autowired
    public RuleValidatorService(URConfigurationManager urConfigurationManager, GroupManager groupManager, UserManager userManager) {
        this.urConfigurationManager = urConfigurationManager;
        this.groupManager = groupManager;
        this.userManager = userManager;
    }

    public void validateRuleParameters(String pattern, String restrictionType, String restrictionValue,
                                       String responseType, String responseValue, String type) throws WrongRuleParameterException, ConfirmationNeededException {
        pattern = CharMatcher.whitespace().trimFrom(pattern);
        restrictionType = CharMatcher.whitespace().trimFrom(restrictionType);
        restrictionValue = CharMatcher.whitespace().trimFrom(restrictionValue);
        responseType = CharMatcher.whitespace().trimFrom(responseType);
        responseValue = CharMatcher.whitespace().trimFrom(responseValue);

        if (Strings.isNullOrEmpty(pattern)) throw new WrongRuleParameterException("Pattern is empty");
        if (pattern.length() < 3) throw new WrongRuleParameterException("Pattern is too short");
        for (Map.Entry<String, String> entry : FORBIDDEN_PATTERNS.entrySet())
            if (pattern.toLowerCase().contains(entry.getKey())) throw new WrongRuleParameterException(entry.getValue());

        if (Strings.isNullOrEmpty(restrictionType)) throw new WrongRuleParameterException("RestrictionType is empty");
        if (!RESTRICTION_TYPES.contains(restrictionType))
            throw new WrongRuleParameterException("Unknown RestrictionType");
        if (Strings.isNullOrEmpty(restrictionValue)) throw new WrongRuleParameterException("RestrictionValue is empty");

        if (Strings.isNullOrEmpty(responseType)) throw new WrongRuleParameterException("ResponseType is empty");
        if (!RESPONSE_TYPE.contains(responseType)) throw new WrongRuleParameterException("Unknown ResponseType");
        if ("redirect".equals(responseType) && Strings.isNullOrEmpty(responseValue))
            throw new WrongRuleParameterException("ResponseValue is empty. Please put some redirect URL");

        if ("byMask".equals(restrictionType)) {
            try {
                new CIDRUtils(restrictionValue);
            } catch (UnknownHostException e) {
                throw new WrongRuleParameterException("Incorrect mask. Use something like 192.168.1.0/24. " + e.getMessage());
            }
        }

        if ("byIp".equals(restrictionType)) {
            try {
                InetAddresses.forString(restrictionValue);
            } catch (IllegalArgumentException e) {
                throw new WrongRuleParameterException("Incorrect IP adress. " + e.getMessage());
            }
        }

        if (type.equals(ADD_MODE)) {
            List<RuleEntity> ruleEntities = urConfigurationManager.getRuleEntities();
            for (RuleEntity ruleEntity : ruleEntities) {
                if (Objects.equals(pattern, ruleEntity.getPattern()) && Objects.equals(restrictionType, ruleEntity.getRestrictionType()) &&
                        Objects.equals(restrictionValue, ruleEntity.getRestrictionValue()) && Objects.equals(responseType, ruleEntity.getResponseType()) &&
                        Objects.equals(responseValue, ruleEntity.getResponseValue())) {
                    throw new WrongRuleParameterException("Duplicate rule found. ID is " + ruleEntity.getID());
                }
            }
        }

        for (Map.Entry<String, String> entry : WARNING_PATTERNS.entrySet())
            if (pattern.toLowerCase().contains(entry.getKey())) throw new ConfirmationNeededException(entry.getValue());
    }

    public RulesWorkResultModel checkRules(String currentUrl, String userName, String ip) {
        ApplicationUser applicationUser = userManager.getUserByName(userName);

        return checkRules(currentUrl, applicationUser, ip);
    }

    public RulesWorkResultModel checkRules(String currentUrl, ApplicationUser applicationUser, String ip) {
        if (Strings.isNullOrEmpty(currentUrl) || applicationUser == null || Strings.isNullOrEmpty(applicationUser.getName())
                || Strings.isNullOrEmpty(ip)) {
            return new RulesWorkResultModel(RulesWorkResultModel.Result.SKIP);
        }

        List<RuleEntity> ruleEntities = urConfigurationManager.getRuleEntities();
        if (ruleEntities == null || ruleEntities.size() == 0) {
            return new RulesWorkResultModel(RulesWorkResultModel.Result.SKIP);
        }

        for (RuleEntity ruleEntity : ruleEntities) {
            if (isCheckMatch(ruleEntity.getPattern(), ruleEntity.getRestrictionType(), ruleEntity.getRestrictionValue(), applicationUser, ip, currentUrl)) {
                RuleModel ruleModel = new RuleModel(ruleEntity);
                return getResultBasedOnDefinedResponse(ruleModel);
            }
        }

        return new RulesWorkResultModel(RulesWorkResultModel.Result.SKIP);
    }


    private RulesWorkResultModel getResultBasedOnDefinedResponse(RuleModel ruleModel) {
        if ("forbidden".equals(ruleModel.getResponseType())) {
            return new RulesWorkResultModel(ruleModel, RulesWorkResultModel.Result.FORBIDDEN);
        }
        if ("redirect".equals(ruleModel.getResponseType())) {
            RulesWorkResultModel rulesWorkResultModel = new RulesWorkResultModel(ruleModel, RulesWorkResultModel.Result.REDIRECT);
            rulesWorkResultModel.setRedirectTo(ruleModel.getResponseValue());

            return rulesWorkResultModel;
        }

        return new RulesWorkResultModel(RulesWorkResultModel.Result.SKIP);
    }

    private boolean isCheckMatch(String pattern, String restrictionType, String restrictionValue, ApplicationUser applicationUser, String ip, String currentUrl) {
        if (currentUrl.contains(pattern)) {
            if ("byName".equals(restrictionType)) {
                if (applicationUser.getName().equalsIgnoreCase(restrictionValue)) {
                    return true;
                }
            }
            if ("byIp".equals(restrictionType)) {
                if (restrictionValue.equalsIgnoreCase(ip)) {
                    return true;
                }
            }
            if ("byMask".equals(restrictionType)) {
                try {
                    CIDRUtils cidrUtils = new CIDRUtils(restrictionValue);
                    if (cidrUtils.isInRange(ip)) return true;
                } catch (UnknownHostException e) {
                }
            }
            if ("byGroup".equals(restrictionType)) {
                return groupManager.isUserInGroup(applicationUser, restrictionValue);
            }
        }

        return false;
    }
}
