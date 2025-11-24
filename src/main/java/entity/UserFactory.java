package entity;

/**
 * Factory for creating CommonUser objects.
 */
public class UserFactory {

    public User create(final String name, final String password) {
        return new User(name, password);
    }
}
