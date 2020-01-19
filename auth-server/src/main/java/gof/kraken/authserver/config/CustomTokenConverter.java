package gof.kraken.authserver.config;

import gof.kraken.authserver.model.CustomUserDetails;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import java.util.HashMap;
import java.util.Map;

public class CustomTokenConverter extends JwtAccessTokenConverter {

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {

        Map<String, Object> additionalInfo = new HashMap<>();
        Map<String, Object> userMapping = new HashMap<>();
        userMapping.put("customer_id", ((CustomUserDetails) authentication.getPrincipal()).getCustomerId());
        userMapping.put("location", ((CustomUserDetails) authentication.getPrincipal()).getLocation());
        additionalInfo.put("extra_info", userMapping);
        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
        accessToken = super.enhance(accessToken, authentication);

        return accessToken;
    }
}