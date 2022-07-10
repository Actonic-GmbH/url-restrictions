package proficient.developer.plugins.jira.urlrestrictions.manager;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.common.base.CharMatcher;
import net.java.ao.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import proficient.developer.plugins.jira.urlrestrictions.ao.RuleEntity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Component
public class URConfigurationManager {
    @ComponentImport
    private final ActiveObjects activeObjects;

    @Autowired
    public URConfigurationManager(ActiveObjects activeObjects) {
        this.activeObjects = activeObjects;
    }

    public List<RuleEntity> getRuleEntities() {
        RuleEntity[] entities = activeObjects.find(RuleEntity.class);
        return Arrays.asList(entities);
    }

    public RuleEntity getRuleEntity(int id) {
        RuleEntity[] entities = activeObjects.find(RuleEntity.class, Query.select().where("ID = ?", id));
        if (entities.length > 0) {
            return entities[0];
        }

        return null;
    }

    public void deleteRuleEntity(int id) {
        RuleEntity ruleEntity = getRuleEntity(id);
        activeObjects.delete(ruleEntity);
    }

    public RuleEntity addRule(String pattern, String restrictionTypeAsString, String restrictionValue, String responseTypeAsString, String responseValue) {
        pattern = CharMatcher.whitespace().trimFrom(pattern);
        restrictionTypeAsString = CharMatcher.whitespace().trimFrom(restrictionTypeAsString);
        restrictionValue = CharMatcher.whitespace().trimFrom(restrictionValue);
        responseTypeAsString = CharMatcher.whitespace().trimFrom(responseTypeAsString);
        responseValue = CharMatcher.whitespace().trimFrom(responseValue);

        RuleEntity ruleEntity = activeObjects.create(RuleEntity.class, new HashMap<>());
        ruleEntity.setPattern(pattern);
        ruleEntity.setRestrictionType(restrictionTypeAsString);
        ruleEntity.setRestrictionValue(restrictionValue);
        ruleEntity.setResponseType(responseTypeAsString);
        ruleEntity.setResponseValue(responseValue);

        ruleEntity.save();

        return ruleEntity;
    }

    public RuleEntity editRule(int id, String pattern, String restrictionTypeAsString, String restrictionValue, String responseTypeAsString, String responseValue) {
        pattern = CharMatcher.whitespace().trimFrom(pattern);
        restrictionTypeAsString = CharMatcher.whitespace().trimFrom(restrictionTypeAsString);
        restrictionValue = CharMatcher.whitespace().trimFrom(restrictionValue);
        responseTypeAsString = CharMatcher.whitespace().trimFrom(responseTypeAsString);
        responseValue = CharMatcher.whitespace().trimFrom(responseValue);

        RuleEntity ruleEntity = getRuleEntity(id);
        ruleEntity.setPattern(pattern);
        ruleEntity.setRestrictionType(restrictionTypeAsString);
        ruleEntity.setRestrictionValue(restrictionValue);
        ruleEntity.setResponseType(responseTypeAsString);
        ruleEntity.setResponseValue(responseValue);

        ruleEntity.save();

        return ruleEntity;
    }
}
