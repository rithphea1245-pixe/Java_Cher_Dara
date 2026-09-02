package com.worldofwonder.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Daily Challenge screen showing today's challenge with streak info and calendar.
 */
public class DailyChallengeScreen extends JPanel {

    private final Dashboard dashboard;

    // Parsed challenge data
    private String gameType = "quiz";
    private int bonusMultiplier = 2;
    private boolean completedToday = false;
    private int currentStreak = 0;
    private int longestStreak = 0;
    private int streakFreezes = 0;

    public DailyChallengeScreen(Dashboard dashboard) {
        this.dashboard = dashboard;
        setOpaque(false);
        setLayout(new BorderLayout());
    }

    public void refresh() {
        removeAll();
        fetchChallengeData();
        buildUI();
        revalidate();
        repaint();
    }

    private void buildUI() {
        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(BorderFactory.createEmptyBorder(30, 0, 40, 0));

        // Header
        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setOpaque(false);
        headerRow.setBorder(BorderFactory.createEmptyBorder(10, 60, 10, 60));
        headerRow.setMaximumSize(new Dimension(800, 60));

        JButton backBtn = UITheme.iconButton("←", "Back", e -> dashboard.getMainUI().showDashboard());
        headerRow.add(backBtn, BorderLayout.WEST);

        JLabel title = new JLabel("📅  Daily Challenge");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(Color.WHITE);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        headerRow.add(title, BorderLayout.CENTER);

        center.add(headerRow);
        center.add(Box.createVerticalStrut(25));

        // Streak card
        JPanel streakCard = UITheme.glassCard();
        streakCard.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 10));
        streakCard.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        streakCard.setMaximumSize(new Dimension(650, 100));
        streakCard.setAlignmentX(CENTER_ALIGNMENT);

        streakCard.add(createStreakPill("🔥", "Current Streak", currentStreak + " days"));
        streakCard.add(createStreakPill("🏅", "Longest Streak", longestStreak + " days"));
        streakCard.add(createStreakPill("❄️", "Streak Freezes", String.valueOf(streakFreezes)));

        center.add(streakCard);
        center.add(Box.createVerticalStrut(20));

        // Today's challenge card
        JPanel challengeCard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Gradient background
                GradientPaint gp = new GradientPaint(0, 0, new Color(30, 30, 70, 200),
                        getWidth(), getHeight(), new Color(60, 30, 100, 200));
                g2.setPaint(gp);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));

                // Border glow
                g2.setStroke(new BasicStroke(2f));
                g2.setColor(new Color(168, 85, 247, 100));
                g2.draw(new RoundRectangle2D.Float(1, 1, getWidth() - 2, getHeight() - 2, 20, 20));
                g2.dispose();
            }
        };
        challengeCard.setOpaque(false);
        challengeCard.setLayout(new BoxLayout(challengeCard, BoxLayout.Y_AXIS));
        challengeCard.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        challengeCard.setMaximumSize(new Dimension(650, 350));
        challengeCard.setAlignmentX(CENTER_ALIGNMENT);

        // Game type icon
        String[] gameIcons = {"🧠", "🔍", "🧪", "💎"};
        String[] gameNames = {"Quiz Challenge", "Word Search Challenge", "Water Sort Challenge", "Words of Wonders Challenge"};
        int gameIdx = 0;
        switch (gameType) {
            case "quiz": gameIdx = 0; break;
            case "wordsearch": gameIdx = 1; break;
            case "cups": gameIdx = 2; break;
            case "words": gameIdx = 3; break;
        }

        JLabel gameIcon = new JLabel(gameIcons[gameIdx]);
        gameIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        gameIcon.setAlignmentX(CENTER_ALIGNMENT);
        challengeCard.add(gameIcon);
        challengeCard.add(Box.createVerticalStrut(10));

        JLabel gameName = new JLabel("Today's Challenge: " + gameNames[gameIdx]);
        gameName.setFont(new Font("Segoe UI", Font.BOLD, 20));
        gameName.setForeground(Color.WHITE);
        gameName.setAlignmentX(CENTER_ALIGNMENT);
        challengeCard.add(gameName);
        challengeCard.add(Box.createVerticalStrut(8));

        JLabel bonusLbl = new JLabel("🎁 " + bonusMultiplier + "x Point Bonus!");
        bonusLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        bonusLbl.setForeground(new Color(255, 200, 50));
        bonusLbl.setAlignmentX(CENTER_ALIGNMENT);
        challengeCard.add(bonusLbl);
        challengeCard.add(Box.createVerticalStrut(20));

        if (completedToday) {
            JLabel doneLbl = new JLabel("✅ Challenge Completed Today!");
            doneLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
            doneLbl.setForeground(new Color(100, 220, 100));
            doneLbl.setAlignmentX(CENTER_ALIGNMENT);
            challengeCard.add(doneLbl);
        } else {
            JButton playBtn = UITheme.accentButton("▶  Play Challenge", UITheme.TEAL);
            playBtn.setAlignmentX(CENTER_ALIGNMENT);
            playBtn.setMaximumSize(new Dimension(250, 50));
            playBtn.addActionListener(e -> {
                // Navigate to the appropriate game screen
                switch (gameType) {
                    case "quiz":
                        dashboard.getMainUI().showScreen("quiz");
                        break;
                    case "wordsearch":
                        dashboard.getMainUI().showScreen("wordsearch");
                        break;
                    case "cups":
                        dashboard.getMainUI().showScreen("cups");
                        break;
                    case "words":
                        dashboard.getMainUI().showScreen("words");
                        break;
                }
            });
            challengeCard.add(playBtn);
        }

        center.add(challengeCard);
        center.add(Box.createVerticalStrut(20));

        // Info card
        JPanel infoCard = UITheme.glassCard();
        infoCard.setLayout(new BoxLayout(infoCard, BoxLayout.Y_AXIS));
        infoCard.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        infoCard.setMaximumSize(new Dimension(650, 150));
        infoCard.setAlignmentX(CENTER_ALIGNMENT);

        JLabel infoTitle = new JLabel("💡 How it works");
        infoTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        infoTitle.setForeground(new Color(200, 200, 255));
        infoTitle.setAlignmentX(LEFT_ALIGNMENT);
        infoCard.add(infoTitle);
        infoCard.add(Box.createVerticalStrut(8));

        String[] tips = {
                "• A new challenge appears every day — rotating between all game modes",
                "• Complete the challenge to earn bonus points (2x multiplier!)",
                "• Play every day to build your streak 🔥",
                "• Streak Freezes protect your streak if you miss a day"
        };
        for (String tip : tips) {
            JLabel tipLbl = new JLabel(tip);
            tipLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            tipLbl.setForeground(new Color(160, 160, 190));
            tipLbl.setAlignmentX(LEFT_ALIGNMENT);
            infoCard.add(tipLbl);
        }

        center.add(infoCard);

        JScrollPane scroll = new JScrollPane(center);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel createStreakPill(String icon, String label, String value) {
        JPanel pill = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(30, 30, 60, 180));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));
                g2.dispose();
            }
        };
        pill.setOpaque(false);
        pill.setPreferredSize(new Dimension(160, 65));
        pill.setLayout(new BoxLayout(pill, BoxLayout.Y_AXIS));
        pill.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        JLabel iconLbl = new JLabel(icon + " " + value);
        iconLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        iconLbl.setForeground(Color.WHITE);
        iconLbl.setAlignmentX(CENTER_ALIGNMENT);

        JLabel lblLabel = new JLabel(label);
        lblLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblLabel.setForeground(new Color(150, 150, 180));
        lblLabel.setAlignmentX(CENTER_ALIGNMENT);

        pill.add(iconLbl);
        pill.add(Box.createVerticalStrut(2));
        pill.add(lblLabel);
        return pill;
    }

    // ── API Helpers ────────────────────────────────────────────────────

    private void fetchChallengeData() {
        try {
            // Fetch daily challenge
            String json = httpGet("http://localhost:8080/api/daily?userId=" + dashboard.getUserId());
            if (json != null && json.contains("\"success\":true")) {
                gameType = strVal(json, "gameType");
                bonusMultiplier = intVal(json, "bonusMultiplier");
                completedToday = json.contains("\"completedToday\":true");
            }

            // Fetch streak
            String streakJson = httpGet("http://localhost:8080/api/streak?userId=" + dashboard.getUserId());
            if (streakJson != null && streakJson.contains("\"success\":true")) {
                currentStreak = intVal(streakJson, "currentStreak");
                longestStreak = intVal(streakJson, "longestStreak");
                streakFreezes = intVal(streakJson, "streakFreezes");
            }
        } catch (Exception e) {
            // Use defaults
        }
    }

    private String httpGet(String urlStr) {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);
            if (conn.getResponseCode() == 200) {
                BufferedReader r = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = r.readLine()) != null) sb.append(line);
                r.close();
                return sb.toString();
            }
        } catch (Exception e) {
            // Not reachable
        }
        return null;
    }

    private String strVal(String json, String key) {
        Pattern p = Pattern.compile("\"" + key + "\"\\s*:\\s*\"([^\"]*)\"");
        Matcher m = p.matcher(json);
        return m.find() ? m.group(1) : "";
    }

    private int intVal(String json, String key) {
        Pattern p = Pattern.compile("\"" + key + "\"\\s*:\\s*(\\d+)");
        Matcher m = p.matcher(json);
        return m.find() ? Integer.parseInt(m.group(1)) : 0;
    }
}
