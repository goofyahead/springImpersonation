package ninja.goofyahead.main.configuration;

import ninja.goofyahead.main.model.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.UserAuthenticationConverter;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ResourceServerClientConfig {

    @Autowired
    public void setJwtAccessTokenConverter(JwtAccessTokenConverter jwtAccessTokenConverter) {
        jwtAccessTokenConverter.setAccessTokenConverter(accessTokenConverter());
    }

    public AccessTokenConverter accessTokenConverter() {
        DefaultAccessTokenConverter defaultAccessTokenConverter = new DefaultAccessTokenConverter();
        defaultAccessTokenConverter.setUserTokenConverter(userAuthenticationConverter());
        return defaultAccessTokenConverter;
    }

    public UserAuthenticationConverter userAuthenticationConverter() {
        UserAuthenticationConverter userAuthenticationConverter = new UserAuthenticationConverter() {

            @Override
            public Map<String, ?> convertUserAuthentication(Authentication authentication) {
                Map<String, Object> response = new LinkedHashMap<>();
                response.put(USERNAME, authentication.getName());
                if (authentication.getAuthorities() != null && !authentication.getAuthorities().isEmpty()) {
                    response.put(AUTHORITIES, AuthorityUtils.authorityListToSet(authentication.getAuthorities()));
                }
                return response;

            }

            @Override
            public Authentication extractAuthentication(Map<String, ?> map) {
                if (map.containsKey(USERNAME)) {
                    Map<?, ?> extraInfo = (Map<?, ?>) map.get("extra_info");
                    CustomUserDetails principal = new CustomUserDetails();
                    principal.setCustomerId(new Long((Integer) extraInfo.get("customer_id")));
                    principal.setLocation((String) extraInfo.get("location"));
                    principal.setUsername((String) map.get("user_name"));
                    return new UsernamePasswordAuthenticationToken(principal, "N/A", getAuthorities(map));
                }
                return null;
            }

            private Collection<? extends GrantedAuthority> defaultAuthorities;

            private Collection<? extends GrantedAuthority> getAuthorities(Map<String, ?> map) {
                if (!map.containsKey(AUTHORITIES)) {
                    return defaultAuthorities;
                }
                Object authorities = map.get(AUTHORITIES);
                if (authorities instanceof String) {
                    return AuthorityUtils.commaSeparatedStringToAuthorityList((String) authorities);
                }
                if (authorities instanceof Collection) {
                    return AuthorityUtils.commaSeparatedStringToAuthorityList(StringUtils
                            .collectionToCommaDelimitedString((Collection<?>) authorities));
                }
                throw new IllegalArgumentException("Authorities must be either a String or a Collection");
            }

        };

        return userAuthenticationConverter;
    }
}
