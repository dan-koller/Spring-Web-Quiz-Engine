package io.dankoller.github.webquizengine.auth;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
    /**
     * This method returns a 401 error code (Unauthorized) to the client if authentication is required and has failed.
     *
     * @param request       That resulted in an <code>AuthenticationException</code>
     * @param response      So that the user agent can begin authentication
     * @param authException That caused the invocation
     * @throws IOException If an error occurs
     */
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
    }
}
