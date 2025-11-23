package use_case.switch_transfer;

public interface SwitchTransferOutputBoundary {
    /**
     * Presents the transfer view with the user's data.
     * @param username The username.
     * @param portfolios The list of portfolio names available for the user.
     */
    void presentTransferView(String username, String[] portfolios);
}