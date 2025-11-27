package usecase.signup;

import java.math.BigDecimal;

import entity.SubAccount;
import entity.User;
import entity.UserFactory;
import usecase.SubAccount.SubAccountDataAccessInterface;

/**
 * The Signup Interactor.
 */
public class SignupInteractor implements SignupInputBoundary {
    private final SignupUserDataAccessInterface userDataAccessObject;
    private final SignupOutputBoundary userPresenter;
    private final UserFactory userFactory;
    private final SubAccountDataAccessInterface subAccountDataAccess;

    public SignupInteractor(final SignupUserDataAccessInterface signupDataAccessInterface,
                            final SignupOutputBoundary signupOutputBoundary,
                            final UserFactory userFactory,
                            final SubAccountDataAccessInterface subAccountDataAccess) {
        this.userDataAccessObject = signupDataAccessInterface;
        this.userPresenter = signupOutputBoundary;
        this.userFactory = userFactory;
        this.subAccountDataAccess = subAccountDataAccess;
    }

    @Override
    public void execute(final SignupInputData signupInputData) {
        final String username = signupInputData.getUsername();
        final String password = signupInputData.getPassword();
        final String repeatPassword = signupInputData.getRepeatPassword();
        if (userDataAccessObject.existsByName(username)) {
            userPresenter.prepareFailView("User already exists.");
            return;
        }
        if (!password.equals(repeatPassword)) {
            userPresenter.prepareFailView("Passwords don't match.");
            return;
        }
        if (password.isEmpty()) {
            userPresenter.prepareFailView("New password cannot be empty");
            return;
        }
        if (username.isEmpty()) {
            userPresenter.prepareFailView("Username cannot be empty");
            return;
        }
        final User user = userFactory.create(username, password);
        userDataAccessObject.save(user);
        final String defaultName = "Main USD Portfolio";
        if (!subAccountDataAccess.exists(username, defaultName)) {
            final SubAccount defaultSub =
                new SubAccount(defaultName, new BigDecimal("1000000"), true);
            subAccountDataAccess.save(username, defaultSub);
        }
        final SignupOutputData outputData = new SignupOutputData(user.getName());
        userPresenter.prepareSuccessView(outputData);
    }

    @Override
    public void switchToLoginView() {
        userPresenter.switchToLoginView();
    }
}
