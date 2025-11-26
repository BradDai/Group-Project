package usecase.logout;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import dataaccess.InMemoryUserDataAccessObject;
import entity.User;
import entity.UserFactory;

class LogoutInteractorTest {

    @Test
    void successTest() {
        final InMemoryUserDataAccessObject userRepository = new InMemoryUserDataAccessObject();

        // For the success test, we need to add Paul to the data access repository before we log in.
        final UserFactory factory = new UserFactory();
        final User user = factory.create("Paul", "password");
        userRepository.save(user);
        userRepository.setCurrentUsername("Paul");

        // This creates a successPresenter that tests whether the test case is as we expect.
        final LogoutOutputBoundary successPresenter = new LogoutOutputBoundary() {
            @Override
            public void prepareSuccessView(final LogoutOutputData user) {
                assertEquals("Paul", user.username());
                assertNull(userRepository.getCurrentUsername());
            }
};

        final LogoutInputBoundary interactor = new LogoutInteractor(userRepository, successPresenter);
        interactor.execute();
        assertNull(userRepository.getCurrentUsername());
    }

}
