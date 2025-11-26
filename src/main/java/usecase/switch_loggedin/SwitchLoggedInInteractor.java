package usecase.switch_loggedin;

import usecase.SubAccount.SubAccountDataAccessInterface;

public class SwitchLoggedInInteractor implements SwitchLoggedInInputBoundary {

    private final SwitchLoggedInOutputBoundary switchLoggedInPresenter;

    public SwitchLoggedInInteractor(final SwitchLoggedInOutputBoundary switchLoggedInOutputBoundary,
                                    final SubAccountDataAccessInterface subAccountDataAccess) {
        this.switchLoggedInPresenter = switchLoggedInOutputBoundary;
    }

    public void switchToLoggedInView() {
        switchLoggedInPresenter.switchToLoggedInView();
    }
}
