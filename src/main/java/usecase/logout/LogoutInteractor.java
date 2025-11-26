package usecase.logout;

/**
 * The Logout Interactor.
 */
public class LogoutInteractor implements LogoutInputBoundary {
    private final LogoutUserDataAccessInterface userDataAccessObject;
    private final LogoutOutputBoundary logoutPresenter;

    public LogoutInteractor(final LogoutUserDataAccessInterface userDataAccessInterface,
                            final LogoutOutputBoundary logoutOutputBoundary) {
        userDataAccessObject = userDataAccessInterface;
        logoutPresenter = logoutOutputBoundary;
    }

    @Override
    public void execute() {
        // * set the current username to null in the DAO
        // * instantiate the `LogoutOutputData`, which needs to contain the username.
        // * tell the presenter to prepare a success view.
        final LogoutOutputData data = new LogoutOutputData(userDataAccessObject.getCurrentUsername());
        userDataAccessObject.setCurrentUsername(null);
        logoutPresenter.prepareSuccessView(data);
    }
}

