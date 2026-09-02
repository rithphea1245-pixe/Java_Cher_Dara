package com.worldofwonder.ui;

import com.worldofwonder.model.Level;
import com.worldofwonder.model.Question;
import com.worldofwonder.model.World;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuizGameScreen extends JPanel {

    private static final String PANEL_WORLDS = "worlds";
    private static final String PANEL_LEVELS = "levels";
    private static final String PANEL_QUIZ = "quiz";
    private static final String PANEL_COMPLETE = "complete";
    private static final char[] OPTION_LETTERS = {'A', 'B', 'C', 'D'};
    private static final int POINTS_PER_QUESTION = 10;

    private final Dashboard dashboard;
    private final QuizApiClient api;
    private final SampleQuizData sample;

    private final CardLayout cards;
    private final JPanel content;

    private List<World> worlds = new ArrayList<>();
    private List<Level> levels = new ArrayList<>();
    private List<Question> questions = new ArrayList<>();
    private int questionIndex;
    private int score;
    private int points;
    private int selectedAnswer = -1;
    private Level currentLevel;
    private boolean sampleMode;

    private JPanel worldsList;
    private JLabel statusLabel;
    private JLabel levelsTitle;
    private JPanel levelsList;
    private JLabel quizLevelName;
    private JLabel progressLabel;
    private UITheme.ProgressBar progressBar;
    private JLabel questionText;
    private JPanel optionsPanel;
    private JButton hintButton;
    private UITheme.HintBox hintLabel;
    private JLabel feedbackLabel;
    private JButton submitButton;
    private JButton nextButton;
    private JPanel completePanel;
    private UITheme.GradientTextLabel completeTitle;
    private JLabel completeText;
    private UITheme.Confetti confetti;

    private javax.swing.Timer questionTimer;
    private int timeLeft;
    private static final int TIME_PER_QUESTION = 30;

    private JLabel timerLabel;
    private JButton fiftyFiftyButton;
    private JButton addTimeButton;
    private JButton skipButton;
    private boolean usedFiftyFifty;
    private boolean usedAddTime;
    private boolean usedSkip;

    public QuizGameScreen(Dashboard dashboard) {
        super(new BorderLayout());
        this.dashboard = dashboard;
        this.api = new QuizApiClient();
        this.sample = new SampleQuizData();
        this.cards = new CardLayout();
        this.content = new JPanel(cards);
        content.setOpaque(false);
        setOpaque(false);

        questionTimer = new javax.swing.Timer(1000, e -> {
            timeLeft--;
            updateTimerDisplay();
            if (timeLeft <= 0) {
                questionTimer.stop();
                handleTimeOut();
            }
        });

        content.add(buildWorldsPanel(), PANEL_WORLDS);
        content.add(buildLevelsPanel(), PANEL_LEVELS);
        content.add(buildQuizPanel(), PANEL_QUIZ);
        completePanel = buildCompletePanel();
        content.add(completePanel, PANEL_COMPLETE);
        confetti = new UITheme.Confetti(completePanel);

        JPanel card = UITheme.card(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(UITheme.PAD_CARD_Y, UITheme.PAD_CARD_X, UITheme.PAD_CARD_Y, UITheme.PAD_CARD_X));
        UIUtil.fixedSize(card, 960, 740);

        JButton back = UITheme.ghostButton("\u2190 Back to Dashboard", UITheme.TEXT_MUTED);
        UIUtil.fixedSize(back, 200, UITheme.BTN_H);
        back.addActionListener(e -> dashboard.showDashboard());
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.setBorder(BorderFactory.createEmptyBorder(2, 2, 12, 2));
        topBar.add(back, BorderLayout.WEST);
        card.add(topBar, BorderLayout.NORTH);
        card.add(content, BorderLayout.CENTER);

        JPanel root = UITheme.pageRoot(card);
        UITheme.autoScale(root, 1040, 830, 0.85, 1.5);
        add(root, BorderLayout.CENTER);

        cards.show(content, PANEL_WORLDS);
        loadWorlds();
    }

    private JPanel buildWorldsPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setOpaque(false);

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        JLabel title = UITheme.title("Choose Your World", UITheme.FONT_PAGE_TITLE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.add(title);
        header.add(Box.createVerticalStrut(4));
        JLabel sub = UITheme.subtitle("Pick a world to begin your quiz adventure");
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.add(sub);
        header.add(Box.createVerticalStrut(6));
        statusLabel = UITheme.sectionTitle(" ", UITheme.FONT_SMALL);
        statusLabel.setForeground(UITheme.TEXT_MUTED);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.add(statusLabel);
        header.add(Box.createVerticalStrut(8));
        panel.add(header, BorderLayout.NORTH);

        worldsList = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 14));
        worldsList.setOpaque(false);
        // Add skeleton placeholders initially
        for (int i = 0; i < 4; i++) {
            worldsList.add(UITheme.skeleton(280, 148, 16));
        }
        panel.add(worldsList, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildLevelsPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setOpaque(false);

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        levelsTitle = UITheme.sectionTitle("Levels", UITheme.FONT_PAGE_TITLE);
        levelsTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.add(levelsTitle);
        header.add(Box.createVerticalStrut(4));
        JLabel sub = UITheme.subtitle("Pick a level to start the quiz");
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.add(sub);
        header.add(Box.createVerticalStrut(8));
        JButton back = UITheme.ghostButton("< Back to Worlds", UITheme.TEXT_MUTED);
        UIUtil.fixedSize(back, 200, UITheme.BTN_H_SM);
        back.addActionListener(e -> showWorlds());
        back.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.add(back);
        header.add(Box.createVerticalStrut(4));
        panel.add(header, BorderLayout.NORTH);

        levelsList = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 14));
        levelsList.setOpaque(false);
        // Add skeleton placeholders initially
        for (int i = 0; i < 6; i++) {
            levelsList.add(UITheme.skeleton(280, 115, 16));
        }
        panel.add(levelsList, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildQuizPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setOpaque(false);

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        quizLevelName = UITheme.sectionTitle("", UITheme.FONT_SECTION);
        quizLevelName.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.add(quizLevelName);
        
        JPanel progressTimerPanel = new JPanel(new BorderLayout());
        progressTimerPanel.setOpaque(false);
        progressTimerPanel.setMaximumSize(new Dimension(640, 30));
        progressTimerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        progressLabel = new JLabel(" ", SwingConstants.LEFT);
        progressLabel.setFont(UITheme.bodyFont(Font.BOLD, UITheme.FONT_BODY));
        progressLabel.setForeground(UITheme.TEXT_MUTED);

        timerLabel = new JLabel("30s", SwingConstants.RIGHT);
        timerLabel.setFont(UITheme.bodyFont(Font.BOLD, UITheme.FONT_BODY));
        timerLabel.setForeground(UITheme.TEXT);

        progressTimerPanel.add(progressLabel, BorderLayout.WEST);
        progressTimerPanel.add(timerLabel, BorderLayout.EAST);

        header.add(Box.createVerticalStrut(4));
        header.add(progressTimerPanel);
        progressBar = new UITheme.ProgressBar();
        progressBar.setPreferredSize(new Dimension(640, 18));
        progressBar.setMaximumSize(new Dimension(640, 18));
        progressBar.setAlignmentX(Component.CENTER_ALIGNMENT);
        header.add(Box.createVerticalStrut(10));
        header.add(progressBar);

        JPanel lifelinesRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        lifelinesRow.setOpaque(false);
        fiftyFiftyButton = UITheme.ghostButton("50/50", UITheme.GOLD);
        addTimeButton = UITheme.ghostButton("+15s", UITheme.TEAL);
        skipButton = UITheme.ghostButton("Skip", UITheme.VIOLET);
        fiftyFiftyButton.addActionListener(e -> useFiftyFifty());
        addTimeButton.addActionListener(e -> useAddTime());
        skipButton.addActionListener(e -> useSkip());
        lifelinesRow.add(fiftyFiftyButton);
        lifelinesRow.add(addTimeButton);
        lifelinesRow.add(skipButton);
        header.add(Box.createVerticalStrut(10));
        header.add(lifelinesRow);
        panel.add(header, BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        questionText = new JLabel(" ", SwingConstants.CENTER);
        questionText.setFont(UITheme.bodyFont(Font.BOLD, UITheme.FONT_CARD_TITLE));
        questionText.setForeground(UITheme.TEXT);
        questionText.setAlignmentX(Component.CENTER_ALIGNMENT);
        questionText.setBorder(BorderFactory.createEmptyBorder(8, 30, 4, 30));
        center.add(questionText);
        center.add(Box.createVerticalStrut(14));
        optionsPanel = new UITheme.SlidePanel(new GridLayout(2, 2, 16, 16));
        optionsPanel.setOpaque(false);
        optionsPanel.setMaximumSize(new Dimension(780, 320));
        optionsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(optionsPanel);
        panel.add(center, BorderLayout.CENTER);

        JPanel south = new JPanel();
        south.setOpaque(false);
        south.setLayout(new BoxLayout(south, BoxLayout.Y_AXIS));
        hintButton = UITheme.ghostButton("Show Hint", UITheme.GOLD);
        UIUtil.fixedSize(hintButton, 160, UITheme.BTN_H_SM);
        hintButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        hintButton.addActionListener(e -> showHint());
        hintLabel = new UITheme.HintBox(" ");
        hintLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        hintLabel.setMaximumSize(new Dimension(760, 60));
        hintLabel.setVisible(false);
        feedbackLabel = new JLabel(" ", SwingConstants.CENTER);
        feedbackLabel.setFont(UITheme.bodyFont(Font.BOLD, UITheme.FONT_BODY));
        feedbackLabel.setForeground(UITheme.TEXT_MUTED);
        feedbackLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        buttonRow.setOpaque(false);
        submitButton = UITheme.primaryButton("Submit Answer");
        UIUtil.fixedSize(submitButton, 210, UITheme.BTN_H);
        submitButton.addActionListener(e -> submitAnswer());
        nextButton = UITheme.secondaryButton("Next Question");
        UIUtil.fixedSize(nextButton, 210, UITheme.BTN_H);
        nextButton.addActionListener(e -> nextQuestion());
        nextButton.setVisible(false);
        buttonRow.add(submitButton);
        buttonRow.add(nextButton);
        south.add(Box.createVerticalStrut(6));
        south.add(hintButton);
        south.add(hintLabel);
        south.add(Box.createVerticalStrut(6));
        south.add(feedbackLabel);
        south.add(Box.createVerticalStrut(6));
        south.add(buttonRow);
        panel.add(south, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildCompletePanel() {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                if (confetti != null) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    UITheme.quality(g2);
                    confetti.paint(g2);
                    g2.dispose();
                }
            }
        };
        panel.setOpaque(false);

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        completeTitle = new UITheme.GradientTextLabel("Level Complete!", 40, UITheme.GOLD, UITheme.CORAL);
        completeTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(completeTitle);
        JLabel sub = UITheme.subtitle("Great job, explorer!");
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(sub);
        completeText = new JLabel(" ", SwingConstants.CENTER);
        completeText.setFont(UITheme.bodyFont(Font.BOLD, UITheme.FONT_CARD_TITLE));
        completeText.setForeground(UITheme.GOLD);
        completeText.setAlignmentX(Component.CENTER_ALIGNMENT);
        completeText.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        center.add(completeText);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, UITheme.GAP_ELEMENT, 0));
        buttons.setOpaque(false);
        buttons.setBorder(BorderFactory.createEmptyBorder(28, 0, 0, 0));
        JButton again = UITheme.primaryButton("Play Again");
        UIUtil.fixedSize(again, 210, UITheme.BTN_H);
        again.addActionListener(e -> startQuiz(currentLevel));
        buttons.add(again);
        JButton levelsBtn = UITheme.secondaryButton("Back to Levels");
        UIUtil.fixedSize(levelsBtn, 210, UITheme.BTN_H);
        levelsBtn.addActionListener(e -> showLevels());
        buttons.add(levelsBtn);
        JButton worldsBtn = UITheme.ghostButton("Choose Another World", UITheme.TEXT_MUTED);
        UIUtil.fixedSize(worldsBtn, 210, UITheme.BTN_H);
        worldsBtn.addActionListener(e -> showWorlds());
        buttons.add(worldsBtn);
        center.add(buttons);
        panel.add(center, BorderLayout.CENTER);
        return panel;
    }

    private void showWorlds() {
        cards.show(content, PANEL_WORLDS);
        loadWorlds();
    }

    private void showLevels() {
        cards.show(content, PANEL_LEVELS);
        renderLevels();
    }

    private void showQuiz() {
        cards.show(content, PANEL_QUIZ);
        renderQuestion();
    }

    private void loadWorlds() {
        statusLabel.setText("Loading worlds...");
        statusLabel.setForeground(UITheme.TEXT_MUTED);
        new SwingWorker<List<World>, Void>() {
            @Override
            protected List<World> doInBackground() {
                try {
                    List<World> fetched = api.getWorlds();
                    sampleMode = false;
                    return fetched;
                } catch (Exception e) {
                    sampleMode = true;
                    return sample.getWorlds();
                }
            }

            @Override
            protected void done() {
                try {
                    worlds = get();
                } catch (Exception e) {
                    sampleMode = true;
                    worlds = sample.getWorlds();
                }
                if (worlds == null || worlds.isEmpty()) {
                    statusLabel.setText("No worlds available.");
                    statusLabel.setForeground(UITheme.ERROR);
                } else {
                    statusLabel.setText(sampleMode ? "Offline mode - sample worlds" : " ");
                }
                renderWorlds();
            }
        }.execute();
    }

    private void renderWorlds() {
        worldsList.removeAll();
        for (World world : worlds) {
            UITheme.TileButton tile = new UITheme.TileButton(world.getName(),
                    world.getDescription(), worldAccent(world), worldEmoji(world.getName()));
            tile.setDark(true);
            tile.setSubtitleColor(new Color(0xd0e8f5));
            UIUtil.fixedSize(tile, 280, 148);
            tile.addActionListener(e -> selectWorld(world));
            worldsList.add(tile);
        }
        worldsList.revalidate();
        worldsList.repaint();
        UITheme.recordBaseTree(worldsList);
        UITheme.rescale(worldsList);
    }

    private void selectWorld(World world) {
        levels = new ArrayList<>();
        try {
            List<Level> fetched = sampleMode ? sample.getLevels(world.getId())
                    : api.getLevels(world.getId());
            levels.addAll(fetched);
            if (levels.isEmpty() && !sampleMode) {
                levels.addAll(sample.getLevels(world.getId()));
            }
        } catch (Exception e) {
            levels.addAll(sample.getLevels(world.getId()));
        }
        levelsTitle.setText(worldEmoji(world.getName()) + "  " + world.getName() + " Levels");
        showLevels();
    }

    private void renderLevels() {
        levelsList.removeAll();
        for (Level level : levels) {
            String subtitle = difficultyLabel(level.getDifficulty())
                    + "  -  " + level.getPointReward() + " points";
            UITheme.TileButton tile = new UITheme.TileButton(level.getName(),
                    subtitle, difficultyAccent(level.getDifficulty()), "\u2b50");
            tile.setDark(true);
            tile.setSubtitleColor(new Color(0xd0e8f5));
            UIUtil.fixedSize(tile, 280, 115);
            tile.addActionListener(e -> startQuiz(level));
            levelsList.add(tile);
        }
        levelsList.revalidate();
        levelsList.repaint();
        UITheme.recordBaseTree(levelsList);
        UITheme.rescale(levelsList);
    }

    private void startQuiz(Level level) {
        currentLevel = level;
        questionIndex = 0;
        score = 0;
        points = 0;
        selectedAnswer = -1;
        usedFiftyFifty = false;
        usedAddTime = false;
        usedSkip = false;
        if (fiftyFiftyButton != null) fiftyFiftyButton.setEnabled(true);
        if (addTimeButton != null) addTimeButton.setEnabled(true);
        if (skipButton != null) skipButton.setEnabled(true);

        quizLevelName.setText(level.getName());
        cards.show(content, PANEL_QUIZ);
        
        // Show skeleton placeholders while loading
        questionText.setText(" ");
        optionsPanel.removeAll();
        for (int i = 0; i < 4; i++) {
            optionsPanel.add(UITheme.skeleton(380, 60, 12));
        }
        optionsPanel.revalidate();
        optionsPanel.repaint();

        new SwingWorker<List<Question>, Void>() {
            @Override
            protected List<Question> doInBackground() {
                try {
                    List<Question> fetched = sampleMode ? sample.getQuestions(level.getId())
                            : api.getQuestions(level.getId());
                    if (fetched == null || fetched.isEmpty()) {
                        fetched = sample.getQuestions(level.getId());
                    }
                    return fetched;
                } catch (Exception e) {
                    return sample.getQuestions(level.getId());
                }
            }

            @Override
            protected void done() {
                try {
                    questions = get();
                } catch (Exception e) {
                    questions = new ArrayList<>();
                }
                if (questions == null) {
                    questions = new ArrayList<>();
                }
                renderQuestion();
            }
        }.execute();
    }

    private void renderQuestion() {
        if (questions == null || questions.isEmpty() || questionIndex >= questions.size()) {
            showLevelComplete();
            return;
        }
        Question question = questions.get(questionIndex);
        questionText.setText("<html><center>" + escapeHtml(question.getQuestionText()) + "</center></html>");
        progressLabel.setText("Question " + (questionIndex + 1) + " of " + questions.size()
                + "  -  Points: " + points);
        progressBar.setProgress(questionIndex / (double) questions.size());

        List<String> options = optionsOf(question);
        optionsPanel.removeAll();
        for (int i = 0; i < options.size(); i++) {
            final int idx = i;
            UITheme.OptionButton btn = new UITheme.OptionButton(letter(i), options.get(i));
            btn.reset();
            btn.playEnter(80 + i * 70);
            btn.addActionListener(e -> selectAnswer(idx));
            optionsPanel.add(btn);
        }
        selectedAnswer = -1;
        timeLeft = TIME_PER_QUESTION;
        updateTimerDisplay();
        questionTimer.start();
        
        if (fiftyFiftyButton != null && !usedFiftyFifty) fiftyFiftyButton.setEnabled(true);
        if (addTimeButton != null && !usedAddTime) addTimeButton.setEnabled(true);
        if (skipButton != null && !usedSkip) skipButton.setEnabled(true);

        hintButton.setEnabled(true);
        hintLabel.setText(" ");
        hintLabel.setVisible(false);
        feedbackLabel.setText(" ");
        feedbackLabel.setForeground(UITheme.TEXT_MUTED);
        submitButton.setVisible(true);
        submitButton.setEnabled(true);
        nextButton.setVisible(false);
        optionsPanel.revalidate();
        optionsPanel.repaint();
        UITheme.recordBaseTree(optionsPanel);
        UITheme.rescale(optionsPanel);
        ((UITheme.SlidePanel) optionsPanel).play();
    }

    private void selectAnswer(int idx) {
        selectedAnswer = idx;
        for (int i = 0; i < optionsPanel.getComponentCount(); i++) {
            UITheme.OptionButton ob = (UITheme.OptionButton) optionsPanel.getComponent(i);
            if (i == idx) {
                ob.setSelected();
            } else {
                ob.reset();
            }
        }
    }

    private void submitAnswer() {
        if (selectedAnswer < 0) {
            feedbackLabel.setForeground(UITheme.ERROR);
            feedbackLabel.setText("Pick an answer first!");
            SoundUtil.playError();
            return;
        }
        questionTimer.stop();
        Question question = questions.get(questionIndex);
        boolean correct = selectedAnswer == correctIndex(question);
        if (correct) {
            points += POINTS_PER_QUESTION;
            score++;
            SoundUtil.playCorrect();
        } else {
            SoundUtil.playError();
        }
        for (int i = 0; i < optionsPanel.getComponentCount(); i++) {
            UITheme.OptionButton ob = (UITheme.OptionButton) optionsPanel.getComponent(i);
            if (i == correctIndex(question)) {
                ob.setCorrect();
            } else if (i == selectedAnswer) {
                ob.setWrong();
            } else {
                ob.reset();
            }
        }
        if (correct) {
            feedbackLabel.setForeground(UITheme.GREEN);
            feedbackLabel.setText("Correct!  +" + POINTS_PER_QUESTION + " points");
        } else {
            feedbackLabel.setForeground(UITheme.ERROR);
            feedbackLabel.setText("Not quite. The answer is " + letter(correctIndex(question)) + ".");
        }
        hintButton.setEnabled(false);
        submitButton.setEnabled(false);
        if (fiftyFiftyButton != null) fiftyFiftyButton.setEnabled(false);
        if (addTimeButton != null) addTimeButton.setEnabled(false);
        if (skipButton != null) skipButton.setEnabled(false);
        progressBar.setProgress((questionIndex + 1) / (double) questions.size());

        syncAnswerToBackend(question, correct);

        if (questionIndex == questions.size() - 1) {
            nextButton.setText("View Results \u2192");
        } else {
            nextButton.setText("Next Question \u2192");
        }
        nextButton.setVisible(true);
    }

    private void syncAnswerToBackend(Question question, boolean correct) {
        int userId = dashboard.getUserId();
        if (userId <= 0 || sampleMode) {
            return;
        }
        String answerLetter = selectedAnswer >= 0 && selectedAnswer < OPTION_LETTERS.length
                ? String.valueOf(OPTION_LETTERS[selectedAnswer]) : "";
        new SwingWorker<QuizApiClient.AnswerResult, Void>() {
            @Override
            protected QuizApiClient.AnswerResult doInBackground() {
                try {
                    return api.submitAnswer(userId, question.getId(), answerLetter);
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void done() {
                try {
                    QuizApiClient.AnswerResult result = get();
                    if (result != null && result.success) {
                        dashboard.updateScore(result.totalPoints);
                    }
                } catch (Exception ignored) {
                }
            }
        }.execute();
    }

    private void nextQuestion() {
        questionIndex++;
        if (questions == null || questionIndex >= questions.size()) {
            showLevelComplete();
        } else {
            renderQuestion();
        }
    }

    private void showHint() {
        Question question = questions.get(questionIndex);
        String hint = question.getHint() == null || question.getHint().isEmpty()
                ? "No hint available." : question.getHint();
        hintLabel.setText("<html><div style='padding:2px 6px;'>" + escapeHtml(hint) + "</div></html>");
        hintLabel.setVisible(true);
        hintButton.setEnabled(false);
        SoundUtil.playHint();
    }

    private void showLevelComplete() {
        questionTimer.stop();
        SoundUtil.playVictory();
        completeText.setText("You answered " + score + " of " + questions.size()
                + " correctly and earned " + points + " points.");
        cards.show(content, PANEL_COMPLETE);
        confetti.launch();
        syncCompletionToBackend();
    }

    private void updateTimerDisplay() {
        timerLabel.setText(timeLeft + "s");
        if (timeLeft <= 5) {
            timerLabel.setForeground(UITheme.ERROR);
        } else {
            timerLabel.setForeground(UITheme.TEXT);
        }
    }

    private void handleTimeOut() {
        feedbackLabel.setForeground(UITheme.ERROR);
        feedbackLabel.setText("Time's up!");
        SoundUtil.playError();
        Question question = questions.get(questionIndex);
        for (int i = 0; i < optionsPanel.getComponentCount(); i++) {
            UITheme.OptionButton ob = (UITheme.OptionButton) optionsPanel.getComponent(i);
            if (i == correctIndex(question)) {
                ob.setCorrect();
            } else {
                ob.reset();
            }
        }
        hintButton.setEnabled(false);
        submitButton.setEnabled(false);
        if (fiftyFiftyButton != null) fiftyFiftyButton.setEnabled(false);
        if (addTimeButton != null) addTimeButton.setEnabled(false);
        if (skipButton != null) skipButton.setEnabled(false);
        progressBar.setProgress((questionIndex + 1) / (double) questions.size());

        if (questionIndex == questions.size() - 1) {
            nextButton.setText("View Results \u2192");
        } else {
            nextButton.setText("Next Question \u2192");
        }
        nextButton.setVisible(true);
    }

    private void useFiftyFifty() {
        if (usedFiftyFifty) return;
        usedFiftyFifty = true;
        fiftyFiftyButton.setEnabled(false);
        
        Question question = questions.get(questionIndex);
        int correct = correctIndex(question);
        List<Integer> wrongIndices = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            if (i != correct && i < optionsPanel.getComponentCount()) wrongIndices.add(i);
        }
        Collections.shuffle(wrongIndices);
        
        if (wrongIndices.size() >= 2) {
            optionsPanel.getComponent(wrongIndices.get(0)).setVisible(false);
            optionsPanel.getComponent(wrongIndices.get(1)).setVisible(false);
        }
        SoundUtil.playClick();
    }

    private void useAddTime() {
        if (usedAddTime) return;
        usedAddTime = true;
        addTimeButton.setEnabled(false);
        timeLeft += 15;
        updateTimerDisplay();
        SoundUtil.playClick();
    }

    private void useSkip() {
        if (usedSkip) return;
        usedSkip = true;
        skipButton.setEnabled(false);
        questionTimer.stop();
        points += POINTS_PER_QUESTION;
        score++;
        SoundUtil.playCorrect();
        nextQuestion();
    }

    private void syncCompletionToBackend() {
        int userId = dashboard.getUserId();
        if (userId <= 0 || sampleMode || points <= 0) {
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

    private static String letter(int i) {
        return String.valueOf(OPTION_LETTERS[i]);
    }

    private static List<String> optionsOf(Question q) {
        List<String> options = new ArrayList<>();
        options.add(q.getOptionA() == null ? "" : q.getOptionA());
        options.add(q.getOptionB() == null ? "" : q.getOptionB());
        options.add(q.getOptionC() == null ? "" : q.getOptionC());
        options.add(q.getOptionD() == null ? "" : q.getOptionD());
        return options;
    }

    private static int correctIndex(Question q) {
        if (q.getCorrectAnswer() == null || q.getCorrectAnswer().isEmpty()) {
            return 0;
        }
        char c = Character.toUpperCase(q.getCorrectAnswer().charAt(0));
        return Math.max(0, Math.min(3, c - 'A'));
    }

    private static Color worldAccent(World world) {
        switch (world.getId() % 3) {
            case 0:
                return UITheme.VIOLET;
            case 1:
                return UITheme.TEAL;
            default:
                return UITheme.CORAL;
        }
    }

    private static String worldEmoji(String name) {
        if (name == null) {
            return "\uD83C\uDF0D";
        }
        String n = name.toLowerCase();
        if (n.contains("space") || n.contains("galax") || n.contains("solar")) {
            return "\uD83D\uDE80";
        }
        if (n.contains("ocean") || n.contains("deep sea") || n.contains("reef") || n.contains("sea")) {
            return "\uD83C\uDF0A";
        }
        if (n.contains("egypt") || n.contains("nile")) {
            return "\uD83C\uDFDB";
        }
        if (n.contains("rainforest") || n.contains("jungle")) {
            return "\uD83C\uDF33";
        }
        if (n.contains("mountain")) {
            return "\u26F0";
        }
        return "\uD83C\uDF0D";
    }

    private static Color difficultyAccent(String difficulty) {
        if (difficulty == null) {
            return UITheme.TEAL;
        }
        switch (difficulty.toLowerCase()) {
            case "easy":
                return UITheme.GREEN;
            case "hard":
                return UITheme.CORAL;
            default:
                return UITheme.GOLD;
        }
    }

    private static String difficultyLabel(String difficulty) {
        if (difficulty == null || difficulty.isEmpty()) {
            return "Quiz";
        }
        String d = difficulty.toLowerCase();
        if (d.equals("easy")) {
            return "Easy";
        }
        if (d.equals("hard")) {
            return "Hard";
        }
        return "Medium";
    }

    private static String escapeHtml(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
