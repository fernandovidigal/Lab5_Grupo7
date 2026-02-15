package cncs.academy.ess.controller;

import cncs.academy.ess.model.User;
import cncs.academy.ess.repository.UserRepository;
import cncs.academy.ess.security.JwtUtil;
import io.javalin.http.Context;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.Handler;
import io.javalin.http.UnauthorizedResponse;
import org.casbin.jcasbin.main.Enforcer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthorizationMiddleware implements Handler {
    private static final Logger logger = LoggerFactory.getLogger(AuthorizationMiddleware.class);
    private final Enforcer enforcer;

    public AuthorizationMiddleware(Enforcer enforcer) {
        this.enforcer = enforcer;
    }

    @Override
    public void handle(Context ctx) throws Exception {
        if (ctx.header("Access-Control-Request-Headers") != null) {
            return;
        }

        if (ctx.path().equals("/user") && ctx.method().name().equals("POST")
        || ctx.path().equals("/login") && ctx.method().name().equals("POST")) {
            return;
        }
        // Check if authorization header exists
        String authorizationHeader = ctx.header("Authorization");
        String path = ctx.path();
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            logger.info("Authorization header is missing or invalid '{}' for path '{}'", authorizationHeader, path);
            throw new UnauthorizedResponse();
        }

        // Extract token from authorization header
        String token = authorizationHeader.substring(7); // Remove "Bearer "

        // Check if token is valid (perform authentication logic)
        Integer userId = JwtUtil.getUserIdFromToken(token);
        String role = JwtUtil.getRoleFromToken(token);

        if (userId == null || role == null) {
            logger.info("Authorization token is invalid ou expired {}", token  );
            throw new UnauthorizedResponse();
        }

        // Add user ID to context for use in route handlers
        ctx.attribute("userId", userId);

        // Verificar permissões com o Casbin
        String routePath = normalizePath(ctx.path());
        String method = ctx.method().name();

        boolean allowed = enforcer.enforce(role, path, method);

        if(!allowed){
            logger.info("Access denied for role '{}' to '{}' '{}'", role, method, routePath);
            throw new ForbiddenResponse("Access Denied");
        }

        logger.info("Access granted for role '{}' to '{}' '{}'", role, method, routePath);

    }

    private String normalizePath(String path){
        // Substituir IDs numéricos por *
        return path.replaceAll("/\\d+", "/*");
    }

    /*private int validateTokenAndGetUserId(Context cts, String token) {
        User user = userRepository.findByUsername(token);
        if (user == null) {
            // user not found, token is invalid
            return -1;
        }
        return user.getId();
    }*/
}

