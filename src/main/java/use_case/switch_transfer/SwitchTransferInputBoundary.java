package use_case.switch_transfer;

public interface SwitchTransferInputBoundary {
    /**
     * Switches to the transfer view and loads the user's portfolio data.
     * @param username The username of the currently logged-in user.
     */
    void switchToTransferView(String username);
}