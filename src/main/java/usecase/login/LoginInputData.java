package usecase.login;

/**
 * The Input Data for the Login Use Case.
 */
public class LoginInputData {

    private final String username;
    private final String password;

    public LoginInputData(final String username, final String password) {
        this.username = username;
        this.password = password;
    }

    String getUsername() {
        return username;
    }

    String getPassword() {
        return password;
    }

}
