package usecase.SubAccount.delete;

import entity.SubAccount;
import org.junit.jupiter.api.Test;
import usecase.SubAccount.SubAccountDataAccessInterface;
import java.math.BigDecimal;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class DeleteSubAccountInteractorTest {

    private static class InMemorySubAccountDAO implements SubAccountDataAccessInterface {
        Map<String, List<SubAccount>> data = new HashMap<>();

        @Override
        public boolean exists(String username, String subName) {
            return data.getOrDefault(username, List.of())
                    .stream().anyMatch(sa -> sa.getName().equalsIgnoreCase(subName));
        }

        @Override
        public void save(String username, SubAccount subAccount) {
            List<SubAccount> list = data.computeIfAbsent(username, u -> new ArrayList<>());
            list.remove(subAccount);
            list.add(subAccount);
        }

        @Override
        public void delete(String username, String subName) {
            List<SubAccount> list = data.get(username);
            if (list != null) {
                list.removeIf(sa -> sa.getName().equalsIgnoreCase(subName));
            }
        }

        @Override
        public List<SubAccount> getSubAccountsOf(String username) {
            return new ArrayList<>(data.getOrDefault(username, List.of()));
        }

        @Override
        public int countByUser(String username) {
            return data.getOrDefault(username, List.of()).size();
        }
    }

    private static class TestPresenter implements DeleteSubAccountOutputBoundary {
        DeleteSubAccountOutputData successData;
        String errorMessage;

        @Override
        public void prepareSuccessView(DeleteSubAccountOutputData outputData) {
            this.successData = outputData;
        }

        @Override
        public void prepareFailView(String errorMessage) {
            this.errorMessage = errorMessage;
        }
    }

    @Test
    void execute_validDelete_removesAccountAndPresentsSuccess() {
        InMemorySubAccountDAO dao = new InMemorySubAccountDAO();
        String user = "brad";
        dao.save(user, new SubAccount("Main USD Portfolio",
                new BigDecimal("1000000"), true)); // 不可删
        dao.save(user, new SubAccount("Temp",
                BigDecimal.ZERO, false));          // 可以删

        TestPresenter presenter = new TestPresenter();
        DeleteSubAccountInteractor interactor =
                new DeleteSubAccountInteractor(dao, presenter);

        interactor.execute(new DeleteSubAccountInputData(user, "Temp"));

        assertEquals(1, dao.countByUser(user));
        assertTrue(dao.exists(user, "Main USD Portfolio"));
        assertFalse(dao.exists(user, "Temp"));
        assertNotNull(presenter.successData);
        assertNull(presenter.errorMessage);
    }

    @Test
    void execute_blankName_presentsError() {
        InMemorySubAccountDAO dao = new InMemorySubAccountDAO();
        TestPresenter presenter = new TestPresenter();
        DeleteSubAccountInteractor interactor =
                new DeleteSubAccountInteractor(dao, presenter);

        interactor.execute(new DeleteSubAccountInputData("bob", "  "));

        assertEquals("Subaccount name cannot be empty.", presenter.errorMessage);
        assertNull(presenter.successData);
    }

    @Test
    void execute_notFound_presentsError() {
        InMemorySubAccountDAO dao = new InMemorySubAccountDAO();
        TestPresenter presenter = new TestPresenter();
        DeleteSubAccountInteractor interactor =
                new DeleteSubAccountInteractor(dao, presenter);

        interactor.execute(new DeleteSubAccountInputData("carol", "NonExisting"));

        assertEquals("Subaccount not found.", presenter.errorMessage);
        assertNull(presenter.successData);
    }

    @Test
    void execute_undeletable_presentsError() {
        InMemorySubAccountDAO dao = new InMemorySubAccountDAO();
        String user = "dave";
        dao.save(user, new SubAccount("Main USD Portfolio",
                new BigDecimal("1000000"), true));

        TestPresenter presenter = new TestPresenter();
        DeleteSubAccountInteractor interactor =
                new DeleteSubAccountInteractor(dao, presenter);

        interactor.execute(new DeleteSubAccountInputData(user, "Main USD Portfolio"));

        assertEquals("This subaccount cannot be deleted.", presenter.errorMessage);
        assertEquals(1, dao.countByUser(user));
    }

    @Test
    void execute_nonZeroBalance_presentsError() {
        InMemorySubAccountDAO dao = new InMemorySubAccountDAO();
        String user = "eve";
        dao.save(user, new SubAccount("Temp",
                new BigDecimal("10.00"), false));

        TestPresenter presenter = new TestPresenter();
        DeleteSubAccountInteractor interactor =
                new DeleteSubAccountInteractor(dao, presenter);

        interactor.execute(new DeleteSubAccountInputData(user, "Temp"));

        assertEquals("Can't delete a subaccount with non-zero balance.", presenter.errorMessage);
        assertEquals(1, dao.countByUser(user));
    }

    @Test
    void execute_nullName_presentsError() {
        InMemorySubAccountDAO dao = new InMemorySubAccountDAO();
        TestPresenter presenter = new TestPresenter();
        DeleteSubAccountInteractor interactor =
                new DeleteSubAccountInteractor(dao, presenter);
        DeleteSubAccountInputData input =
                new DeleteSubAccountInputData("alice", null);
        interactor.execute(input);
        assertEquals("Subaccount name cannot be empty.", presenter.errorMessage);
        assertNull(presenter.successData);
        assertEquals(0, dao.countByUser("alice"));
    }

    @Test
    void execute_inconsistentDao_targetNull_presentsNotFound() {
        SubAccountDataAccessInterface dao = new SubAccountDataAccessInterface() {
            @Override
            public boolean exists(String username, String subName) {
                return true;
            }
            @Override
            public void save(String username, SubAccount subAccount) {
            }
            @Override
            public void delete(String username, String subName) {}

            @Override
            public List<SubAccount> getSubAccountsOf(String username) {
                return List.of();
            }

            @Override
            public int countByUser(String username) {
                return 0;
            }
        };
        TestPresenter presenter = new TestPresenter();
        DeleteSubAccountInteractor interactor =
                new DeleteSubAccountInteractor(dao, presenter);
        interactor.execute(new DeleteSubAccountInputData("alice", "Ghost"));
        assertEquals("Subaccount not found.", presenter.errorMessage);
        assertNull(presenter.successData);
    }
}