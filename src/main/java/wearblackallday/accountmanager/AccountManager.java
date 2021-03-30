package wearblackallday.accountmanager;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.google.gson.Gson;
import wearblackallday.swing.components.CustomPanel;
import wearblackallday.swing.components.FrameBuilder;
import wearblackallday.swing.components.SelectionBox;

import javax.swing.*;
import java.awt.Dimension;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AccountManager {

    private static final Gson GSON = new Gson().newBuilder().setPrettyPrinting().create();
    public static final AccountStorage STORAGE;
    public static final Output OUTPUT;
    private static final SelectionBox<Account.Elo> ELO_SELECTION;
    private static final SelectionBox<Account.Region> REGION_SELECTION;

    static {
        FlatDarculaLaf.install();
        OUTPUT = new Output();
        ELO_SELECTION = new SelectionBox<>(elo -> elo.toString().toLowerCase(), Account.Elo.values());
        REGION_SELECTION = new SelectionBox<>(Account.Region.values());
        try {
            File file = new File("accounts.json");
            STORAGE = file.createNewFile() ? new AccountStorage() : GSON.fromJson(new FileReader(file), AccountStorage.class);
        } catch (IOException exception) {
            throw new RuntimeException("IO is shit");
        }
    }

    public static void main(String[] args) {
        CustomPanel menu = new CustomPanel(200, 40)
                .addTextField("user:pass, user1:pass1...", "input")
                .addComponent(() -> ELO_SELECTION, (customPanel, selectionBox) ->
                        selectionBox.addActionListener(e -> OUTPUT.update()))
                .addComponent(() -> REGION_SELECTION, (customPanel, selectionBox) ->
                        selectionBox.addActionListener(e -> OUTPUT.update()))
                .addButton("add Accounts", (customPanel, button, event) -> {
                    String[] accounts = customPanel.getText("input").split(",");
                    for (String acc : accounts) {
                        String[] credentials = acc.trim().split(":");
                        Account account = new Account(credentials[0], credentials[1],
                                ELO_SELECTION.getSelected(), REGION_SELECTION.getSelected());
                        STORAGE.accounts.add(account);
                    }
                    OUTPUT.update();
                });
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));

        OUTPUT.update();

        FrameBuilder.newBuilder().centered().sizeLocked().visible().title("AccountManager").contentPane(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, OUTPUT, menu)).create();
    }

    public static class Output extends JScrollPane {
        private final JPanel viewPanel = new JPanel();
        public Output() {
            this.viewPanel.setLayout(new BoxLayout(this.viewPanel, BoxLayout.Y_AXIS));
            this.setPreferredSize(new Dimension(660, 800));
            this.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            this.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            this.setViewportView(this.viewPanel);
        }
        public void update() {
            this.viewPanel.removeAll();
            STORAGE.getFits().forEach(account -> this.viewPanel.add(account.display()));
            this.repaint();
            this.revalidate();
            try {
                new PrintStream("accounts.json").print(GSON.toJson(STORAGE));
            } catch (FileNotFoundException e) {
                System.out.println("this never happens");
            }
        }
    }

    public static class AccountStorage {
        public List<Account> accounts = new ArrayList<>();

        public List<Account> getFits() {
            return this.accounts.stream().filter(account ->
                    account.elo == ELO_SELECTION.getSelected() && account.region == REGION_SELECTION.getSelected()).collect(Collectors.toList());
        }
    }
}