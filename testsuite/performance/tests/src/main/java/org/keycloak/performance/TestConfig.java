package org.keycloak.performance;

/**
 * @author <a href="mailto:mstrukel@redhat.com">Marko Strukelj</a>
 */
public class TestConfig {

    public static String serverUrl = System.getProperty("serverUrl", "http://localhost:8080/auth");
    public static String authRealm = System.getProperty("authRealm", "master");
    public static String authUser = System.getProperty("authUser", "admin");
    public static String authPassword = System.getProperty("authPassword", "admin");
    public static String authClient = System.getProperty("authClient", "admin-cli");

    public static final int numOfWorkers = Integer.getInteger("numOfWorkers", 1);

    public static final int numOfRealms = Integer.getInteger("numOfRealms", 1);
    public static final int usersPerRealm = Integer.getInteger("usersPerRealm", 2);
    public static final int clientsPerRealm = Integer.getInteger("clientsPerRealm", 2);
    public static final int realmRoles = Integer.getInteger("realmRoles", 2);
    public static final int realmRolesPerUser = Integer.getInteger("realmRolesPerUser", 2);
    public static final int clientRolesPerUser = Integer.getInteger("clientRolesPerUser", 2);
    public static final int clientRolesPerClient = Integer.getInteger("clientRolesPerClient", 2);

    public static final int runUsers = Integer.getInteger("runUsers", 1);
    public static final int userThinkTime = Integer.getInteger("userThinkTime", 5);
    public static final int refreshTokenPeriod = Integer.getInteger("refreshTokenPeriod", 10);

    static {
        // if KEYCLOAK_SERVER_URIS env var is set, and system property serverUrl is not set,
        // take the first server uri, and set it as serverUrl
        if (System.getProperty("serverUrl") == null) {
            String env = System.getenv("KEYCLOAK_SERVER_URIS");
            if (env != null) {
                TestConfig.serverUrl = env.split(" ")[0];
            }
        }
    }

    static void validateConfiguration() {
        if (realmRolesPerUser > realmRoles) {
            throw new RuntimeException("Can't have more realmRolesPerUser than there are realmRoles");
        }
        if (clientRolesPerUser > clientsPerRealm * clientRolesPerClient) {
            throw new RuntimeException("Can't have more clientRolesPerUser than there are all client roles (clientsPerRealm * clientRolesPerClient)");
        }
    }

    public static String toStringDatasetProperties() {
        return String.format("numOfRealms: %s\nusersPerRealm: %s\nclientsPerRealm: %s\nrealmRoles: %s\nrealmRolesPerUser: %s\nclientRolesPerUser: %s\nclientRolesPerClient: %s",
                numOfRealms, usersPerRealm, clientsPerRealm, realmRoles, realmRolesPerUser, clientRolesPerUser, clientRolesPerClient);
    }
}
