package wearblackallday.accountmanager;

import com.formdev.flatlaf.intellijthemes.FlatOneDarkIJTheme;
import wearblackallday.accountmanager.util.*;
import wearblackallday.javautils.swing.SwingUtils;
import wearblackallday.javautils.swing.components.SelectionBox;

import javax.swing.*;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import static wearblackallday.accountmanager.util.Account.Elo;
import static wearblackallday.accountmanager.util.Account.Region;

public class AccountManager extends JFrame implements ActionListener {

	private static final AccountManager INSTANCE = new AccountManager();

	private final Box output = new Box(BoxLayout.Y_AXIS);
	private final SelectionBox<Elo> eloSelection = new SelectionBox<>(elo -> elo.toString().toLowerCase(), Elo.values());
	private final SelectionBox<Region> regionSelection = new SelectionBox<>(Region.values());

	public static void main(String[] args) {
		FlatOneDarkIJTheme.setup();
		SwingUtilities.updateComponentTreeUI(INSTANCE);
		get().setVisible(true);
	}

	private AccountManager() {
		super("AccountManager");

		this.eloSelection.addActionListener(this);
		this.regionSelection.addActionListener(this);
		this.setContentPane(this.buildContentPane());
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.pack();
		this.setLocationRelativeTo(null);
	}

	private Container buildContentPane() {
		JTextArea input = new JTextArea();

		JScrollPane display = new JScrollPane(this.output);
		display.setPreferredSize(new Dimension(700, 800));
		display.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		display.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

		JButton addButton = Factory.button("add Accounts", () -> {
			for(String acc : input.getText().trim().split("[,\r\n\s\t]+")) {
				Storage.get().accounts.add(new Account(
					acc.substring(0, acc.indexOf(':')),
					acc.substring(acc.indexOf(':') + 1),
					get().eloSelection.getSelected(),
					get().regionSelection.getSelected()
				));
			}
			input.setText("");
		});

		return new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, display,
			SwingUtils.addSet(new Box(BoxLayout.Y_AXIS), input, this.eloSelection, this.regionSelection, addButton));
	}

	public static AccountManager get() {
		return INSTANCE;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		this.output.removeAll();
		Storage.get().accounts.stream()
			.filter(account -> account.elo() == this.eloSelection.getSelected()
				&& account.region() == this.regionSelection.getSelected())
			.map(Account::display)
			.forEach(this.output::add);
		this.output.repaint();
		this.output.revalidate();
	}
}