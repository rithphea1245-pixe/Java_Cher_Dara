package com.worldofwonder.ui;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.RadialGradientPaint;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class WordSearchGameScreen extends JPanel {

    private static final int CELL = 26;
    private static final int PAD = 8;
    private static final int POINTS_PER_LETTER = 10;
    private static final int COMPLETION_BONUS = 50;

    private static final int[][] DIR_HV = {{0, 1}, {1, 0}};
    private static final int[][] DIR_DIAG = {{0, 1}, {1, 0}, {1, 1}, {-1, 1}};
    private static final int[][] DIR_ALL = {
            {0, 1}, {1, 0}, {1, 1}, {-1, 1},
            {0, -1}, {-1, 0}, {-1, -1}, {1, -1},
    };

    private static final String[][] THEMES = {
            {"PYRAMID", "NILE", "PHARAOH", "SPHINX", "DESERT", "MUMMY", "CAMEL", "TOMB", "SCARAB", "ANUBIS"},
            {"PLANET", "COMET", "ROCKET", "GALAXY", "NEBULA", "MOON", "MARS", "STAR", "ORBIT", "VENUS"},
            {"OCEAN", "CORAL", "WHALE", "SHARK", "TIDE", "SHELL", "REEF", "DOLPHIN", "KELP", "ANEMONE"},
            {"DINOSAUR", "FOSSIL", "JURASSIC", "TRICERATOPS", "VELOCIRAPTOR", "PTERODACTYL", "ROAR", "PREDATOR", "HERBIVORE", "CRETACEOUS"},
            {"CASTLE", "KNIGHT", "DRAGON", "WIZARD", "KINGDOM", "ARMOR", "SWORD", "SHIELD", "CROWN", "QUEST"},
            {"JUNGLE", "TOUCAN", "CANOPY", "PARROT", "MONKEY", "TROPICAL", "BUTTERFLY", "ORCHID", "FERN", "VIPER"},
    };

    private enum Difficulty {
        EASY("Easy", 8, 9, 4, 5, DIR_HV),
        MEDIUM("Medium", 11, 12, 7, 8, DIR_DIAG),
        HARD("Hard", 16, 18, 10, 13, DIR_ALL);

        final String label;
        final int sizeMin;
        final int sizeMax;
        final int countMin;
        final int countMax;
        final int[][] directions;

        Difficulty(String label, int sizeMin, int sizeMax, int countMin, int countMax, int[][] directions) {
            this.label = label;
            this.sizeMin = sizeMin;
            this.sizeMax = sizeMax;
            this.countMin = countMin;
            this.countMax = countMax;
            this.directions = directions;
        }
    }

    private final Dashboard dashboard;
    private final QuizApiClient api;
    private final Random random = new Random();

    private final JPanel viewCard;
    private final JPanel difficultyPanel;
    private final JPanel gamePanel;
    private final GridBoard board;
    private final JPanel wordListPanel;
    private final JLabel progressLabel;
    private final JLabel pointsLabel;
    private final JLabel feedbackLabel;
    private final JLabel difficultyLabel;
    private final JPanel completePanel;
    private JLabel completeText;
    private UITheme.Confetti confetti;
    private Point hoverCell;

    private Difficulty difficulty = Difficulty.MEDIUM;
    private int size;
    private char[][] grid;
    private final List<PlacedWord> words = new ArrayList<>();
    private final Set<Long> foundCells = new HashSet<>();
    private final List<Point> selection = new ArrayList<>();
    private int points;
    private boolean dragging;
    private Point selectionStart;

    public WordSearchGameScreen(Dashboard dashboard) {
        super(new BorderLayout());
        this.dashboard = dashboard;
        this.api = new QuizApiClient();
        setOpaque(false);

        this.difficultyPanel = buildDifficultyPanel();
        this.completePanel = buildCompletePanel();
        this.confetti = new UITheme.Confetti(completePanel);

        this.board = new GridBoard();
        this.wordListPanel = new JPanel(new WrapLayout(FlowLayout.CENTER, 8, 8));
        this.wordListPanel.setOpaque(false);
        this.wordListPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        this.progressLabel = UITheme.badge("", UITheme.VIOLET);
        this.pointsLabel = UITheme.badge("", UITheme.GOLD);
        this.feedbackLabel = new JLabel("", SwingConstants.CENTER);
        this.difficultyLabel = UITheme.badge("", UITheme.TEAL);
        this.gamePanel = buildGamePanel();

        this.viewCard = UITheme.card(new BorderLayout());
        viewCard.setBorder(BorderFactory.createEmptyBorder(UITheme.PAD_CARD_Y, UITheme.PAD_CARD_X, UITheme.PAD_CARD_Y, UITheme.PAD_CARD_X));
        UIUtil.fixedSize(viewCard, 1000, 700);
        viewCard.add(buildHeader(), BorderLayout.NORTH);
        viewCard.add(difficultyPanel, BorderLayout.CENTER);

        JPanel root = UITheme.screenPage(viewCard);

        UITheme.autoScale(root, 1080, 790, 0.85, 1.5);
        UITheme.recordBaseTree(difficultyPanel);
        UITheme.recordBaseTree(gamePanel);
        UITheme.recordBaseTree(completePanel);

        add(root, BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JButton back = UITheme.ghostButton("\u2190 Back to Dashboard", UITheme.TEXT_MUTED);
        UIUtil.fixedSize(back, 200, UITheme.BTN_H);
        back.addActionListener(e -> dashboard.showDashboard());
        return UITheme.screenHeader(back, "Word Search", 30);
    }

    private JPanel buildDifficultyPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel title = UITheme.title("Choose your difficulty", 26);
        panel.add(title, BorderLayout.NORTH);

        JPanel buttons = new JPanel();
        buttons.setLayout(new javax.swing.BoxLayout(buttons, javax.swing.BoxLayout.Y_AXIS));
        buttons.setOpaque(false);
        buttons.setBorder(BorderFactory.createEmptyBorder(28, 0, 0, 0));
        buttons.add(new DifficultyCard(Difficulty.EASY, "Smaller grid, fewer words", UITheme.GREEN, "\uD83C\uDF31"));
        buttons.add(Box.createVerticalStrut(16));
        buttons.add(new DifficultyCard(Difficulty.MEDIUM, "More words and diagonal paths", UITheme.GOLD, "\u26A1"));
        buttons.add(Box.createVerticalStrut(16));
        buttons.add(new DifficultyCard(Difficulty.HARD, "Large grid, all directions including backwards", UITheme.CORAL, "\uD83D\uDD25"));

        JPanel wrap = UIUtil.centered(buttons);
        wrap.setOpaque(false);
        panel.add(wrap, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildGamePanel() {
        JPanel panel = new JPanel(new BorderLayout(16, 12));
        panel.setOpaque(false);

        JPanel meta = new JPanel(new FlowLayout(FlowLayout.CENTER, 28, 4));
        meta.setOpaque(false);
        difficultyLabel.setForeground(UITheme.TEXT);
        progressLabel.setForeground(UITheme.TEXT_MUTED);
        pointsLabel.setForeground(UITheme.GOLD);
        meta.add(difficultyLabel);
        meta.add(progressLabel);
        meta.add(pointsLabel);
        panel.add(meta, BorderLayout.NORTH);

        feedbackLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, UITheme.FONT_BODY));
        feedbackLabel.setForeground(UITheme.TEXT_MUTED);
        panel.add(feedbackLabel, BorderLayout.SOUTH);

        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);

        JPanel boardWrap = new JPanel(new GridBagLayout());
        boardWrap.setOpaque(false);
        GridBagConstraints boardGbc = new GridBagConstraints();
        boardGbc.fill = GridBagConstraints.BOTH;
        boardGbc.weightx = 1;
        boardGbc.weighty = 1;
        boardWrap.add(board, boardGbc);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new java.awt.Insets(0, 0, 0, 12);
        center.add(boardWrap, gbc);

        JPanel right = new JPanel(new BorderLayout(0, 12));
        right.setOpaque(false);
        right.setPreferredSize(new Dimension(260, 0));

        JLabel wordsTitle = UITheme.title("Words to find", 18);
        right.add(wordsTitle, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(wordListPanel);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        right.add(scroll, BorderLayout.CENTER);

        JPanel actions = new JPanel(new GridLayout(2, 1, 0, 12));
        actions.setOpaque(false);
        JButton newPuzzle = UITheme.primaryButton("New Puzzle");
        UIUtil.fixedSize(newPuzzle, 220, UITheme.BTN_H);
        newPuzzle.addActionListener(e -> startPuzzle(difficulty));
        actions.add(newPuzzle);
        JButton changeDifficulty = UITheme.ghostButton("Change Difficulty", UITheme.TEXT_MUTED);
        UIUtil.fixedSize(changeDifficulty, 220, UITheme.BTN_H);
        changeDifficulty.addActionListener(e -> showDifficultyPanel());
        actions.add(changeDifficulty);

        JPanel actionsWrap = UIUtil.centered(actions);
        actionsWrap.setOpaque(false);
        right.add(actionsWrap, BorderLayout.SOUTH);

        GridBagConstraints rightGbc = new GridBagConstraints();
        rightGbc.gridx = 1;
        rightGbc.gridy = 0;
        rightGbc.weightx = 0;
        rightGbc.weighty = 1;
        rightGbc.fill = GridBagConstraints.VERTICAL;
        center.add(right, rightGbc);
        panel.add(center, BorderLayout.CENTER);
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
                new UITheme.GradientTextLabel("Puzzle Complete!", 34, UITheme.GOLD, UITheme.CORAL);
        panel.add(title, BorderLayout.NORTH);

        completeText = new JLabel("", SwingConstants.CENTER);
        completeText.setFont(UITheme.bodyFont(Font.PLAIN, 18));
        completeText.setForeground(UITheme.TEXT_MUTED);
        panel.add(completeText, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new GridLayout(3, 1, 0, 14));
        buttons.setOpaque(false);
        buttons.setBorder(BorderFactory.createEmptyBorder(20, 24, 0, 24));

        JButton again = UITheme.primaryButton("New Puzzle");
        UIUtil.fixedSize(again, 340, UITheme.BTN_H);
        again.addActionListener(e -> startPuzzle(difficulty));
        buttons.add(again);

        JButton change = UITheme.ghostButton("Change Difficulty", UITheme.TEXT_MUTED);
        UIUtil.fixedSize(change, 340, UITheme.BTN_H);
        change.addActionListener(e -> showDifficultyPanel());
        buttons.add(change);

        JButton dashboardBtn = UITheme.ghostButton("Back to Dashboard", UITheme.TEXT_MUTED);
        UIUtil.fixedSize(dashboardBtn, 340, UITheme.BTN_H);
        dashboardBtn.addActionListener(e -> dashboard.showDashboard());
        buttons.add(dashboardBtn);

        JPanel wrap = UIUtil.centered(buttons);
        wrap.setOpaque(false);
        panel.add(wrap, BorderLayout.SOUTH);
        return panel;
    }

    private void showDifficultyPanel() {
        viewCard.removeAll();
        viewCard.add(difficultyPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void showGamePanel() {
        viewCard.removeAll();
        viewCard.add(gamePanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void showCompletePanel() {
        SoundUtil.playVictory();
        completeText.setText("You found all " + words.size() + " words!");
        viewCard.removeAll();
        viewCard.add(completePanel, BorderLayout.CENTER);
        revalidate();
        repaint();
        confetti.launch();
    }

    /* ================= Puzzle generation ================= */

    private void startPuzzle(Difficulty diff) {
        difficulty = diff;
        words.clear();
        foundCells.clear();
        selection.clear();
        points = 0;
        dragging = false;

        int chosenSize = randInt(diff.sizeMin, diff.sizeMax);
        char[][] result = null;
        List<PlacedWord> placed = null;
        for (int attempt = 0; attempt < 30; attempt++) {
            int s = randInt(diff.sizeMin, diff.sizeMax);
            String[] theme = THEMES[random.nextInt(THEMES.length)];
            List<String> candidates = new ArrayList<>();
            for (String word : theme) {
                if (word.length() <= s) {
                    candidates.add(word);
                }
            }
            shuffle(candidates);
            int count = randInt(diff.countMin, diff.countMax);
            int take = Math.min(count, candidates.size());
            List<String> chosen = candidates.subList(0, take);
            chosen.sort((a, b) -> b.length() - a.length());

            char[][] g = new char[s][s];
            List<PlacedWord> p = new ArrayList<>();
            boolean ok = true;
            for (String word : chosen) {
                if (!placeWord(g, word, s, diff.directions, p)) {
                    ok = false;
                    break;
                }
            }
            if (ok) {
                fillRandom(g, s);
                result = g;
                placed = p;
                chosenSize = s;
                break;
            }
        }
        if (result == null) {
            result = new char[chosenSize][chosenSize];
            placed = new ArrayList<>();
            fillRandom(result, chosenSize);
        }

        size = chosenSize;
        grid = result;
        words.addAll(placed);
        board.invalidateCache();
        board.revalidate();
        renderPuzzle();
        showGamePanel();
    }

    private boolean placeWord(char[][] g, String word, int s, int[][] directions, List<PlacedWord> placed) {
        for (int t = 0; t < 400; t++) {
            int[] dir = directions[random.nextInt(directions.length)];
            int dr = dir[0];
            int dc = dir[1];
            int startR = random.nextInt(s);
            int startC = random.nextInt(s);
            int endR = startR + (word.length() - 1) * dr;
            int endC = startC + (word.length() - 1) * dc;
            if (endR < 0 || endR >= s || endC < 0 || endC >= s) {
                continue;
            }
            boolean fits = true;
            for (int i = 0; i < word.length(); i++) {
                int r = startR + i * dr;
                int c = startC + i * dc;
                char existing = g[r][c];
                if (existing != 0 && existing != word.charAt(i)) {
                    fits = false;
                    break;
                }
            }
            if (!fits) {
                continue;
            }
            List<Point> cells = new ArrayList<>();
            for (int i = 0; i < word.length(); i++) {
                int r = startR + i * dr;
                int c = startC + i * dc;
                g[r][c] = word.charAt(i);
                cells.add(new Point(r, c));
            }
            placed.add(new PlacedWord(word, cells));
            return true;
        }
        return false;
    }

    private void fillRandom(char[][] g, int s) {
        for (int r = 0; r < s; r++) {
            for (int c = 0; c < s; c++) {
                if (g[r][c] == 0) {
                    g[r][c] = (char) ('A' + random.nextInt(26));
                }
            }
        }
    }

    private void renderPuzzle() {
        difficultyLabel.setText("Difficulty: " + difficulty.label);
        renderWordList();
        updateMeta();
        feedbackLabel.setText("Drag across letters to find the words.");
        feedbackLabel.setForeground(UITheme.TEXT_MUTED);
    }

    private void renderWordList() {
        wordListPanel.removeAll();
        for (PlacedWord w : words) {
            JLabel chip = UITheme.chipLabel(w.word);
            if (w.found) {
                chip.setForeground(UITheme.GREEN);
                chip.setBorder(BorderFactory.createCompoundBorder(
                        new UITheme.PillBorder(new Color(61, 220, 151, 170), 2),
                        BorderFactory.createEmptyBorder(5, 14, 5, 14)));
                Map<TextAttribute, Object> attrs = new HashMap<>(chip.getFont().getAttributes());
                attrs.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
                chip.setFont(new Font(attrs));
            }
            wordListPanel.add(chip);
        }
        wordListPanel.revalidate();
        wordListPanel.repaint();
    }

    private void updateMeta() {
        int found = 0;
        for (PlacedWord w : words) {
            if (w.found) {
                found++;
            }
        }
        progressLabel.setText(found + " / " + words.size() + " found");
        pointsLabel.setText("+" + points + " pts");
    }

    /* ================= Selection & matching ================= */

    private static long key(int r, int c) {
        return ((long) r << 32) | (c & 0xffffffffL);
    }

    private boolean isFoundCell(Point p) {
        return foundCells.contains(key(p.x, p.y));
    }

    private boolean isFoundCell(int r, int c) {
        return foundCells.contains(key(r, c));
    }

    private void beginSelection(Point cell) {
        if (isFoundCell(cell)) {
            return;
        }
        dragging = true;
        selectionStart = cell;
        selection.clear();
        selection.add(cell);
        SoundUtil.playLetterSelect(0);
        board.repaint();
    }

    private void extendSelection(Point target) {
        if (!dragging || selectionStart == null || target.equals(selectionStart)) {
            if (dragging && selectionStart != null) {
                selection.clear();
                selection.add(selectionStart);
                board.repaint();
            }
            return;
        }
        Point start = selectionStart;
        int dr = target.x - start.x;
        int dc = target.y - start.y;

        int dirR;
        int dirC;
        int ar = Math.abs(dr);
        int ac = Math.abs(dc);
        if (ar > ac) {
            dirR = Integer.signum(dr);
            dirC = 0;
        } else if (ac > ar) {
            dirR = 0;
            dirC = Integer.signum(dc);
        } else {
            dirR = Integer.signum(dr);
            dirC = Integer.signum(dc);
        }

        int steps = Math.max(ar, ac);
        if (start.x + dirR * steps < 0 || start.x + dirR * steps >= size
                || start.y + dirC * steps < 0 || start.y + dirC * steps >= size) {
            int stepsR = dirR > 0 ? size - 1 - start.x : dirR < 0 ? start.x : Integer.MAX_VALUE;
            int stepsC = dirC > 0 ? size - 1 - start.y : dirC < 0 ? start.y : Integer.MAX_VALUE;
            steps = Math.min(steps, Math.min(stepsR, stepsC));
            if (steps == Integer.MAX_VALUE) {
                steps = 0;
            }
        }

        int oldSize = selection.size();
        selection.clear();
        for (int i = 0; i <= steps; i++) {
            selection.add(new Point(start.x + dirR * i, start.y + dirC * i));
        }
        if (selection.size() != oldSize) {
            SoundUtil.playLetterSelect(selection.size() - 1);
        }
        board.repaint();
    }

    private void endSelection() {
        if (!dragging) {
            return;
        }
        dragging = false;
        selectionStart = null;
        checkWordMatch();
        selection.clear();
        board.repaint();
    }

    private void checkWordMatch() {
        if (selection.size() < 2) {
            return;
        }
        for (PlacedWord w : words) {
            if (w.found || w.cells.size() != selection.size()) {
                continue;
            }
            if (cellsMatch(selection, w.cells) || cellsMatch(selection, reversed(w.cells))) {
                markWordFound(w);
                return;
            }
        }
    }

    private boolean cellsMatch(List<Point> a, List<Point> b) {
        if (a.size() != b.size()) {
            return false;
        }
        for (int i = 0; i < a.size(); i++) {
            if (!a.get(i).equals(b.get(i))) {
                return false;
            }
        }
        return true;
    }

    private List<Point> reversed(List<Point> list) {
        List<Point> copy = new ArrayList<>(list);
        java.util.Collections.reverse(copy);
        return copy;
    }

    private void markWordFound(PlacedWord w) {
        w.found = true;
        for (Point p : w.cells) {
            foundCells.add(key(p.x, p.y));
        }
        int earned = w.word.length() * POINTS_PER_LETTER;
        points += earned;
        SoundUtil.playCorrect();
        feedbackLabel.setText("Found " + w.word + "! +" + earned + " points");
        feedbackLabel.setForeground(UITheme.GREEN);
        renderWordList();
        updateMeta();
        board.invalidateCache();

        boolean allFound = true;
        for (PlacedWord word : words) {
            if (!word.found) {
                allFound = false;
                break;
            }
        }
        if (allFound) {
            points += COMPLETION_BONUS;
            completeText.setText("You found all " + words.size() + " words and earned "
                    + points + " points.\n(Grid: " + size + "x" + size + ", " + difficulty.label + ")");
            completeText.setForeground(UITheme.GOLD);
            SwingUtilities.invokeLater(this::showCompletePanel);
            syncCompletionToBackend();
        }
    }

    /* ================= Helpers ================= */

    private void syncCompletionToBackend() {
        int userId = dashboard.getUserId();
        if (userId <= 0 || points <= 0) {
            return;
        }
        new SwingWorker<Integer, Void>() {
            @Override
            protected Integer doInBackground() {
                try {
                    return api.completePuzzle(userId, points);
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

    private int randInt(int min, int max) {
        return min + random.nextInt(max - min + 1);
    }

    private void shuffle(List<String> list) {
        for (int i = list.size() - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            String tmp = list.get(i);
            list.set(i, list.get(j));
            list.set(j, tmp);
        }
    }

    private static class PlacedWord {
        final String word;
        final List<Point> cells;
        boolean found = false;

        PlacedWord(String word, List<Point> cells) {
            this.word = word;
            this.cells = cells;
        }
    }

    private class DifficultyCard extends JButton {

        private final Difficulty difficulty;
        private final String subtitle;
        private final String icon;
        private final Color accent;
        private final UITheme.SmoothHover hover;
        private boolean pressed;
        private BufferedImage chrome;
        private int chromeW = -1;
        private int chromeH = -1;

        DifficultyCard(Difficulty difficulty, String subtitle, Color accent, String icon) {
            this.difficulty = difficulty;
            this.subtitle = subtitle;
            this.accent = accent;
            this.icon = icon;
            this.hover = new UITheme.SmoothHover(this, this::repaint);
            setContentAreaFilled(false);
            setOpaque(false);
            setFocusPainted(false);
            setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));
            setPreferredSize(new Dimension(580, 100));
            setMaximumSize(new Dimension(580, 100));
            setAlignmentX(Component.CENTER_ALIGNMENT);
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) { hover.enter(); }

                @Override
                public void mouseExited(MouseEvent e) { hover.exit(); }

                @Override
                public void mousePressed(MouseEvent e) { pressed = true; repaint(); }

                @Override
                public void mouseReleased(MouseEvent e) { pressed = false; repaint(); }
            });
            addActionListener(e -> startPuzzle(difficulty));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            UITheme.paintQuality(g2);
            int w = getWidth();
            int h = getHeight();
            float hv = hover.value();
            int radius = Math.min(28, UITheme.capsule(h));

            if (chrome == null || chromeW != w || chromeH != h) {
                chrome = new BufferedImage(Math.max(1, w), Math.max(1, h), BufferedImage.TYPE_INT_ARGB);
                Graphics2D cg = chrome.createGraphics();
                UITheme.quality(cg);
                UITheme.softShadow(cg, 0, 3, w, h - 3, radius);
                cg.setColor(UITheme.PANEL_BG);
                cg.fillRoundRect(0, 0, w, h, radius, radius);
                cg.setColor(UITheme.PANEL_BORDER);
                cg.setStroke(new BasicStroke(1.2f));
                cg.drawRoundRect(1, 1, w - 3, h - 3, radius, radius);
                cg.dispose();
                chromeW = w;
                chromeH = h;
            }
            g2.drawImage(chrome, 0, 0, null);

            if (hv > 0.02f) {
                int glowR = Math.round(Math.max(w, h) * 0.85f);
                g2.setPaint(new RadialGradientPaint(
                        new Point2D.Float(w * 0.3f, h / 2f), glowR,
                        new float[]{0f, 0.6f, 1f},
                        new Color[]{
                                new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), Math.round(50 * hv)),
                                new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), Math.round(18 * hv)),
                                new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 0)}));
                g2.fillRoundRect(0, 0, w, h, radius, radius);
            }

            if (pressed) {
                g2.setColor(new Color(0, 0, 0, 45));
                g2.fillRoundRect(0, 0, w, h, radius, radius);
            }

            if (hv > 0.02f) {
                g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(),
                        Math.round(120 * hv)));
                g2.setStroke(new BasicStroke(1.6f + 0.8f * hv));
                g2.drawRoundRect(1, 1, w - 3, h - 3, radius, radius);
            }

            int lift = Math.round(3f * hv);
            int yOff = -lift;

            int badgeSize = 44;
            int badgeX = 18;
            int badgeY = (h - badgeSize) / 2 + yOff;
            g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 35));
            g2.fillOval(badgeX, badgeY, badgeSize, badgeSize);
            g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 100));
            g2.setStroke(new BasicStroke(1.4f));
            g2.drawOval(badgeX, badgeY, badgeSize, badgeSize);

            g2.setFont(UITheme.displayFont(Font.BOLD, 22));
            FontMetrics im = g2.getFontMetrics();
            g2.setColor(isEnabled() ? accent : new Color(0x8fa2bd));
            g2.drawString(icon, badgeX + (badgeSize - im.stringWidth(icon)) / 2,
                    badgeY + (badgeSize - im.getHeight()) / 2 + im.getAscent());

            int textX = badgeX + badgeSize + 20;
            int availW = w - textX - 20;

            int tf = 22;
            int sf = 14;
            g2.setFont(UITheme.displayFont(Font.BOLD, tf));
            FontMetrics tm = g2.getFontMetrics();
            String diffLabel = difficulty.label.toUpperCase();
            int titleY = h / 2 - 6 + yOff;
            g2.setColor(isEnabled() ? UITheme.TEXT : new Color(0x8fa2bd));
            String clippedTitle = diffLabel;
            if (tm.stringWidth(clippedTitle) > availW) {
                while (clippedTitle.length() > 1 && tm.stringWidth(clippedTitle + "...") > availW) {
                    clippedTitle = clippedTitle.substring(0, clippedTitle.length() - 1);
                }
                clippedTitle = clippedTitle + "...";
            }
            g2.drawString(clippedTitle, textX, titleY);

            g2.setFont(UITheme.bodyFont(Font.PLAIN, sf));
            FontMetrics sm = g2.getFontMetrics();
            g2.setColor(isEnabled() ? UITheme.TEXT_MUTED : new Color(0x8fa2bd));
            String clippedSub = subtitle;
            if (sm.stringWidth(clippedSub) > availW) {
                while (clippedSub.length() > 1 && sm.stringWidth(clippedSub + "...") > availW) {
                    clippedSub = clippedSub.substring(0, clippedSub.length() - 1);
                }
                clippedSub = clippedSub + "...";
            }
            g2.drawString(clippedSub, textX, titleY + tm.getDescent() + 6 + sm.getAscent());

            int tagW = 70;
            int tagH = 22;
            int tagX = w - tagW - 18;
            int tagY = (h - tagH) / 2 + yOff;
            g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 40));
            g2.fillRoundRect(tagX, tagY, tagW, tagH, tagH / 2, tagH / 2);
            g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 110));
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(tagX, tagY, tagW, tagH, tagH / 2, tagH / 2);

            g2.setFont(UITheme.displayFont(Font.BOLD, 10));
            FontMetrics tfm = g2.getFontMetrics();
            String gridInfo = difficulty.sizeMin + "x" + difficulty.sizeMin;
            g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue()));
            g2.drawString(gridInfo, tagX + (tagW - tfm.stringWidth(gridInfo)) / 2,
                    tagY + (tagH - tfm.getHeight()) / 2 + tfm.getAscent());

            g2.dispose();
        }
    }

    private class GridBoard extends JPanel {

        private static final Color CELL_BG = new Color(255, 255, 255, 22);
        private static final Color CELL_BORDER = new Color(255, 255, 255, 42);
        private static final Color LEAD_BG = new Color(255, 201, 60, 85);
        private static final Color SELECT_BG = new Color(32, 211, 194, 70);
        private static final Color HOVER_BG = new Color(32, 211, 194, 45);

        private BufferedImage base;
        private int baseCell = -1;

        GridBoard() {
            setOpaque(false);
            MouseAdapter handler = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    Point cell = cellAt(e.getPoint());
                    if (cell != null) {
                        beginSelection(cell);
                    }
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    Point cell = cellAt(e.getPoint());
                    if (cell != null) {
                        extendSelection(cell);
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    endSelection();
                }

                @Override
                public void mouseMoved(MouseEvent e) {
                    Point cell = cellAt(e.getPoint());
                    if (!java.util.Objects.equals(cell, hoverCell)) {
                        hoverCell = cell;
                        repaint();
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    if (hoverCell != null) {
                        hoverCell = null;
                        repaint();
                    }
                }
            };
            addMouseListener(handler);
            addMouseMotionListener(handler);
        }

        void invalidateCache() {
            base = null;
            baseCell = -1;
            repaint();
        }

        @Override
        public Dimension getPreferredSize() {
            int side = PAD * 2 + size * CELL;
            return new Dimension(side, side);
        }

        private int cell() {
            int w = getWidth();
            int h = getHeight();
            if (w <= 0 || h <= 0) {
                return CELL;
            }
            int cw = (w - 2 * PAD) / size;
            int ch = (h - 2 * PAD) / size;
            return Math.max(CELL, Math.min(cw, ch));
        }

        private int pad() {
            return Math.max(PAD, (int) Math.round(cell() * PAD / (double) CELL));
        }

        private int gridX() {
            return Math.max(0, (getWidth() - (pad() * 2 + size * cell())) / 2);
        }

        private int gridY() {
            return Math.max(0, (getHeight() - (pad() * 2 + size * cell())) / 2);
        }

        private Point cellAt(Point p) {
            int cell = cell();
            int pad = pad();
            int x0 = gridX();
            int y0 = gridY();
            int c = (p.x - x0 - pad) / cell;
            int r = (p.y - y0 - pad) / cell;
            if (r < 0 || r >= size || c < 0 || c >= size) {
                return null;
            }
            return new Point(r, c);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int cell = cell();
            int pad = pad();
            int x0 = gridX();
            int y0 = gridY();
            ensureBase(cell, pad, x0, y0);

            Graphics2D g2 = (Graphics2D) g.create();
            UITheme.paintQuality(g2);
            g2.drawImage(base, 0, 0, null);

            if (!selection.isEmpty()) {
                Point lead = selection.get(0);
                g2.setColor(LEAD_BG);
                fillCell(g2, lead, cell, pad, x0, y0);
                for (int i = 1; i < selection.size(); i++) {
                    g2.setColor(SELECT_BG);
                    fillCell(g2, selection.get(i), cell, pad, x0, y0);
                }
            }
            if (hoverCell != null && !selection.contains(hoverCell) && !isFoundCell(hoverCell)) {
                g2.setColor(HOVER_BG);
                fillCell(g2, hoverCell, cell, pad, x0, y0);
            }
            g2.dispose();
        }

        private void fillCell(Graphics2D g2, Point p, int cell, int pad, int x0, int y0) {
            int x = x0 + pad + p.y * cell;
            int y = y0 + pad + p.x * cell;
            g2.fill(new RoundRectangle2D.Float(x + 1, y + 1, cell - 2, cell - 2, 9, 9));
        }

        private void ensureBase(int cell, int pad, int x0, int y0) {
            int w = getWidth();
            int h = getHeight();
            if (base != null && base.getWidth() == w && base.getHeight() == h && baseCell == cell) {
                return;
            }
            base = new BufferedImage(Math.max(1, w), Math.max(1, h), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = base.createGraphics();
            UITheme.quality(g2);
            int fontSize = Math.max(15, (int) Math.round(cell * 15 / (double) CELL));
            g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, fontSize));

            for (int r = 0; r < size; r++) {
                for (int c = 0; c < size; c++) {
                    int x = x0 + pad + c * cell;
                    int y = y0 + pad + r * cell;
                    boolean found = isFoundCell(r, c);
                    g2.setColor(found ? new Color(61, 220, 151, 70) : CELL_BG);
                    g2.fill(new RoundRectangle2D.Float(x + 1, y + 1, cell - 2, cell - 2, 9, 9));
                    g2.setColor(found ? UITheme.GREEN : CELL_BORDER);
                    g2.setStroke(new java.awt.BasicStroke(1.2f));
                    g2.draw(new RoundRectangle2D.Float(x + 1, y + 1, cell - 2, cell - 2, 9, 9));

                    g2.setColor(UITheme.TEXT);
                    String letter = String.valueOf(grid[r][c]);
                    java.awt.FontMetrics fm = g2.getFontMetrics();
                    int tx = x + (cell - fm.stringWidth(letter)) / 2;
                    int ty = y + (cell - fm.getHeight()) / 2 + fm.getAscent();
                    g2.drawString(letter, tx, ty);
                }
            }
            g2.dispose();
            baseCell = cell;
        }
    }

    private static class WrapLayout extends FlowLayout {
        WrapLayout(int align, int hgap, int vgap) {
            super(align, hgap, vgap);
        }

        @Override
        public Dimension preferredLayoutSize(Container target) {
            return layoutSize(target, true);
        }

        @Override
        public Dimension minimumLayoutSize(Container target) {
            Dimension minimum = layoutSize(target, false);
            minimum.width -= (getHgap() + 1);
            return minimum;
        }

        private Dimension layoutSize(Container target, boolean preferred) {
            synchronized (target.getTreeLock()) {
                int targetWidth = target.getSize().width;
                Container container = target;

                if (targetWidth == 0) {
                    targetWidth = Integer.MAX_VALUE;
                }

                int hgap = getHgap();
                int vgap = getVgap();
                Insets insets = target.getInsets();
                int horizontalInsetsAndGap = insets.left + insets.right + (hgap * 2);
                int maxWidth = targetWidth - horizontalInsetsAndGap;

                Dimension dim = new Dimension(0, 0);
                int rowWidth = 0;
                int rowHeight = 0;

                int nmembers = target.getComponentCount();
                for (int i = 0; i < nmembers; i++) {
                    Component m = target.getComponent(i);
                    if (m.isVisible()) {
                        Dimension d = preferred ? m.getPreferredSize() : m.getMinimumSize();
                        if (rowWidth + d.width > maxWidth) {
                            addRow(dim, rowWidth, rowHeight);
                            rowWidth = 0;
                            rowHeight = 0;
                        }
                        if (rowWidth != 0) {
                            rowWidth += hgap;
                        }
                        rowWidth += d.width;
                        rowHeight = Math.max(rowHeight, d.height);
                    }
                }
                addRow(dim, rowWidth, rowHeight);

                dim.width += horizontalInsetsAndGap;
                dim.height += insets.top + insets.bottom + vgap * 2;

                JScrollPane scrollPane = (JScrollPane) SwingUtilities.getAncestorOfClass(JScrollPane.class, target);
                if (scrollPane != null && target.isValid()) {
                    dim.width = Math.min(dim.width, scrollPane.getViewport().getSize().width);
                }

                return dim;
            }
        }

        private void addRow(Dimension dim, int rowWidth, int rowHeight) {
            dim.width = Math.max(dim.width, rowWidth);
            if (dim.height > 0) {
                dim.height += getVgap();
            }
            dim.height += rowHeight;
        }
    }
}
