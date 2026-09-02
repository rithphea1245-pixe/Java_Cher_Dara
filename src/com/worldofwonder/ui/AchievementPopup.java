package com.worldofwonder.ui;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;

/**
 * Achievement popup toast that slides in from the top-right when a badge is earned.
 * Call AchievementPopup.show(parentComponent, icon, name, description) to display.
 */
public class AchievementPopup {

    private static final int POPUP_WIDTH = 340;
    private static final int POPUP_HEIGHT = 80;
    private static final int SLIDE_DURATION = 400;
    private static final int DISPLAY_DURATION = 3500;

    public static void show(Component parent, String icon, String name, String description) {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(parent);
        if (topFrame == null) return;

        JPanel popup = new JPanel() {
            private float alpha = 0f;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

                // Background with glassmorphism
                RoundRectangle2D bg = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(new Color(20, 20, 50, 220));
                g2.fill(bg);

                // Gold border
                g2.setStroke(new BasicStroke(2f));
                g2.setColor(new Color(255, 200, 50, 180));
                g2.draw(bg);

                // Shimmer effect
                GradientPaint shimmer = new GradientPaint(
                        0, 0, new Color(255, 215, 0, 40),
                        getWidth(), getHeight(), new Color(255, 180, 0, 10));
                g2.setPaint(shimmer);
                g2.fill(bg);

                g2.dispose();
                super.paintComponent(g);
            }

            public void setAlpha(float a) {
                this.alpha = a;
                repaint();
            }

            public float getAlpha() {
                return alpha;
            }
        };

        popup.setOpaque(false);
        popup.setLayout(new BorderLayout(10, 0));
        popup.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // Icon
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        iconLabel.setPreferredSize(new Dimension(50, 50));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        popup.add(iconLabel, BorderLayout.WEST);

        // Text content
        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("🏅 Achievement Unlocked!");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        titleLabel.setForeground(new Color(255, 200, 50));
        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(2));

        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameLabel.setForeground(Color.WHITE);
        textPanel.add(nameLabel);

        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        descLabel.setForeground(new Color(180, 180, 200));
        textPanel.add(descLabel);

        popup.add(textPanel, BorderLayout.CENTER);
        popup.setSize(POPUP_WIDTH, POPUP_HEIGHT);

        JLayeredPane layeredPane = topFrame.getLayeredPane();
        int startX = topFrame.getWidth() - POPUP_WIDTH - 20;
        int startY = -POPUP_HEIGHT;
        int endY = 20;

        popup.setBounds(startX, startY, POPUP_WIDTH, POPUP_HEIGHT);
        layeredPane.add(popup, JLayeredPane.POPUP_LAYER);

        // Slide-in animation
        long startTime = System.currentTimeMillis();
        Timer slideIn = new Timer(16, null);
        slideIn.addActionListener(e -> {
            float progress = Math.min(1f, (System.currentTimeMillis() - startTime) / (float) SLIDE_DURATION);
            float eased = 1f - (1f - progress) * (1f - progress);
            int y = (int) (startY + (endY - startY) * eased);
            popup.setBounds(startX, y, POPUP_WIDTH, POPUP_HEIGHT);

            try {
                java.lang.reflect.Method setAlpha = popup.getClass().getMethod("setAlpha", float.class);
                setAlpha.invoke(popup, eased);
            } catch (Exception ignored) {}

            if (progress >= 1f) {
                ((Timer) e.getSource()).stop();
                // Hold, then slide out
                Timer holdTimer = new Timer(DISPLAY_DURATION, ev -> {
                    long fadeStart = System.currentTimeMillis();
                    Timer slideOut = new Timer(16, null);
                    slideOut.addActionListener(ev2 -> {
                        float p = Math.min(1f, (System.currentTimeMillis() - fadeStart) / (float) SLIDE_DURATION);
                        int y2 = (int) (endY + (startY - endY) * p);
                        popup.setBounds(startX, y2, POPUP_WIDTH, POPUP_HEIGHT);
                        try {
                            java.lang.reflect.Method sa = popup.getClass().getMethod("setAlpha", float.class);
                            sa.invoke(popup, 1f - p);
                        } catch (Exception ignored2) {}
                        if (p >= 1f) {
                            ((Timer) ev2.getSource()).stop();
                            layeredPane.remove(popup);
                            layeredPane.repaint();
                        }
                    });
                    slideOut.start();
                });
                holdTimer.setRepeats(false);
                holdTimer.start();
            }
        });
        slideIn.start();
    }
}
