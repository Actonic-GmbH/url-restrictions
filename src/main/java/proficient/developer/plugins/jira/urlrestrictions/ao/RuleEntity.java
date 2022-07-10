package proficient.developer.plugins.jira.urlrestrictions.ao;

import net.java.ao.Entity;
import net.java.ao.schema.Table;

/**
 *
 */
@Table("ur_rules")
public interface RuleEntity extends Entity {
    String getPattern();
    void setPattern(String pattern);

    String getRestrictionType();
    void setRestrictionType(String restrictionType);

    String getResponseType();
    void setResponseType(String responseType);

    String getRestrictionValue();
    void setRestrictionValue(String restrictionValue);

    String getResponseValue();
    void setResponseValue(String responseValue);
}
