package kazantseva.project.OnlineStore.config;

import kazantseva.project.OnlineStore.model.request.AuthRequest;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
public class KeycloakClient {

    private final RestTemplate restTemplate;

    @Value("${keycloak.clientId}")
    private String clientId;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.serverUrl}")
    private String keycloakUrl;

    public AccessTokenResponse authenticate(AuthRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("username", request.getEmail());
        parameters.add("password", request.getPassword());
        parameters.add("grant_type", "password");
        parameters.add("client_id", clientId);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(parameters, headers);

        return restTemplate.exchange(getAuthUrl(),
                HttpMethod.POST,
                entity,
                AccessTokenResponse.class).getBody();
    }

    public AccessTokenResponse refreshToken(String refreshToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("grant_type", "refresh_token");
        parameters.add("client_id", clientId);
        parameters.add("refresh_token", refreshToken);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(parameters, headers);

        return restTemplate.exchange(getAuthUrl(),
                HttpMethod.POST,
                entity,
                AccessTokenResponse.class).getBody();
    }

    private String getAuthUrl() {
        return UriComponentsBuilder.fromHttpUrl(keycloakUrl)
                .pathSegment("realms")
                .pathSegment(realm)
                .pathSegment("protocol")
                .pathSegment("openid-connect")
                .pathSegment("token")
                .toUriString();
    }
}
