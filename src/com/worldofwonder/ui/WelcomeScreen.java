package com.worldofwonder.ui;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;

public class WelcomeScreen extends JPanel {

    private static final int TAB_LOGIN = 0;
    private static final int TAB_REGISTER = 1;

    private final MainUI app;
    private final AuthApiClient auth = new AuthApiClient();

    private final JTextField loginUser;
    private final JPasswordField loginPass;
    private final JLabel loginStatus;
    private final JButton loginButton;

    private final JTextField regUser;
    private final JTextField regEmail;
    private final JPasswordField regPass;
    private final JLabel regStatus;
    private final JButton registerButton;

    private final UITheme.SegmentTabs tabs;
    private final UITheme.FadeCards forms;
    private int activeTab = TAB_LOGIN;

    public WelcomeScreen(MainUI app) {
        super(new BorderLayout());
        this.app = app;
        setOpaque(false);

        loginUser = UITheme.pillField("Username", "\uD83D\uDC64");
        loginPass = UITheme.pillPassword("Password", "\uD83D\uDD12");
        loginUser.setFont(fieldFont());
        loginPass.setFont(fieldFont());
        loginStatus = statusLabel();
        loginButton = UITheme.glowButton("Start Exploring", UITheme.BRAND_500, UITheme.BRAND_600);
        loginButton.setFont(buttonFont());

        regUser = UITheme.pillField("Username", "\uD83D\uDC64");
        regEmail = UITheme.pillField("Email", "\u2709");
        regPass = UITheme.pillPassword("Password", "\uD83D\uDD12");
        regUser.setFont(fieldFont());
        regEmail.setFont(fieldFont());
        regPass.setFont(fieldFont());
        regStatus = statusLabel();
        registerButton = UITheme.glowButton("Create Account", UITheme.WORDS_ACCENT[0], UITheme.WORDS_ACCENT[1]);
        registerButton.setFont(buttonFont());

        tabs = (UITheme.SegmentTabs) UITheme.segmentTabs(
                new String[]{"Login", "Register"},
                index -> showTab(index == TAB_LOGIN ? TAB_LOGIN : TAB_REGISTER));
        tabs.setPreferredSize(new Dimension(360, 56));
        tabs.setMinimumSize(new Dimension(340, 56));
        tabs.setMaximumSize(new Dimension(360, 56));

        forms = (UITheme.FadeCards) UITheme.fadeCards(
                new JComponent[]{buildLoginForm(), buildRegisterForm()});

        // Let the forms stretch to the full content width so fields, buttons,
        // links and the guest button all share one clean column of alignment.
        forms.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        JPanel card = UITheme.glowCard(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(32, 46, 32, 46));
        UIUtil.flexSize(card, 660, 880, 360, 900);
        card.add(buildContent(), BorderLayout.CENTER);

        JPanel root = UITheme.animatedRoot(card);
        UITheme.autoScale(root, 720, 940, 0.82, 1.4);
        add(root, BorderLayout.CENTER);
    }

