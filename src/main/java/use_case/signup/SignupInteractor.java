package use_case.signup;

import entity.User;
import entity.UserFactory;
import use_case.SubAccount.SubAccountDataAccessInterface;
import entity.SubAccount;
import java.math.BigDecimal;

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
        if (userDataAccessObject.existsByName(signupInputData.getUsername())) {
            userPresenter.prepareFailView("User already exists.");
            return;
        }
        if (!signupInputData.getPassword().equals(signupInputData.getRepeatPassword())) {
            userPresenter.prepareFailView("Passwords don't match.");
            return;
        }
        if ("".equals(signupInputData.getPassword())) {
            userPresenter.prepareFailView("New password cannot be empty");
            return;
        }
        if ("".equals(signupInputData.getUsername())) {
            userPresenter.prepareFailView("Username cannot be empty");
            return;
        }

        User user = userFactory.create(
                signupInputData.getUsername(), signupInputData.getPassword());
        userDataAccessObject.save(user);

        String defaultName = "Main CAD Portfolio";
        if (!subAccountDataAccess.exists(user.getName(), defaultName)) {
            SubAccount defaultSub = new SubAccount(
                    defaultName,
                    new BigDecimal("1000000"),
                    true
            );
            subAccountDataAccess.save(user.getName(), defaultSub);
        }

        SignupOutputData outputData = new SignupOutputData(user.getName());
        userPresenter.prepareSuccessView(outputData);
    }

    @Override
    public void switchToLoginView() {
        userPresenter.switchToLoginView();
    }
}