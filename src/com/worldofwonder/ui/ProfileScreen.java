package com.worldofwonder.ui;

import com.worldofwonder.model.Achievement;
import com.worldofwonder.model.UserStats;
import com.worldofwonder.model.UserStreak;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User Profile screen showing avatar, statistics, and earned achievements.
 */
public class ProfileScreen extends JPanel {

    private final Dashboard dashboard;

    public ProfileScreen(Dashboard dashboard) {
        this.dashboard = dashboard;
        setOpaque(false);
        setLayout(new BorderLayout());
    }

    public void refresh() {
        removeAll();
        buildUI();
        revalidate();
        repaint();
    }

    private void buildUI() {
        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(BorderFactory.createEmptyBorder(30, 0, 40, 0));

        // Header row
        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setOpaque(false);
        headerRow.setBorder(BorderFactory.createEmptyBorder(10, 60, 10, 60));
        headerRow.setMaximumSize(new Dimension(800, 60));

        JButton backBtn = UITheme.iconButton("←", "Back", e -> dashboard.getMainUI().showDashboard());
        headerRow.add(backBtn, BorderLayout.WEST);

        JLabel title = new JLabel("👤  My Profile");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(Color.WHITE);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        headerRow.add(title, BorderLayout.CENTER);

        center.add(headerRow);
        center.add(Box.createVerticalStrut(20));

        // Fetch stats in background
        SwingWorker<UserStats, Void> worker = new SwingWorker<>() {
            @Override
            protected UserStats doInBackground() {
                return fetchStats();
            }

            @Override
            protected void done() {
                try {
                    UserStats stats = get();
                    if (stats == null) {
                        stats = new UserStats();
                        stats.setUsername(dashboard.getUsername());
                    }
                    addProfileContent(center, stats);
                    center.revalidate();
                    center.repaint();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();

        JScrollPane scroll = new JScrollPane(center);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scroll, BorderLayout.CENTER);
    }

    private void addProfileContent(JPanel parent, UserStats stats) {
        // Avatar card
        JPanel avatarCard = UITheme.glassCard();
        avatarCard.setLayout(new BoxLayout(avatarCard, BoxLayout.Y_AXIS));
        avatarCard.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        avatarCard.setMaximumSize(new Dimension(700, 200));
        avatarCard.setAlignmentX(CENTER_ALIGNMENT);

        // Avatar circle with initials
        String username = stats.getUsername() != null ? stats.getUsername() : "Player";
        String initials = username.length() >= 2
                ? username.substring(0, 2).toUpperCase()
                : username.toUpperCase();

        JPanel avatarCircle = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Gradient circle
                int cx = getWidth() / 2, cy = getHeight() / 2, r = 40;
                GradientPaint gp = new GradientPaint(cx - r, cy - r,
                        new Color(99, 102, 241), cx + r, cy + r, new Color(168, 85, 247));
                g2.setPaint(gp);
                g2.fillOval(cx - r, cy - r, r * 2, r * 2);

                // Border
                g2.setStroke(new BasicStroke(3f));
                g2.setColor(new Color(255, 255, 255, 60));
                g2.drawOval(cx - r, cy - r, r * 2, r * 2);

                // Initials
                g2.setFont(new Font("Segoe UI", Font.BOLD, 28));
                g2.setColor(Color.WHITE);
                FontMetrics fm = g2.getFontMetrics();
                int tw = fm.stringWidth(initials);
                g2.drawString(initials, cx - tw / 2, cy + fm.getAscent() / 2 - 2);
                g2.dispose();
            }
        };
        avatarCircle.setPreferredSize(new Dimension(100, 100));
        avatarCircle.setMaximumSize(new Dimension(100, 100));
        avatarCircle.setOpaque(false);
        avatarCircle.setAlignmentX(CENTER_ALIGNMENT);
        avatarCard.add(avatarCircle);

        JLabel nameLbl = new JLabel(username);
        nameLbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        nameLbl.setForeground(Color.WHITE);
        nameLbl.setAlignmentX(CENTER_ALIGNMENT);
        avatarCard.add(Box.createVerticalStrut(8));
        avatarCard.add(nameLbl);

        JLabel pointsLbl = new JLabel("⭐ " + stats.getTotalPoints() + " points");
        pointsLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pointsLbl.setForeground(new Color(200, 180, 255));
        pointsLbl.setAlignmentX(CENTER_ALIGNMENT);
        avatarCard.add(pointsLbl);

        parent.add(avatarCard);
        parent.add(Box.createVerticalStrut(16));

        // Stats grid
        JPanel statsCard = UITheme.glassCard();
        statsCard.setLayout(new GridLayout(2, 4, 12, 12));
        statsCard.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        statsCard.setMaximumSize(new Dimension(700, 160));
        statsCard.setAlignmentX(CENTER_ALIGNMENT);

        statsCard.add(statPill("🎮", "Games Played", String.valueOf(stats.getTotalGamesPlayed())));
        statsCard.add(statPill("✅", "Completed", String.valueOf(stats.getTotalGamesCompleted())));
        statsCard.add(statPill("📊", "Win Rate", stats.getWinRate() + "%"));
        statsCard.add(statPill("⭐", "Total Stars", String.valueOf(stats.getTotalStars())));
        statsCard.add(statPill("🧠", "Quiz", String.valueOf(stats.getQuizGames())));
        statsCard.add(statPill("🔍", "Word Search", String.valueOf(stats.getWordSearchGames())));
        statsCard.add(statPill("🧪", "Water Sort", String.valueOf(stats.getCupsGames())));
        statsCard.add(statPill("💎", "Words", String.valueOf(stats.getWowGames())));

        parent.add(statsCard);
        parent.add(Box.createVerticalStrut(16));

        // Achievements section
        JPanel achCard = UITheme.glassCard();
        achCard.setLayout(new BoxLayout(achCard, BoxLayout.Y_AXIS));
        achCard.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        achCard.setMaximumSize(new Dimension(700, 500));
        achCard.setAlignmentX(CENTER_ALIGNMENT);

        JLabel achTitle = new JLabel("🏅 Achievements (" + stats.getAchievementCount() + "/20)");
        achTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        achTitle.setForeground(new Color(200, 200, 255));
        achTitle.setAlignmentX(LEFT_ALIGNMENT);
        achCard.add(achTitle);
        achCard.add(Box.createVerticalStrut(12));

        // Fetch achievements
        List<Achievement> earned = fetchUserAchievements();
        List<Achievement> all = fetchAllAchievements();

        if (all.isEmpty()) {
            JLabel noAch = new JLabel("No achievements loaded — start playing to earn badges!");
            noAch.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            noAch.setForeground(new Color(140, 140, 170));
            noAch.setAlignmentX(LEFT_ALIGNMENT);
            achCard.add(noAch);
        } else {
            JPanel grid = new JPanel(new GridLayout(0, 4, 8, 8));
            grid.setOpaque(false);
            grid.setAlignmentX(LEFT_ALIGNMENT);

            java.util.Set<String> earnedKeys = new java.util.HashSet<>();
            for (Achievement a : earned) earnedKeys.add(a.getKey());

            for (Achievement a : all) {
                boolean unlocked = earnedKeys.contains(a.getKey());
                grid.add(createBadgeTile(a, unlocked));
            }
            achCard.add(grid);
        }

        parent.add(achCard);
    }

