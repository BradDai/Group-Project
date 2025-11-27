package dataaccess;

import java.util.HashMap;
import java.util.Map;

import entity.User;
import usecase.change_password.ChangePasswordUserDataAccessInterface;
import usecase.login.LoginUserDataAccessInterface;
import usecase.logout.LogoutUserDataAccessInterface;
import usecase.signup.SignupUserDataAccessInterface;

/**
 * In-memory implementation of the DAO for storing user data. This implementation does
 * NOT persist data between runs of the program.
 */
public class InMemoryUserDataAccessObject implements SignupUserDataAccessInterface,
    LoginUserDataAccessInterface,
    ChangePasswordUserDataAccessInterface,
    LogoutUserDataAccessInterface {

    private final Map<String, User> users = new HashMap<>();

    private String currentUsername;

    @Override
    public boolean existsByName(final String identifier) {
        return users.containsKey(identifier);
    }

    @Override
    public void save(final User user) {
        users.put(user.getName(), user);
    }

    @Override
    public User get(final String username) {
        return users.get(username);
    }

    @Override
    public void setCurrentUsername(final String name) {
        currentUsername = name;
    }

    @Override
    public String getCurrentUsername() {
        return currentUsername;
    }

    @Override
    public void changePassword(final User user) {
        // Replace the old entry with the new password
        users.put(user.getName(), user);
    }

}
