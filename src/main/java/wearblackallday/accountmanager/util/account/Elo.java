package wearblackallday.accountmanager.util.account;

public enum Elo {
	UNRANKED, IRON, BRONZE, SILVER, GOLD, PLATINUM, DIAMOND, MASTER, GRANDMASTER, CHALLENGER;

	@Override
	public String toString() {
		return this.name().toLowerCase();
	}
}
