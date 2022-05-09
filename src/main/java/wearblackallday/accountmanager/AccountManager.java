package wearblackallday.accountmanager;

import com.formdev.flatlaf.intellijthemes.FlatOneDarkIJTheme;
import wearblackallday.accountmanager.util.*;
import wearblackallday.accountmanager.util.account.*;
import wearblackallday.javautils.swing.SwingUtils;
import wearblackallday.javautils.swing.components.SelectionBox;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.*;

public class AccountManager extends JFrame {

	static {
		if(System.getProperty("os.name").startsWith("Mac OS"))
			System.setProperty("apple.awt.application.appearance", "system");
		FlatOneDarkIJTheme.setup();
	}

	private static final AccountManager INSTANCE = new AccountManager();

	private final JPanel output = new JPanel(new GridLayout(0, 1));
	private final SelectionBox<Elo> eloSelection = new SelectionBox<>(Elo.values());
	private final SelectionBox<Region> regionSelection = new SelectionBox<>(Region.values());

	public static void main(String[] args) {
		get().setVisible(true);
	}

	private AccountManager() {
		super("AccountManager");

		this.eloSelection.selectIfContains(Storage.get().getEloContext());
		this.regionSelection.selectIfContains(Storage.get().getRegionContext());

		this.eloSelection.addActionListener(e -> this.refresh());
		this.regionSelection.addActionListener(e -> this.refresh());

		JButton addButton = Factory.button("add Accounts", () -> {
			String newAccounts = JOptionPane.showInputDialog(this, Credentials.FORMAT, "add Accounts", JOptionPane.PLAIN_MESSAGE);
			if(newAccounts != null) {
				for(String acc : newAccounts.trim().split("[,\\s]+")) {
					Storage.get().add(new Credentials(acc));
				}
			}
		});

		this.setJMenuBar(SwingUtils.addSet(new JMenuBar(), this.eloSelection, this.regionSelection, addButton));
		this.setContentPane(new JScrollPane(this.output));
		try {
			this.setIconImage(ImageIO.read(AccountManager.class.getResource("/icon.png")));
		} catch(IOException ignored) {
		}
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		this.setLocationRelativeTo(null);

		this.refresh();
	}

	public void refresh() {
		Storage.get().setContext(this.eloSelection.getSelected(), this.regionSelection.getSelected());
		this.output.removeAll();
		for(Credentials credentials : Storage.get().getCredentials()) {
			this.output.add(Factory.loginDisplay(credentials));
		}
		this.getContentPane().setPreferredSize(new Dimension(500, Math.min(this.output.getComponentCount(), 10) * 40));
		this.pack();
		this.output.revalidate();
		this.output.repaint();
	}

	@Override
	public void setIconImage(Image image) {
		super.setIconImage(image);
		if(Taskbar.isTaskbarSupported() && Taskbar.getTaskbar().isSupported(Taskbar.Feature.ICON_IMAGE))
			Taskbar.getTaskbar().setIconImage(image);
	}

	public static AccountManager get() {
		return INSTANCE;
	}
}