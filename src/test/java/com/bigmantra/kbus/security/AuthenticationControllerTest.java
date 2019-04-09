package com.bigmantra.kbus.security;

import com.bigmantra.kbus.AbstractWebIntegrationTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
public class AuthenticationControllerTest extends AbstractWebIntegrationTest {

    private static boolean setUpIsDone = false;


    @Value("${security.route.authentication}")
    private String authenticationRoute;

    @Value("${security.route.authentication.refresh}")
    private String refreshRoute;

    @Autowired
    private TokenUtils tokenUtils;

    @Autowired
    private UserRepository userRepo;

    @Before
    public void setUp() throws Exception {

        if (setUpIsDone) {
            return;
        }

        userRepo.save(UserObjectMother.getNormalUserFor("username", "password"));
        userRepo.save(UserObjectMother.getAdminUserFor("admin", "admin"));
        userRepo.save(UserObjectMother.getExpiredUserFor("expired", "expired"));
        userRepo.save(UserObjectMother.getAdminUserFor("Administrator", "Adm$n$strator"));

        setUpIsDone = true;

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void requestingAuthenticationWithNoCredentialsReturnsBadRequest() throws Exception {


        mvc.perform(post("/auth")//
                .content("")//
                .contentType(MediaType.APPLICATION_JSON_VALUE)//
                .accept(MediaType.APPLICATION_JSON_VALUE))//
                .andDo(MockMvcResultHandlers.print())//
                .andExpect(status().isBadRequest());//

    }

    @Test
    public void requestingAuthenticationWithInvalidCredentialsReturnsUnauthorized() throws Exception {

        mvc.perform(post("/auth")//
                .content(asJsonString(SecurityTestApiConfig.INVALID_AUTHENTICATION_REQUEST))
                .contentType(MediaType.APPLICATION_JSON_VALUE)//
                .accept(MediaType.APPLICATION_JSON_VALUE))//
                .andDo(MockMvcResultHandlers.print())//
                .andExpect(status().isUnauthorized());//


    }

    @Test
    public void requestingProtectedResourceWithValidCredentialsReturnsOK() throws Exception {

        MvcResult result = mvc.perform(post("/auth")//
                .content(asJsonString(SecurityTestApiConfig.ADMIN_AUTHENTICATION_REQUEST))
                .contentType(MediaType.APPLICATION_JSON_VALUE)//
                .accept(MediaType.APPLICATION_JSON_VALUE))//
                .andDo(MockMvcResultHandlers.print())//
                .andExpect(status().isOk())//
                .andExpect(jsonPath("$.token", userNameFromToken(is("admin"))))//
                .andReturn();


/*		ObjectMapper mapper = new ObjectMapper();
		AuthenticationResponse authResp = mapper.readValue(result.getResponse().getContentAsString(), AuthenticationResponse.class);


		mvc.perform(get("/user")//
				.header("X-Auth-Token", authResp.getToken())
				.contentType(MediaType.APPLICATION_JSON_VALUE)//
				.accept(MediaType.APPLICATION_JSON_VALUE))//
				.andDo(MockMvcResultHandlers.print())//
				.andExpect(status().isOk());//*/


    }

    @Test
    public void requestingAuthenticationRefreshWithNoAuthorizationTokenReturnsUnauthorized() throws Exception {


        mvc.perform(get("/auth/refresh")//
                .contentType(MediaType.APPLICATION_JSON_VALUE)//
                .accept(MediaType.APPLICATION_JSON_VALUE))//
                .andDo(MockMvcResultHandlers.print())//
                .andExpect(status().isUnauthorized());

    }


    @Test
    public void requestingAuthWithExpiredTokenReturnsUnauthorized() throws Exception {


        String token = tokenUtils.getExpiredTokenForWebUser("expired");
        log.debug("token is {}", token);


    }

    private FeatureMatcher<String, String> userNameFromToken(Matcher<String> matcher) {
        return new FeatureMatcher<String, String>(matcher, "User Name Parsed from Token", "userNameFromToken") {
            @Override
            protected String featureValueOf(String actual) {
                return tokenUtils.getUsernameFromToken(actual);
            }
        };
    }


}
