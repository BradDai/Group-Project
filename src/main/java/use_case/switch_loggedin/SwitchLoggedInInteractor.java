package use_case.switch_loggedin;

import use_case.SubAccount.SubAccountDataAccessInterface;

public class SwitchLoggedInInteractor implements SwitchLoggedInInputBoundary {

    private final SwitchLoggedInOutputBoundary switchLoggedInPresenter;
    private final SubAccountDataAccessInterface subAccountDataAccess;

    public SwitchLoggedInInteractor(final SwitchLoggedInOutputBoundary switchLoggedInOutputBoundary,
                                    final SubAccountDataAccessInterface subAccountDataAccess) {
        this.switchLoggedInPresenter = switchLoggedInOutputBoundary;
        this.subAccountDataAccess = subAccountDataAccess;
    }

    public void switchToLoggedInView() {
        switchLoggedInPresenter.switchToLoggedInView();
    }
}