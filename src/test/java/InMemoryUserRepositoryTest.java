import cncs.academy.ess.model.User;
import cncs.academy.ess.repository.memory.InMemoryUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryUserRepositoryTest {
    private InMemoryUserRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryUserRepository();
    }

    @Test
    void saveAndFindById_ShouldReturnSavedUser() {
        User user = new User("jane", "password");
        int id = repository.save(user);
        User savedUser = repository.findById(id);
        assertEquals(user, savedUser);
    }

    @Test
    void save_ShouldAssignUniqueIds(){
        User u1 = new User("alice", "pass");
        User u2 = new User("bob", "pass");

        int id1 = repository.save(u1);
        int id2 = repository.save(u2);

        assertNotEquals(id1, id2);
    }

    @Test
    void findByUsername_shouldReturnNullWhenNotExists(){
        User result = repository.findByUsername("ghost");

        assertNull(result);
    }

    @Test
    void findById_shouldReturnNullWhenNotExists(){
        User result = repository.findById(9999);
        assertNull(result);
    }

    @Test
    void findAll_shouldReturnNotNull(){
        List<User> users = repository.findAll();
        assertNotNull(users);
    }

    @Test
    void deleteById_shouldReturnNull(){
        // Eliminar o utilizador
        repository.deleteById(1);
        //Verificar se ele existe
        assertNull(repository.findById(1));
    }
}