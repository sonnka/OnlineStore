package kazantseva.project.OnlineStore.service.impl;

public class AuthServiceImpl {
//
//    @Value("${keycloak.realm}")
//    private String realm;
//
//    @Value("${keycloak.server.url}")
//    private String serverUrl;
//
//    @Value("${keycloak.client.id}")
//    private String clientId;
//
//    @Value("${keycloak.client.secret.key}")
//    private String clientSecret;
//
//    @Value("${keycloak.admin.username}")
//    private String userName;
//
//    @Value("${keycloak.admin.password}")
//    private String password;
//    private ResteasyClientBuilder resteasyClientBuilder;
//
//    public void addUser(CreateCustomer customer) {
//        UsersResource usersResource = getInstance().realm(realm).users();
//
//        CredentialRepresentation credentialRepresentation = createPasswordCredentials(customer.getPassword());
//
//        UserRepresentation user = new UserRepresentation();
//        user.setUsername(customer.getEmail());
//        user.setCredentials(Collections.singletonList(credentialRepresentation));
//        user.setFirstName(customer.getName());
//        user.setLastName(customer.getSurname());
//        user.setEmail(customer.getEmail());
//        user.setEnabled(true);
//        user.setEmailVerified(false);
//
//        usersResource.create(user);
//    }
//
//    public void deleteUser(String userId) {
//        UsersResource usersResource = getInstance().realm(realm).users();
//        usersResource.get(userId)
//                .remove();
//    }
//
//    private CredentialRepresentation createPasswordCredentials(String password) {
//        CredentialRepresentation passwordCredentials = new CredentialRepresentation();
//        passwordCredentials.setTemporary(false);
//        passwordCredentials.setType(CredentialRepresentation.PASSWORD);
//        passwordCredentials.setValue(password);
//        return passwordCredentials;
//    }
//
//    private Keycloak getInstance() {
//        return KeycloakBuilder.builder()
//                .serverUrl(serverUrl)
//                .realm(realm)
//                .grantType(OAuth2Constants.PASSWORD)
//                .username(userName)
//                .password(password)
//                .clientId(clientId)
//                .clientSecret(clientSecret)
//                .resteasyClient(resteasyClientBuilder
//                        .connectionPoolSize(10)
//                        .build())
//                .build();
//    }
}
