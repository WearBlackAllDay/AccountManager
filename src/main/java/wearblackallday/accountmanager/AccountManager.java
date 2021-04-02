package wearblackallday.accountmanager;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import wearblackallday.swing.components.CustomPanel;
import wearblackallday.swing.components.SelectionBox;
import wearblackallday.swing.components.builder.FrameBuilder;

import javax.swing.*;
import java.awt.Dimension;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AccountManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
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
        FrameBuilder.newBuilder().centered().sizeLocked().visible().title("AccountManager")
                .contentPane(new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, OUTPUT,
                        new CustomPanel(200, 40)
                                .boxLayout(BoxLayout.Y_AXIS)
                                .addTextField("user:pass, user1:pass1...", "input")
                                .addComponent(() -> ELO_SELECTION, (customPanel, selectionBox) ->
                                        selectionBox.addActionListener(e -> OUTPUT.update()))
                                .addComponent(() -> REGION_SELECTION, (customPanel, selectionBox) ->
                                        selectionBox.addActionListener(e -> OUTPUT.update()))
                                .addButton("add Accounts", (customPanel, button, event) -> {
                                    Stream.of(customPanel.getText("input").split(","))
                                            .map(account -> account.trim().split(":"))
                                            .forEach(credential -> STORAGE.accounts.add(new Account(credential[0], credential[1],
                                                    ELO_SELECTION.getSelected(), REGION_SELECTION.getSelected())));
                                    OUTPUT.update();
                                }))).create();
        OUTPUT.update();
    }

    public static class Output extends JScrollPane {
        private final Box viewPanel = new Box(BoxLayout.Y_AXIS);
        public Output() {
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