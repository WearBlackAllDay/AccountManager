package wearblackallday.accountmanager;

import wearblackallday.data.Strings;
import wearblackallday.swing.components.CustomPanel;

public class Account {
    public String name;
    public String password;
    public Elo elo;
    public Region region;

    public Account(String name, String password, Elo elo, Region region) {
        this.name = name;
        this.password = password;
        this.elo = elo;
        this.region = region;
    }

    public CustomPanel display() {
        return new CustomPanel(300, 30)
                .addButton(this.name, (customPanel, button, event) -> Strings.clipboard(this.name))
                .addButton(this.password, (customPanel, button, event) -> Strings.clipboard(this.password))
                .addButton("X", 30, 30, (customPanel, button, event) -> {
                    AccountManager.STORAGE.accounts.remove(this);
                    AccountManager.OUTPUT.update();
                });
    }

    public enum Elo {
        IRON, BRONZE, SILVER, GOLD, PLATINUM, DIAMOND, MASTER, GRANDMASTER, CHALLENGER
    }

    public enum Region {
        EUW, EUNE, NA, OCE, RU, JP, TUR, KR
    }
}
