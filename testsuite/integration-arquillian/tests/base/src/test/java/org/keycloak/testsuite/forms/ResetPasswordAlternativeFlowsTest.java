/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.keycloak.testsuite.forms;

import java.util.List;

import javax.mail.internet.MimeMessage;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.TargetsContainer;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.models.AuthenticationFlowModel;
import org.keycloak.models.RealmModel;
import org.keycloak.models.utils.DefaultAuthenticationFlows;
import org.keycloak.representations.idm.AuthenticationFlowRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.testsuite.AbstractTestRealmKeycloakTest;
import org.keycloak.testsuite.admin.ApiUtil;
import org.keycloak.testsuite.admin.authentication.AbstractAuthenticationTest;
import org.keycloak.testsuite.model.ClientModelTest;
import org.keycloak.testsuite.pages.AppPage;
import org.keycloak.testsuite.pages.ErrorPage;
import org.keycloak.testsuite.pages.LoginPage;
import org.keycloak.testsuite.pages.LoginPasswordResetPage;
import org.keycloak.testsuite.pages.LoginPasswordUpdatePage;
import org.keycloak.testsuite.pages.LoginTotpPage;
import org.keycloak.testsuite.pages.LoginUsernameOnlyPage;
import org.keycloak.testsuite.pages.PasswordPage;
import org.keycloak.testsuite.runonserver.RunOnServerDeployment;
import org.keycloak.testsuite.util.FlowUtil;
import org.keycloak.testsuite.util.GreenMailRule;
import org.keycloak.testsuite.util.MailUtils;
import org.keycloak.testsuite.util.URLUtils;
import org.keycloak.testsuite.util.UserBuilder;

import static org.junit.Assert.assertEquals;
import static org.keycloak.testsuite.arquillian.DeploymentTargetModifier.AUTH_SERVER_CURRENT;

