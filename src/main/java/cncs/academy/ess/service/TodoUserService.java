package cncs.academy.ess.service;

import cncs.academy.ess.model.User;
import cncs.academy.ess.repository.UserRepository;
import cncs.academy.ess.security.JwtUtil;
import cncs.academy.ess.security.PasswordHasher;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class TodoUserService {
    private final UserRepository repository;

    public TodoUserService(UserRepository userRepository) {
        this.repository = userRepository;
    }
    public User addUser(String username, String password) throws Exception {
        String hashedPassword = PasswordHasher.hashPassword(password);
        User user = new User(username, hashedPassword, "Base");
        int id = repository.save(user);
        user.setId(id);
        return user;
    }
    public User getUser(int id) {
        return repository.findById(id);
    }

    public void deleteUser(int id) {
        repository.deleteById(id);
    }

    public String login(String username, String password) throws Exception {
        User user = repository.findByUsername(username);
        if (user == null) {
            return null;
        }
        boolean validPassword = PasswordHasher.validatePassword(password, user.getPassword());
        if (!validPassword) {
            return null;
        }
        return "Bearer " + JwtUtil.generateToken(user);
    }
}
