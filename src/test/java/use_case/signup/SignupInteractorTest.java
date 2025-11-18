package use_case.signup;

import data_access.InMemoryUserDataAccessObject;
import data_access.InMemorySubAccountDataAccess;
import entity.UserFactory;
import entity.User;
import org.junit.jupiter.api.Test;
import use_case.SubAccount.SubAccountDataAccessInterface;

import static org.junit.jupiter.api.Assertions.*;

class SignupInteractorTest {

    @Test
    void successTest() {
        SignupInputData inputData =
                new SignupInputData("Paul", "password", "password");

        SignupUserDataAccessInterface userRepository =
                new InMemoryUserDataAccessObject();
        SubAccountDataAccessInterface subAccountRepository =
                new InMemorySubAccountDataAccess();
        SignupOutputBoundary successPresenter = new SignupOutputBoundary() {
            @Override
            public void prepareSuccessView(SignupOutputData user) {
                assertEquals("Paul", user.getUsername());
                assertTrue(userRepository.existsByName("Paul"));
                assertTrue(subAccountRepository.exists("Paul", "Main USD Portfolio"));
            }
            @Override
            public void prepareFailView(String error) {
                fail("Use case failure is unexpected.");
            }
            @Override
            public void switchToLoginView() {
            }
        };
        SignupInputBoundary interactor = new SignupInteractor(
                userRepository,
                successPresenter,
                new UserFactory(),
                subAccountRepository
        );

        interactor.execute(inputData);
    }}