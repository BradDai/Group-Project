package interfaceadapter.logged_in;

import interfaceadapter.ViewManagerModel;
import usecase.change_password.ChangePasswordOutputBoundary;
import usecase.change_password.ChangePasswordOutputData;

/**
 * The Presenter for the Change Password Use Case.
 */
public class ChangePasswordPresenter implements ChangePasswordOutputBoundary {

    private final LoggedInViewModel loggedInViewModel;

    public ChangePasswordPresenter(final ViewManagerModel viewManagerModel,
                                   final LoggedInViewModel loggedInViewModel) {
        this.loggedInViewModel = loggedInViewModel;
    }

    @Override
    public void prepareSuccessView(final ChangePasswordOutputData outputData) {
        loggedInViewModel.getState().setPassword("");
        loggedInViewModel.getState().setPasswordError(null);
        loggedInViewModel.firePropertyChange("password");
    }

    @Override
    public void prepareFailView(final String error) {
        loggedInViewModel.getState().setPasswordError(error);
        loggedInViewModel.firePropertyChange("password");
    }
}
