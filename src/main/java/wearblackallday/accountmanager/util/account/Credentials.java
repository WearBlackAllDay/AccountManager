package wearblackallday.accountmanager.util.account;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public record Credentials(String username, String password) {
	public static final String FORMAT = "username:password...";

	public static final TypeAdapter<Credentials> ADAPTER = new TypeAdapter<>() {
		@Override
		public void write(JsonWriter out, Credentials credentials) throws IOException {
			out.beginObject();
			out.name("username");
			out.value(credentials.username());
			out.name("password");
			out.value(credentials.password());
			out.endObject();
		}

		@Override
		public Credentials read(JsonReader in) throws IOException {
			in.beginObject();
			in.skipValue();
			String name = in.nextString();
			in.skipValue();
			String password = in.nextString();
			in.endObject();
			return new Credentials(name, password);
		}
	};

	public Credentials(String compact) {
		this(compact.substring(0, compact.indexOf(':')),
			compact.substring(compact.indexOf(':') + 1));
	}

	@Override
	public String toString() {
		return this.username +  ":" + this.password;
	}
}
