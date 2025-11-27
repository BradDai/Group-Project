package app;

import dataaccess.FileSubAccountDataAccessJSON;
import dataaccess.FileUserDataAccessObject;
import entity.UserFactory;
import interfaceadapter.ViewManagerModel;

/**
 * Aggregates smaller auth-related configurators.
 */
public class AuthUseCaseConfigurator {

    private final SignupLoginUseCaseConfigurator signupLoginConfigurator;
    private final LogoutChangePasswordUseCaseConfigurator logoutChangePasswordConfigurator;

    public AuthUseCaseConfigurator(
            final ViewManagerModel viewManagerModel,
            final UserFactory userFactory,
            final FileUserDataAccessObject userDataAccessObject,
            final FileSubAccountDataAccessJSON subAccountDataAccess,
            final ViewConfigurator views
    ) {
        this.signupLoginConfigurator =
                new SignupLoginUseCaseConfigurator(
                        viewManagerModel,
                        userFactory,
                        userDataAccessObject,
                        subAccountDataAccess,
                        views
                );

        this.logoutChangePasswordConfigurator =
                new LogoutChangePasswordUseCaseConfigurator(
                        viewManagerModel,
                        userFactory,
                        userDataAccessObject,
                        views
                );
    }
    /**
     * Wires use cases.
     */

    public void wireUseCases() {
        signupLoginConfigurator.wireUseCases();
        logoutChangePasswordConfigurator.wireUseCases();
    }
}
