package wearblackallday.accountmanager;

import com.formdev.flatlaf.intellijthemes.FlatOneDarkIJTheme;
import wearblackallday.accountmanager.util.*;
import wearblackallday.accountmanager.util.account.*;
import wearblackallday.javautils.swing.Events;
import wearblackallday.javautils.swing.SwingUtils;
import wearblackallday.javautils.swing.components.SelectionBox;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class AccountManager extends JFrame {

	private static final AccountManager INSTANCE = new AccountManager();

	public final JTextArea input = new JTextArea();
	private final Box output = new Box(BoxLayout.Y_AXIS);
	private final SelectionBox<Elo> eloSelection = new SelectionBox<>(Elo.values());
	private final SelectionBox<Region> regionSelection = new SelectionBox<>(Region.values());

	public static void main(String[] args) throws IOException {
		FlatOneDarkIJTheme.setup();
		SwingUtilities.updateComponentTreeUI(INSTANCE);
		if(Taskbar.isTaskbarSupported())
			Taskbar.getTaskbar().setIconImage(ImageIO.read(AccountManager.class.getResource("/icon.png")));
		get().setVisible(true);
	}

	private AccountManager() {
		super("AccountManager");

		this.eloSelection.addActionListener(e -> this.refresh());
		this.eloSelection.selectIfContains(Storage.get().getEloContext());

		this.regionSelection.addActionListener(e -> this.refresh());
		this.regionSelection.selectIfContains(Storage.get().getRegionContext());

		this.setContentPane(this.buildContentPane());
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		this.pack();
		this.setLocationRelativeTo(null);
		this.refresh();
	}

	private Container buildContentPane() {
		this.input.addKeyListener(Events.Keyboard.onReleased(e -> ((JSplitPane)this.getContentPane()).grabFocus()));

		JScrollPane display = new JScrollPane(this.output);
		display.setPreferredSize(new Dimension(700, 800));
		display.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		display.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

		JButton addButton = Factory.button("add Accounts", () -> {
			for(String acc : this.input.getText().trim().split("[,\\s]+")) {
				Storage.get().add(new Credentials(acc));
			}
			this.input.setText("");
		});

		this.getRootPane().setDefaultButton(addButton);
		return new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, display,
			SwingUtils.addSet(new Box(BoxLayout.Y_AXIS), this.input, this.eloSelection, this.regionSelection, addButton));
	}

	public void refresh() {
		Storage.get().setContext(this.regionSelection.getSelected(), this.eloSelection.getSelected());
		this.output.removeAll();
		Storage.get().getCredentials().stream()
			.map(Factory::loginDisplay)
			.forEach(this.output::add);
		this.output.revalidate();
		this.output.repaint();
	}

	public static AccountManager get() {
		return INSTANCE;
	}
}