/**
 * Test for the various alternatives of reset-credentials flow or browser flow (non-default setup of the  flows)
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class ResetPasswordAlternativeFlowsTest extends AbstractTestRealmKeycloakTest {

    @Deployment
    @TargetsContainer(AUTH_SERVER_CURRENT)
    public static WebArchive deploy() {
        return RunOnServerDeployment.create(UserResource.class, ClientModelTest.class)
                .addPackages(true,
                        "org.keycloak.testsuite",
                        "org.keycloak.testsuite.model");
    }


    private String userId;

    @Rule
    public GreenMailRule greenMail = new GreenMailRule();

    @Page
    protected LoginPage loginPage;

    @Page
    protected LoginUsernameOnlyPage loginUsernameOnlyPage;

    @Page
    protected PasswordPage passwordPage;

    @Page
    protected LoginPasswordResetPage resetPasswordPage;

    @Page
    protected LoginPasswordUpdatePage updatePasswordPage;

    @Page
    protected LoginTotpPage loginTotpPage;

    @Page
    protected ErrorPage errorPage;

    @Page
    protected AppPage appPage;

    @Override
    public void configureTestRealm(RealmRepresentation testRealm) {
    }

    @Before
    public void setup() {
        log.info("Adding login-test user");
        UserRepresentation user = UserBuilder.create()
                .username("login-test")
                .email("login@test.com")
                .enabled(true)
                .build();

        userId = ApiUtil.createUserAndResetPasswordWithAdminClient(testRealm(), user, "password");
        getCleanup().addUserId(userId);
    }


    // Test with default reset-credentials flow and alternative browser flow with separate username and password screen.
    //
    // Click "Forget password" on browser flow passwordPAge and assert that button "Back" is not available as we switched to
    // different flow (reset-credentials" flow).
    @Test
    public void testBackButtonWhenSwitchToResetCredentialsFlowFromAlternativeBrowserFlow() {
        try {
            BrowserFlowTest.configureBrowserFlowWithAlternativeCredentials(testingClient);

            // Provide username and then click "Forget password"
            provideUsernameAndClickResetPassword("login-test");

            // Click "back to login" link. Should be on password page of the browser flow (under URL "authenticate")
            resetPasswordPage.backToLogin();
            passwordPage.assertCurrent();
            Assert.assertTrue(URLUtils.currentUrlMatches("/login-actions/authenticate"));
            passwordPage.assertBackButtonAvailability(true);

            // Click "back". Should be on usernameForm
            passwordPage.clickBackButton();
            loginUsernameOnlyPage.assertCurrent();
        } finally {
            revertFlows();
        }
    }


    // Test with default reset-credentials flow and alternative browser flow with separate username and password screen.
    //
    // Provide username and click "Forget password" on browser flow. Then provide non-existing username in reset-credentials 1st screen.
    // User should be cleared from authentication context and no email should be sent
    @Test
    public void testNotExistingUserProvidedInResetCredentialsFlow() {
        try {
            BrowserFlowTest.configureBrowserFlowWithAlternativeCredentials(testingClient);

            // Provide username and then click "Forget password"
            provideUsernameAndClickResetPassword("login-test");

            // Provide non-existent username after "login-test" user already set in the context by browser flow
            resetPasswordPage.changePassword("non-existent");

            loginUsernameOnlyPage.assertCurrent();
            assertEquals("You should receive an email shortly with further instructions.", loginUsernameOnlyPage.getSuccessMessage());

            // Assert no email was sent as user was cleared
            assertEquals(0, greenMail.getReceivedMessages().length);

        } finally {
            revertFlows();
        }
    }


    // Test with default reset-credentials flow and alternative browser flow with separate username and password screen.
    //
    // Provide username and click "Forget password" on browser flow. Then provide different username in reset-credentials 1st screen than provided earlier
    // on browser flow username screen. There should be an error and no email should be sent
    @Test
    public void testDifferentUserProvidedInResetCredentialsFlow() {
        try {
            BrowserFlowTest.configureBrowserFlowWithAlternativeCredentials(testingClient);

            // Provide username and then click "Forget password"
            provideUsernameAndClickResetPassword("login-test");

            // Provide existing username "test-user@localhost" for different user than "login-test", which was set earlier by browser flow
            resetPasswordPage.changePassword("test-user@localhost");

            // Should be on error page
            errorPage.assertCurrent();

            // Assert no email was sent
            assertEquals(0, greenMail.getReceivedMessages().length);
        } finally {
            revertFlows();
        }
    }


    // Test with default reset-credentials flow and alternative browser flow with separate username and password screen.
    //
    // Provide username and click "Forget password" on browser flow. Then provide same username in reset-credentials 1st screen than provided earlier
    // on browser flow username screen. There should be an email successfully sent.
    @Test
    public void testSameUserProvidedInResetCredentialsFlow() {
        try {
            BrowserFlowTest.configureBrowserFlowWithAlternativeCredentials(testingClient);

            // Provide username and then click "Forget password"
            provideUsernameAndClickResetPassword("login-test");

            // Provide same username "login-test" as earlier in browser flow
            resetPasswordPage.changePassword("login-test");

            loginUsernameOnlyPage.assertCurrent();
            assertEquals("You should receive an email shortly with further instructions.", loginUsernameOnlyPage.getSuccessMessage());

            // Assert email was sent
            assertEquals(1, greenMail.getReceivedMessages().length);
        } finally {
            revertFlows();
        }
    }


    // Test with alternative reset-credentials flow with removed ResetCredentialChooseUser authenticator and with alternative browser
    // flow with separate username and password screen.
    //
    // Provide username and click "Forget password" on browser flow. Then provide same username in reset-credentials 1st screen than provided earlier
    // on browser flow username screen. There should be an email successfully sent.
    @Test
    public void testResetCredentialsFlowWithUsernameProvidedFromBrowserFlow() throws Exception {
        try {
            BrowserFlowTest.configureBrowserFlowWithAlternativeCredentials(testingClient);
            configureResetCredentialsFlowWithoutChooseUser();

            // provides username
            loginUsernameOnlyPage.open();
            loginUsernameOnlyPage.login("login-test");

            Assert.assertTrue(passwordPage.isCurrent());

            // Click "Forget password"
            passwordPage.clickResetPassword();

            // Should be directly back on the loginPage with the message about sent email
            loginUsernameOnlyPage.assertCurrent();
            assertEquals("You should receive an email shortly with further instructions.", loginUsernameOnlyPage.getSuccessMessage());

            // Assert email was sent
            assertEquals(1, greenMail.getReceivedMessages().length);

            // Successfully reset password
            MimeMessage message = greenMail.getReceivedMessages()[0];

            String changePasswordUrl = MailUtils.getPasswordResetEmailLink(message);

            driver.navigate().to(changePasswordUrl.trim());

            updatePasswordPage.assertCurrent();
            updatePasswordPage.changePassword("resetPassword", "resetPassword");

            // Assert user authenticated
            Assert.assertEquals(AppPage.RequestType.AUTH_RESPONSE, appPage.getRequestType());
            Assert.assertNotNull(oauth.getCurrentQuery().get(OAuth2Constants.CODE));
        } finally {
            revertFlows();
        }
    }


    private void provideUsernameAndClickResetPassword(String username) {
        // provides username
        loginUsernameOnlyPage.open();
        loginUsernameOnlyPage.login(username);

        Assert.assertTrue(passwordPage.isCurrent());

        // Click "Forget password"
        passwordPage.clickResetPassword();

        // Assert switched to the "reset-credentials" flow, but button "back" not available
        resetPasswordPage.assertCurrent();
        Assert.assertTrue(URLUtils.currentUrlMatches("/login-actions/reset-credentials"));
        resetPasswordPage.assertBackButtonAvailability(false);
    }


    // Configure reset-credentials flow without ResetCredentialsChooseUser authenticator.
    // This is good setup together with browser flow configured to have separate username/password screens
    private void configureResetCredentialsFlowWithoutChooseUser() {
        final String newFlowAlias = "resetcred - alternative";
        testingClient.server("test").run(session -> FlowUtil.inCurrentRealm(session).copyResetCredentialsFlow(newFlowAlias));
        testingClient.server("test").run(session -> FlowUtil.inCurrentRealm(session)
                .selectFlow(newFlowAlias)
                .removeExecution(0)
                .defineAsResetCredentialsFlow()
        );
    }


    private void revertFlows() {
        List<AuthenticationFlowRepresentation> flows = testRealm().flows().getFlows();

        // Set default flows
        RealmRepresentation realm = testRealm().toRepresentation();
        realm.setBrowserFlow(DefaultAuthenticationFlows.BROWSER_FLOW);
        realm.setResetCredentialsFlow(DefaultAuthenticationFlows.RESET_CREDENTIALS_FLOW);
        testRealm().update(realm);

        // Delete created flows
        AuthenticationFlowRepresentation newBrowserFlow = AbstractAuthenticationTest.findFlowByAlias("browser - alternative", flows);
        if (newBrowserFlow != null) {
            testRealm().flows().deleteFlow(newBrowserFlow.getId());
        }

        AuthenticationFlowRepresentation newResetCredFlow = AbstractAuthenticationTest.findFlowByAlias("resetcred - alternative", flows);
        if (newResetCredFlow != null) {
            testRealm().flows().deleteFlow(newResetCredFlow.getId());
        }
    }
}
