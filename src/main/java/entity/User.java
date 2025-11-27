package entity;

/**
 * A simple entity representing a user. Users have a username and password.
 */
public class User {

    private final String name;
    private final String password;
    private final SubAccount[] subaccounts;

    /**
     * Creates a new user with the given non-empty name and non-empty password.
     *
     * @param name     the username
     * @param password the password
     * @throws IllegalArgumentException if the password or name are empty
     */
    public User(final String name, final String password) {
        if ("".equals(name)) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if ("".equals(password)) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        this.name = name;
        this.password = password;
        this.subaccounts = null;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public SubAccount[] getSubaccounts() {
        return subaccounts;
    }
}
