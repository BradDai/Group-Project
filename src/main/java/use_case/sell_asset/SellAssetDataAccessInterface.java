package use_case.sell_asset;

import entity.User;

public interface SellAssetDataAccessInterface {
    User getUser(User user);
    void saveUser(User user);
}
