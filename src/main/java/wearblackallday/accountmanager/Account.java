package wearblackallday.accountmanager;

import wearblackallday.data.Strings;
import wearblackallday.swing.components.CustomPanel;

import javax.swing.*;
import java.util.Objects;

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

	public JPanel display() {
		return new CustomPanel(300, 30)
			.addButton(this.name, (panel, button, event) -> Strings.clipboard(this.name))
			.addButton(this.password, (panel, button, event) -> Strings.clipboard(this.password))
			.addButton("X", 30, 30, (panel, button, event) -> {
				AccountManager.STORAGE.accounts.remove(this);
				AccountManager.update();
			});
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Account)) return false;
		return ((Account)obj).name.equals(this.name) &&
			((Account)obj).password.equals(this.password) &&
			((Account)obj).elo == this.elo &&
			((Account)obj).region == this.region;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.name, this.password, this.elo, this.region);
	}

	public enum Elo {
		UNRANKED, IRON, BRONZE, SILVER, GOLD, PLATINUM, DIAMOND, MASTER, GRANDMASTER, CHALLENGER
	}

	public enum Region {
		EUW, EUNE, NA, OCE, RU, JP, TUR, KR
	}
}
