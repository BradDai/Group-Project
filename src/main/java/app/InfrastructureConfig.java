package app;

import dataaccess.FileSubAccountDataAccessJSON;
import dataaccess.FileTransactionDataAccess;
import dataaccess.FileUserDataAccessObject;
import dataaccess.TransactionDataAccessInterface;
import entity.UserFactory;

/**
 * Holds infrastructure objects (DAOs, factories, API keys).
 * Centralized here so higher-level classes depend on fewer types.
 */
public class InfrastructureConfig {

    private static final String TWELVE_DATA_API_KEY =
            "ebcea301f0ad46579daa6b6dea349164";

    private final UserFactory userFactory;
    private final FileUserDataAccessObject userDataAccessObject;
    private final FileSubAccountDataAccessJSON subAccountDataAccess;
    private final TransactionDataAccessInterface transactionDataAccessObject;

    public InfrastructureConfig() {
        this.userFactory = new UserFactory();
        this.userDataAccessObject =
                new FileUserDataAccessObject("users.csv", userFactory);
        this.subAccountDataAccess =
                new FileSubAccountDataAccessJSON("subaccounts.json");
        this.transactionDataAccessObject =
                new FileTransactionDataAccess("data/transactions");
    }

    public UserFactory getUserFactory() {
        return userFactory;
    }

    public FileUserDataAccessObject getUserDataAccessObject() {
        return userDataAccessObject;
    }

    public FileSubAccountDataAccessJSON getSubAccountDataAccess() {
        return subAccountDataAccess;
    }

    public TransactionDataAccessInterface getTransactionDataAccessObject() {
        return transactionDataAccessObject;
    }

    public String getTwelveDataApiKey() {
        return TWELVE_DATA_API_KEY;
    }
}
