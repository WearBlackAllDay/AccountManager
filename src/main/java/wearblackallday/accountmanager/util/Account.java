package wearblackallday.accountmanager.util;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import wearblackallday.javautils.data.Strings;
import wearblackallday.javautils.swing.components.LPanel;

import javax.swing.*;
import java.io.IOException;
import java.util.Arrays;

public record Account(String name, String password, Elo elo, Region region) {
	public JPanel display() {
		return new LPanel()
			.defaultSize(300, 30)
			.addButton(this.name, () -> Strings.clipboard(this.name))
			.addButton(this.password, () -> Strings.clipboard(this.password))
			.addComponent(Factory.button("^", () -> {
				Storage.get().accounts.remove(this);
				Storage.get().accounts.add(this.rankUp());
			}))
			.addComponent(Factory.button("x", () -> Storage.get().accounts.remove(this)));
	}

	private Account rankUp() {
		Elo nextElo = Elo.values()[Arrays.asList(Elo.values()).indexOf(this.elo) + 1];
		return new Account(this.name, this.password, nextElo, this.region);
	}

	public enum Elo {
		UNRANKED, IRON, BRONZE, SILVER, GOLD, PLATINUM, DIAMOND, MASTER, GRANDMASTER, CHALLENGER
	}

	public enum Region {
		EUW, EUNE, NA, OCE, RU, JP, TUR, KR
	}

	public static class Adapter extends TypeAdapter<Account> {

		@Override
		public void write(JsonWriter out, Account account) throws IOException {
			out.beginObject();
			out.name("name");
			out.value(account.name());
			out.name("password");
			out.value(account.password());
			out.name("elo");
			out.value(account.elo().name());
			out.name("region");
			out.value(account.region().name());
			out.endObject();
		}

		@Override
		public Account read(JsonReader in) throws IOException {
			in.beginObject();
			in.skipValue();
			String name = in.nextString();
			in.skipValue();
			String password = in.nextString();
			in.skipValue();
			Elo elo = Elo.valueOf(in.nextString());
			in.skipValue();
			Region region = Region.valueOf(in.nextString());
			in.endObject();
			return new Account(name, password, elo, region);
		}
	}
}