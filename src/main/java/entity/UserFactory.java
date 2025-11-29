package entity;

/**
 * Factory for creating CommonUser objects.
 */
public class UserFactory {

    /**
     * Create.
     * @param name .
     * @param password .
     * @return .
     */
    public User create(final String name, final String password) {
        return new User(name, password);
    }
}
