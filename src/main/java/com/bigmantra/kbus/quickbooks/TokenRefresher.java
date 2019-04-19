package com.bigmantra.kbus.quickbooks;


import com.bigmantra.kbus.domain.Setting;
import com.bigmantra.kbus.domain.SettingRepository;
import com.intuit.oauth2.client.OAuth2PlatformClient;
import com.intuit.oauth2.data.BearerTokenResponse;
import com.intuit.oauth2.exception.OAuthException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Slf4j
public class TokenRefresher {

    private OAuth2PlatformClientFactory factory;
    private SettingRepository settingsRepo;
    private static final String QBO_ACCESS_TOKEN = "QBO_ACCESS_TOKEN";
    private static final String QBO_REFRESH_TOKEN = "QBO_REFRESH_TOKEN";

    private static final String QBO_REALM_ID = "QBO_REALM_ID";
    private static final String QBO_CSRF_TOKEN = "QBO_CSRF_TOKEN";

    public TokenRefresher(OAuth2PlatformClientFactory factory,SettingRepository settingsRepo){

        this.settingsRepo=settingsRepo;
        this.factory=factory;
    }


    @Getter
    private String currentQuickbooksAccessToken;

    @Getter
    private String currentRealmId;

    @Getter
    private String currentCsrfToken;

    @Getter
    private String currentRefreshToken;

    @Scheduled(cron = "0 0/30 * * * ?")
    public void refreshAccessToken() {

        OAuth2PlatformClient client = factory.getOAuth2PlatformClient();
        Setting refreshTokenSetting = settingsRepo
                .findById(QBO_REFRESH_TOKEN)
                .orElseThrow(()->new RuntimeException("No Quickbooks Refresh Token found in database. cannot refresh."));
        Setting accessTokenSetting = settingsRepo
                .findById(QBO_ACCESS_TOKEN)
                .orElseThrow(()->new RuntimeException("No Quickbooks Access Token found in database. cannot refresh."));

        try {
            BearerTokenResponse bearerTokenResponse = client.refreshToken(refreshTokenSetting.getValue());
            accessTokenSetting.setValue(bearerTokenResponse.getAccessToken());
            refreshTokenSetting.setValue(bearerTokenResponse.getRefreshToken());
            settingsRepo.save(accessTokenSetting);
            settingsRepo.save(refreshTokenSetting);
            currentQuickbooksAccessToken=accessTokenSetting.getValue();
            log.debug(">>>>>>>>>>>>Successfully refreshed bearer tokens!");


        } catch (OAuthException e) {
            new RuntimeException("Error while calling refreshtoken",e);

        }

    }

    @PostConstruct
    public void initialize() {
        Setting refreshTokenSetting = settingsRepo
                .findById(QBO_REFRESH_TOKEN)
                .orElse(Setting.builder().name(QBO_REFRESH_TOKEN).build());
        Setting accessTokenSetting = settingsRepo
                .findById(QBO_ACCESS_TOKEN)
                .orElse(Setting.builder().name(QBO_ACCESS_TOKEN).build());
        Setting csrfTokenSetting = settingsRepo
                .findById(QBO_CSRF_TOKEN)
                .orElse(Setting.builder().name(QBO_CSRF_TOKEN).build());
        Setting realmIdSetting = settingsRepo
                .findById(QBO_REALM_ID)
                .orElse(Setting.builder().name(QBO_REALM_ID).build());

        currentQuickbooksAccessToken=accessTokenSetting.getValue();
        currentCsrfToken=csrfTokenSetting.getValue();
        currentRealmId=realmIdSetting.getValue();
        currentRefreshToken=refreshTokenSetting.getValue();


    }

}
