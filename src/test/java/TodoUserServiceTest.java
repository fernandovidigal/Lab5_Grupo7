import cncs.academy.ess.model.User;
import cncs.academy.ess.repository.UserRepository;
import cncs.academy.ess.security.PasswordHasher;
import cncs.academy.ess.service.TodoUserService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class TodoUserServiceTest {
    @Test
    void login_shouldReturnValidJWTTokenWhenCredentialsMatch() throws Exception {
        // Arrange
        UserRepository mockRepo = Mockito.mock(UserRepository.class);
        TodoUserService service = new TodoUserService(mockRepo);

        String username = "francisco";
        String password = "xpto12345";
        //String hashedPassword = "b4b3d5c28e317e1f46cc326cf10749f9:3b4f9ccf626b1650cda5477bc8ae205d0bbee8b7d156454f5b5e65791251c444";

        String hashedPassword = PasswordHasher.hashPassword(password);

        User mockUser = new User(username, hashedPassword);
        mockUser.setId(1);

        Mockito.when(mockRepo.findByUsername(username))
                .thenReturn(mockUser);

        // Act
        String token = service.login(username, password);

        // Assert
        // assert: Prefixo Bearer
        assertNotNull(token);
        assertTrue(token.startsWith("Bearer "), "Token deve começar por 'Bearer '");

        // Extrair o JWT
        String jwtToken = token.substring("Bearer ".length()).trim();

        // assert: Estrutura do JWT (3 Partes)
        String[] jwtParts = jwtToken.split("\\.");
        assertEquals(3, jwtParts.length, "Token JWT deve ter 3 partes: header.payload.signature");

        // Descodificar o JWT
        DecodedJWT jwt = JWT.decode(jwtToken);

        // assert: claim iss
        assertEquals("tasklist-api", jwt.getIssuer(), "Issuer not valid");

        // assert: userId
        Claim userIdClaim = jwt.getClaim("userId");
        assertNotNull(userIdClaim, "JWT deve conter claim 'userId'");
        assertFalse(userIdClaim.isNull(), "Claim 'userId' não pode ser null");
        assertEquals(mockUser.getId(), userIdClaim.asInt(), "Claim e userId devem ser iguais");

        // assert: issuedAt e expiresAt (iat e exp)
        Date issuedAt = jwt.getIssuedAt();
        Date expiresAt = jwt.getExpiresAt();

        assertNotNull(issuedAt, "JWT deve conter issuedAt claim");
        assertNotNull(expiresAt, "JWT deve conter expiresAt claim");
        assertTrue(expiresAt.after(issuedAt), "expiresAt deve ser depois de issuedAt");
        assertTrue(expiresAt.after(new Date()), "expiresAt deve estar no futuro");

        // Verificar se o repositório foi chamado
        Mockito.verify(mockRepo, Mockito.times(1)).findByUsername(username);
    }
}
