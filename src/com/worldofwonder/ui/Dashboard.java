package com.worldofwonder.ui;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.GradientPaint;
import java.awt.BasicStroke;
import java.awt.geom.RoundRectangle2D;

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

    public MainUI getMainUI() {
        return app;
    }

    public String getUsername() {
        return username;
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

        // ── Daily Challenge Banner ──────────────────────────────────────
        JPanel dailyBanner = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(99, 102, 241, 180),
                        getWidth(), 0, new Color(168, 85, 247, 180));
                g2.setPaint(gp);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                g2.dispose();
            }
        };
        dailyBanner.setOpaque(false);
        dailyBanner.setLayout(new BorderLayout(10, 0));
        dailyBanner.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));
        dailyBanner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        dailyBanner.setAlignmentX(CENTER_ALIGNMENT);
        dailyBanner.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));

        JLabel bannerLeft = new JLabel("📅 Daily Challenge Available!  •  Earn 2x bonus points today");
        bannerLeft.setFont(new Font("Segoe UI", Font.BOLD, 13));
        bannerLeft.setForeground(Color.WHITE);
        dailyBanner.add(bannerLeft, BorderLayout.CENTER);

        JLabel bannerArrow = new JLabel("Play →");
        bannerArrow.setFont(new Font("Segoe UI", Font.BOLD, 13));
        bannerArrow.setForeground(new Color(255, 255, 255, 200));
        dailyBanner.add(bannerArrow, BorderLayout.EAST);

        dailyBanner.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                app.showDailyChallenge();
            }
        });

        center.add(dailyBanner);
        center.add(Box.createVerticalStrut(12));

        // ── Quick Stats Row ─────────────────────────────────────────────
        JPanel statsRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        statsRow.setOpaque(false);
        statsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        statsRow.setAlignmentX(CENTER_ALIGNMENT);

        statsRow.add(quickStatPill("⭐", totalPoints + " pts"));
        statsRow.add(quickStatPill("🔥", "Streak"));
        statsRow.add(quickStatPill("🏆", "Rankings"));

        center.add(statsRow);
        center.add(Box.createVerticalStrut(12));

        // ── Title ───────────────────────────────────────────────────────
        javax.swing.JLabel title = new UITheme.GradientTextLabel("Choose your game",
                UITheme.FONT_PAGE_TITLE + 4, UITheme.BRAND_400, UITheme.WORDS_ACCENT[2]);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(title);
        center.add(Box.createVerticalStrut(UITheme.GAP_TIGHT));

        JLabel subtitle = UITheme.subtitle("Pick a destination and start earning points");
        subtitle.setFont(UITheme.bodyFont(Font.PLAIN, UITheme.FONT_BODY + 1));
        subtitle.setForeground(UITheme.TEXT_MUTED);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(subtitle);
        center.add(Box.createVerticalStrut(UITheme.GAP_SECTION));

        // ── 2×2 Game Card Grid ──────────────────────────────────────────
        JPanel gamesGrid = new JPanel(new GridLayout(2, 2, 16, 16));
        gamesGrid.setOpaque(false);
        gamesGrid.setMaximumSize(new Dimension(900, 560));
        gamesGrid.setAlignmentX(CENTER_ALIGNMENT);

        gamesGrid.add(gameCard("quiz", UITheme.GameIcon.QUIZ, "World of Wonder Quiz",
                "Travel the world, answer questions, earn points", UITheme.QUIZ_ACCENT[0]));
        gamesGrid.add(gameCard("wordsearch", UITheme.GameIcon.WORDSEARCH, "Word Search Puzzles",
                "Hunt for hidden words in a letter grid", UITheme.WORDSEARCH_ACCENT[0]));
        gamesGrid.add(gameCard("cups", UITheme.GameIcon.CUPS, "Cups - Water Sort",
                "Pour colored water until each cup is pure", UITheme.CUPS_ACCENT[0]));
        gamesGrid.add(gameCard("words", UITheme.GameIcon.WORDS, "Words of Wonders",
                "Connect letters and complete crosswords", UITheme.WORDS_ACCENT[0]));

        JPanel gridWrapper = new JPanel();
        gridWrapper.setOpaque(false);
        gridWrapper.setLayout(new BoxLayout(gridWrapper, BoxLayout.X_AXIS));
        gridWrapper.add(Box.createHorizontalGlue());
        gridWrapper.add(gamesGrid);
        gridWrapper.add(Box.createHorizontalGlue());

        JScrollPane scrollPane = new JScrollPane(gridWrapper);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);

        center.add(scrollPane);
        return center;
    }

    private JPanel quickStatPill(String icon, String text) {
        JPanel pill = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(30, 30, 60, 150));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                g2.dispose();
            }
        };
        pill.setOpaque(false);
        pill.setLayout(new FlowLayout(FlowLayout.CENTER, 6, 4));
        pill.setPreferredSize(new Dimension(130, 35));

        JLabel lbl = new JLabel(icon + " " + text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(Color.WHITE);
        pill.add(lbl);
        return pill;
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

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);

        // Score display
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
        scoreValueLocal.setForeground(UITheme.WARNING);
        scoreRow.add(scoreValueLocal);
        scoreRow.setAlignmentX(Component.RIGHT_ALIGNMENT);
        scoreBox.add(scoreRow);
        this.scoreValue = scoreValueLocal;

        right.add(scoreBox);

        // Navigation buttons
        JButton profileBtn = UITheme.iconButton("👤", "Profile", e -> app.showProfile());
        right.add(profileBtn);

        JButton leaderboardBtn = UITheme.iconButton("🏆", "Rank", e -> app.showLeaderboard());
        right.add(leaderboardBtn);

        JButton settingsBtn = UITheme.iconButton("⚙️", "Settings", e -> app.showSettings());
        right.add(settingsBtn);

        JButton logout = UITheme.ghostButton("Logout", UITheme.DANGER);
        UIUtil.fixedSize(logout, 100, UITheme.BTN_H);
        logout.addActionListener(e -> logout());
        right.add(logout);

        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    private JButton gameCard(String cardName, UITheme.GameIcon icon, String label, String subtitle, java.awt.Color accent) {
        UITheme.GameModeCard card = new UITheme.GameModeCard(icon, label, subtitle, accent);
        if ("quiz".equals(cardName)) {
            card.setBand(UITheme.QUIZ_ACCENT[2], UITheme.QUIZ_ACCENT[3]);
        } else if ("wordsearch".equals(cardName)) {
            card.setBand(UITheme.WORDSEARCH_ACCENT[2], UITheme.WORDSEARCH_ACCENT[3]);
        } else if ("words".equals(cardName)) {
            card.setBand(UITheme.WORDS_ACCENT[2], UITheme.WORDS_ACCENT[3]);
        } else {
            card.setBand(UITheme.CUPS_ACCENT[2], UITheme.CUPS_ACCENT[3]);
        }
        UIUtil.flexSize(card, 420, 260, 260, Integer.MAX_VALUE);
        card.addActionListener(e -> app.showScreen(cardName));
        return card;
    }

    private void logout() {
        AmbientMusic.stop();
        app.showWelcome();
    }

    public void showScreen(String cardName) {
        app.showScreen(cardName);
    }

    public void showDashboard() {
        app.showDashboard();
    }
}
