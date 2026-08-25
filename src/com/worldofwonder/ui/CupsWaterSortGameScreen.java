package com.worldofwonder.ui;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CupsWaterSortGameScreen extends JPanel {

    private static final String VIEW_DIFFICULTY = "difficulty";
    private static final String VIEW_GAME = "game";
    private static final String VIEW_COMPLETE = "complete";

    private static final int TUBE_W = 56;
    private static final int TUBE_GAP = 14;
    private static final int SEG_H = 30;
    private static final int BASE_H = 16;
    private static final int WALL = 5;
    private static final int TOP_PAD = 44;
    private static final int SIDE_PAD = 20;
    private static final int BOTTOM_PAD = 20;
    private static final int EMPTY_TUBES = 2;

    private static final Color[] WATER_COLORS = {
            new Color(0xE74C3C),
            new Color(0x2ECC71),
            new Color(0x3498DB),
            new Color(0xF1C40F),
            new Color(0x9B59B6),
            new Color(0xE67E22),
            new Color(0x1ABC9C),
            new Color(0xE84393),
    };

    private static final String[] WATER_NAMES = {
            "Ruby", "Emerald", "Ocean", "Gold", "Amethyst", "Tangerine", "Teal", "Orchid",
    };

    private static String colorName(Color c) {
        for (int i = 0; i < WATER_COLORS.length; i++) {
            if (WATER_COLORS[i].getRGB() == c.getRGB()) {
                return WATER_NAMES[i];
            }
        }
        return "Water";
    }

    private final Dashboard dashboard;
    private final QuizApiClient api;
    private final GameBoard board;
    private final JLabel difficultyLabel;
    private final JLabel movesLabel;
    private final JLabel completeText;
    private JPanel completePanel;
    private UITheme.Confetti confetti;

    private final CardLayout cards = new CardLayout();
    private final JPanel content = new JPanel(cards);

    private int capacity;
    private int colorCount;
    private final List<Tube> tubes = new ArrayList<>();
    private final List<List<Color>> initialTubes = new ArrayList<>();
    private Tube selected;
    private int moves;
    private boolean won;
    private boolean pouring;
    private PourAnim pourAnim;

    public CupsWaterSortGameScreen(Dashboard dashboard) {
        super(new BorderLayout());
        this.dashboard = dashboard;
        this.api = new QuizApiClient();
        setOpaque(false);
        this.capacity = 4;
        this.colorCount = 4;

        this.board = new GameBoard();
        this.difficultyLabel = UITheme.badge("", UITheme.TEAL);
        this.movesLabel = UITheme.badge("Moves: 0", UITheme.GOLD);
        this.completeText = new JLabel("", SwingConstants.CENTER);
        completeText.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, UITheme.FONT_CARD_TITLE));
        completeText.setForeground(UITheme.TEXT_MUTED);

        this.completePanel = buildCompletePanel();
        this.confetti = new UITheme.Confetti(completePanel);

        content.add(buildDifficultyPanel(), VIEW_DIFFICULTY);
        content.add(buildGamePanel(), VIEW_GAME);
        content.add(completePanel, VIEW_COMPLETE);

        JPanel viewCard = UITheme.card(new BorderLayout());
        viewCard.setBorder(BorderFactory.createEmptyBorder(UITheme.PAD_CARD_Y, UITheme.PAD_CARD_X, UITheme.PAD_CARD_Y, UITheme.PAD_CARD_X));
        UIUtil.fixedSize(viewCard, 1020, 700);
        viewCard.add(buildHeader(), BorderLayout.NORTH);
        viewCard.add(content, BorderLayout.CENTER);

        JPanel root = UITheme.screenPage(viewCard);

        UITheme.autoScale(root, 1100, 790, 0.85, 1.5);

        add(root, BorderLayout.CENTER);

        cards.show(content, VIEW_DIFFICULTY);
        newGame();
    }

    private JPanel buildHeader() {
        JButton back = UITheme.ghostButton("\u2190 Back to Dashboard", UITheme.TEXT_MUTED);
        UIUtil.fixedSize(back, 200, UITheme.BTN_H);
        back.addActionListener(e -> dashboard.showDashboard());
        return UITheme.screenHeader(back, "Cups Water Sort", 30);
    }

    private JPanel buildDifficultyPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        panel.add(UITheme.sectionTitle("Choose your difficulty", 26), BorderLayout.NORTH);

        JPanel buttons = new JPanel(new GridLayout(3, 1, 0, 20));
        buttons.setOpaque(false);
        buttons.setBorder(BorderFactory.createEmptyBorder(28, 34, 0, 34));
        buttons.add(difficultyTile("Easy", "4 colors \u2022 2 empty cups", UITheme.GREEN, 4));
        buttons.add(difficultyTile("Medium", "6 colors \u2022 2 empty cups", UITheme.GOLD, 6));
        buttons.add(difficultyTile("Hard", "8 colors \u2022 2 empty cups", UITheme.CORAL, 8));

        JPanel wrap = UIUtil.centered(buttons);
        wrap.setOpaque(false);
        panel.add(wrap, BorderLayout.CENTER);
        return panel;
    }

    private JButton difficultyTile(String name, String description, Color accent, int colors) {
        UITheme.TileButton button = new UITheme.TileButton(name, description, accent);
        button.setDark(true);
        button.setSubtitleColor(new Color(0xd0e8f5));
        UIUtil.fixedSize(button, 580, 100);
        button.addActionListener(e -> startGame(colors));
        return button;
    }

    private JPanel buildGamePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setOpaque(false);

        JPanel meta = new JPanel(new FlowLayout(FlowLayout.CENTER, 24, 6));
        meta.setOpaque(false);
        meta.add(difficultyLabel);
        meta.add(movesLabel);
        panel.add(meta, BorderLayout.NORTH);

        JPanel boardCenter = new JPanel(new GridBagLayout());
        boardCenter.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        boardCenter.add(board, gbc);
        panel.add(boardCenter, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 0));
        actions.setOpaque(false);
        actions.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));

        JButton restart = UITheme.ghostButton("Restart", UITheme.TEXT_MUTED);
        UIUtil.fixedSize(restart, 200, 50);
        restart.addActionListener(e -> restartCurrentGame());
        actions.add(restart);

        JButton change = UITheme.ghostButton("Change Difficulty", UITheme.TEXT_MUTED);
        UIUtil.fixedSize(change, 200, 50);
        change.addActionListener(e -> showDifficulty());
        actions.add(change);

        JButton newGame = UITheme.primaryButton("New Game");
        UIUtil.fixedSize(newGame, 200, 50);
        newGame.addActionListener(e -> newGame());
        actions.add(newGame);

        panel.add(actions, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildCompletePanel() {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                if (confetti != null) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    UITheme.paintQuality(g2);
                    confetti.paint(g2);
                    g2.dispose();
                }
            }
        };
        panel.setOpaque(false);

        UITheme.GradientTextLabel title =
                new UITheme.GradientTextLabel("Cups Solved!", 34, UITheme.GOLD, UITheme.TEAL);
        panel.add(title, BorderLayout.NORTH);
        panel.add(completeText, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new GridLayout(3, 1, 0, 14));
        buttons.setOpaque(false);
        buttons.setBorder(BorderFactory.createEmptyBorder(20, 34, 0, 34));

        JButton again = UITheme.primaryButton("New Puzzle");
        UIUtil.fixedSize(again, 340, 54);
        again.addActionListener(e -> startGame(colorCount));
        buttons.add(again);

        JButton change = UITheme.ghostButton("Change Difficulty", UITheme.TEXT_MUTED);
        UIUtil.fixedSize(change, 340, 54);
        change.addActionListener(e -> showDifficulty());
        buttons.add(change);

        JButton dashboardBtn = UITheme.ghostButton("Back to Dashboard", UITheme.TEXT_MUTED);
        UIUtil.fixedSize(dashboardBtn, 340, 54);
        dashboardBtn.addActionListener(e -> dashboard.showDashboard());
        buttons.add(dashboardBtn);

        JPanel wrap = UIUtil.centered(buttons);
        wrap.setOpaque(false);
        panel.add(wrap, BorderLayout.SOUTH);
        return panel;
    }

    private void startGame(int colors) {
        colorCount = colors;
        capacity = 4;
        won = false;
        selected = null;
        moves = 0;
        difficultyLabel.setText("Difficulty: " + (colors <= 4 ? "Easy" : colors <= 6 ? "Medium" : "Hard"));
        newGame();
        cards.show(content, VIEW_GAME);
    }

    private void showDifficulty() {
        cards.show(content, VIEW_DIFFICULTY);
    }

    private void restartCurrentGame() {
        if (initialTubes.isEmpty()) {
            newGame();
            return;
        }
        won = false;
        selected = null;
        moves = 0;
        pouring = false;
        if (pourAnim != null) {
            pourAnim.timer.stop();
            pourAnim = null;
        }
        board.cancelShake();
        updateMoveLabel();

        tubes.clear();
        for (List<Color> initList : initialTubes) {
            Tube t = new Tube(capacity);
            t.segments.addAll(initList);
            tubes.add(t);
        }
        board.revalidate();
        board.repaint();
    }

    private void newGame() {
        won = false;
        selected = null;
        moves = 0;
        pouring = false;
        if (pourAnim != null) {
            pourAnim.timer.stop();
            pourAnim = null;
        }
        board.cancelShake();
        updateMoveLabel();

        tubes.clear();
        while (true) {
            tubes.clear();
            List<Color> pool = new ArrayList<>();
            for (int c = 0; c < colorCount; c++) {
                for (int k = 0; k < capacity; k++) {
                    pool.add(WATER_COLORS[c]);
                }
            }
            Collections.shuffle(pool);

            int index = 0;
            for (int i = 0; i < colorCount; i++) {
                Tube tube = new Tube(capacity);
                for (int k = 0; k < capacity; k++) {
                    tube.segments.add(pool.get(index++));
                }
                tubes.add(tube);
            }
            for (int i = 0; i < EMPTY_TUBES; i++) {
                tubes.add(new Tube(capacity));
            }

            if (!startsSolved()) {
                break;
            }
        }

        initialTubes.clear();
        for (Tube t : tubes) {
            initialTubes.add(new ArrayList<>(t.segments));
        }

        board.revalidate();
        board.repaint();
    }

    private boolean startsSolved() {
        for (Tube tube : tubes) {
            if (tube.segments.isEmpty()) {
                continue;
            }
            Color first = tube.segments.get(0);
            boolean mono = true;
            for (Color segment : tube.segments) {
                if (!segment.equals(first)) {
                    mono = false;
                    break;
                }
            }
            if (mono) {
                return true;
            }
        }
        return false;
    }

    private void updateMoveLabel() {
        movesLabel.setText("Moves: " + moves);
    }

    private void onTubeClick(Tube clicked) {
        if (won || pouring) {
            return;
        }
        if (selected == null) {
            if (!clicked.isEmpty()) {
                selected = clicked;
                SoundUtil.playLetterSelect(0);
                board.repaint();
            }
            return;
        }
        if (clicked == selected) {
            selected = null;
            board.repaint();
            return;
        }
        int count = planned(selected, clicked);
        if (count <= 0) {
            SoundUtil.playError();
            board.shake(clicked);
            board.repaint();
            return;
        }
        Tube from = selected;
        selected = null;
        startPour(from, clicked, count);
    }

    private int planned(Tube from, Tube to) {
        if (from.isEmpty() || to.isFull()) {
            return 0;
        }
        if (!to.isEmpty() && !to.topColor().equals(from.topColor())) {
            return 0;
        }
        Color color = from.topColor();
        int count = 0;
        for (int i = from.segments.size() - 1; i >= 0; i--) {
            if (to.segments.size() + count >= to.capacity || !color.equals(from.segments.get(i))) {
                break;
            }
            count++;
        }
        return count;
    }

    private void startPour(Tube from, Tube to, int count) {
        pouring = true;
        SoundUtil.playPour();
        pourAnim = new PourAnim(from, to, from.topColor(), count);
    }

    private class PourAnim {
        final Tube from;
        final Tube to;
        final Color color;
        final int count;
        final javax.swing.Timer timer;
        float t;
        boolean committed;

        PourAnim(Tube from, Tube to, Color color, int count) {
            this.from = from;
            this.to = to;
            this.color = color;
            this.count = count;
            timer = new javax.swing.Timer(16, e -> tick());
            timer.start();
        }

        void tick() {
            t += 1f / 32f;
            if (t >= 0.5f && !committed) {
                committed = true;
                for (int i = 0; i < count; i++) {
                    to.segments.add(from.segments.remove(from.segments.size() - 1));
                }
                moves++;
                updateMoveLabel();
                if (isSolved()) {
                    won = true;
                    SoundUtil.playVictory();
                    int earnedPoints = Math.max(1, colorCount * 10 - moves);
                    completeText.setText("You sorted all the colors in " + moves + " moves! +" + earnedPoints + " points");
                    completeText.setForeground(UITheme.GOLD);
                    SwingUtilities.invokeLater(() -> {
                        cards.show(content, VIEW_COMPLETE);
                        confetti.launch();
                    });
                    syncCompletionToBackend(earnedPoints);
                }
            }
            if (t >= 1f) {
                timer.stop();
                pouring = false;
            }
            board.repaint();
        }

        void paint(Graphics2D g2) {
            int fi = tubes.indexOf(from);
            int ti = tubes.indexOf(to);
            if (fi < 0 || ti < 0) {
                return;
            }
            Rectangle fr = getTubeRect(fi);
            Rectangle tr = getTubeRect(ti);
            float fx = fr.x + fr.width / 2f;
            float fy = fr.y;
            float tx = tr.x + tr.width / 2f;
            float ty = tr.y;
            float lift = 2.6f * SEG_H;

            float px;
            float py;
            float alpha = 1f;
            if (t < 0.35f) {
                float k = smooth(t / 0.35f);
                px = fx;
                py = fy - lift * k;
            } else if (t < 0.65f) {
                float k = (t - 0.35f) / 0.30f;
                px = fx + (tx - fx) * k;
                py = fy - lift;
            } else {
                float k = smooth((t - 0.65f) / 0.35f);
                px = tx;
                py = (fy - lift) + lift * k;
                alpha = Math.max(0f, 1f - (t - 0.5f) * 2f);
            }

            int bw = TUBE_W - 2 * WALL;
            int bh = SEG_H * Math.min(count, 3);
            int bx = Math.round(px - bw / 2f);
            int by = Math.round(py - bh);
            g2.setComposite(AlphaComposite.SrcOver.derive(alpha));
            g2.setColor(color);
            g2.fillRoundRect(bx, by, bw, bh, 12, 12);
            g2.setColor(new Color(255, 255, 255, 110));
            g2.setStroke(new java.awt.BasicStroke(1.4f));
            g2.drawRoundRect(bx, by, bw, bh, 12, 12);
            g2.setComposite(AlphaComposite.SrcOver.derive(1f));
        }

        private float smooth(float k) {
            return k * k * (3f - 2f * k);
        }
    }

    private void syncCompletionToBackend(int earnedPoints) {
        int userId = dashboard.getUserId();
        if (userId <= 0 || earnedPoints <= 0) {
            return;
        }
        new SwingWorker<Integer, Void>() {
            @Override
            protected Integer doInBackground() {
                try {
                    return api.completePuzzle(userId, earnedPoints);
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void done() {
                try {
                    Integer totalPoints = get();
                    if (totalPoints != null) {
                        dashboard.updateScore(totalPoints);
                    }
                } catch (Exception ignored) {
                }
            }
        }.execute();
    }

    private boolean isSolved() {
        for (Tube tube : tubes) {
            if (tube.segments.isEmpty()) {
                continue;
            }
            if (tube.segments.size() != capacity) {
                return false;
            }
            Color first = tube.segments.get(0);
            for (Color segment : tube.segments) {
                if (!segment.equals(first)) {
                    return false;
                }
            }
        }
        return true;
    }

    private Rectangle getTubeRect(int index) {
        int x = SIDE_PAD + index * (TUBE_W + TUBE_GAP);
        int y = TOP_PAD;
        return new Rectangle(x, y, TUBE_W, capacity * SEG_H + BASE_H);
    }

    private static class Tube {
        final int capacity;
        final List<Color> segments = new ArrayList<>();

        Tube(int capacity) {
            this.capacity = capacity;
        }

        boolean isEmpty() {
            return segments.isEmpty();
        }

        boolean isFull() {
            return segments.size() >= capacity;
        }

        Color topColor() {
            return segments.isEmpty() ? null : segments.get(segments.size() - 1);
        }
    }

    private class GameBoard extends JPanel {

        private Tube hoverTube;
        private Tube shakeTube;
        private float shakeT = -1f;
        private javax.swing.Timer shakeTimer;

        GameBoard() {
            setOpaque(false);
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    Tube clicked = tubeAt(e.getPoint());
                    if (clicked != null) {
                        onTubeClick(clicked);
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    if (hoverTube != null) {
                        hoverTube = null;
                        repaint();
                    }
                }
            });
            addMouseMotionListener(new MouseAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    Tube tube = tubeAt(e.getPoint());
                    if (tube != hoverTube) {
                        hoverTube = tube;
                        repaint();
                    }
                }
            });
        }

        void cancelShake() {
            shakeTube = null;
            shakeT = -1f;
            if (shakeTimer != null) {
                shakeTimer.stop();
                shakeTimer = null;
            }
        }

        void shake(Tube tube) {
            if (tube == null) {
                return;
            }
            shakeTube = tube;
            shakeT = 0f;
            if (shakeTimer != null) {
                shakeTimer.stop();
            }
            shakeTimer = new javax.swing.Timer(16, e -> {
                shakeT += 1f / 24f;
                if (shakeT >= 1f) {
                    shakeT = -1f;
                    shakeTube = null;
                    shakeTimer.stop();
                }
                repaint();
            });
            shakeTimer.start();
        }

        @Override
        public Dimension getPreferredSize() {
            int width = SIDE_PAD * 2 + tubes.size() * TUBE_W + (tubes.size() - 1) * TUBE_GAP;
            int height = TOP_PAD + capacity * SEG_H + BASE_H + BOTTOM_PAD;
            return new Dimension(width, height);
        }

        private Tube tubeAt(Point p) {
            Dimension natural = getPreferredSize();
            double sx = natural.width > 0 ? getWidth() / (double) natural.width : 1.0;
            double sy = natural.height > 0 ? getHeight() / (double) natural.height : 1.0;
            int x = sx > 0 ? (int) Math.round(p.x / sx) : p.x;
            int y = sy > 0 ? (int) Math.round(p.y / sy) : p.y;
            for (int i = 0; i < tubes.size(); i++) {
                if (getTubeRect(i).contains(x, y)) {
                    return tubes.get(i);
                }
            }
            return null;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            UITheme.paintQuality(g2);

            Dimension natural = getPreferredSize();
            double sx = natural.width > 0 ? getWidth() / (double) natural.width : 1.0;
            double sy = natural.height > 0 ? getHeight() / (double) natural.height : 1.0;
            if (sx > 0 && sy > 0 && (sx != 1.0 || sy != 1.0)) {
                g2.scale(sx, sy);
            }

            for (int i = 0; i < tubes.size(); i++) {
                Tube tube = tubes.get(i);
                int dx = (shakeTube == tube && shakeT >= 0f)
                        ? (int) Math.round(Math.sin(shakeT * Math.PI * 3) * 5 * (1 - shakeT)) : 0;
                if (dx != 0) {
                    g2.translate(dx, 0);
                }
                drawTube(g2, tube, getTubeRect(i), tube == selected, tube == hoverTube,
                        shakeTube == tube && shakeT >= 0f);
                if (dx != 0) {
                    g2.translate(-dx, 0);
                }
            }

            if (selected != null && !selected.isEmpty()) {
                drawTopBlob(g2, selected);
            }

            if (pourAnim != null && pouring) {
                pourAnim.paint(g2);
            }

            g2.dispose();
        }

        private void drawTube(Graphics2D g2, Tube tube, Rectangle r, boolean isSelected,
                              boolean isHovered, boolean shaking) {
            int bottom = r.y + capacity * SEG_H;
            int tubeH = capacity * SEG_H + BASE_H;

            g2.setColor(new Color(11, 26, 44, 175));
            g2.fill(new RoundRectangle2D.Float(r.x, r.y, r.width, tubeH, 18, 18));
            g2.setColor(new Color(255, 255, 255, 40));
            g2.setStroke(new java.awt.BasicStroke(1.3f));
            g2.draw(new RoundRectangle2D.Float(r.x + 1.5f, r.y + 1.5f, r.width - 3f, tubeH - 3f, 17, 17));

            for (int j = 0; j < tube.segments.size(); j++) {
                int y = bottom - (j + 1) * SEG_H;
                Color color = tube.segments.get(j);
                g2.setColor(color);
                g2.fillRoundRect(r.x + WALL, y + 2, r.width - 2 * WALL, SEG_H - 3, 12, 12);
                g2.setColor(new Color(255, 255, 255, 48));
                g2.fillRoundRect(r.x + WALL + 3, y + 3, r.width - 2 * WALL - 6,
                        Math.max(4, (SEG_H - 3) / 3), 6, 6);
                g2.setColor(new Color(0, 0, 0, 26));
                g2.fillRoundRect(r.x + WALL, y + SEG_H - 6, r.width - 2 * WALL, 4, 4, 4);
            }

            g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
            String countText = tube.segments.size() + "/" + capacity;
            java.awt.FontMetrics fm = g2.getFontMetrics();
            int bw = fm.stringWidth(countText) + 16;
            int by = r.y - 26;
            g2.setColor(new Color(11, 26, 44, 230));
            g2.fillRoundRect(r.x + (r.width - bw) / 2, by, bw, 19, 9, 9);
            g2.setColor(UITheme.TEXT);
            g2.drawString(countText, r.x + (r.width - fm.stringWidth(countText)) / 2, by + 14);

            g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
            fm = g2.getFontMetrics();
            String name = tube.isEmpty() ? "Empty" : colorName(tube.topColor());
            g2.setColor(UITheme.TEXT_MUTED);
            g2.drawString(name, r.x + (r.width - fm.stringWidth(name)) / 2, bottom + BASE_H + 5 + fm.getAscent());

            if (isSelected) {
                g2.setColor(new Color(32, 211, 194, 60));
                g2.setStroke(new java.awt.BasicStroke(8f));
                g2.draw(new RoundRectangle2D.Float(r.x - 6, r.y - 6, r.width + 12, tubeH + 12, 22, 22));
                g2.setColor(UITheme.TEAL);
                g2.setStroke(new java.awt.BasicStroke(4.5f));
                g2.draw(new RoundRectangle2D.Float(r.x - 3, r.y - 3, r.width + 6, tubeH + 6, 20, 20));
            } else if (shaking) {
                g2.setColor(new Color(255, 93, 93, 90));
                g2.setStroke(new java.awt.BasicStroke(5f));
                g2.draw(new RoundRectangle2D.Float(r.x - 4, r.y - 4, r.width + 8, tubeH + 8, 20, 20));
                g2.setColor(UITheme.ERROR);
                g2.setStroke(new java.awt.BasicStroke(2f));
                g2.draw(new RoundRectangle2D.Float(r.x - 2, r.y - 2, r.width + 4, tubeH + 4, 19, 19));
            } else if (isHovered) {
                g2.setColor(new Color(32, 211, 194, 55));
                g2.setStroke(new java.awt.BasicStroke(5f));
                g2.draw(new RoundRectangle2D.Float(r.x - 4, r.y - 4, r.width + 8, tubeH + 8, 20, 20));
                g2.setColor(new Color(32, 211, 194, 160));
                g2.setStroke(new java.awt.BasicStroke(2f));
                g2.draw(new RoundRectangle2D.Float(r.x - 2, r.y - 2, r.width + 4, tubeH + 4, 19, 19));
            }
        }

        private void drawTopBlob(Graphics2D g2, Tube tube) {
            int index = tubes.indexOf(tube);
            Rectangle r = getTubeRect(index);
            Color color = tube.topColor();

            int blobW = TUBE_W - 2 * WALL;
            int blobH = SEG_H;
            int x = r.x + WALL;
            int y = r.y - blobH - 10;

            g2.setColor(color);
            g2.fillRoundRect(x, y, blobW, blobH, 12, 12);
            g2.setColor(new Color(255, 255, 255, 120));
            g2.setStroke(new java.awt.BasicStroke(1.6f));
            g2.drawRoundRect(x, y, blobW, blobH, 12, 12);
        }
    }
}
