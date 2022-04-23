package wearblackallday.accountmanager.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import wearblackallday.accountmanager.util.account.*;

import java.io.*;
import java.util.*;

public class Storage {
	private static final Gson GSON = new GsonBuilder()
		.setPrettyPrinting()
		.enableComplexMapKeySerialization()
		.registerTypeAdapter(Credentials.class, Credentials.ADAPTER)
		.create();

	private static final File FILE = new File("accounts.json");

	private static final Storage INSTANCE = load();

	private Region regionContext;
	private Elo eloContext;
	private final Map<Region, Map<Elo, Set<Credentials>>> accounts = new EnumMap<>(Region.class);

	static {
		Runtime.getRuntime().addShutdownHook(new Thread(Storage::save));
	}

	public void add(Credentials credentials) {
		this.accounts.putIfAbsent(this.regionContext, new EnumMap<>(Elo.class));
		this.accounts.get(this.regionContext).putIfAbsent(this.eloContext, new HashSet<>());
		this.accounts.get(this.regionContext).get(this.eloContext).add(credentials);
	}

	public void remove(Credentials credentials) {
		this.accounts.get(this.regionContext).get(this.eloContext).remove(credentials);
	}

	public Set<Credentials> getCredentials() {
		return this.accounts.getOrDefault(this.regionContext, Map.of()).getOrDefault(this.eloContext, Set.of());
	}

	public Region getRegionContext() {
		return this.regionContext;
	}

	public Elo getEloContext() {
		return this.eloContext;
	}

	public void setContext(Region region, Elo elo) {
		this.regionContext = region;
		this.eloContext = elo;
	}

	private static Storage load() {
		try {
			if(!FILE.createNewFile()) return GSON.fromJson(new FileReader(FILE), Storage.class);
		} catch(IOException ignored) {
		}
		return new Storage();
	}

	private static void save() {
		try(PrintStream out = new PrintStream(FILE)) {
			out.print(GSON.toJson(INSTANCE));
		} catch(FileNotFoundException ignored) {
		}
	}

	public static Storage get() {
		return INSTANCE;
	}
}
