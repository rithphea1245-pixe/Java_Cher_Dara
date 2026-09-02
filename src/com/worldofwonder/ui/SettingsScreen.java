package com.worldofwonder.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * Settings screen with sound/music toggles, theme selection, and about section.
 */
public class SettingsScreen extends JPanel {

    private final Dashboard dashboard;
    private boolean soundEnabled = true;
    private boolean musicEnabled = AmbientMusic.isEnabled();
    private int selectedTheme = 0; // 0=Dark, 1=Midnight, 2=Ocean
    private static final String[] THEME_NAMES = {"Dark Indigo", "Midnight Purple", "Ocean Blue"};
    private static final Color[][] THEME_COLORS = {
            {new Color(0x1a1a3e), new Color(0x0a0a1a)},
            {new Color(0x2d1b4e), new Color(0x0f0a1a)},
            {new Color(0x0d2b45), new Color(0x050d1a)},
    };

    public SettingsScreen(Dashboard dashboard) {
        this.dashboard = dashboard;
        setOpaque(false);
        setLayout(new BorderLayout());
        buildUI();
    }

    public void refresh() {
        musicEnabled = AmbientMusic.isEnabled();
        removeAll();
        buildUI();
        revalidate();
        repaint();
    }

    private void buildUI() {
        // Center panel with settings cards
        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(BorderFactory.createEmptyBorder(40, 0, 40, 0));

        // Header
        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setOpaque(false);
        headerRow.setBorder(BorderFactory.createEmptyBorder(20, 60, 10, 60));
        headerRow.setMaximumSize(new Dimension(700, 70));

        JButton backBtn = UITheme.iconButton("←", "Back", e -> dashboard.getMainUI().showDashboard());
        headerRow.add(backBtn, BorderLayout.WEST);

        JLabel title = new JLabel("⚙️  Settings");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(Color.WHITE);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        headerRow.add(title, BorderLayout.CENTER);

        center.add(headerRow);
        center.add(Box.createVerticalStrut(20));

        // Sound section
        center.add(createSectionCard("🔊 Sound & Music", new JComponent[]{
                createToggle("Sound Effects", soundEnabled, on -> {
                    soundEnabled = on;
                    SoundUtil.setMuted(!on);
                }),
                createToggle("Background Music", musicEnabled, on -> {
                    musicEnabled = on;
                    AmbientMusic.setEnabled(on);
                    if (on) AmbientMusic.start();
                    else AmbientMusic.stop();
                })
        }));

        center.add(Box.createVerticalStrut(16));

        // Theme section
        center.add(createSectionCard("🎨 Theme", new JComponent[]{
                createThemeSelector()
        }));

        center.add(Box.createVerticalStrut(16));

        // About section
        center.add(createSectionCard("ℹ️ About", new JComponent[]{
                createAboutPanel()
        }));

        // Wrap in scroll pane
        JScrollPane scroll = new JScrollPane(center);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        add(scroll, BorderLayout.CENTER);
    }

    private JPanel createSectionCard(String title, JComponent[] content) {
        JPanel card = UITheme.glassCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        card.setMaximumSize(new Dimension(600, 400));
        card.setAlignmentX(CENTER_ALIGNMENT);

        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lbl.setForeground(new Color(200, 200, 255));
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        card.add(lbl);
        card.add(Box.createVerticalStrut(12));

        for (JComponent c : content) {
            c.setAlignmentX(LEFT_ALIGNMENT);
            card.add(c);
            card.add(Box.createVerticalStrut(8));
        }

        return card;
    }

    private JPanel createToggle(String label, boolean initial, java.util.function.Consumer<Boolean> onChange) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(550, 40));

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lbl.setForeground(Color.WHITE);
        row.add(lbl, BorderLayout.WEST);

        // Custom toggle switch
        JPanel toggle = new JPanel() {
            boolean on = initial;

            {
                setPreferredSize(new Dimension(52, 28));
                setOpaque(false);
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        on = !on;
                        onChange.accept(on);
                        repaint();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Track
                Color trackColor = on ? new Color(99, 102, 241) : new Color(60, 60, 80);
                g2.setColor(trackColor);
                g2.fillRoundRect(0, 2, 52, 24, 24, 24);

                // Thumb
                int thumbX = on ? 28 : 4;
                g2.setColor(Color.WHITE);
                g2.fillOval(thumbX, 5, 18, 18);

                g2.dispose();
            }
        };

        row.add(toggle, BorderLayout.EAST);
        return row;
    }

    private JPanel createThemeSelector() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(550, 90));

        for (int i = 0; i < THEME_NAMES.length; i++) {
            int idx = i;
            JPanel themeCard = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    // Gradient preview
                    GradientPaint gp = new GradientPaint(0, 0, THEME_COLORS[idx][0],
                            getWidth(), getHeight(), THEME_COLORS[idx][1]);
                    g2.setPaint(gp);
                    g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 14, 14));

                    // Selected border
                    if (idx == selectedTheme) {
                        g2.setStroke(new BasicStroke(2.5f));
                        g2.setColor(new Color(99, 102, 241));
                        g2.draw(new RoundRectangle2D.Float(1, 1, getWidth() - 2, getHeight() - 2, 14, 14));
                    }

                    g2.dispose();
                }
            };
            themeCard.setPreferredSize(new Dimension(130, 70));
            themeCard.setOpaque(false);
            themeCard.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            themeCard.setLayout(new BorderLayout());

            JLabel nameLbl = new JLabel(THEME_NAMES[i], SwingConstants.CENTER);
            nameLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
            nameLbl.setForeground(Color.WHITE);
            themeCard.add(nameLbl, BorderLayout.SOUTH);

            themeCard.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    selectedTheme = idx;
                    row.repaint();
                    SoundUtil.playClick();
                }
            });

            row.add(themeCard);
        }
        return row;
    }

    private JPanel createAboutPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setMaximumSize(new Dimension(550, 120));

        String[] lines = {
                "World of Wonder v2.0",
                "A multi-game educational puzzle adventure",
                "",
                "Featuring: Quiz · Word Search · Words of Wonders · Water Sort",
                "Built with Java Swing · PostgreSQL"
        };

        for (String line : lines) {
            JLabel lbl = new JLabel(line);
            lbl.setFont(new Font("Segoe UI", line.equals(lines[0]) ? Font.BOLD : Font.PLAIN,
                    line.equals(lines[0]) ? 15 : 12));
            lbl.setForeground(line.isEmpty() ? Color.WHITE : new Color(180, 180, 210));
            lbl.setAlignmentX(LEFT_ALIGNMENT);
            panel.add(lbl);
        }

        return panel;
    }
}
