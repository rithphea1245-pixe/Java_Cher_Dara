package com.worldofwonder.ui;

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
 * Leaderboard screen with animated podium for top 3 and scrollable ranked list.
 */
public class LeaderboardScreen extends JPanel {

    private final Dashboard dashboard;

    public LeaderboardScreen(Dashboard dashboard) {
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

        // Header
        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setOpaque(false);
        headerRow.setBorder(BorderFactory.createEmptyBorder(10, 60, 10, 60));
        headerRow.setMaximumSize(new Dimension(800, 60));

        JButton backBtn = UITheme.iconButton("←", "Back", e -> dashboard.getMainUI().showDashboard());
        headerRow.add(backBtn, BorderLayout.WEST);

        JLabel title = new JLabel("🏆  Leaderboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(Color.WHITE);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        headerRow.add(title, BorderLayout.CENTER);

        center.add(headerRow);
        center.add(Box.createVerticalStrut(20));

        // Fetch leaderboard data in background
        SwingWorker<List<String[]>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<String[]> doInBackground() {
                return fetchLeaderboard();
            }

            @Override
            protected void done() {
                try {
                    List<String[]> data = get();
                    addLeaderboardContent(center, data);
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

    private void addLeaderboardContent(JPanel parent, List<String[]> data) {
        if (data.isEmpty()) {
            JLabel noData = new JLabel("No players yet — be the first to play!");
            noData.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            noData.setForeground(new Color(150, 150, 180));
            noData.setAlignmentX(CENTER_ALIGNMENT);
            parent.add(noData);
            return;
        }

        // Podium for top 3
        if (data.size() >= 3) {
            JPanel podium = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
            podium.setOpaque(false);
            podium.setMaximumSize(new Dimension(700, 220));
            podium.setAlignmentX(CENTER_ALIGNMENT);

            // Order: 2nd, 1st, 3rd for visual podium
            podium.add(createPodiumCard(data.get(1), 2, 160));
            podium.add(createPodiumCard(data.get(0), 1, 200));
            podium.add(createPodiumCard(data.get(2), 3, 140));

            parent.add(podium);
            parent.add(Box.createVerticalStrut(24));
        }

        // Full list
        JPanel listCard = UITheme.glassCard();
        listCard.setLayout(new BoxLayout(listCard, BoxLayout.Y_AXIS));
        listCard.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        listCard.setMaximumSize(new Dimension(700, 800));
        listCard.setAlignmentX(CENTER_ALIGNMENT);

        for (int i = 0; i < data.size(); i++) {
            String[] entry = data.get(i);
            JPanel row = createRankRow(i + 1, entry[0], entry[1]);
            row.setAlignmentX(LEFT_ALIGNMENT);
            listCard.add(row);
            if (i < data.size() - 1) {
                JSeparator sep = new JSeparator();
                sep.setForeground(new Color(60, 60, 100, 80));
                sep.setMaximumSize(new Dimension(660, 1));
                listCard.add(sep);
            }
        }

        parent.add(listCard);
    }

    private JPanel createPodiumCard(String[] entry, int rank, int height) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color[] colors = {
                        new Color(255, 200, 50, 40), // Gold
                        new Color(180, 190, 210, 40), // Silver
                        new Color(200, 130, 50, 40),  // Bronze
                };
                Color[] borders = {
                        new Color(255, 200, 50, 150),
                        new Color(180, 190, 210, 120),
                        new Color(200, 130, 50, 120),
                };

                g2.setColor(colors[rank - 1]);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
                g2.setStroke(new BasicStroke(2f));
                g2.setColor(borders[rank - 1]);
                g2.draw(new RoundRectangle2D.Float(1, 1, getWidth() - 2, getHeight() - 2, 16, 16));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(170, height));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));

        String[] medals = {"🥇", "🥈", "🥉"};
        JLabel medalLbl = new JLabel(medals[rank - 1]);
        medalLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        medalLbl.setAlignmentX(CENTER_ALIGNMENT);

        // Avatar circle
        String initials = entry[0].length() >= 2
                ? entry[0].substring(0, 2).toUpperCase() : entry[0].toUpperCase();
        JPanel avatar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int cx = getWidth() / 2, cy = getHeight() / 2, r = 22;
                g2.setColor(new Color(99, 102, 241));
                g2.fillOval(cx - r, cy - r, r * 2, r * 2);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
                g2.setColor(Color.WHITE);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(initials, cx - fm.stringWidth(initials) / 2, cy + fm.getAscent() / 2 - 2);
                g2.dispose();
            }
        };
        avatar.setOpaque(false);
        avatar.setPreferredSize(new Dimension(50, 50));
        avatar.setMaximumSize(new Dimension(50, 50));
        avatar.setAlignmentX(CENTER_ALIGNMENT);

        JLabel nameLbl = new JLabel(entry[0]);
        nameLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameLbl.setForeground(Color.WHITE);
        nameLbl.setAlignmentX(CENTER_ALIGNMENT);

        JLabel ptsLbl = new JLabel(entry[1] + " pts");
        ptsLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        ptsLbl.setForeground(new Color(200, 200, 255));
        ptsLbl.setAlignmentX(CENTER_ALIGNMENT);

        card.add(medalLbl);
        card.add(Box.createVerticalStrut(6));
        card.add(avatar);
        card.add(Box.createVerticalStrut(4));
        card.add(nameLbl);
        card.add(ptsLbl);

        return card;
    }

    private JPanel createRankRow(int rank, String name, String points) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        row.setMaximumSize(new Dimension(660, 50));

        // Highlight current user
        boolean isMe = name.equalsIgnoreCase(dashboard.getUsername());

        // Rank
        String rankIcon = rank <= 3
                ? new String[]{"🥇", "🥈", "🥉"}[rank - 1]
                : "#" + rank;
        JLabel rankLbl = new JLabel(rankIcon);
        rankLbl.setFont(new Font("Segoe UI", rank <= 3 ? Font.PLAIN : Font.BOLD, rank <= 3 ? 20 : 14));
        rankLbl.setForeground(isMe ? new Color(99, 102, 241) : new Color(150, 150, 180));
        rankLbl.setPreferredSize(new Dimension(50, 30));
        row.add(rankLbl, BorderLayout.WEST);

        // Name
        JLabel nameLbl = new JLabel(name + (isMe ? " (You)" : ""));
        nameLbl.setFont(new Font("Segoe UI", isMe ? Font.BOLD : Font.PLAIN, 14));
        nameLbl.setForeground(isMe ? new Color(99, 102, 241) : Color.WHITE);
        row.add(nameLbl, BorderLayout.CENTER);

        // Points
        JLabel ptsLbl = new JLabel(points + " pts");
        ptsLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        ptsLbl.setForeground(new Color(200, 200, 255));
        row.add(ptsLbl, BorderLayout.EAST);

        if (isMe) {
            row.setBackground(new Color(99, 102, 241, 20));
            row.setOpaque(true);
        }

        return row;
    }

    // ── API Helper ────────────────────────────────────────────────────

    private List<String[]> fetchLeaderboard() {
        List<String[]> entries = new ArrayList<>();
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(
                    "http://localhost:8080/api/leaderboard").openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(3000);
            if (conn.getResponseCode() == 200) {
                BufferedReader r = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = r.readLine()) != null) sb.append(line);
                r.close();
                String json = sb.toString();

                // Parse simple JSON array of {username, totalPoints}
                Pattern p = Pattern.compile("\"username\"\\s*:\\s*\"([^\"]*)\".*?\"totalPoints\"\\s*:\\s*(\\d+)");
                Matcher m = p.matcher(json);
                while (m.find()) {
                    entries.add(new String[]{m.group(1), m.group(2)});
                }
            }
        } catch (Exception e) {
            // Server not reachable
        }
        return entries;
    }
}
