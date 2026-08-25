package com.worldofwonder.ui;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

public class Dashboard extends JPanel {

    private final MainUI app;

    private String username = "Guest";
    private boolean isGuest = true;
    private int userId = 0;
    private String token = null;
    private int totalPoints = 0;

    private final JPanel content;
    private JPanel topbar;
    private JLabel scoreValue;

    public Dashboard(MainUI app) {
        super(new BorderLayout());
        this.app = app;
        setOpaque(false);

        this.content = new JPanel(new BorderLayout(0, UITheme.GAP_SECTION));
        content.setOpaque(false);
        this.topbar = buildTopbar();
        content.add(topbar, BorderLayout.NORTH);
        content.add(buildCenter(), BorderLayout.CENTER);

        JPanel card = UITheme.card(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(UITheme.PAD_CARD_Y, UITheme.PAD_CARD_X, UITheme.PAD_CARD_Y, UITheme.PAD_CARD_X));
        UIUtil.flexSize(card, 1020, 780, 400, 1400);
        card.add(content, BorderLayout.CENTER);

        JPanel root = UITheme.pageRoot(card);
        UITheme.autoScale(root, 1100, 780, 0.82, 1.6);
        add(root, BorderLayout.CENTER);
    }

    public void setUser(String username, boolean isGuest, int userId, String token, int totalPoints) {
        this.username = username;
        this.isGuest = isGuest;
        this.userId = userId;
        this.token = token;
        this.totalPoints = totalPoints;
        content.remove(topbar);
        topbar = buildTopbar();
        content.add(topbar, BorderLayout.NORTH);
        content.revalidate();
        content.repaint();
    }

    public int getUserId() {
        return userId;
    }

    public String getToken() {
        return token;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void updateScore(int newTotal) {
        this.totalPoints = newTotal;
        if (scoreValue != null) {
            scoreValue.setText(String.valueOf(newTotal));
        }
    }

    private JPanel buildCenter() {
        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        javax.swing.JLabel title = new UITheme.GradientTextLabel("Choose your game",
                UITheme.FONT_PAGE_TITLE + 4, new java.awt.Color(0xe8f4ff), new java.awt.Color(0xc8b0ff));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(title);
        center.add(Box.createVerticalStrut(UITheme.GAP_TIGHT));

        JLabel subtitle = UITheme.subtitle("Pick a destination and start earning points");
        subtitle.setFont(UITheme.bodyFont(Font.PLAIN, UITheme.FONT_BODY + 1));
        subtitle.setForeground(new java.awt.Color(0xb8c8e8));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(subtitle);
        center.add(Box.createVerticalStrut(UITheme.GAP_SECTION));

        JPanel games = new JPanel(new GridLayout(2, 2, UITheme.GAP_SECTION, UITheme.GAP_SECTION));
        games.setOpaque(false);

        games.add(gameCard("quiz", UITheme.GameIcon.QUIZ, "World of Wonder Quiz",
                "Travel the world, answer questions, and earn points on every stop.", UITheme.VIOLET));
        games.add(gameCard("wordsearch", UITheme.GameIcon.WORDSEARCH, "Word Search Puzzles",
                "Hunt for hidden words in a letter grid and earn points on every find.", UITheme.PINK));
        games.add(gameCard("cups", UITheme.GameIcon.CUPS, "Cups - Water Sort",
                "Pour the colored water until every cup holds a single color.", UITheme.TEAL));
        games.add(gameCard("words", UITheme.GameIcon.WORDS, "Words of Wonders",
                "Connect letters, find hidden words, and complete crossword puzzles.", UITheme.GOLD));

        center.add(games);
        return center;
    }

    private JPanel buildTopbar() {
        JPanel bar = UITheme.roundedBar();

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        left.setOpaque(false);
        left.add(UITheme.avatar(isGuest ? "?" : username));

        JLabel welcomeLabel = new JLabel("Welcome, " + (isGuest ? "Guest" : username));
        welcomeLabel.setFont(UITheme.displayFont(Font.BOLD, UITheme.FONT_CARD_TITLE));
        welcomeLabel.setForeground(UITheme.TEXT);
        left.add(welcomeLabel);
        bar.add(left, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 0));
        right.setOpaque(false);

        JPanel scoreBox = new JPanel();
        scoreBox.setOpaque(false);
        scoreBox.setLayout(new BoxLayout(scoreBox, BoxLayout.Y_AXIS));

        JLabel scoreLabel = new JLabel("Total Points", SwingConstants.RIGHT);
        scoreLabel.setFont(UITheme.displayFont(Font.BOLD, UITheme.FONT_BADGE));
        scoreLabel.setForeground(UITheme.TEXT_MUTED);
        scoreLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        scoreBox.add(scoreLabel);

        JPanel scoreRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        scoreRow.setOpaque(false);
        scoreRow.add(UITheme.coin(30));

        JLabel scoreValueLocal = new JLabel(String.valueOf(totalPoints), SwingConstants.RIGHT);
        scoreValueLocal.setFont(UITheme.displayFont(Font.BOLD, UITheme.FONT_SECTION));
        scoreValueLocal.setForeground(UITheme.GOLD);
        scoreRow.add(scoreValueLocal);
        scoreRow.setAlignmentX(Component.RIGHT_ALIGNMENT);
        scoreBox.add(scoreRow);
        this.scoreValue = scoreValueLocal;

        right.add(scoreBox);

        JButton logout = UITheme.ghostButton("Logout", UITheme.CORAL);
        UIUtil.fixedSize(logout, 140, UITheme.BTN_H);
        logout.addActionListener(e -> logout());
        right.add(logout);

        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    private JButton gameCard(String cardName, UITheme.GameIcon icon, String label, String subtitle, java.awt.Color accent) {
        UITheme.GameModeCard card = new UITheme.GameModeCard(icon, label, subtitle, accent);
        if ("quiz".equals(cardName)) {
            card.setBand(new java.awt.Color(0x1a1050), new java.awt.Color(0x3a25a0));
        } else if ("wordsearch".equals(cardName)) {
            card.setBand(new java.awt.Color(0x4a1040), new java.awt.Color(0xd04080));
        } else if ("words".equals(cardName)) {
            card.setBand(new java.awt.Color(0x2a1a00), new java.awt.Color(0xd4a020));
        } else {
            card.setBand(new java.awt.Color(0x0a2848), new java.awt.Color(0x20d0c0));
        }
        UIUtil.flexSize(card, 420, 280, 260, Integer.MAX_VALUE);
        card.addActionListener(e -> app.showScreen(cardName));
        return card;
    }

    private void logout() {
        app.showWelcome();
    }

    public void showScreen(String cardName) {
        app.showScreen(cardName);
    }

    public void showDashboard() {
        app.showDashboard();
    }
}
