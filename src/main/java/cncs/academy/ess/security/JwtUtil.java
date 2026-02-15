package cncs.academy.ess.security;

import cncs.academy.ess.model.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;

public class JwtUtil {
    private static final String SECRET_KEY = "sagrahERAaeraeRaehgraer43t1#$g$#143bQqerwg141QEB";
    private static final String ISSUER = "tasklist-api";
    private static final long EXPIRATION_TIME = 3600000; // 1 hora

    // gerar Token JWT assinado para o utilizador
    public static String generateToken(User user){
        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
        return JWT.create()
                .withIssuer(ISSUER)
                .withSubject(user.getUsername())
                .withClaim("userId", user.getId())
                .withClaim("role", user.getRole())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(algorithm);
    }

    // Valida o token JWT e retorna o username. Retorna Null se inválido
    public static DecodedJWT validateToken(String token){
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .build();
            return verifier.verify(token);
        } catch (JWTVerificationException e){
            return null;
        }
    }

    public static Integer getUserIdFromToken(String token){
        DecodedJWT jwt = validateToken(token);
        if(jwt == null){
            return null;
        }
        return jwt.getClaim("userId").asInt();
    }

    public static String getRoleFromToken(String token){
        DecodedJWT jwt = validateToken(token);
        if(jwt == null){
            return null;
        }
        return jwt.getClaim("role").asString();
    }
}




