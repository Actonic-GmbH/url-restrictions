package proficient.developer.plugins.jira.urlrestrictions.filter;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import proficient.developer.plugins.jira.urlrestrictions.model.RulesWorkResultModel;
import proficient.developer.plugins.jira.urlrestrictions.service.RuleValidatorService;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class RestrictionsFilter implements Filter {
    private static final Logger log = LoggerFactory.getLogger(RestrictionsFilter.class);
    @ComponentImport
    private JiraAuthenticationContext jiraAuthenticationContext;
    private RuleValidatorService ruleValidatorService;

    @Autowired
    public RestrictionsFilter(
        JiraAuthenticationContext jiraAuthenticationContext,
        RuleValidatorService ruleValidatorService
    ) {
        this.jiraAuthenticationContext = jiraAuthenticationContext;
        this.ruleValidatorService = ruleValidatorService;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;

        String url = httpServletRequest.getRequestURL().toString();
        String ip = httpServletRequest.getRemoteAddr();

        RulesWorkResultModel rulesWorkResultModel = ruleValidatorService.checkRules(url, jiraAuthenticationContext.getLoggedInUser(), ip);
        if (rulesWorkResultModel.getResult() == RulesWorkResultModel.Result.FORBIDDEN) {
            log.info("FORBIDDEN. url = " + url + ". ip = " + ip);
            HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
            httpServletResponse.reset();
            httpServletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);

            return;
        }
        if (rulesWorkResultModel.getResult() == RulesWorkResultModel.Result.REDIRECT) {
            log.info("REDIRECT. url = " + url + ". ip = " + ip);
            HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
            httpServletResponse.reset();
            httpServletResponse.setStatus(HttpServletResponse.SC_TEMPORARY_REDIRECT);
            httpServletResponse.setHeader("Location", rulesWorkResultModel.getRedirectTo());

            return;
        }
        log.info("ALLOWED. url = " + url + ". ip = " + ip);

        chain.doFilter(servletRequest, servletResponse);
    }

    private String getFullURL(HttpServletRequest request) {
        StringBuffer requestURL = request.getRequestURL();
        String queryString = request.getQueryString();

        if (queryString == null) {
            return requestURL.toString();
        } else {
            return requestURL.append('?').append(queryString).toString();
        }
    }

    @Override
    public void destroy() {
    }

    public void setRuleValidatorService(RuleValidatorService ruleValidatorService) {
        this.ruleValidatorService = ruleValidatorService;
    }

    private String isLoggedInUser() {
        return String.valueOf(jiraAuthenticationContext.isLoggedInUser());
    }

    public void setJiraAuthenticationContext(JiraAuthenticationContext jiraAuthenticationContext) {
        this.jiraAuthenticationContext = jiraAuthenticationContext;
    }
}
