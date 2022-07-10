package proficient.developer.plugins.jira.urlrestrictions.rest;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import proficient.developer.plugins.jira.urlrestrictions.manager.URConfigurationManager;
import proficient.developer.plugins.jira.urlrestrictions.model.RulesWorkResultModel;
import proficient.developer.plugins.jira.urlrestrictions.service.RuleValidatorService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/checker")
public class CheckerRest {
    private final static Logger log = LoggerFactory.getLogger(CheckerRest.class);
    private final RuleValidatorService ruleValidatorService;
    private final URConfigurationManager urConfigurationManager;

    /* */
    public CheckerRest(URConfigurationManager urConfigurationManager, RuleValidatorService ruleValidatorService) {
        this.urConfigurationManager = urConfigurationManager;
        this.ruleValidatorService = ruleValidatorService;
    }

    @GET
    @Path("check")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getRule(@QueryParam("url") String url, @QueryParam("userName") String userName, @QueryParam("ip") String ip) {
        RulesWorkResultModel rulesWorkResultModel = ruleValidatorService.checkRules(url, userName, ip);

        return Response.ok(new Gson().toJson(rulesWorkResultModel)).build();
    }
}