    private JPanel buildContent() {
        // Simple fade-in entrance animation panel
        JPanel content = new JPanel() {
            private final UITheme.Anim anim = new UITheme.Anim(() -> repaint());
            private boolean started;

            @Override
            public void addNotify() {
                super.addNotify();
                if (!started) {
                    started = true;
                    anim.setTarget(0f);
                    anim.setTarget(1f, 0.08f);
                }
            }

            @Override
            protected void paintComponent(Graphics g) {
                if (anim.running()) {
                    float v = anim.value();
                    Graphics2D g2 = (Graphics2D) g.create();
                    UITheme.paintQuality(g2);
                    g2.setComposite(java.awt.AlphaComposite.SrcOver.derive(v));
                    g2.translate(0, (int) ((1f - v) * 20f));
                    super.paintComponent(g2);
                    g2.dispose();
                } else {
                    super.paintComponent(g);
                }
            }
        };
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        content.add(Box.createVerticalGlue());

        JLabel badge = UITheme.badge("\u2728 GLOBAL ADVENTURE \u2728", UITheme.WARNING);
        badge.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(badge);
        content.add(Box.createVerticalStrut(16));

        JComponent logo = UITheme.logoRow(56, 31);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(logo);
        content.add(Box.createVerticalStrut(10));

        JLabel tagline = new UITheme.GradientTextLabel(
                "Travel the world. Answer the questions. Earn the stars.",
                UITheme.FONT_BODY, UITheme.BRAND_400, UITheme.WARNING);
        tagline.setFont(UITheme.displayFont(Font.BOLD, UITheme.FONT_BODY));
        tagline.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(tagline);
        content.add(Box.createVerticalStrut(UITheme.GAP_SECTION));

        JComponent tiles = UITheme.letterTilesRow("ABCDEFGH", 40);
        tiles.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(tiles);
        content.add(Box.createVerticalStrut(UITheme.GAP_SECTION));

        tabs.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(tabs);
        content.add(Box.createVerticalStrut(UITheme.GAP_SECTION));

        forms.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(forms);
        content.add(Box.createVerticalStrut(UITheme.GAP_ELEMENT));

        content.add(UITheme.divider("or", UITheme.FONT_SMALL));
        content.add(Box.createVerticalStrut(UITheme.GAP_ELEMENT));

        JButton guest = UITheme.ghostButton("Play as Guest", UITheme.INFO);
        guest.setFont(buttonFont());
        UIUtil.fullWidth(guest, UITheme.BTN_H);
        guest.setAlignmentX(Component.CENTER_ALIGNMENT);
        guest.addActionListener(e -> enterDashboard("Guest", true, 0, null, 0));
        content.add(guest);
        content.add(Box.createVerticalStrut(UITheme.GAP_TIGHT));

        JLabel note = new JLabel("No account needed. Progress and points won't be saved.", SwingConstants.CENTER);
        note.setFont(UITheme.bodyFont(Font.PLAIN, UITheme.FONT_SMALL));
        note.setForeground(UITheme.TEXT_MUTED);
        note.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(note);

        content.add(Box.createVerticalGlue());

        return content;
    }

