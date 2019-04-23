package com.bigmantra.kbus.quickbooks;


import com.bigmantra.kbus.domain.Setting;
import com.bigmantra.kbus.domain.SettingRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intuit.ipp.data.CompanyInfo;
import com.intuit.ipp.data.Error;
import com.intuit.ipp.exception.FMSException;
import com.intuit.ipp.exception.InvalidTokenException;
import com.intuit.ipp.services.DataService;
import com.intuit.ipp.services.QueryResult;
import com.intuit.oauth2.client.OAuth2PlatformClient;
import com.intuit.oauth2.config.OAuth2Config;
import com.intuit.oauth2.config.Scope;
import com.intuit.oauth2.data.BearerTokenResponse;
import com.intuit.oauth2.data.PlatformResponse;
import com.intuit.oauth2.exception.InvalidRequestException;
import com.intuit.oauth2.exception.OAuthException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@Slf4j
public class QuickbooksConnectionController {

    @Autowired
    private OAuth2PlatformClientFactory factory;

    @Autowired
    private SettingRepository settingsRepo;

    @Autowired
    private QBOServiceHelper helper;

    private static final String QBO_ACCESS_TOKEN = "QBO_ACCESS_TOKEN";
    private static final String QBO_REFRESH_TOKEN = "QBO_REFRESH_TOKEN";
    private static final String QBO_REALM_ID = "QBO_REALM_ID";
    private static final String QBO_CSRF_TOKEN = "QBO_CSRF_TOKEN";


    private static final String failureMsg = "Failed";

    @RequestMapping("/quickbooks")
    public String home() {
        return "home";
    }

    @RequestMapping("/connected")
    public String connected() {
        return "connected";
    }


    /**
     * Controller mapping for connectToQuickbooks button
     *
     * @return
     */
    @RequestMapping("/connectToQuickbooks")
    public View connectToQuickbooks(HttpSession session) {
        log.info("inside connectToQuickbooks ");
        OAuth2Config oauth2Config = factory.getOAuth2Config();

        String redirectUri = factory.getPropertyValue("OAuth2AppRedirectUri");

        String csrf = oauth2Config.generateCSRFToken();
        session.setAttribute("csrfToken", csrf);
        try {
            List<Scope> scopes = new ArrayList<Scope>();
            scopes.add(Scope.Accounting);
            return new RedirectView(oauth2Config.prepareUrl(scopes, redirectUri, csrf), true, true, false);
        } catch (InvalidRequestException e) {
            log.error("Exception calling connectToQuickbooks ", e);
        }
        return null;
    }


    /**
     * This is the redirect handler you configure in your app on developer.intuit.com
     * The Authorization code has a short lifetime.
     * Hence Unless a user action is quick and mandatory, proceed to exchange the Authorization Code for
     * BearerToken
     *
     * @param state
     * @param realmId
     * @param session
     * @return
     */
    @RequestMapping(value="/oauth2redirect", method = RequestMethod.GET)
    public String callBackFromOAuth(@RequestParam("code") String authCode, @RequestParam("state") String state, @RequestParam(value = "realmId", required = false) String realmId, HttpSession session) {
        log.debug("inside oauth2redirect of sample");
        try {
            String csrfToken = (String) session.getAttribute("csrfToken");
            if (csrfToken.equals(state)) {
                session.setAttribute("realmId", realmId);
                session.setAttribute("auth_code", authCode);

                OAuth2PlatformClient client = factory.getOAuth2PlatformClient();
                String redirectUri = factory.getPropertyValue("OAuth2AppRedirectUri");
                log.debug("inside oauth2redirect of sample -- redirectUri " + redirectUri);

                BearerTokenResponse bearerTokenResponse = client.retrieveBearerTokens(authCode, redirectUri);

                session.setAttribute("access_token", bearerTokenResponse.getAccessToken());
                session.setAttribute("refresh_token", bearerTokenResponse.getRefreshToken());

                saveTokensAsSettings(bearerTokenResponse);
                saveRealmAndCsrfToken(realmId, csrfToken);

                log.debug("Access token is " + bearerTokenResponse.getAccessToken());
                log.debug("Refresh token is " + bearerTokenResponse.getRefreshToken());

                return new JSONObject().put("response", "Success").toString();
            }
            log.debug("csrf token mismatch ");
        } catch (OAuthException e) {
            log.error("Exception in callback handler ", e);
        }
        return null;
    }

    private void saveRealmAndCsrfToken(@RequestParam(value = "realmId", required = false) String realmId, String csrfToken) {
        Setting csrfTokenSetting = settingsRepo
                .findById(QBO_CSRF_TOKEN)
                .orElse(Setting.builder()
                        .name(QBO_CSRF_TOKEN).build());

        Setting realmIdSetting = settingsRepo
                .findById(QBO_REALM_ID)
                .orElse(Setting.builder()
                        .name(QBO_REALM_ID).build());

        csrfTokenSetting.setValue(csrfToken);
        realmIdSetting.setValue(realmId);

        settingsRepo.save(csrfTokenSetting);
        settingsRepo.save(realmIdSetting);
    }

