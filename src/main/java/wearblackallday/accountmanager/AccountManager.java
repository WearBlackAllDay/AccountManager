package wearblackallday.accountmanager;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import wearblackallday.data.Strings;
import wearblackallday.swing.components.CustomPanel;
import wearblackallday.swing.components.SelectionBox;
import wearblackallday.swing.components.builder.FrameBuilder;

import javax.swing.*;
import java.awt.Dimension;
import java.io.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class AccountManager {

	static {
		FlatDarculaLaf.install();
	}

	public static AccountStorage STORAGE = new AccountStorage();

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final File FILE = new File("accounts.json");
	private static final Box OUTPUT = new Box(BoxLayout.Y_AXIS);
	private static final SelectionBox<Account.Elo> ELO_SELECTION
		= new SelectionBox<>(elo -> elo.toString().toLowerCase(), Account.Elo.values());
	private static final SelectionBox<Account.Region> REGION_SELECTION
		= new SelectionBox<>(Account.Region.values());

	public static void main(String[] args) throws IOException {
		if(!FILE.createNewFile()) {
			STORAGE = GSON.fromJson(new FileReader(FILE), AccountStorage.class);
		}

		JTextArea input = new JTextArea();

		JScrollPane display = new JScrollPane();
		display.setPreferredSize(new Dimension(660, 800));
		display.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		display.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		display.setViewportView(OUTPUT);

		new FrameBuilder().visible().sizeLocked().centered().title("AccountManager")
			.contentPane(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, display,
				new CustomPanel(200, 40)
					.boxLayout(BoxLayout.Y_AXIS)
					.addComponent(input)
					.addComponent(() -> ELO_SELECTION, (panel, selectionBox) ->
						selectionBox.addActionListener(e -> update()))
					.addComponent(() -> REGION_SELECTION, (panel, selectionBox) ->
						selectionBox.addActionListener(e -> update()))
					.addButton("add Accounts", (panel, button, event) -> {
						Arrays.stream(input.getText().contains(",") ?
							input.getText().split(",") : Strings.splitLines(input.getText()))
							.map(account -> account.trim().split(":"))
							.forEach(credential -> STORAGE.accounts.add(new Account(credential[0], credential[1],
								ELO_SELECTION.getSelected(), REGION_SELECTION.getSelected())));
						update();
					}))).create();
		update();
	}

	public static void update() {
		OUTPUT.removeAll();
		STORAGE.accounts.stream()
			.filter(account -> account.elo == ELO_SELECTION.getSelected()
				&& account.region == REGION_SELECTION.getSelected())
			.map(Account::display)
			.forEach(OUTPUT::add);
		OUTPUT.repaint();
		OUTPUT.revalidate();
		try {
			new PrintStream(FILE).print(GSON.toJson(STORAGE));
		} catch(FileNotFoundException e) {
			System.out.println("this never happens");
		}
	}

	public static class AccountStorage {
		public Set<Account> accounts = new HashSet<>();
	}
}