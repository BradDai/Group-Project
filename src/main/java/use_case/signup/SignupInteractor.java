package use_case.signup;

import entity.User;
import entity.UserFactory;
import entity.SubAccount;
import use_case.SubAccount.SubAccountDataAccessInterface;

import java.math.BigDecimal;

/**
 * The Signup Interactor.
 */
public class SignupInteractor implements SignupInputBoundary {
    private final SignupUserDataAccessInterface userDataAccessObject;
    private final SignupOutputBoundary userPresenter;
    private final UserFactory userFactory;
    private final SubAccountDataAccessInterface subAccountDataAccess;

    public SignupInteractor(SignupUserDataAccessInterface signupDataAccessInterface,
                            SignupOutputBoundary signupOutputBoundary,
                            UserFactory userFactory,
                            SubAccountDataAccessInterface subAccountDataAccess) {
        this.userDataAccessObject = signupDataAccessInterface;
        this.userPresenter = signupOutputBoundary;
        this.userFactory = userFactory;
        this.subAccountDataAccess = subAccountDataAccess;
    }
    @Override
    public void execute(SignupInputData signupInputData) {
        String username = signupInputData.getUsername();
        String password = signupInputData.getPassword();
        String repeatPassword = signupInputData.getRepeatPassword();
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
        User user = userFactory.create(username, password);
        userDataAccessObject.save(user);
        String defaultName = "Main USD Portfolio";
        if (!subAccountDataAccess.exists(username, defaultName)) {
            SubAccount defaultSub =
                    new SubAccount(defaultName, new BigDecimal("1000000"), true);
            subAccountDataAccess.save(username, defaultSub);
        }
        SignupOutputData outputData = new SignupOutputData(user.getName());
        userPresenter.prepareSuccessView(outputData);
    }
    @Override
    public void switchToLoginView() {
        userPresenter.switchToLoginView();
    }
}