    private void saveTokensAsSettings(BearerTokenResponse bearerTokenResponse) {
        Setting accessToken = settingsRepo
                .findById(QBO_ACCESS_TOKEN)
                .orElse(Setting.builder()
                        .name(QBO_ACCESS_TOKEN).build());

        accessToken.setValue(bearerTokenResponse.getAccessToken());

        Setting refreshToken = settingsRepo
                .findById(QBO_REFRESH_TOKEN)
                .orElse(Setting.builder()
                        .name(QBO_REFRESH_TOKEN).build());

        refreshToken.setValue(bearerTokenResponse.getRefreshToken());


        settingsRepo.save(accessToken);
        settingsRepo.save(refreshToken);
    }

    /**
     * Sample QBO API call using OAuth2 tokens
     *
     * @param session
     * @return
     */
    @ResponseBody
    @RequestMapping("/quickbooks/getCompanyInfo")
    public String callQBOCompanyInfo(HttpSession session) {

        String realmId = (String) session.getAttribute("realmId");
        if (StringUtils.isEmpty(realmId)) {
            return new JSONObject().put("response", "No realm ID.  QBO calls only work if the accounting scope was passed!").toString();
        }
        String accessToken = (String) session.getAttribute("access_token");

        try {

            //get DataService
            DataService service = helper.getDataService(realmId, accessToken);

            // get all companyinfo
            String sql = "select * from companyinfo";
            QueryResult queryResult = service.executeQuery(sql);
            return processResponse(failureMsg, queryResult);

        }
        /*
         * Handle 401 status code -
         * If a 401 response is received, refresh tokens should be used to get a new access token,
         * and the API call should be tried again.
         */ catch (InvalidTokenException e) {
            return refreshTokens(session, realmId, accessToken, e);

        } catch (FMSException e) {
            List<Error> list = e.getErrorList();
            list.forEach(error -> log.error("Error while calling executeQuery :: " + error.getMessage()));
            return new JSONObject().put("response", failureMsg).toString();
        }

    }

    private String refreshTokens(HttpSession session, String realmId, String accessToken, InvalidTokenException e) {
        log.error("Error while calling executeQuery :: " + e.getMessage());

        //refresh tokens
        log.info("received 401 during companyinfo call, refreshing tokens now");
        OAuth2PlatformClient client = factory.getOAuth2PlatformClient();
        String refreshToken = (String) session.getAttribute("refresh_token");

        try {
            BearerTokenResponse bearerTokenResponse = client.refreshToken(refreshToken);
            session.setAttribute("access_token", bearerTokenResponse.getAccessToken());
            session.setAttribute("refresh_token", bearerTokenResponse.getRefreshToken());

            //call company info again using new tokens
            log.info("calling companyinfo using new tokens");
            DataService service = helper.getDataService(realmId, accessToken);

            // get all companyinfo
            String sql = "select * from companyinfo";
            QueryResult queryResult = service.executeQuery(sql);
            return processResponse(failureMsg, queryResult);

        } catch (OAuthException e1) {
            log.error("Error while calling bearer token :: " + e.getMessage());
            return new JSONObject().put("response", failureMsg).toString();
        } catch (FMSException e1) {
            log.error("Error while calling company currency :: " + e.getMessage());
            return new JSONObject().put("response", failureMsg).toString();
        }
    }

    private String processResponse(String failureMsg, QueryResult queryResult) {
        if (!queryResult.getEntities().isEmpty() && queryResult.getEntities().size() > 0) {
            CompanyInfo companyInfo = (CompanyInfo) queryResult.getEntities().get(0);
            log.info("Companyinfo -> CompanyName: " + companyInfo.getCompanyName());
            ObjectMapper mapper = new ObjectMapper();
            try {
                String jsonInString = mapper.writeValueAsString(companyInfo);
                return jsonInString;
            } catch (JsonProcessingException e) {
                log.error("Exception while getting company info ", e);
                return new JSONObject().put("response", failureMsg).toString();
            }

        }
        return failureMsg;
    }


    /**
     * Call to refresh tokens
     *
     * @param session
     * @return
     */
    @ResponseBody
    @RequestMapping("/quickbooks/refreshToken")
    public String refreshToken(HttpSession session) {

        String failureMsg = "Failed";

        try {

            OAuth2PlatformClient client = factory.getOAuth2PlatformClient();
            String refreshToken = (String) session.getAttribute("refresh_token");
            BearerTokenResponse bearerTokenResponse = client.refreshToken(refreshToken);
            session.setAttribute("access_token", bearerTokenResponse.getAccessToken());
            session.setAttribute("refresh_token", bearerTokenResponse.getRefreshToken());
            String jsonString = new JSONObject()
                    .put("access_token", bearerTokenResponse.getAccessToken())
                    .put("refresh_token", bearerTokenResponse.getRefreshToken()).toString();
            return jsonString;
        } catch (Exception ex) {
            log.error("Exception while calling refreshToken ", ex);
            return new JSONObject().put("response", failureMsg).toString();
        }

    }

    /**
     * Call to revoke tokens
     *
     * @param session
     * @return
     */
    @ResponseBody
    @RequestMapping("/quickbooks/revokeToken")
    public String revokeToken(HttpSession session) {

        String failureMsg = "Failed";

        try {

            OAuth2PlatformClient client = factory.getOAuth2PlatformClient();
            String refreshToken = (String) session.getAttribute("refresh_token");
            PlatformResponse response = client.revokeToken(refreshToken);
            log.info("raw result for revoke token request= " + response.getStatus());
            return new JSONObject().put("response", "Revoke successful").toString();
        } catch (Exception ex) {
            log.error("Exception while calling revokeToken ", ex);
            return new JSONObject().put("response", failureMsg).toString();
        }

    }


}
