package london.secondscreen.livehub.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Component
public class JwtAuthenticationConverter implements Function<ServerWebExchange, Mono<Authentication>> {
    public static final String AUTH_HEADER_NAME = "X-AUTH-TOKEN";
    public static final String AUTH_PARAM_NAME = "token";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ReactiveUserDetailsService userDetailsService;
    private final JwtTokenUtil jwtTokenUtil;

    public JwtAuthenticationConverter(ReactiveUserDetailsService userDetailsService, JwtTokenUtil jwtTokenUtil) {
        Assert.notNull(userDetailsService, "userDetailsService cannot be null");
        Assert.notNull(jwtTokenUtil, "jwtTokenUtil cannot be null");

        this.userDetailsService = userDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public Mono<Authentication> apply(ServerWebExchange exchange) throws BadCredentialsException {
        ServerHttpRequest request = exchange.getRequest();
        try {
            Authentication authentication = null;
            String username = null;

            String authToken = exchange.getRequest().getHeaders().getFirst(AUTH_HEADER_NAME);

            if (authToken == null && !request.getQueryParams().isEmpty()) {
                String authTokenParam = request.getQueryParams().getFirst(AUTH_PARAM_NAME);
                if (authTokenParam != null) authToken = authTokenParam;
            }

            if (authToken != null) {
                try {
                    username = jwtTokenUtil.getUsernameFromToken(authToken);
                } catch (IllegalArgumentException e) {
                    logger.error("an error occurred during getting username from token", e);
                } catch (Exception e) {
                    logger.warn("the token is expired and not valid anymore", e);
                }
            } else {
                logger.warn("couldn't find bearer string, will ignore the header");
            }

            logger.info("checking authentication for user " + username);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                return Mono.just(new JwtPreAuthenticationToken(authToken, username));
            }

            return Mono.just(authentication);
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid token...");
        }
    }
}
