package usecase.SubAccount.create;

import entity.SubAccount;
import org.junit.jupiter.api.Test;
import usecase.SubAccount.SubAccountDataAccessInterface;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class CreateSubAccountInteractorTest {

    private static class InMemorySubAccountDAO implements SubAccountDataAccessInterface {
        Map<String, List<SubAccount>> data = new HashMap<>();

        @Override
        public boolean exists(String username, String subName) {
            return data.getOrDefault(username, List.of())
                    .stream()
                    .anyMatch(sa -> sa.getName().equalsIgnoreCase(subName));
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

    private static class TestPresenter implements CreateSubAccountOutputBoundary {
        CreateSubAccountOutputData successData;
        String errorMessage;

        @Override
        public void prepareSuccessView(CreateSubAccountOutputData outputData) {
            this.successData = outputData;
        }

        @Override
        public void prepareFailView(String errorMessage) {
            this.errorMessage = errorMessage;
        }
    }

    @Test
    void execute_validName_createsSubAccountAndPresentsSuccess() {
        InMemorySubAccountDAO dao = new InMemorySubAccountDAO();
        TestPresenter presenter = new TestPresenter();
        CreateSubAccountInteractor interactor =
                new CreateSubAccountInteractor(dao, presenter);

        String username = "alice";
        CreateSubAccountInputData input =
                new CreateSubAccountInputData(username, "Growth Portfolio");
        interactor.execute(input);
        List<SubAccount> accounts = dao.getSubAccountsOf(username);
        assertEquals(1, accounts.size());
        SubAccount sa = accounts.get(0);
        assertEquals("Growth Portfolio", sa.getName());
        assertEquals(BigDecimal.ZERO, sa.getBalanceUSD());
        assertFalse(sa.isUndeletable());
        assertNotNull(presenter.successData);
        assertNull(presenter.errorMessage);
        assertEquals(username, presenter.successData.username());
        assertEquals(1, presenter.successData.allSubAccounts().size());
    }

    @Test
    void execute_blankName_presentsErrorAndDoesNotSave() {
        InMemorySubAccountDAO dao = new InMemorySubAccountDAO();
        TestPresenter presenter = new TestPresenter();
        CreateSubAccountInteractor interactor =
                new CreateSubAccountInteractor(dao, presenter);
        interactor.execute(new CreateSubAccountInputData("bob", "  "));
        assertEquals(0, dao.countByUser("bob"));
        assertNull(presenter.successData);
        assertEquals("Subaccount name cannot be empty.", presenter.errorMessage);
    }

    @Test
    void execute_maxLimitReached_presentsError() {
        InMemorySubAccountDAO dao = new InMemorySubAccountDAO();
        String user = "carol";
        for (int i = 0; i < 5; i++) {
            dao.save(user, new SubAccount("P" + i, BigDecimal.ZERO, false));
        }
        TestPresenter presenter = new TestPresenter();
        CreateSubAccountInteractor interactor =
                new CreateSubAccountInteractor(dao, presenter);
        interactor.execute(new CreateSubAccountInputData(user, "NewOne"));
        assertEquals(5, dao.countByUser(user));
        assertEquals("Maximum subaccount limit reached (5).", presenter.errorMessage);
        assertNull(presenter.successData);
    }

    @Test
    void execute_duplicateName_presentsError() {
        InMemorySubAccountDAO dao = new InMemorySubAccountDAO();
        String user = "dave";
        dao.save(user, new SubAccount("Main USD Portfolio", BigDecimal.ZERO, true));
        TestPresenter presenter = new TestPresenter();
        CreateSubAccountInteractor interactor =
                new CreateSubAccountInteractor(dao, presenter);
        interactor.execute(new CreateSubAccountInputData(user, "Main USD Portfolio"));
        assertEquals(1, dao.countByUser(user));
        assertEquals("Subaccount with this name already exists.", presenter.errorMessage);
        assertNull(presenter.successData);
    }

    @Test
    void execute_nullName_presentsErrorAndDoesNotSave() {
        InMemorySubAccountDAO dao = new InMemorySubAccountDAO();
        TestPresenter presenter = new TestPresenter();
        CreateSubAccountInteractor interactor =
                new CreateSubAccountInteractor(dao, presenter);
        CreateSubAccountInputData input =
                new CreateSubAccountInputData("bob", null);
        interactor.execute(input);
        assertEquals(0, dao.countByUser("bob"));
        assertNull(presenter.successData);
        assertEquals("Subaccount name cannot be empty.", presenter.errorMessage);
    }
}