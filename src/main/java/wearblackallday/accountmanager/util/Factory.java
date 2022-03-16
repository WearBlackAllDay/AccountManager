package wearblackallday.accountmanager.util;

import wearblackallday.accountmanager.AccountManager;

import javax.swing.*;

public class Factory {

	public static JButton button(String title, Runnable action) {
		JButton button = new JButton(title);
		button.addActionListener(AccountManager.get());
		button.addActionListener(e -> action.run());
		return button;
	}
}
