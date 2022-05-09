package wearblackallday.accountmanager.util;

import wearblackallday.accountmanager.AccountManager;
import wearblackallday.accountmanager.util.account.Credentials;
import wearblackallday.accountmanager.util.account.Elo;
import wearblackallday.javautils.data.Strings;
import wearblackallday.javautils.swing.components.LPanel;

import javax.swing.*;
import java.util.*;

public class Factory {

	private static final Map<Credentials, JPanel> DISPLAY_CACHE = new HashMap<>();

	public static JButton button(String title, Runnable action) {
		JButton button = new JButton(title);
		button.addActionListener(e -> {
			action.run();
			AccountManager.get().refresh();
		});
		return button;
	}

	public static JPanel loginDisplay(Credentials credentials) {
		return DISPLAY_CACHE.computeIfAbsent(credentials, Factory::buildLoginDisplay);
	}

	private static JPanel buildLoginDisplay(Credentials credentials) {
		return new LPanel()
			.defaultSize(200, 30)
			.addButton(credentials.username(), () -> Strings.clipboard(credentials.username()))
			.addButton(credentials.password(), () -> Strings.clipboard(credentials.password()))
			.addComponent(Factory.button("\uD83D\uDD04", () -> {
				Elo newElo = (Elo)JOptionPane.showInputDialog(AccountManager.get(),
					"new elo of\s" + credentials.username(),
					"change Elo",
					JOptionPane.QUESTION_MESSAGE,
					null,
					Elo.values(),
					null);
				if(newElo != null) {
					Storage.get().remove(credentials);
					Storage.get().add(credentials, newElo);
				}
			}))
			.addComponent(Factory.button("\uD83D\uDDD1", () -> {
				Storage.get().remove(credentials);
				DISPLAY_CACHE.remove(credentials);
			}));
	}

}
