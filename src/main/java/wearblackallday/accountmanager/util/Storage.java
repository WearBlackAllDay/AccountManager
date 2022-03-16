package wearblackallday.accountmanager.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class Storage {
	private static final Gson GSON = new GsonBuilder()
		.setPrettyPrinting()
		.registerTypeAdapter(Account.class, new Account.Adapter())
		.create();

	private static final File FILE = new File("accounts.json");

	private static final Storage INSTANCE = load();

	public final Set<Account> accounts = new HashSet<>();

	static {
		Runtime.getRuntime().addShutdownHook(new Thread(Storage::save));
	}

	private static Storage load() {
		try {
			if(!FILE.createNewFile()) return GSON.fromJson(new FileReader(FILE), Storage.class);
		} catch(IOException ignored) {
		}
		return new Storage();
	}

	private static void save() {
		try {
			new PrintStream(FILE).print(GSON.toJson(INSTANCE));
		} catch(FileNotFoundException ignored) {
		}
	}

	public static Storage get() {
		return INSTANCE;
	}
}
