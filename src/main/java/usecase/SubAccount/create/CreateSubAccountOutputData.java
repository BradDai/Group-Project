package usecase.SubAccount.create;

import java.util.List;

import entity.SubAccount;

/**
 * Output data that Presenter will use to update ViewModel.
 */
public record CreateSubAccountOutputData(String username, List<SubAccount> allSubAccounts) {

}
