package org.keycloak.performance;

/**
 * This class takes dataset parameters, and test run configuration parameters, and produces various iterators, than can
 * be used to create test dataset, and to create test session data consistent with the test dataset.
 *
 * For example, we usually need a big test dataset of realms, realm roles, clients - some public, some private using secrets, some with standard flow, some using direct grants,
 * some defining client roles, then we have users, with usernames, and passwords, maybe some clients have service accounts to act on their own behalf.
 *
 * Then we have tests which should simulate a huge number of different users, and client apps.
 * Each client app can behave slightly differently, so we would want to simulate several different behaviors depending on the app.
 * Roles that user has can play a role in how the app should behave.
 *
 * If we simply concentrate on login(), and logout()
 *
 * @author <a href="mailto:mstrukel@redhat.com">Marko Strukelj</a>
 */
public class SessionBehavior {
}
