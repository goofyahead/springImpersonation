package ninja.goofyahead.main.interceptor;

import ninja.goofyahead.main.annotations.AllowImpersonation;
import ninja.goofyahead.main.model.CustomUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class ImpersonateInterceptor extends HandlerInterceptorAdapter {

    private static final Logger logger = LoggerFactory.getLogger(ImpersonateInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<? extends GrantedAuthority> adminAuthority = authentication.getAuthorities().stream().filter(elem -> elem.getAuthority().equalsIgnoreCase("ROLE_ADMIN")).findFirst();
        if (adminAuthority.isPresent()) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            if (handlerMethod.hasMethodAnnotation(AllowImpersonation.class)) {
                Optional<String> onBehalfOf = Optional.ofNullable(request.getHeader("on-behalf-of"));
                CustomUserDetails current = (CustomUserDetails) authentication.getPrincipal();
                current.setAdminImpersonating(true);
                current.setCustomerId(Long.valueOf(onBehalfOf.get()));
                addPermissions((Collection<SimpleGrantedAuthority>) authentication.getAuthorities());
                logger.debug("Got this intent to be this person: " + onBehalfOf.get());
                logger.debug("User is: " + current.getUsername());
            }
        } else {
            logger.debug("You are no admin so I will ignore any on-behalf-of header");
        }
        return super.preHandle(request, response, handler);
    }

    /**
     * Sample of modifying permissions that should be retrieved from the clientService
     * @param previousAuthorities
     */
    private void addPermissions(Collection<SimpleGrantedAuthority> previousAuthorities) {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("READ_COMPANY");
        List<SimpleGrantedAuthority> updatedAuthorities = new ArrayList<>();
        updatedAuthorities.add(authority);
        updatedAuthorities.addAll(previousAuthorities);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        SecurityContextHolder.getContext().getAuthentication().getPrincipal(),
                        SecurityContextHolder.getContext().getAuthentication().getCredentials(),
                        updatedAuthorities)
        );
    }
}
