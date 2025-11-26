package usecase.signup;

import dataaccess.InMemoryUserDataAccessObject;
import dataaccess.InMemorySubAccountDataAccess;
import entity.UserFactory;

import org.junit.jupiter.api.Test;
import usecase.SubAccount.SubAccountDataAccessInterface;

import static org.junit.jupiter.api.Assertions.*;

class SignupInteractorTest {

    @Test
    void successTest() {
        final SignupInputData inputData =
                new SignupInputData("Paul", "password", "password");

        final SignupUserDataAccessInterface userRepository =
                new InMemoryUserDataAccessObject();
        final SubAccountDataAccessInterface subAccountRepository =
                new InMemorySubAccountDataAccess();
        final SignupOutputBoundary successPresenter = new SignupOutputBoundary() {
            @Override
            public void prepareSuccessView(final SignupOutputData user) {
                assertEquals("Paul", user.username());
                assertTrue(userRepository.existsByName("Paul"));
                assertTrue(subAccountRepository.exists("Paul", "Main USD Portfolio"));
            }
            @Override
            public void prepareFailView(final String error) {
                fail("Use case failure is unexpected.");
            }
            @Override
            public void switchToLoginView() {
            }
        };
        final SignupInputBoundary interactor = new SignupInteractor(
                userRepository,
                successPresenter,
                new UserFactory(),
                subAccountRepository
        );

        interactor.execute(inputData);
    }}