    private JPanel statPill(String icon, String label, String value) {
        JPanel pill = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(30, 30, 60, 150));
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));
                g2.dispose();
            }
        };
        pill.setOpaque(false);
        pill.setLayout(new BoxLayout(pill, BoxLayout.Y_AXIS));
        pill.setBorder(BorderFactory.createEmptyBorder(10, 8, 10, 8));

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

    private JPanel createBadgeTile(Achievement a, boolean unlocked) {
        JPanel tile = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = unlocked ? new Color(40, 40, 80, 200) : new Color(25, 25, 40, 150);
                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                if (unlocked) {
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.setColor(new Color(255, 200, 50, 100));
                    g2.draw(new RoundRectangle2D.Float(1, 1, getWidth() - 2, getHeight() - 2, 12, 12));
                }
                g2.dispose();
            }
        };
        tile.setOpaque(false);
        tile.setLayout(new BoxLayout(tile, BoxLayout.Y_AXIS));
        tile.setBorder(BorderFactory.createEmptyBorder(8, 4, 8, 4));
        tile.setToolTipText(a.getDescription());

        JLabel icon = new JLabel(unlocked ? a.getIcon() : "🔒");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        icon.setAlignmentX(CENTER_ALIGNMENT);
        icon.setForeground(unlocked ? Color.WHITE : new Color(80, 80, 100));

        JLabel name = new JLabel(a.getName());
        name.setFont(new Font("Segoe UI", Font.BOLD, 9));
        name.setForeground(unlocked ? Color.WHITE : new Color(80, 80, 100));
        name.setAlignmentX(CENTER_ALIGNMENT);

        tile.add(icon);
        tile.add(Box.createVerticalStrut(2));
        tile.add(name);
        return tile;
    }

    // ── API Helpers ────────────────────────────────────────────────────

    private UserStats fetchStats() {
        try {
            String json = httpGet("http://localhost:8080/api/stats?userId=" + dashboard.getUserId());
            if (json != null && json.contains("\"success\":true")) {
                UserStats s = new UserStats();
                s.setUserId(dashboard.getUserId());
                s.setUsername(strVal(json, "username"));
                s.setTotalGamesPlayed(intVal(json, "totalGamesPlayed"));
                s.setTotalGamesCompleted(intVal(json, "totalGamesCompleted"));
                s.setTotalPoints(intVal(json, "totalPoints"));
                s.setTotalStars(intVal(json, "totalStars"));
                s.setFavoriteGameType(strVal(json, "favoriteGameType"));
                s.setQuizGames(intVal(json, "quizGames"));
                s.setWordSearchGames(intVal(json, "wordSearchGames"));
                s.setCupsGames(intVal(json, "cupsGames"));
                s.setWowGames(intVal(json, "wowGames"));
                s.setAchievementCount(intVal(json, "achievementCount"));
                return s;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<Achievement> fetchUserAchievements() {
        return fetchAchievementList("http://localhost:8080/api/achievements/user?userId=" + dashboard.getUserId());
    }

    private List<Achievement> fetchAllAchievements() {
        return fetchAchievementList("http://localhost:8080/api/achievements");
    }

    private List<Achievement> fetchAchievementList(String url) {
        List<Achievement> list = new ArrayList<>();
        try {
            String json = httpGet(url);
            if (json == null) return list;
            // Simple parsing of achievement array
            Pattern p = Pattern.compile(
                    "\\{\"id\":(\\d+),\"key\":\"([^\"]*)\",\"name\":\"([^\"]*)\",\"description\":\"([^\"]*)\","
                            + "\"icon\":\"([^\"]*)\",\"conditionType\":\"([^\"]*)\",\"conditionValue\":(\\d+)\\}");
            Matcher m = p.matcher(json);
            while (m.find()) {
                Achievement a = new Achievement();
                a.setId(Integer.parseInt(m.group(1)));
                a.setKey(m.group(2));
                a.setName(m.group(3));
                a.setDescription(m.group(4));
                a.setIcon(m.group(5));
                a.setConditionType(m.group(6));
                a.setConditionValue(Integer.parseInt(m.group(7)));
                list.add(a);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
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
            // Server not reachable
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
