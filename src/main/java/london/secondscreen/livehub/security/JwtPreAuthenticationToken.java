package london.secondscreen.livehub.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;

import javax.security.auth.Subject;

public class JwtPreAuthenticationToken extends AbstractAuthenticationToken {
    private final String authToken;
    private final String username;

    public JwtPreAuthenticationToken(final String authToken, final String username) {
        super(null);
        this.authToken = authToken;
        this.username = username;
        setAuthenticated(false);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }

    @Override
    public boolean implies(Subject subject) {
        return false;
    }

    public String getAuthToken() {
        return authToken;
    }

    public String getUsername() {
        return username;
    }
}
