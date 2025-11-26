package usecase.login;

import dataaccess.InMemoryUserDataAccessObject;
import entity.SubAccount;
import entity.User;
import entity.UserFactory;
import org.junit.jupiter.api.Test;
import usecase.SubAccount.SubAccountDataAccessInterface;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LoginInteractorTest {

    private final SubAccountDataAccessInterface subAccountRepository = new StubSubAccountDataAccess();

    @Test
    void successTest() {
        final LoginInputData inputData = new LoginInputData("Paul", "password");
        final InMemoryUserDataAccessObject userRepository = new InMemoryUserDataAccessObject();

        // For the success test, we need to add Paul to the data access repository before we log in.
        final UserFactory factory = new UserFactory();
        final User user = factory.create("Paul", "password");
        userRepository.save(user);

        // This creates a successPresenter that tests whether the test case is as we expect.
        final LoginOutputBoundary successPresenter = new LoginOutputBoundary() {
            @Override
            public void prepareSuccessView(final LoginOutputData user) {
                assertEquals("Paul", user.username());
            }

            @Override
            public void prepareFailView(final String error) {
                fail("Use case failure is unexpected.");
            }

            @Override
            public void switchToSignupView() {

            }
        };

        final LoginInputBoundary interactor = new LoginInteractor(userRepository, successPresenter, subAccountRepository);
        interactor.execute(inputData);
    }


    @Test
    void failurePasswordMismatchTest() {
        final LoginInputData inputData = new LoginInputData("Paul", "wrong");
        final InMemoryUserDataAccessObject userRepository = new InMemoryUserDataAccessObject();

        // For this failure test, we need to add Paul to the data access repository before we log in, and
        // the passwords should not match.
        final UserFactory factory = new UserFactory();
        final User user = factory.create("Paul", "password");
        userRepository.save(user);

        // This creates a presenter that tests whether the test case is as we expect.
        final LoginOutputBoundary failurePresenter = new LoginOutputBoundary() {
            @Override
            public void prepareSuccessView(final LoginOutputData user) {
                // this should never be reached since the test case should fail
                fail("Use case success is unexpected.");
            }

            @Override
            public void prepareFailView(final String error) {
                assertEquals("Incorrect password for \"Paul\".", error);
            }

            @Override
            public void switchToSignupView() {

            }
        };

        final LoginInputBoundary interactor = new LoginInteractor(userRepository, failurePresenter, subAccountRepository);
        interactor.execute(inputData);
    }

    @Test
    void failureUserDoesNotExistTest() {
        final LoginInputData inputData = new LoginInputData("Paul", "password");
        final InMemoryUserDataAccessObject userRepository = new InMemoryUserDataAccessObject();

        // Add Paul to the repo so that when we check later they already exist

        // This creates a presenter that tests whether the test case is as we expect.
        final LoginOutputBoundary failurePresenter = new LoginOutputBoundary() {
            @Override
            public void prepareSuccessView(final LoginOutputData user) {
                // this should never be reached since the test case should fail
                fail("Use case success is unexpected.");
            }

            @Override
            public void prepareFailView(final String error) {
                assertEquals("Paul: Account does not exist.", error);
            }

            @Override
            public void switchToSignupView() {

            }
        };

        final LoginInputBoundary interactor = new LoginInteractor(userRepository, failurePresenter, subAccountRepository);
        interactor.execute(inputData);
    }

    private static class StubSubAccountDataAccess implements SubAccountDataAccessInterface {
        @Override
        public boolean exists(final String username, final String subName) { return false; }

        @Override
        public void save(final String username, final SubAccount subAccount) {}

        @Override
        public List<SubAccount> getSubAccountsOf(final String username) { return new ArrayList<>(); }

        @Override
        public int countByUser(final String username) { return 0; }

        @Override
        public void delete(final String username, final String subName) {}
    }
}
