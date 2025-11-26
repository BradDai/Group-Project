package usecase.SubAccount.delete;

import java.util.List;

import entity.SubAccount;

public record DeleteSubAccountOutputData(String username, List<SubAccount> subAccounts) {
}
