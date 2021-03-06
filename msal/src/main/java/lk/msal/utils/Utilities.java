package lk.msal.utils;

import com.microsoft.graph.authentication.BaseAuthenticationProvider;
import com.microsoft.graph.models.User;
import com.microsoft.graph.requests.GraphServiceClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import javax.annotation.Nonnull;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class Utilities {

    private Utilities() {
        throw new IllegalStateException("Utility class. Don't instantiate");
    }

    public static Map<String,String> filterClaims(OidcUser principal) {
        final String[] claimKeys = {"sub", "aud", "ver", "iss", "name", "oid", "preferred_username"};
        final List<String> includeClaims = Arrays.asList(claimKeys);

        Map<String,String> filteredClaims = new HashMap<>();
        includeClaims.forEach(claim -> {
            if (principal.getIdToken().getClaims().containsKey(claim)) {
                filteredClaims.put(claim, principal.getIdToken().getClaims().get(claim).toString());
            }
        });
        return filteredClaims;
    }

    public static Map<String,String> graphUserProperties(OAuth2AuthorizedClient graphAuthorizedClient) {
        final GraphServiceClient graphServiceClient = Utilities.getGraphServiceClient(graphAuthorizedClient);
        final User user = graphServiceClient.me().buildRequest().get();
        Map<String,String> userProperties = new HashMap<>();

        if (user == null) {
            userProperties.put("Graph Error", "GraphSDK returned null User object.");
        } else {
            userProperties.put("Display Name", user.displayName);
            userProperties.put("Phone Number", user.mobilePhone);
            userProperties.put("City", user.city);
            userProperties.put("Given Name", user.givenName);
        }
        return userProperties;
    }

    public static GraphServiceClient getGraphServiceClient(@Nonnull OAuth2AuthorizedClient graphAuthorizedClient) {
        return GraphServiceClient.builder().authenticationProvider(new GraphAuthenticationProvider(graphAuthorizedClient))
                .buildClient();
    }

    private static class GraphAuthenticationProvider extends BaseAuthenticationProvider {

        private OAuth2AuthorizedClient graphAuthorizedClient;

        public GraphAuthenticationProvider(@Nonnull OAuth2AuthorizedClient graphAuthorizedClient) {
            this.graphAuthorizedClient = graphAuthorizedClient;
        }

        @Override
        public CompletableFuture<String> getAuthorizationTokenAsync(@Nonnull final URL requestUrl){
            return CompletableFuture.completedFuture(graphAuthorizedClient.getAccessToken().getTokenValue());
        }
    }

}
