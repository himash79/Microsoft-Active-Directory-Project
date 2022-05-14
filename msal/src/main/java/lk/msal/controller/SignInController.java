package lk.msal.controller;

import lk.msal.utils.Utilities;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SignInController {

    private String hydrateUI(Model model, String fragment) {
        model.addAttribute("bodyContent", String.format("content/%s.html", fragment));
        return "base";
    }

    @GetMapping(value = {"/", "sign_in_status", "/index"})
    public String status(Model model) {
        return hydrateUI(model, "status");
    }

    @GetMapping(path = "/token_details")
    public String tokenDetails(Model model, @AuthenticationPrincipal OidcUser principal) {
        model.addAttribute("claims", Utilities.filterClaims(principal));
        return hydrateUI(model, "token");
    }

    @GetMapping(path = "/call_graph")
    public String callGraph(Model model, @RegisteredOAuth2AuthorizedClient("graph") OAuth2AuthorizedClient graphAuthorizedClient) {
        model.addAttribute("user", Utilities.graphUserProperties(graphAuthorizedClient));
        return hydrateUI(model, "graph");
    }

    @GetMapping(path = "/survey")
    public String survey(Model model) {
        return hydrateUI(model, "survey");
    }

}