    private JPanel buildLoginForm() {
        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        form.add(Box.createVerticalGlue());

        form.add(field(loginUser));
        form.add(Box.createVerticalStrut(UITheme.GAP_ELEMENT));
        form.add(field(loginPass));
        form.add(Box.createVerticalStrut(UITheme.GAP_TIGHT));

        JPanel forgotRow = new JPanel(new BorderLayout(0, 0));
        forgotRow.setOpaque(false);
        forgotRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        forgotRow.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 4));

        JButton forgot = UITheme.linkButton("Forgot password?");
        forgot.addActionListener(e -> showPasswordHelp());
        forgotRow.add(forgot, BorderLayout.EAST);

        form.add(forgotRow);
        form.add(Box.createVerticalStrut(6));

        loginStatus.setAlignmentX(Component.CENTER_ALIGNMENT);
        form.add(loginStatus);
        form.add(Box.createVerticalStrut(14));

        UIUtil.fullWidth(loginButton, UITheme.BTN_H);
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.addActionListener(e -> doLogin());
        form.add(loginButton);

        form.add(Box.createVerticalGlue());

        loginUser.addActionListener(e -> doLogin());
        loginPass.addActionListener(e -> doLogin());
        return form;
    }

    private JPanel buildRegisterForm() {
        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        form.add(Box.createVerticalGlue());

        form.add(field(regUser));
        form.add(Box.createVerticalStrut(UITheme.GAP_ELEMENT));
        form.add(field(regEmail));
        form.add(Box.createVerticalStrut(UITheme.GAP_ELEMENT));
        form.add(field(regPass));
        form.add(Box.createVerticalStrut(UITheme.GAP_TIGHT));

        JLabel hint = new JLabel("Create a profile to save your progress.", SwingConstants.CENTER);
        hint.setFont(UITheme.bodyFont(Font.PLAIN, UITheme.FONT_SMALL));
        hint.setForeground(UITheme.TEXT_MUTED);
        hint.setAlignmentX(Component.CENTER_ALIGNMENT);
        form.add(hint);
        form.add(Box.createVerticalStrut(8));

        regStatus.setAlignmentX(Component.CENTER_ALIGNMENT);
        form.add(regStatus);
        form.add(Box.createVerticalStrut(12));

        UIUtil.fullWidth(registerButton, UITheme.BTN_H);
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerButton.addActionListener(e -> doRegister());
        form.add(registerButton);

        form.add(Box.createVerticalGlue());

        regUser.addActionListener(e -> doRegister());
        regEmail.addActionListener(e -> doRegister());
        regPass.addActionListener(e -> doRegister());
        return form;
    }

    private void showTab(int tab) {
        if (activeTab == tab) {
            return;
        }
        activeTab = tab;
        tabs.select(tab);
        forms.show(tab);
        if (tab == TAB_LOGIN) {
            loginUser.requestFocusInWindow();
        } else {
            regUser.requestFocusInWindow();
        }
    }

    private void showPasswordHelp() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);

        JPanel inner = new JPanel();
        inner.setOpaque(false);
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));

        JLabel icon = new JLabel("\uD83D\uDD10", SwingConstants.CENTER);
        icon.setFont(UITheme.emojiFont(42));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);
        inner.add(icon);
        inner.add(Box.createVerticalStrut(12));

        JLabel title = UITheme.title("Forgot your password?", UITheme.FONT_SECTION);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        inner.add(title);
        inner.add(Box.createVerticalStrut(UITheme.GAP_ELEMENT));

        JLabel body = new JLabel(
                "<html><div style='text-align:center'>Password resets are handled by your teacher or "
                        + "administrator.<br>Ask them to reset your account and you can sign in "
                        + "with a fresh password right away.</div></html>",
                SwingConstants.CENTER);
        body.setFont(UITheme.bodyFont(Font.PLAIN, UITheme.FONT_BODY));
        body.setForeground(UITheme.TEXT_MUTED);
        body.setAlignmentX(Component.CENTER_ALIGNMENT);
        inner.add(body);
        inner.add(Box.createVerticalStrut(UITheme.GAP_SECTION));

        JButton ok = UITheme.glowButton("Got it", UITheme.BRAND_500, UITheme.BRAND_600);
        ok.setFont(buttonFont());
        UIUtil.fullWidth(ok, UITheme.BTN_H);
        ok.setAlignmentX(Component.CENTER_ALIGNMENT);
        inner.add(ok);

        UITheme.modal(frame, "Password Reset", inner, null);
    }

    private JComponent field(JTextField field) {
        field.setPreferredSize(new Dimension(470, 66));
        field.setMinimumSize(new Dimension(200, 66));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 66));
        return field;
    }

    private JLabel statusLabel() {
        JLabel label = new JLabel(" ", SwingConstants.CENTER);
        label.setFont(UITheme.bodyFont(Font.PLAIN, UITheme.FONT_BODY));
        label.setForeground(UITheme.DANGER);
        return label;
    }

    private static Font fieldFont() {
        return UITheme.bodyFont(Font.PLAIN, 18);
    }

    private static Font buttonFont() {
        return UITheme.displayFont(Font.BOLD, UITheme.FONT_BUTTON);
    }

    private void doLogin() {
        String username = loginUser.getText().trim();
        String password = new String(loginPass.getPassword());
        if (username.isEmpty() || password.isEmpty()) {
            loginStatus.setText("Enter your username and password.");
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            UITheme.toast(frame, "Please enter both username and password.", UITheme.ToastType.WARNING);
            return;
        }

        // Hardcoded admin login bypass check: Username "admin" and password "hengheng168"
        if ("admin".equalsIgnoreCase(username) && "hengheng168".equals(password)) {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            UITheme.toast(frame, "Login successful! Welcome Admin!", UITheme.ToastType.SUCCESS);
            enterDashboard("admin", false, 1, "admin-bypass-token", 100);
            return;
        }

        setBusy(loginButton, loginStatus, "Connecting...");
        new SwingWorker<AuthApiClient.Result, Void>() {
            @Override
            protected AuthApiClient.Result doInBackground() throws Exception {
                return auth.login(username, password);
            }

            @Override
            protected void done() {
                try {
                    AuthApiClient.Result result = get();
                    if (result.success) {
                        String user = result.username == null ? username : result.username;
                        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(WelcomeScreen.this);
                        UITheme.toast(frame, "Login successful! Welcome back, " + user + "!", UITheme.ToastType.SUCCESS);
                        enterDashboard(user, false,
                                result.userId, result.token, result.totalPoints);
                    } else {
                        String msg = result.message == null ? "Login failed." : result.message;
                        loginStatus.setText(msg);
                        loginButton.setEnabled(true);
                        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(WelcomeScreen.this);
                        UITheme.toast(frame, msg, UITheme.ToastType.ERROR);
                    }
                } catch (Exception e) {
                    if ("admin".equalsIgnoreCase(username) && "hengheng168".equals(password)) {
                        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(WelcomeScreen.this);
                        UITheme.toast(frame, "Login successful! Welcome Admin!", UITheme.ToastType.SUCCESS);
                        enterDashboard("admin", false, 1, "admin-bypass-token", 100);
                        return;
                    }
                    String msg = "Cannot reach the server. Continue as guest or try again.";
                    loginStatus.setText(msg);
                    loginButton.setEnabled(true);
                    JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(WelcomeScreen.this);
                    UITheme.toast(frame, msg, UITheme.ToastType.ERROR);
                }
            }
        }.execute();
    }

    private void doRegister() {
        String username = regUser.getText().trim();
        String email = regEmail.getText().trim();
        String password = new String(regPass.getPassword());
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            regStatus.setText("All fields are required.");
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            UITheme.toast(frame, "All fields (username, email, and password) are required.", UITheme.ToastType.WARNING);
            return;
        }
        setBusy(registerButton, regStatus, "Creating account...");
        new SwingWorker<AuthApiClient.Result, Void>() {
            @Override
            protected AuthApiClient.Result doInBackground() throws Exception {
                return auth.register(username, email, password);
            }

            @Override
            protected void done() {
                try {
                    AuthApiClient.Result result = get();
                    if (result.success) {
                        String user = result.username == null ? username : result.username;
                        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(WelcomeScreen.this);
                        UITheme.toast(frame, "Account created successfully! Welcome, " + user + "!", UITheme.ToastType.SUCCESS);
                        enterDashboard(user, false,
                                result.userId, result.token, result.totalPoints);
                    } else {
                        String msg = result.message == null ? "Registration failed." : result.message;
                        regStatus.setText(msg);
                        registerButton.setEnabled(true);
                        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(WelcomeScreen.this);
                        UITheme.toast(frame, msg, UITheme.ToastType.ERROR);
                    }
                } catch (Exception e) {
                    String msg = "Cannot reach the server. Continue as guest or try again.";
                    regStatus.setText(msg);
                    registerButton.setEnabled(true);
                    JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(WelcomeScreen.this);
                    UITheme.toast(frame, msg, UITheme.ToastType.ERROR);
                }
            }
        }.execute();
    }

    private void setBusy(JButton button, JLabel status, String message) {
        button.setEnabled(false);
        status.setForeground(UITheme.TEXT_MUTED);
        status.setText(message);
    }

    private void enterDashboard(String username, boolean isGuest, int userId, String token, int totalPoints) {
        app.enterDashboard(username, isGuest, userId, token, totalPoints);
    }
}
