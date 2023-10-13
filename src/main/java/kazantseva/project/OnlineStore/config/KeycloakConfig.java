package kazantseva.project.OnlineStore.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.ws.rs.core.Response;
import java.util.List;

@Configuration
@Getter
@Setter
@RequiredArgsConstructor
@ConfigurationProperties("keycloak")
@Slf4j
public class KeycloakConfig {

    private static final String ADMIN_USERNAME = "1@gmail.com";
    private static final String ADMIN_PASSWORD = "admin";
    private static final String CRED_TYPE = "Password";

    private String serverUrl;

    private String realm;

    private String clientId;

    private String username;

    private String password;

    private String roleBuyer;

    private String roleAdmin;

    private String secret;

    private static UserRepresentation getUserRepresentation() {
        CredentialRepresentation adminCredentials = new CredentialRepresentation();
        adminCredentials.setType(CRED_TYPE);
        adminCredentials.setValue(ADMIN_PASSWORD);

        UserRepresentation admin = new UserRepresentation();
        admin.setUsername(ADMIN_USERNAME);
        admin.setEmail(ADMIN_USERNAME);
        admin.setFirstName("Harry");
        admin.setLastName("Potter");
        admin.setEmailVerified(Boolean.TRUE);
        admin.setCredentials(List.of(adminCredentials));
        admin.setEnabled(Boolean.TRUE);
        return admin;
    }

    @Bean
    public Keycloak keycloak() {
        ResteasyClientBuilder resteasyClientBuilder = new ResteasyClientBuilderImpl();

        Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .grantType(OAuth2Constants.PASSWORD)
                .realm("master")
                .clientId("admin-cli")
                .username(username)
                .password(password)
                .resteasyClient(resteasyClientBuilder.connectionPoolSize(10).build())
                .build();

        RealmResource realmResource = keycloak.realm(realm);
        UsersResource usersResource = realmResource.users();

        RealmRepresentation newRealm = new RealmRepresentation();
        newRealm.setId(realm);
        newRealm.setRealm(realm);
        newRealm.setEnabled(true);

        try {
            keycloak.realms().create(newRealm);
        } catch (Exception e) {
            log.error("Something went wrong when creating the realm : {}", e.getMessage());
        }

        ClientRepresentation clientRepresentation = new ClientRepresentation();
        clientRepresentation.setClientId(clientId);
        clientRepresentation.setName(clientId);
        clientRepresentation.setRootUrl("http://localhost:8080");
        clientRepresentation.setWebOrigins(List.of("*"));
        clientRepresentation.setStandardFlowEnabled(Boolean.TRUE);
        clientRepresentation.setPublicClient(Boolean.TRUE);
        clientRepresentation.setDirectAccessGrantsEnabled(Boolean.TRUE);

        try (Response response = realmResource.clients().create(clientRepresentation)) {
        } catch (Exception e) {
            log.error("Something went wrong when creating the keycloak client : {}", e.getMessage());
        }

        RoleRepresentation managerRole = new RoleRepresentation();
        managerRole.setId(roleBuyer);
        managerRole.setName(roleBuyer);
        managerRole.setClientRole(true);

        RoleRepresentation adminRole = new RoleRepresentation();
        adminRole.setId(roleAdmin);
        adminRole.setName(roleAdmin);
        adminRole.setClientRole(true);

        try {
            realmResource.roles().create(managerRole);
            realmResource.roles().create(adminRole);
        } catch (Exception e) {
            log.error("Something went wrong when creating the realm role : {}", e.getMessage());
        }

        UserRepresentation admin = getUserRepresentation();


        try (Response response1 = usersResource.create(admin)) {
            String userId = usersResource.searchByUsername(ADMIN_USERNAME, Boolean.FALSE).get(0).getId();
            RoleRepresentation adminRoleRepresentation = realmResource.roles().get(roleAdmin).toRepresentation();
            RoleRepresentation managerRoleRepresentation = realmResource.roles().get(roleBuyer).toRepresentation();

            usersResource.get(userId).roles().realmLevel().add(List.of(adminRoleRepresentation, managerRoleRepresentation));
        } catch (Exception e) {
            log.error("Something went wrong when creating the keycloak user : {}", e.getMessage());
        }

        return keycloak;
    }
}
