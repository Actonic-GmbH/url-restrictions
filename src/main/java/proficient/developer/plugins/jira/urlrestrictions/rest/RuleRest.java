package proficient.developer.plugins.jira.urlrestrictions.rest;

import com.atlassian.jira.permission.GlobalPermissionKey;
import com.atlassian.jira.security.GlobalPermissionManager;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import proficient.developer.plugins.jira.urlrestrictions.ao.RuleEntity;
import proficient.developer.plugins.jira.urlrestrictions.exception.ConfirmationNeededException;
import proficient.developer.plugins.jira.urlrestrictions.exception.WrongRuleParameterException;
import proficient.developer.plugins.jira.urlrestrictions.manager.URConfigurationManager;
import proficient.developer.plugins.jira.urlrestrictions.model.RuleModel;
import proficient.developer.plugins.jira.urlrestrictions.service.RuleValidatorService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Objects;

@Path("/")
public class RuleRest {
    private final static Logger log = LoggerFactory.getLogger(RuleRest.class);
    private final RuleValidatorService ruleValidatorService;
    private final URConfigurationManager urConfigurationManager;
    private final JiraAuthenticationContext jiraAuthenticationContext;
    private final GlobalPermissionManager globalPermissionManager;

    /* */
    public RuleRest(URConfigurationManager urConfigurationManager, RuleValidatorService ruleValidatorService,
                    JiraAuthenticationContext jiraAuthenticationContext, GlobalPermissionManager globalPermissionManager) {
        this.urConfigurationManager = urConfigurationManager;
        this.ruleValidatorService = ruleValidatorService;
        this.jiraAuthenticationContext = jiraAuthenticationContext;
        this.globalPermissionManager = globalPermissionManager;
    }

    private boolean isUserAllowed() {
        ApplicationUser applicationUser = jiraAuthenticationContext.getLoggedInUser();
        if (applicationUser != null) {
            if (globalPermissionManager.hasPermission(GlobalPermissionKey.SYSTEM_ADMIN, applicationUser)) {
                return true;
            }
        }

        return false;
    }

    @PUT
    @Path("rule")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    public Response addRule(@FormParam("id") int id, @FormParam("pattern") String pattern,
                            @FormParam("restrictionType") String restrictionType, @FormParam("restrictionValue") String restrictionValue,
                            @FormParam("responseType") String responseType, @FormParam("responseValue") String responseValue,
                            @FormParam("confirmed") String confirmed) {
        if (!isUserAllowed()) return Response.serverError().build();

        Gson gson = new Gson();

        if (Strings.isNullOrEmpty(confirmed) || !Objects.equals("yes", confirmed)) {
            try {
                ruleValidatorService.validateRuleParameters(pattern, restrictionType, restrictionValue, responseType, responseValue, "add");
            } catch (WrongRuleParameterException e) {
                return Response.serverError().entity(gson.toJson(e.getMessage())).build();
            } catch (ConfirmationNeededException e) {
                return Response.ok(gson.toJson(ImmutableMap.of("type", "confirm", "text", e.getMessage()))).build();
            }
        }

        RuleEntity ruleEntity = urConfigurationManager.addRule(pattern, restrictionType, restrictionValue, responseType, responseValue);
        RuleModel ruleModel = new RuleModel(ruleEntity);
        log.debug(ruleModel.toString());

        return Response.ok(gson.toJson(ImmutableMap.of("type", "ok", "rule", ruleModel))).build();
    }

    @POST
    @Path("rule")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED})
    public Response editRule(@FormParam("ruleId") int ruleId, @FormParam("pattern") String pattern,
                             @FormParam("restrictionType") String restrictionType, @FormParam("restrictionValue") String restrictionValue,
                             @FormParam("responseType") String responseType, @FormParam("responseValue") String responseValue,
                             @FormParam("confirmed") String confirmed) {
        if (!isUserAllowed()) return Response.serverError().build();

        Gson gson = new Gson();

        if (Strings.isNullOrEmpty(confirmed) || !Objects.equals("yes", confirmed)) {
            try {
                ruleValidatorService.validateRuleParameters(pattern, restrictionType, restrictionValue, responseType, responseValue, "edit");
            } catch (WrongRuleParameterException e) {
                return Response.serverError().entity(gson.toJson(e.getMessage())).build();
            } catch (ConfirmationNeededException e) {
                return Response.ok(gson.toJson(ImmutableMap.of("type", "confirm", "text", e.getMessage()))).build();
            }
        }

        RuleEntity ruleEntity = urConfigurationManager.editRule(ruleId, pattern, restrictionType, restrictionValue, responseType, responseValue);
        RuleModel ruleModel = new RuleModel(ruleEntity);
        log.debug(ruleModel.toString());

        return Response.ok(new Gson().toJson(ImmutableMap.of("type", "ok", "rule", ruleModel))).build();
    }

    @GET
    @Path("rule/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getRule(@PathParam("id") int id) {
        if (!isUserAllowed()) return Response.serverError().build();

        RuleEntity ruleEntity = urConfigurationManager.getRuleEntity(id);
        RuleModel ruleModel = new RuleModel(ruleEntity);

        return Response.ok(new Gson().toJson(ruleModel)).build();
    }

    @DELETE
    @Path("rule/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response deleteRule(@PathParam("id") int id) {
        if (!isUserAllowed()) return Response.serverError().build();

        urConfigurationManager.deleteRuleEntity(id);

        return Response.ok(new Gson().toJson("Done")).build();
    }
}
