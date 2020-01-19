package ninja.goofyahead.main.interceptor;

import ninja.goofyahead.main.annotations.AllowImpersonation;
import ninja.goofyahead.main.model.CustomUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
                current.setCustomerId(Long.valueOf(onBehalfOf.get()));
                logger.debug("Got this intent to be this person: " + onBehalfOf.get());
                logger.debug("User is: " + current.getUsername());
            }
        } else {
            logger.debug("You are no admin so I will ignore any on-behalf-of header");
        }
        return super.preHandle(request, response, handler);
    }
}
