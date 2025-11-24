package use_case.login;

import data_access.InMemoryUserDataAccessObject;
import entity.SubAccount;
import entity.User;
import entity.UserFactory;
import org.junit.jupiter.api.Test;
import use_case.SubAccount.SubAccountDataAccessInterface;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LoginInteractorTest {

    private final SubAccountDataAccessInterface subAccountRepository = new StubSubAccountDataAccess();

    @Test
    void successTest() {
        LoginInputData inputData = new LoginInputData("Paul", "password");
        InMemoryUserDataAccessObject userRepository = new InMemoryUserDataAccessObject();

        // For the success test, we need to add Paul to the data access repository before we log in.
        UserFactory factory = new UserFactory();
        User user = factory.create("Paul", "password");
        userRepository.save(user);

        // This creates a successPresenter that tests whether the test case is as we expect.
        LoginOutputBoundary successPresenter = new LoginOutputBoundary() {
            @Override
            public void prepareSuccessView(LoginOutputData user) {
                assertEquals("Paul", user.getUsername());
            }

            @Override
            public void prepareFailView(String error) {
                fail("Use case failure is unexpected.");
            }

            @Override
            public void switchToSignupView() {

            }
        };

        LoginInputBoundary interactor = new LoginInteractor(userRepository, successPresenter, subAccountRepository);
        interactor.execute(inputData);
    }


    @Test
    void failurePasswordMismatchTest() {
        LoginInputData inputData = new LoginInputData("Paul", "wrong");
        InMemoryUserDataAccessObject userRepository = new InMemoryUserDataAccessObject();

        // For this failure test, we need to add Paul to the data access repository before we log in, and
        // the passwords should not match.
        UserFactory factory = new UserFactory();
        User user = factory.create("Paul", "password");
        userRepository.save(user);

        // This creates a presenter that tests whether the test case is as we expect.
        LoginOutputBoundary failurePresenter = new LoginOutputBoundary() {
            @Override
            public void prepareSuccessView(LoginOutputData user) {
                // this should never be reached since the test case should fail
                fail("Use case success is unexpected.");
            }

            @Override
            public void prepareFailView(String error) {
                assertEquals("Incorrect password for \"Paul\".", error);
            }

            @Override
            public void switchToSignupView() {

            }
        };

        LoginInputBoundary interactor = new LoginInteractor(userRepository, failurePresenter, subAccountRepository);
        interactor.execute(inputData);
    }

    @Test
    void failureUserDoesNotExistTest() {
        LoginInputData inputData = new LoginInputData("Paul", "password");
        InMemoryUserDataAccessObject userRepository = new InMemoryUserDataAccessObject();

        // Add Paul to the repo so that when we check later they already exist

        // This creates a presenter that tests whether the test case is as we expect.
        LoginOutputBoundary failurePresenter = new LoginOutputBoundary() {
            @Override
            public void prepareSuccessView(LoginOutputData user) {
                // this should never be reached since the test case should fail
                fail("Use case success is unexpected.");
            }

            @Override
            public void prepareFailView(String error) {
                assertEquals("Paul: Account does not exist.", error);
            }

            @Override
            public void switchToSignupView() {

            }
        };

        LoginInputBoundary interactor = new LoginInteractor(userRepository, failurePresenter, subAccountRepository);
        interactor.execute(inputData);
    }

    private static class StubSubAccountDataAccess implements SubAccountDataAccessInterface {
        @Override
        public boolean exists(String username, String subName) { return false; }

        @Override
        public void save(String username, SubAccount subAccount) {}

        @Override
        public List<SubAccount> getSubAccountsOf(String username) { return new ArrayList<>(); }

        @Override
        public int countByUser(String username) { return 0; }

        @Override
        public void delete(String username, String subName) {}
    }
}