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
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
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
import java.awt.LinearGradientPaint;
import java.awt.Point;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class WordsOfWondersGameScreen extends JPanel {

    private static final String VIEW_DIFF = "diff";
    private static final String VIEW_GAME = "game";
    private static final String VIEW_DONE = "done";

    private static final int PTS_WORD = 20;
    private static final int HINT_COST = 15;
    private static final int HAMMER_COST = 30;
    private static final int CELL = 34;
    private static final int CG = 4;

    private static final String[][] PUZZLES = {
            {"EARTH", "HEART", "HATER", "TEAR", "HEAR", "RATE", "HARE", "TARE", "HART"},
            {"GARDEN", "DANGER", "RANGED", "GRADE", "RANGE", "GRAND", "READ", "DEAR", "AGED"},
            {"LISTEN", "SILENT", "TINSEL", "INLET", "LENT", "SENT", "SITE", "NEST", "LINE"},
            {"CASTLE", "SCALE", "LACES", "LATE", "SALE", "SEAL", "LACE", "CAST", "ACES"},
            {"PLANET", "PLANT", "LEAPT", "PETAL", "PLEAT", "PLATE", "PLANE", "LANE", "LEAP"},
            {"GUITAR", "GRIT", "GAIT", "TRIG", "RUG", "GUT", "TAG", "RAG", "AIR"},
            {"WONDER", "DROWN", "ROWED", "OWNER", "WORN", "WORD", "NODE", "DREW", "RODE"},
            {"FROZEN", "FROZE", "ZONE", "ZERO", "FORE", "FERN", "FOE", "ONE", "ORE"},
    };

    private static final String[][] BONUS_WORDS = {
            {"ART", "EAR", "EAT", "ERA", "HAT", "HER", "RAT", "TAR", "TEA", "THE"},
            {"AGE", "AND", "ARE", "DEN", "EAR", "END", "ERA", "RAG", "RAN", "RED"},
            {"ITS", "LET", "LIE", "LIT", "NET", "NIT", "SET", "SIN", "SIT", "TEN", "TIE", "TIN"},
            {"ACT", "ALE", "ALT", "ATE", "CAT", "EAT", "LET", "SAT", "SEA", "SET", "TEA"},
            {"ANT", "APE", "APT", "EAT", "LAP", "NAP", "PAL", "PAN", "PAT", "PEA", "PEN", "PET", "TAP", "TEN"},
            {"ART", "RUG", "TUG", "RIG", "TAR", "RAT"},
            {"DOW", "END", "NEW", "NOD", "NOW", "ONE", "ORE", "OWE", "OWN", "RED", "ROE", "ROW", "WED", "WON"},
            {"FOR", "NOR", "REF", "FEZ"}
    };

    private final Dashboard dashboard;
    private final QuizApiClient api;
    private final Random rnd = new Random();

    private String[] data;
    private List<String> targetWords;
    private List<String> bonusList;
    private final Set<String> foundWords = new HashSet<>();
    private final Set<String> foundBonus = new HashSet<>();
    private final Map<Integer, Set<Integer>> hintedMap = new HashMap<>();
    private List<Character> circleLetters;
    private final List<Integer> selection = new ArrayList<>();
    private int points;
    private boolean gameActive;

    private final CardLayout cards;
    private final JPanel content;
    private CrosswordGrid xGrid;
    private LetterCircle lCircle;
    private JLabel statusLbl, ptsLbl, feedLbl, curWordLbl;
    private UITheme.ProgressBar pBar;
    private JButton hintBtn, hammerBtn, bonusBtn;
    private JPanel donePanel;
    private JLabel doneText;
    private UITheme.Confetti confetti;
    private javax.swing.Timer feedTimer;

    public WordsOfWondersGameScreen(Dashboard dashboard) {
        super(new BorderLayout());
        this.dashboard = dashboard;
        this.api = new QuizApiClient();
        setOpaque(false);

        this.cards = new CardLayout();
        this.content = new JPanel(cards);
        content.setOpaque(false);

        content.add(buildDiffPanel(), VIEW_DIFF);
        content.add(buildGamePanel(), VIEW_GAME);
        donePanel = buildDonePanel();
        content.add(donePanel, VIEW_DONE);
        confetti = new UITheme.Confetti(donePanel);

        JPanel viewCard = UITheme.card(new BorderLayout());
        viewCard.setBorder(BorderFactory.createEmptyBorder(
                UITheme.PAD_CARD_Y, UITheme.PAD_CARD_X, UITheme.PAD_CARD_Y, UITheme.PAD_CARD_X));
        UIUtil.fixedSize(viewCard, 1020, 700);
        viewCard.add(buildHeader(), BorderLayout.NORTH);
        viewCard.add(content, BorderLayout.CENTER);

        JPanel root = UITheme.screenPage(viewCard);
        UITheme.autoScale(root, 1100, 790, 0.85, 1.5);
        add(root, BorderLayout.CENTER);

        cards.show(content, VIEW_DIFF);
    }

    private JPanel buildHeader() {
        JButton back = UITheme.ghostButton("\u2190 Back to Dashboard", UITheme.TEXT_MUTED);
        UIUtil.fixedSize(back, 200, UITheme.BTN_H);
        back.addActionListener(e -> dashboard.showDashboard());
        return UITheme.screenHeader(back, "Words of Wonders", 30);
    }

    private JPanel buildDiffPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        panel.add(UITheme.sectionTitle("Choose your puzzle", 26), BorderLayout.NORTH);

        JPanel buttons = new JPanel(new GridLayout(3, 1, 0, 20));
        buttons.setOpaque(false);
        buttons.setBorder(BorderFactory.createEmptyBorder(28, 34, 0, 34));

        buttons.add(diffTile("Easy", "5 letters \u2022 9 words to find", UITheme.SUCCESS, 0));
        buttons.add(diffTile("Medium", "6 letters \u2022 9 words to find", UITheme.WARNING, 1));
        buttons.add(diffTile("Hard", "6 letters \u2022 9 words to find", UITheme.DANGER, 5));

        JPanel wrap = UIUtil.centered(buttons);
        wrap.setOpaque(false);
        panel.add(wrap, BorderLayout.CENTER);
        return panel;
    }

    private JButton diffTile(String name, String desc, Color accent, int puzzleIdx) {
        UITheme.TileButton tile = new UITheme.TileButton(name, desc, accent);
        tile.setDark(true);
        tile.setSubtitleColor(new Color(0xd0e8f5));
        UIUtil.fixedSize(tile, 580, 100);
        tile.addActionListener(e -> startGame(puzzleIdx));
        return tile;
    }

    private JPanel buildGamePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);

        JPanel meta = new JPanel();
        meta.setOpaque(false);
        meta.setLayout(new BoxLayout(meta, BoxLayout.Y_AXIS));

        JPanel metaRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 4));
        metaRow.setOpaque(false);
        statusLbl = UITheme.badge("0 / 9 words", UITheme.WORDS_ACCENT[0]);
        ptsLbl = UITheme.badge("Points: 0", UITheme.WARNING);
        metaRow.add(statusLbl);
        metaRow.add(ptsLbl);
        metaRow.setAlignmentX(Component.CENTER_ALIGNMENT);
        meta.add(metaRow);

        pBar = new UITheme.ProgressBar();
        pBar.setPreferredSize(new Dimension(500, 16));
        pBar.setMaximumSize(new Dimension(500, 16));
        JPanel barWrap = UIUtil.centered(pBar);
        barWrap.setOpaque(false);
        barWrap.setAlignmentX(Component.CENTER_ALIGNMENT);
        meta.add(barWrap);

        curWordLbl = new JLabel(" ", SwingConstants.CENTER);
        curWordLbl.setFont(UITheme.displayFont(Font.BOLD, 22));
        curWordLbl.setForeground(UITheme.WORDS_ACCENT[0]);
        curWordLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel wordWrap = UIUtil.centered(curWordLbl);
        wordWrap.setOpaque(false);
        wordWrap.setAlignmentX(Component.CENTER_ALIGNMENT);
        meta.add(wordWrap);

        panel.add(meta, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;

        xGrid = new CrosswordGrid(new ArrayList<>(), foundWords, hintedMap);
        gbc.gridx = 0;
        gbc.weightx = 0.42;
        center.add(xGrid, gbc);

        lCircle = new LetterCircle(new ArrayList<>(), selection);
        gbc.gridx = 1;
        gbc.weightx = 0.58;
        center.add(lCircle, gbc);

        panel.add(center, BorderLayout.CENTER);

        feedLbl = new JLabel(" ", SwingConstants.CENTER);
        feedLbl.setFont(UITheme.bodyFont(Font.BOLD, UITheme.FONT_BODY));
        feedLbl.setForeground(UITheme.TEXT_MUTED);

        JPanel south = new JPanel();
        south.setOpaque(false);
        south.setLayout(new BoxLayout(south, BoxLayout.Y_AXIS));

        JPanel feedWrap = UIUtil.centered(feedLbl);
        feedWrap.setOpaque(false);
        feedWrap.setAlignmentX(Component.CENTER_ALIGNMENT);
        south.add(feedWrap);
        south.add(Box.createVerticalStrut(4));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 0));
        btnRow.setOpaque(false);

        hintBtn = UITheme.accentButton("\uD83D\uDCA1 Hint (-" + HINT_COST + ")", UITheme.WARNING);
        UIUtil.fixedSize(hintBtn, 180, UITheme.BTN_H_SM);
        hintBtn.addActionListener(e -> useHint());
        btnRow.add(hintBtn);

        hammerBtn = UITheme.accentButton("\uD83D\uDD28 Reveal (-" + HAMMER_COST + ")", UITheme.DANGER);
        UIUtil.fixedSize(hammerBtn, 200, UITheme.BTN_H_SM);
        hammerBtn.addActionListener(e -> useHammer());
        btnRow.add(hammerBtn);

        JButton shuffleBtn = UITheme.ghostButton("Shuffle", UITheme.BRAND_400);
        UIUtil.fixedSize(shuffleBtn, 140, UITheme.BTN_H_SM);
        shuffleBtn.addActionListener(e -> shuffleLetters());
        btnRow.add(shuffleBtn);

        JButton clearBtn = UITheme.ghostButton("Clear", UITheme.TEXT_MUTED);
        UIUtil.fixedSize(clearBtn, 120, UITheme.BTN_H_SM);
        clearBtn.addActionListener(e -> clearSelection());
        btnRow.add(clearBtn);
        
        bonusBtn = UITheme.ghostButton("\uD83C\uDF81 0", UITheme.WARNING);
        bonusBtn.setToolTipText("Bonus words found");
        UIUtil.fixedSize(bonusBtn, 80, UITheme.BTN_H_SM);
        bonusBtn.setEnabled(false);
        btnRow.add(bonusBtn);

        JButton newBtn = UITheme.secondaryButton("New Game");
        UIUtil.fixedSize(newBtn, 140, UITheme.BTN_H_SM);
        newBtn.addActionListener(e -> showDiff());
        btnRow.add(newBtn);

        south.add(btnRow);
        panel.add(south, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel buildDonePanel() {
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

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        UITheme.GradientTextLabel title =
                new UITheme.GradientTextLabel("Puzzle Complete!", 36, UITheme.WARNING, UITheme.WORDS_ACCENT[0]);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(title);
        center.add(Box.createVerticalStrut(8));

        JLabel sub = UITheme.subtitle("Amazing word-finding skills!");
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(sub);

        doneText = new JLabel(" ", SwingConstants.CENTER);
        doneText.setFont(UITheme.bodyFont(Font.BOLD, UITheme.FONT_CARD_TITLE));
        doneText.setForeground(UITheme.WARNING);
        doneText.setAlignmentX(Component.CENTER_ALIGNMENT);
        doneText.setBorder(BorderFactory.createEmptyBorder(16, 0, 0, 0));
        center.add(doneText);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, UITheme.GAP_ELEMENT, 0));
        buttons.setOpaque(false);
        buttons.setBorder(BorderFactory.createEmptyBorder(28, 0, 0, 0));
        JButton again = UITheme.primaryButton("Play Again");
        UIUtil.fixedSize(again, 210, UITheme.BTN_H);
        again.addActionListener(e -> startGame(rnd.nextInt(PUZZLES.length)));
        buttons.add(again);
        JButton diffBtn = UITheme.secondaryButton("Change Difficulty");
        UIUtil.fixedSize(diffBtn, 210, UITheme.BTN_H);
        diffBtn.addActionListener(e -> showDiff());
        buttons.add(diffBtn);
        JButton dashBtn = UITheme.ghostButton("Back to Dashboard", UITheme.TEXT_MUTED);
        UIUtil.fixedSize(dashBtn, 210, UITheme.BTN_H);
        dashBtn.addActionListener(e -> dashboard.showDashboard());
        buttons.add(dashBtn);
        center.add(buttons);

        panel.add(center, BorderLayout.CENTER);
        return panel;
    }

    private void startGame(int puzzleIdx) {
        data = PUZZLES[puzzleIdx];
        bonusList = new ArrayList<>();
        Collections.addAll(bonusList, BONUS_WORDS[puzzleIdx]);
        targetWords = new ArrayList<>();
        for (int i = 0; i < data.length; i++) {
            targetWords.add(data[i]);
        }
        foundWords.clear();
        foundBonus.clear();
        hintedMap.clear();
        selection.clear();
        points = 0;
        gameActive = true;

        circleLetters = new ArrayList<>();
        for (char c : data[0].toCharArray()) {
            circleLetters.add(c);
        }
        Collections.shuffle(circleLetters, rnd);

        xGrid = new CrosswordGrid(targetWords, foundWords, hintedMap);
        lCircle = new LetterCircle(circleLetters, selection);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;

        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);
        gbc.gridx = 0;
        gbc.weightx = 0.42;
        center.add(xGrid, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.58;
        center.add(lCircle, gbc);

        JPanel gamePanel = (JPanel) content.getComponent(1);
        java.awt.Component oldCenter = gamePanel.getComponent(1);
        gamePanel.remove(oldCenter);
        gamePanel.add(center, BorderLayout.CENTER);

        updateMeta();
        feedLbl.setText(" ");
        curWordLbl.setText(" ");
        gamePanel.revalidate();
        gamePanel.repaint();

        cards.show(content, VIEW_GAME);
    }

    private void shuffleLetters() {
        if (!gameActive) return;
        Collections.shuffle(circleLetters, rnd);
        SoundUtil.playShuffle();
        clearSelection();
    }

    private void onNodeClick(int idx) {
        if (!gameActive || idx < 0 || idx >= circleLetters.size()) return;

        if (selection.contains(idx)) {
            if (selection.size() >= 2 && selection.get(selection.size() - 2) == idx) {
                selection.remove(selection.size() - 1);
                String word = buildWord();
                curWordLbl.setText(word.isEmpty() ? " " : word);
                SoundUtil.playLetterSelect(selection.size() - 1);
                lCircle.repaint();
                return;
            }
            return;
        }

        selection.add(idx);
        SoundUtil.playLetterSelect(selection.size() - 1);
        String word = buildWord();
        curWordLbl.setText(word);

        for (String target : targetWords) {
            if (!foundWords.contains(target) && target.equals(word)) {
                onWordFound(target);
                return;
            }
        }
        if (bonusList.contains(word) && !foundBonus.contains(word)) {
            onBonusFound(word);
            return;
        }
        lCircle.repaint();
    }

    private void onDragSubmit() {
        if (!gameActive || selection.isEmpty()) return;
        String word = buildWord();
        curWordLbl.setText(word);

        for (String target : targetWords) {
            if (!foundWords.contains(target) && target.equals(word)) {
                onWordFound(target);
                return;
            }
        }

        if (foundWords.contains(word)) {
            SoundUtil.playError();
            showFeed("Already found: " + word, UITheme.WARNING);
        } else if (foundBonus.contains(word)) {
            SoundUtil.playError();
            showFeed("Bonus already found: " + word, UITheme.WARNING);
        } else if (bonusList.contains(word)) {
            onBonusFound(word);
        } else if (word.length() >= 2) {
            SoundUtil.playError();
            showFeed("Not in puzzle: " + word, UITheme.DANGER);
        }
        clearSelection();
    }

    private void clearSelection() {
        selection.clear();
        curWordLbl.setText(" ");
        if (lCircle != null) {
            lCircle.setDragPoint(null);
            lCircle.repaint();
        }
    }

    private void onWordFound(String word) {
        foundWords.add(word);
        points += PTS_WORD;
        SoundUtil.playCorrect();
        clearSelection();
        updateMeta();
        xGrid.repaint();
        showFeed("Found: " + word + "!  +" + PTS_WORD + " pts", UITheme.SUCCESS);

        if (foundWords.size() >= targetWords.size()) {
            gameActive = false;
            SoundUtil.playVictory();
            javax.swing.Timer t = new javax.swing.Timer(800, e -> showDone());
            t.setRepeats(false);
            t.start();
        }
    }

    private void onBonusFound(String word) {
        foundBonus.add(word);
        points += PTS_WORD / 2; // Bonus words give half points
        SoundUtil.playCorrect();
        clearSelection();
        updateMeta();
        showFeed("Bonus! " + word + "  +" + (PTS_WORD / 2) + " pts", UITheme.WARNING);
    }

    private void useHint() {
        if (!gameActive || points < HINT_COST) {
            SoundUtil.playError();
            showFeed("Not enough points!", UITheme.DANGER);
            return;
        }
        List<String> unfound = new ArrayList<>();
        for (String w : targetWords) {
            if (!foundWords.contains(w)) unfound.add(w);
        }
        if (unfound.isEmpty()) return;

        String pick = unfound.get(rnd.nextInt(unfound.size()));
        int wIdx = targetWords.indexOf(pick);
        Set<Integer> hinted = hintedMap.computeIfAbsent(wIdx, k -> new HashSet<>());

        List<Integer> unrevealed = new ArrayList<>();
        for (int i = 0; i < pick.length(); i++) {
            if (!hinted.contains(i)) unrevealed.add(i);
        }
        if (unrevealed.isEmpty()) return;

        hinted.add(unrevealed.get(rnd.nextInt(unrevealed.size())));
        points -= HINT_COST;
        SoundUtil.playHint();
        updateMeta();
        xGrid.repaint();
        showFeed("Letter revealed in #" + (wIdx + 1) + "!  -" + HINT_COST + " pts", UITheme.WARNING);
    }

    private void useHammer() {
        if (!gameActive || points < HAMMER_COST) {
            SoundUtil.playError();
            showFeed("Not enough points!", UITheme.DANGER);
            return;
        }
        List<String> unfound = new ArrayList<>();
        for (String w : targetWords) {
            if (!foundWords.contains(w)) unfound.add(w);
        }
        if (unfound.isEmpty()) return;

        String pick = unfound.get(rnd.nextInt(unfound.size()));
        foundWords.add(pick);
        points += PTS_WORD - HAMMER_COST;
        SoundUtil.playHint();
        updateMeta();
        xGrid.repaint();
        showFeed("Revealed: " + pick + "!  net " + (PTS_WORD - HAMMER_COST) + " pts", UITheme.DANGER);

        if (foundWords.size() >= targetWords.size()) {
            gameActive = false;
            SoundUtil.playVictory();
            javax.swing.Timer t = new javax.swing.Timer(800, e -> showDone());
            t.setRepeats(false);
            t.start();
        }
    }

    private void updateMeta() {
        statusLbl.setText(foundWords.size() + " / " + targetWords.size() + " words");
        ptsLbl.setText("Points: " + points);
        pBar.setProgress((double) foundWords.size() / Math.max(1, targetWords.size()));
        hintBtn.setEnabled(gameActive && points >= HINT_COST);
        hammerBtn.setEnabled(gameActive && points >= HAMMER_COST);
        if (bonusBtn != null) {
            bonusBtn.setText("\uD83C\uDF81 " + foundBonus.size());
        }
    }

    private void showFeed(String text, Color color) {
        feedLbl.setText(text);
        feedLbl.setForeground(color);
        if (feedTimer != null) feedTimer.stop();
        feedTimer = new javax.swing.Timer(2000, e -> {
            feedLbl.setText(" ");
            feedTimer.stop();
        });
        feedTimer.setRepeats(false);
        feedTimer.start();
    }

    private void showDone() {
        doneText.setText("You found all " + targetWords.size() + " words and earned " + points + " points.");
        cards.show(content, VIEW_DONE);
        confetti.launch();
        syncToBackend();
    }

    private void showDiff() {
        cards.show(content, VIEW_DIFF);
    }

    private String buildWord() {
        StringBuilder sb = new StringBuilder();
        for (int i : selection) {
            sb.append(circleLetters.get(i));
        }
        return sb.toString();
    }

    private boolean isValidPrefix(String prefix) {
        for (String w : targetWords) {
            if (!foundWords.contains(w) && w.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    private void syncToBackend() {
        int userId = dashboard.getUserId();
        if (userId <= 0 || points <= 0) return;
        new SwingWorker<Integer, Void>() {
            @Override
            protected Integer doInBackground() {
                try { return api.completePuzzle(userId, points); } catch (Exception e) { return null; }
            }
            @Override
            protected void done() {
                try {
                    Integer total = get();
                    if (total != null) dashboard.updateScore(total);
                } catch (Exception ignored) {}
            }
        }.execute();
    }

    private int nodeAt(Point p, int cw, int ch) {
        Dimension natural = lCircle.getPreferredSize();
        double sx = natural.width > 0 ? cw / (double) natural.width : 1.0;
        double sy = natural.height > 0 ? ch / (double) natural.height : 1.0;
        int lx = sx > 0 ? (int) Math.round(p.x / sx) : p.x;
        int ly = sy > 0 ? (int) Math.round(p.y / sy) : p.y;

        int cx = natural.width / 2;
        int cy = natural.height / 2;
        float radius = Math.min(natural.width, natural.height) * 0.34f;
        int nr = Math.max(22, Math.round(radius * 0.22f));

        for (int i = 0; i < circleLetters.size(); i++) {
            float a = (float) (Math.PI * 2 * i / circleLetters.size() - Math.PI / 2);
            int nx = (int) (cx + radius * Math.cos(a));
            int ny = (int) (cy + radius * Math.sin(a));
            if (Math.hypot(lx - nx, ly - ny) <= nr + 12) {
                return i;
            }
        }
        return -1;
    }

    // ── Crossword Grid ───────────────────────────────────────────────

    private class CrosswordGrid extends JPanel {
        private final List<String> words;
        private final Set<String> found;
        private final Map<Integer, Set<Integer>> hinted;

        CrosswordGrid(List<String> words, Set<String> found, Map<Integer, Set<Integer>> hinted) {
            this.words = words;
            this.found = found;
            this.hinted = hinted;
            setOpaque(false);
            int maxLen = 0;
            for (String w : words) maxLen = Math.max(maxLen, w.length());
            int w = 36 + maxLen * (CELL + CG) + 16;
            int h = words.size() * (CELL + CG) + 16;
            setPreferredSize(new Dimension(Math.max(180, w), Math.max(200, h)));
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

            int startY = 8;
            Font numFont = UITheme.displayFont(Font.BOLD, 13);
            Font cellFont = UITheme.displayFont(Font.BOLD, 18);
            FontMetrics nfm = g2.getFontMetrics(numFont);
            FontMetrics cfm = g2.getFontMetrics(cellFont);

            for (int i = 0; i < words.size(); i++) {
                String word = words.get(i);
                boolean isFound = found.contains(word);
                Set<Integer> hintSet = hinted.getOrDefault(i, Collections.emptySet());
                int y = startY + i * (CELL + CG);

                g2.setFont(numFont);
                g2.setColor(isFound ? UITheme.SUCCESS : UITheme.TEXT_MUTED);
                String num = (i + 1) + ".";
                g2.drawString(num, 4, y + (CELL - nfm.getHeight()) / 2 + nfm.getAscent());

                for (int j = 0; j < word.length(); j++) {
                    int cx = 36 + j * (CELL + CG);

                    if (isFound) {
                        g2.setColor(new Color(32, 211, 194, 40));
                    } else if (hintSet.contains(j)) {
                        g2.setColor(new Color(255, 201, 60, 28));
                    } else {
                        g2.setColor(new Color(14, 26, 42, 160));
                    }
                    g2.fillRoundRect(cx, y, CELL, CELL, 8, 8);

                    if (isFound) {
                        g2.setColor(new Color(32, 211, 194, 100));
                    } else if (hintSet.contains(j)) {
                        g2.setColor(new Color(255, 201, 60, 80));
                    } else {
                        g2.setColor(new Color(255, 255, 255, 28));
                    }
                    g2.setStroke(new BasicStroke(1f));
                    g2.drawRoundRect(cx, y, CELL, CELL, 8, 8);

                    if (isFound || hintSet.contains(j)) {
                        String ch = String.valueOf(word.charAt(j));
                        g2.setFont(cellFont);
                        g2.setColor(isFound ? Color.WHITE : new Color(0xffc93c));
                        g2.drawString(ch, cx + (CELL - cfm.stringWidth(ch)) / 2,
                                y + (CELL - cfm.getHeight()) / 2 + cfm.getAscent());
                    }
                }
            }
            g2.dispose();
        }
    }

    // ── Letter Circle ────────────────────────────────────────────────

    private class LetterCircle extends JPanel {
        private List<Character> letters;
        private final List<Integer> selection;
        private int nodeR = 26;
        private Point dragPoint;
        private boolean dragging;

        LetterCircle(List<Character> letters, List<Integer> selection) {
            this.letters = letters;
            this.selection = selection;
            setOpaque(false);
            setPreferredSize(new Dimension(380, 380));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (!gameActive) return;
                    int idx = nodeAt(e.getPoint(), getWidth(), getHeight());
                    if (idx >= 0) {
                        dragging = true;
                        selection.clear();
                        selection.add(idx);
                        SoundUtil.playLetterSelect(0);
                        curWordLbl.setText(buildWord());
                        dragPoint = e.getPoint();
                        repaint();
                    } else {
                        Dimension natural = getPreferredSize();
                        double sx = natural.width > 0 ? getWidth() / (double) natural.width : 1.0;
                        double sy = natural.height > 0 ? getHeight() / (double) natural.height : 1.0;
                        int lx = sx > 0 ? (int) Math.round(e.getPoint().x / sx) : e.getPoint().x;
                        int ly = sy > 0 ? (int) Math.round(e.getPoint().y / sy) : e.getPoint().y;
                        int cx = natural.width / 2;
                        int cy = natural.height / 2;
                        float radius = Math.min(natural.width, natural.height) * 0.34f;
                        int cR = Math.max(24, Math.round(radius * 0.20f));
                        if (Math.hypot(lx - cx, ly - cy) <= cR + 4) {
                            shuffleLetters();
                        }
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    if (dragging) {
                        dragging = false;
                        dragPoint = null;
                        onDragSubmit();
                    }
                }
            });

            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    if (!dragging || !gameActive) return;
                    dragPoint = e.getPoint();
                    int idx = nodeAt(dragPoint, getWidth(), getHeight());
                    if (idx >= 0) {
                        if (selection.size() >= 2 && selection.get(selection.size() - 2) == idx) {
                            // Backtracking
                            selection.remove(selection.size() - 1);
                            SoundUtil.playLetterSelect(selection.size() - 1);
                            curWordLbl.setText(buildWord());
                        } else if (!selection.contains(idx)) {
                            selection.add(idx);
                            SoundUtil.playLetterSelect(selection.size() - 1);
                            String word = buildWord();
                            curWordLbl.setText(word);
                            for (String target : targetWords) {
                                if (!foundWords.contains(target) && target.equals(word)) {
                                    dragging = false;
                                    dragPoint = null;
                                    onWordFound(target);
                                    return;
                                }
                            }
                        }
                    }
                    repaint();
                }
            });
        }

        void setDragPoint(Point p) {
            this.dragPoint = p;
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

            int w = natural.width;
            int h = natural.height;
            int cx = w / 2;
            int cy = h / 2;
            float radius = Math.min(w, h) * 0.34f;
            nodeR = Math.max(22, Math.round(radius * 0.22f));

            g2.setPaint(new RadialGradientPaint(
                    new Point2D.Float(cx, cy), radius + nodeR + 20,
                    new float[]{0f, 1f},
                    new Color[]{new Color(14, 26, 42, 80), new Color(14, 26, 42, 20)}));
            g2.fillOval(cx - (int) radius - nodeR - 20, cy - (int) radius - nodeR - 20,
                    (int) (radius + nodeR + 20) * 2, (int) (radius + nodeR + 20) * 2);

            g2.setColor(new Color(255, 255, 255, 18));
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawOval(cx - (int) radius - nodeR - 10, cy - (int) radius - nodeR - 10,
                    (int) (radius + nodeR + 10) * 2, (int) (radius + nodeR + 10) * 2);

            if (selection.size() > 1) {
                g2.setColor(new Color(32, 211, 194, 100));
                g2.setStroke(new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int[] xs = new int[selection.size()];
                int[] ys = new int[selection.size()];
                for (int i = 0; i < selection.size(); i++) {
                    float a = (float) (Math.PI * 2 * selection.get(i) / letters.size() - Math.PI / 2);
                    xs[i] = (int) (cx + radius * Math.cos(a));
                    ys[i] = (int) (cy + radius * Math.sin(a));
                }
                g2.drawPolyline(xs, ys, xs.length);

                g2.setColor(new Color(32, 211, 194, 180));
                g2.setStroke(new BasicStroke(2f));
                g2.drawPolyline(xs, ys, xs.length);
            }

            if (dragging && dragPoint != null && !selection.isEmpty()) {
                Dimension natural2 = getPreferredSize();
                double dsx = natural2.width > 0 ? getWidth() / (double) natural2.width : 1.0;
                double dsy = natural2.height > 0 ? getHeight() / (double) natural2.height : 1.0;
                int dlx = dsx > 0 ? (int) Math.round(dragPoint.x / dsx) : dragPoint.x;
                int dly = dsy > 0 ? (int) Math.round(dragPoint.y / dsy) : dragPoint.y;

                int lastIdx = selection.get(selection.size() - 1);
                float a = (float) (Math.PI * 2 * lastIdx / letters.size() - Math.PI / 2);
                int lnx = (int) (cx + radius * Math.cos(a));
                int lny = (int) (cy + radius * Math.sin(a));

                g2.setColor(new Color(32, 211, 194, 60));
                g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(lnx, lny, dlx, dly);
                g2.setColor(new Color(32, 211, 194, 140));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawLine(lnx, lny, dlx, dly);
            }

            Set<Integer> selSet = new HashSet<>(selection);
            for (int i = 0; i < letters.size(); i++) {
                float a = (float) (Math.PI * 2 * i / letters.size() - Math.PI / 2);
                int nx = (int) (cx + radius * Math.cos(a));
                int ny = (int) (cy + radius * Math.sin(a));
                boolean selected = selSet.contains(i);

                if (selected) {
                    g2.setColor(new Color(32, 211, 194, 60));
                    g2.fillOval(nx - nodeR - 4, ny - nodeR - 4, (nodeR + 4) * 2, (nodeR + 4) * 2);
                    g2.setColor(UITheme.WORDS_ACCENT[0]);
                } else {
                    g2.setPaint(new LinearGradientPaint(nx, ny - nodeR, nx, ny + nodeR,
                            new float[]{0f, 1f},
                            new Color[]{new Color(26, 50, 80, 220), new Color(14, 26, 42, 240)}));
                }
                g2.fillOval(nx - nodeR, ny - nodeR, nodeR * 2, nodeR * 2);

                g2.setColor(selected ? new Color(255, 255, 255, 120) : new Color(255, 255, 255, 35));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawOval(nx - nodeR, ny - nodeR, nodeR * 2, nodeR * 2);

                g2.setPaint(new LinearGradientPaint(nx - nodeR, ny - nodeR, nx + nodeR, ny,
                        new float[]{0f, 1f},
                        new Color[]{new Color(255, 255, 255, selected ? 50 : 22), new Color(255, 255, 255, 0)}));
                g2.fillOval(nx - nodeR + 2, ny - nodeR + 2, nodeR * 2 - 4, nodeR - 2);

                int fontSize = Math.max(16, Math.round(radius * 0.19f));
                g2.setFont(UITheme.displayFont(Font.BOLD, fontSize));
                FontMetrics fm = g2.getFontMetrics();
                String s = String.valueOf(letters.get(i));
                g2.setColor(Color.WHITE);
                g2.drawString(s, nx - fm.stringWidth(s) / 2, ny + fm.getAscent() / 2 - 1);
            }

            int cR = Math.max(24, Math.round(radius * 0.20f));
            g2.setPaint(new LinearGradientPaint(cx, cy - cR, cx, cy + cR,
                    new float[]{0f, 1f},
                    new Color[]{new Color(30, 55, 85, 200), new Color(18, 34, 54, 220)}));
            g2.fillOval(cx - cR, cy - cR, cR * 2, cR * 2);
            g2.setColor(new Color(32, 211, 194, 80));
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawOval(cx - cR, cy - cR, cR * 2, cR * 2);

            g2.setFont(UITheme.displayFont(Font.BOLD, Math.max(10, Math.round(cR * 0.48f))));
            FontMetrics sfm = g2.getFontMetrics();
            g2.setColor(UITheme.TEXT_MUTED);
            g2.drawString("\u21BB", cx - sfm.stringWidth("\u21BB") / 2, cy + sfm.getAscent() / 2 - 1);

            g2.dispose();
        }
    }
}
