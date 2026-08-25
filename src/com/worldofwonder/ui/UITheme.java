package com.worldofwonder.ui;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import java.awt.CardLayout;
import java.awt.BasicStroke;
import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.LinearGradientPaint;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.IntConsumer;

public final class UITheme {

    private UITheme() {
    }

    public static final Color BG_TOP = new Color(0x1b3a5c);
    public static final Color BG_BOTTOM = new Color(0x0d1b2a);
    public static final Color PANEL_BG = new Color(255, 255, 255, 20);
    public static final Color PANEL_BG_HOVER = new Color(255, 255, 255, 34);
    public static final Color PANEL_BORDER = new Color(255, 255, 255, 41);
    public static final Color CARD_BG = new Color(20, 39, 61, 175);
    public static final Color TEXT = Color.WHITE;
    public static final Color TEXT_MUTED = new Color(0xc8d4e6);
    public static final Color ICE = new Color(0xeaf6ff);
    public static final Color TEAL = new Color(0x20d3c2);
    public static final Color TEAL_DARK = new Color(0x0fa896);
    public static final Color GOLD = new Color(0xffc93c);
    public static final Color GOLD_DARK = new Color(0xf5a623);
    public static final Color CORAL = new Color(0xff6b6b);
    public static final Color CORAL_DARK = new Color(0xe64545);
    public static final Color VIOLET = new Color(0x9b5de5);
    public static final Color PINK = new Color(0xf15bb5);
    public static final Color GREEN = new Color(0x3ddc97);
    public static final Color ERROR = new Color(0xff5d5d);
    public static final Color DISABLED_FILL = new Color(255, 255, 255, 22);
    public static final Color FIELD_BG = new Color(255, 255, 255, 23);
    public static final Color FIELD_BORDER = new Color(255, 255, 255, 41);

    /** Radius for the large outer cards (36-40px). */
    public static final int CARD_RADIUS = 40;
    /** Radius for fully-rounded capsule shapes (fields, buttons, pills). */
    public static final int CAPSULE = Integer.MAX_VALUE;

    // ── Typography hierarchy ──────────────────────────────────────────
    public static final int FONT_HERO      = 36;
    public static final int FONT_PAGE_TITLE  = 30;
    public static final int FONT_SECTION     = 22;
    public static final int FONT_CARD_TITLE  = 18;
    public static final int FONT_BODY        = 15;
    public static final int FONT_SMALL       = 13;
    public static final int FONT_BADGE       = 11;
    public static final int FONT_BUTTON      = 15;

    // ── Spacing system ────────────────────────────────────────────────
    public static final int PAD_SCREEN_X = 32;
    public static final int PAD_SCREEN_Y = 28;
    public static final int PAD_CARD_X   = 30;
    public static final int PAD_CARD_Y   = 26;
    public static final int GAP_XL       = 32;
    public static final int GAP_LG       = 24;
    public static final int GAP_SECTION  = 22;
    public static final int GAP_ELEMENT  = 14;
    public static final int GAP_TIGHT    = 8;
    public static final int BTN_H        = 52;
    public static final int BTN_H_SM     = 42;

    private static final String DISPLAY_FAMILY;
    private static final String BODY_FAMILY;
    private static final String EMOJI_FAMILY;

    static {
        List<String> available = Arrays.asList(
                GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
        DISPLAY_FAMILY = firstAvailable(available, new String[]{
                "Baloo 2", "Baloo", "Baloo Bhai 2", "Fredoka", "Fredoka One",
                "Quicksand", "Nunito", "Montserrat", "Poppins", "Comfortaa", "SansSerif"});
        BODY_FAMILY = firstAvailable(available, new String[]{
                "Nunito", "Ubuntu Sans", "Ubuntu", "Noto Sans", "DejaVu Sans", "SansSerif"});
        EMOJI_FAMILY = resolveEmojiFamily(available);
    }

    private static String firstAvailable(List<String> available, String[] candidates) {
        for (String candidate : candidates) {
            for (String family : available) {
                if (family.equalsIgnoreCase(candidate)) {
                    return family;
                }
            }
        }
        return "SansSerif";
    }

    private static String resolveEmojiFamily(List<String> available) {
        for (String family : available) {
            if (family.equalsIgnoreCase("Noto Color Emoji")
                    || family.equalsIgnoreCase("Noto Emoji")
                    || family.equalsIgnoreCase("Apple Color Emoji")
                    || family.equalsIgnoreCase("Segoe UI Emoji")
                    || family.equalsIgnoreCase("EmojiOne Color")
                    || family.equalsIgnoreCase("Symbola")) {
                return family;
            }
        }
        return null;
    }

    public static Font emojiFont(int size) {
        return EMOJI_FAMILY != null ? new Font(EMOJI_FAMILY, Font.PLAIN, size) : displayFont(Font.BOLD, size);
    }

    public static Font displayFont(int style, int size) {
        return new Font(DISPLAY_FAMILY, style, size);
    }

    public static Font bodyFont(int style, int size) {
        return new Font(BODY_FAMILY, style, size);
    }

    private static final List<float[]> STARS = buildStars();

    private static List<float[]> buildStars() {
        Random rnd = new Random(4242);
        List<float[]> stars = new ArrayList<>();
        for (int i = 0; i < 110; i++) {
            stars.add(new float[]{
                    rnd.nextFloat(),
                    rnd.nextFloat() * 0.85f,
                    0.6f + rnd.nextFloat() * 1.3f,
                    0.25f + rnd.nextFloat() * 0.55f});
        }
        return stars;
    }

    public static void scenic(Graphics2D g2, int w, int h) {
        g2.setPaint(new LinearGradientPaint(0, 0, 0, h,
                new float[]{0f, 0.35f, 0.65f, 1f},
                new Color[]{new Color(0x050510), new Color(0x0e0e2e), new Color(0x1a1050), new Color(0x2a1570)}));
        g2.fillRect(0, 0, w, h);

        glow(g2, w, h, 0.15f, 0.08f, 0.58f, new Color(120, 60, 220, 110));
        glow(g2, w, h, 0.82f, 0.12f, 0.52f, new Color(220, 60, 160, 90));
        glow(g2, w, h, 0.50f, 0.35f, 0.42f, new Color(32, 200, 190, 75));
        glow(g2, w, h, 0.28f, 0.88f, 0.50f, new Color(180, 80, 240, 70));
        glow(g2, w, h, 0.80f, 0.82f, 0.45f, new Color(255, 180, 60, 55));
        glow(g2, w, h, 0.55f, 0.60f, 0.65f, new Color(80, 140, 255, 50));

        g2.setPaint(new LinearGradientPaint(0, 0, w, h,
                new float[]{0f, 0.5f, 1f},
                new Color[]{new Color(255, 255, 255, 20),
                        new Color(255, 255, 255, 0),
                        new Color(0, 0, 0, 0)}));
        g2.fillRect(0, 0, w, h);

        for (float[] s : STARS) {
            g2.setColor(new Color(255, 255, 255, (int) (s[3] * 255)));
            g2.fillOval((int) (s[0] * w), (int) (s[1] * h),
                    Math.max(1, Math.round(s[2])), Math.max(1, Math.round(s[2])));
        }

        g2.setPaint(new RadialGradientPaint(
                new Point2D.Float(w * 0.5f, h * 0.55f), Math.max(w, h) * 0.9f,
                new float[]{0f, 1f},
                new Color[]{new Color(0, 0, 0, 0), new Color(0, 0, 0, 120)}));
        g2.fillRect(0, 0, w, h);
    }

    private static void glow(Graphics2D g2, int w, int h, float cx, float cy, float r, Color color) {
        float radius = r * Math.max(w, h);
        g2.setPaint(new RadialGradientPaint(
                new Point2D.Float(cx * w, cy * h), radius,
                new float[]{0f, 1f},
                new Color[]{color, new Color(color.getRed(), color.getGreen(), color.getBlue(), 0)}));
        g2.fillRect(0, 0, w, h);
    }

    private static final Color[] SHADOW_PASS = {
            new Color(0, 0, 0, 8),
            new Color(0, 0, 0, 12),
            new Color(0, 0, 0, 16),
            new Color(0, 0, 0, 20),
            new Color(0, 0, 0, 24),
    };

    public static void softShadow(Graphics2D g2, int x, int y, int w, int h, int radius) {
        for (int i = 0; i < 5; i++) {
            g2.setColor(SHADOW_PASS[i]);
            g2.drawRoundRect(x + i + 1, y + i + 3, w - 2 * (i + 1) - 1, h - 2 * (i + 1) - 1, radius, radius);
        }
    }

    public static JPanel gradientPanel(LayoutManager layout) {
        JPanel panel = new JPanel(layout) {
            private BufferedImage bg;

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                int w = getWidth();
                int h = getHeight();
                if (bg == null || bg.getWidth() != w || bg.getHeight() != h) {
                    bg = new BufferedImage(Math.max(1, w), Math.max(1, h), BufferedImage.TYPE_INT_RGB);
                    Graphics2D img = bg.createGraphics();
                    quality(img);
                    scenic(img, w, h);
                    img.dispose();
                }
                g.drawImage(bg, 0, 0, null);
            }
        };
        return panel;
    }

    public static Graphics2D quality(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_LCD_CONTRAST, 140);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        return g2;
    }

    /** Lighter quality preset for high-frequency paints (typing, hover, pulse).
     *  Skips expensive render-quality/fractional-metric hints that cause
     *  perceived lag during input and interaction. */
    public static Graphics2D paintQuality(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_LCD_CONTRAST, 140);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
        return g2;
    }

    /** Fully-rounded capsule radius for the given component height. */
    static int capsule(int height) {
        return Math.max(12, height / 2);
    }

    static Color lerp(Color a, Color b, float t) {
        if (t <= 0) {
            return a;
        }
        if (t >= 1) {
            return b;
        }
        return new Color(
                Math.round(a.getRed() + (b.getRed() - a.getRed()) * t),
                Math.round(a.getGreen() + (b.getGreen() - a.getGreen()) * t),
                Math.round(a.getBlue() + (b.getBlue() - a.getBlue()) * t),
                Math.round(a.getAlpha() + (b.getAlpha() - a.getAlpha()) * t));
    }

    static final class SmoothHover {
        private final JComponent component;
        private final javax.swing.Timer timer;
        private final Runnable onUpdate;
        private float value;
        private boolean target;

        SmoothHover(JComponent component, Runnable onUpdate) {
            this.component = component;
            this.onUpdate = onUpdate;
            this.timer = new javax.swing.Timer(16, e -> tick());
            timer.setRepeats(true);
        }

        void enter() {
            target = true;
            if (!timer.isRunning()) {
                timer.start();
            }
        }

        void exit() {
            target = false;
            if (!timer.isRunning()) {
                timer.start();
            }
        }

        float value() {
            return value;
        }

        private void tick() {
            float goal = target ? 1f : 0f;
            value += (goal - value) * 0.24f;
            if (Math.abs(goal - value) < 0.02f) {
                value = goal;
                timer.stop();
            }
            if (component.isShowing()) {
                onUpdate.run();
            }
        }
    }

    public static JPanel card(LayoutManager layout) {
        JPanel panel = new JPanel(layout) {
            private BufferedImage chrome;

            @Override
            protected void paintComponent(Graphics g) {
                int w = getWidth();
                int h = getHeight();
                if (chrome == null || chrome.getWidth() != w || chrome.getHeight() != h) {
                    chrome = new BufferedImage(Math.max(1, w), Math.max(1, h),
                            BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g2 = chrome.createGraphics();
                    quality(g2);
                    softShadow(g2, 0, 0, w, h, 24);
                    g2.setPaint(new LinearGradientPaint(0, 0, 0, h,
                            new float[]{0f, 0.45f, 1f},
                            new Color[]{new Color(255, 255, 255, 38),
                                    new Color(20, 39, 61, 150),
                                    new Color(13, 27, 42, 160)}));
                    g2.fillRoundRect(0, 0, w, h, CARD_RADIUS, CARD_RADIUS);

                    Shape body = new RoundRectangle2D.Float(1, 1, w - 3, h - 3, CARD_RADIUS, CARD_RADIUS);
                    g2.setClip(body);
                    g2.setPaint(new LinearGradientPaint(w * 0.15f, 0, w * 0.85f, h,
                            new float[]{0f, 0.5f, 1f},
                            new Color[]{new Color(32, 211, 194, 22),
                                    new Color(255, 255, 255, 0),
                                    new Color(155, 93, 229, 20)}));
                    g2.fillRect(0, 0, w, h);

                    int glowR = Math.round(Math.min(w, h) * 0.9f);
                    g2.setPaint(new RadialGradientPaint(
                            new Point2D.Float(-glowR * 0.35f, -glowR * 0.35f), glowR,
                            new float[]{0f, 1f},
                            new Color[]{new Color(32, 211, 194, 70), new Color(32, 211, 194, 0)}));
                    g2.fillRect(0, 0, w, h);
                    g2.setPaint(new RadialGradientPaint(
                            new Point2D.Float(w + glowR * 0.35f, h + glowR * 0.35f), glowR,
                            new float[]{0f, 1f},
                            new Color[]{new Color(155, 93, 229, 85), new Color(155, 93, 229, 0)}));
                    g2.fillRect(0, 0, w, h);
                    g2.setClip(null);

                    g2.setColor(new Color(255, 255, 255, 34));
                    g2.drawRoundRect(1, 1, w - 3, h - 3, CARD_RADIUS, CARD_RADIUS);
                    g2.setStroke(new BasicStroke(1.6f));
                    g2.setPaint(new LinearGradientPaint(0, 0, w, h,
                            new float[]{0f, 0.5f, 1f},
                            new Color[]{new Color(32, 211, 194, 150),
                                    new Color(255, 255, 255, 60),
                                    new Color(155, 93, 229, 160)}));
                    g2.draw(new RoundRectangle2D.Float(1.4f, 1.4f, w - 3.8f, h - 3.8f, CARD_RADIUS, CARD_RADIUS));
                    g2.setClip(0, 0, w, 4);
                    g2.setStroke(new BasicStroke(1.6f));
                    g2.setColor(new Color(255, 255, 255, 80));
                    g2.draw(new RoundRectangle2D.Float(1.2f, 1.2f, w - 2.4f, h - 2.4f, CARD_RADIUS, CARD_RADIUS));
                    g2.setClip(null);
                    g2.dispose();
                }
                g.drawImage(chrome, 0, 0, null);
            }
        };
        panel.setOpaque(false);
        return panel;
    }

    public static JPanel centerInCard(JComponent child, int width, int height) {
        JPanel holder = new JPanel(new java.awt.GridBagLayout());
        holder.setOpaque(false);
        JPanel card = card(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(PAD_SCREEN_Y, PAD_SCREEN_X, PAD_SCREEN_Y, PAD_SCREEN_X));
        UIUtil.flexSize(card, width, height, Math.min(320, width), Integer.MAX_VALUE);
        card.add(child, BorderLayout.CENTER);
        holder.add(card);
        return holder;
    }

    public static JLabel title(String text, int size) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(displayFont(Font.BOLD, size > 0 ? size : FONT_PAGE_TITLE));
        label.setForeground(TEXT);
        return label;
    }

    public static JLabel subtitle(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(bodyFont(Font.PLAIN, FONT_BODY));
        label.setForeground(TEXT_MUTED);
        return label;
    }

    public static JButton primaryButton(String text) {
        return new RoundedButton(text, TEAL, TEAL_DARK, Color.WHITE, true);
    }

    public static JButton secondaryButton(String text) {
        return new RoundedButton(text, GOLD, GOLD_DARK, Color.WHITE, true);
    }

    public static JButton dangerButton(String text) {
        return new RoundedButton(text, CORAL, CORAL_DARK, Color.WHITE, true);
    }

    public static JButton accentButton(String text, Color color) {
        return new RoundedButton(text, color, color, Color.WHITE, false);
    }

    public static JButton outlineButton(String text, Color color) {
        RoundedButton button = (RoundedButton) accentButton(text, color);
        button.outline = true;
        return button;
    }

    public static JButton ghostButton(String text, Color color) {
        RoundedButton button = (RoundedButton) accentButton(text, color);
        button.glass = true;
        return button;
    }

    public static <T extends JTextField> T styleField(T field) {
        field.setOpaque(false);
        field.setForeground(TEXT);
        field.setCaretColor(TEXT);
        field.setSelectionColor(TEAL);
        field.setSelectedTextColor(Color.WHITE);
        field.setFont(bodyFont(Font.PLAIN, 15));
        field.setBorder(BorderFactory.createCompoundBorder(
                new RoundBorder(FIELD_BORDER, CAPSULE, 1),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)));
        return field;
    }

    public static JTextField placeholderField(String placeholder) {
        return styleField(new PlaceholderField(placeholder));
    }

    public static JPasswordField placeholderPassword(String placeholder) {
        return styleField(new PlaceholderPasswordField(placeholder));
    }

    public static JPanel pageRoot(JComponent card) {
        return screenPage(card);
    }

    /** Transparent page container that centers the given card while letting a
     *  single shared background layer (owned by MainUI) show through. */
    public static JPanel screenPage(JComponent card) {
        JPanel root = new JPanel(new GridBagLayout());
        root.setOpaque(false);
        root.add(card);
        return root;
    }

    public static void autoScale(JComponent root, int baseW, int baseH, double minFactor, double maxFactor) {
        for (Component child : root.getComponents()) {
            recordBase(child);
        }
        double[] lastScale = {1.0};
        root.putClientProperty("wow.lastScale", lastScale);
        root.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int w = root.getWidth();
                int h = root.getHeight();
                if (w <= 0 || h <= 0) {
                    return;
                }
                double scale = Math.min(w / (double) baseW, h / (double) baseH);
                scale = Math.max(minFactor, Math.min(maxFactor, scale));
                if (Math.abs(scale - lastScale[0]) < 0.01) {
                    return;
                }
                lastScale[0] = scale;
                for (Component child : root.getComponents()) {
                    applyScale(child, scale);
                }
                root.revalidate();
                root.repaint();
            }
        });
    }

    public static void rescale(JComponent root) {
        Object o = root.getClientProperty("wow.lastScale");
        if (!(o instanceof double[])) {
            return;
        }
        double scale = ((double[]) o)[0];
        for (Component child : root.getComponents()) {
            applyScale(child, scale);
        }
        root.revalidate();
        root.repaint();
    }

    public static void recordBaseTree(JComponent c) {
        recordBase(c);
    }

    private static void recordBase(Component c) {
        if (c instanceof JComponent) {
            JComponent jc = (JComponent) c;
            boolean[] set = new boolean[3];
            set[0] = jc.isPreferredSizeSet();
            set[1] = jc.isMinimumSizeSet();
            set[2] = jc.isMaximumSizeSet();
            Dimension[] dims = new Dimension[3];
            dims[0] = set[0] ? jc.getPreferredSize() : null;
            dims[1] = set[1] ? jc.getMinimumSize() : null;
            dims[2] = set[2] ? jc.getMaximumSize() : null;
            jc.putClientProperty("wow.baseDims", dims);
            jc.putClientProperty("wow.dimsSet", set);
            Font f = jc.getFont();
            if (f != null) {
                jc.putClientProperty("wow.baseFont", f);
            }
        }
        if (c instanceof Container) {
            for (Component child : ((Container) c).getComponents()) {
                recordBase(child);
            }
        }
    }

    private static void applyScale(Component c, double scale) {
        if (c instanceof JComponent) {
            JComponent jc = (JComponent) c;
            Dimension[] dims = (Dimension[]) jc.getClientProperty("wow.baseDims");
            boolean[] set = (boolean[]) jc.getClientProperty("wow.dimsSet");
            if (dims != null) {
                if (set[0] && dims[0] != null) {
                    jc.setPreferredSize(scaleDim(dims[0], scale));
                }
                if (set[1] && dims[1] != null) {
                    jc.setMinimumSize(scaleDim(dims[1], scale));
                }
                if (set[2] && dims[2] != null) {
                    jc.setMaximumSize(scaleDim(dims[2], scale));
                }
            }
            Font f = (Font) jc.getClientProperty("wow.baseFont");
            if (f != null) {
                jc.setFont(f.deriveFont((float) (f.getSize() * scale)));
            }
        }
        if (c instanceof Container) {
            for (Component child : ((Container) c).getComponents()) {
                applyScale(child, scale);
            }
        }
    }

    private static Dimension scaleDim(Dimension d, double scale) {
        if (d == null) {
            return null;
        }
        int w = d.width == Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) Math.round(d.width * scale);
        int h = d.height == Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) Math.round(d.height * scale);
        return new Dimension(w, h);
    }

    public static class BackgroundManager {
        private static final Map<String, BufferedImage> CACHE = new java.util.concurrent.ConcurrentHashMap<>();

        /** Game artwork is scoped to active game screens only. Neutral screens
         *  (welcome / dashboard) return null so FloatRoot paints the clean
         *  dark scenic gradient instead of any game art bleeding through. */
        public static BufferedImage getImageForScreen(String screenName) {
            if (screenName == null) {
                return null;
            }
            switch (screenName.toLowerCase().trim()) {
                case "quiz":
                    return getOrLoadImage("Game1.jpeg");
                case "wordsearch":
                    return getOrLoadImage("Game2.jpeg");
                case "cups":
                    return getOrLoadImage("Game3.jpeg");
                case "words":
                    return getOrLoadImage("Game4.jpeg");
                default:
                    return null;
            }
        }

        public static BufferedImage getOrLoadImage(String filename) {
            return CACHE.computeIfAbsent(filename, BackgroundManager::loadImage);
        }

        private static BufferedImage loadImage(String filename) {
            String[] candidatePaths = {
                "images/" + filename,
                "backend/images/" + filename,
                "../images/" + filename,
                "../../images/" + filename,
                System.getProperty("user.dir") + "/images/" + filename,
                System.getProperty("user.dir") + "/backend/images/" + filename
            };
            for (String path : candidatePaths) {
                java.io.File file = new java.io.File(path);
                if (file.exists() && file.isFile()) {
                    try {
                        BufferedImage img = javax.imageio.ImageIO.read(file);
                        if (img != null) {
                            return img;
                        }
                    } catch (Exception ignored) {
                    }
                }
            }
            try {
                java.io.InputStream is = UITheme.class.getResourceAsStream("/images/" + filename);
                if (is == null) is = UITheme.class.getResourceAsStream("/" + filename);
                if (is == null) is = UITheme.class.getClassLoader().getResourceAsStream("images/" + filename);
                if (is != null) {
                    BufferedImage img = javax.imageio.ImageIO.read(is);
                    if (img != null) {
                        return img;
                    }
                }
            } catch (Exception ignored) {
            }
            return null;
        }
    }

    public static JPanel animatedRoot(JComponent card) {
        FloatRoot root = new FloatRoot(new GridBagLayout());
        root.add(card);
        return root;
    }

    /** Root panel with the floating-letter animated background, using the
     *  given layout manager. Use for every full-screen page so the letters
     *  stay visible behind the dashboard and all game views. */
    public static FloatRoot animatedRoot(LayoutManager layout) {
        return new FloatRoot(layout);
    }

    public static class FloatRoot extends JPanel {

        private final List<Particle> particles;
        private final javax.swing.Timer timer;
        private final Rectangle oldBounds = new Rectangle();
        private final Rectangle newBounds = new Rectangle();
        private final Rectangle dirty = new Rectangle();
        private BufferedImage background;
        private boolean scattered;
        private long lastTickNanos = System.nanoTime();
        private String currentTheme = "welcome";

        public FloatRoot(LayoutManager layout) {
            super(layout);
            setOpaque(false);
            particles = new ArrayList<>();
            Random rnd = new Random();
            String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
            for (int i = 0; i < 26; i++) {
                particles.add(new Particle(alphabet.charAt(rnd.nextInt(alphabet.length())), rnd));
            }
            addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    background = null;
                }
            });
            timer = new javax.swing.Timer(16, e -> {
                if (!isShowing()) {
                    return;
                }
                long now = System.nanoTime();
                float dt = Math.min(0.05f, (now - lastTickNanos) / 1_000_000_000f);
                lastTickNanos = now;
                int w = getWidth();
                int h = getHeight();
                if (!scattered && w > 0 && h > 0) {
                    for (Particle p : particles) {
                        p.reset(w, h);
                    }
                    scattered = true;
                }
                boolean first = true;
                for (Particle p : particles) {
                    p.bounds(oldBounds);
                    p.advance(dt, w, h);
                    p.bounds(newBounds);
                    int minX = Math.min(oldBounds.x, newBounds.x);
                    int minY = Math.min(oldBounds.y, newBounds.y);
                    int maxX = Math.max(oldBounds.x + oldBounds.width, newBounds.x + newBounds.width);
                    int maxY = Math.max(oldBounds.y + oldBounds.height, newBounds.y + newBounds.height);
                    if (first) {
                        dirty.setBounds(minX, minY, maxX - minX, maxY - minY);
                        first = false;
                    } else {
                        int dMinX = Math.min(dirty.x, minX);
                        int dMinY = Math.min(dirty.y, minY);
                        int dMaxX = Math.max(dirty.x + dirty.width, maxX);
                        int dMaxY = Math.max(dirty.y + dirty.height, maxY);
                        dirty.setBounds(dMinX, dMinY, dMaxX - dMinX, dMaxY - dMinY);
                    }
                }
                if (!first) {
                    repaint(dirty.x, dirty.y, dirty.width, dirty.height);
                }
            });
            timer.setRepeats(true);
        }

        public void setTheme(String theme) {
            if (theme == null) theme = "dashboard";
            if (theme.equalsIgnoreCase(this.currentTheme)) {
                return;
            }
            this.currentTheme = theme;
            this.background = null;
            repaint();
        }

        public String getTheme() {
            return currentTheme;
        }

        @Override
        public void addNotify() {
            super.addNotify();
            timer.start();
        }

        @Override
        public void removeNotify() {
            timer.stop();
            super.removeNotify();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int w = getWidth();
            int h = getHeight();
            if (w <= 0 || h <= 0) return;

            if (background == null || background.getWidth() != w || background.getHeight() != h) {
                background = new BufferedImage(Math.max(1, w), Math.max(1, h),
                        BufferedImage.TYPE_INT_ARGB);
                Graphics2D bg = background.createGraphics();
                quality(bg);
                renderCompositeBackground(bg, w, h, currentTheme);
                bg.dispose();
            }
            Graphics2D g2 = (Graphics2D) g.create();
            paintQuality(g2);
            g2.drawImage(background, 0, 0, null);
            for (Particle p : particles) {
                g2.drawImage(p.image, (int) Math.floor(p.x + p.ox), (int) Math.floor(p.y + p.oy), null);
            }
            g2.dispose();
        }

        private void renderCompositeBackground(Graphics2D bg, int w, int h, String theme) {
            BufferedImage img = BackgroundManager.getImageForScreen(theme);
            if (img != null) {
                int imgW = img.getWidth();
                int imgH = img.getHeight();
                double scale = Math.max((double) w / imgW, (double) h / imgH);
                int dw = (int) Math.round(imgW * scale);
                int dh = (int) Math.round(imgH * scale);
                int dx = (w - dw) / 2;
                int dy = (h - dh) / 2;
                bg.drawImage(img, dx, dy, dw, dh, null);

                // Deep elegant contrast overlay so glassmorphism and text shine
                bg.setPaint(new LinearGradientPaint(0, 0, 0, h,
                        new float[]{0f, 0.45f, 1f},
                        new Color[]{
                                new Color(10, 20, 36, 175),
                                new Color(13, 27, 42, 195),
                                new Color(5, 12, 22, 230)}));
                bg.fillRect(0, 0, w, h);

                // Subtle radial dark vignette
                bg.setPaint(new RadialGradientPaint(
                        new Point2D.Float(w * 0.5f, h * 0.5f), Math.max(w, h) * 0.8f,
                        new float[]{0f, 1f},
                        new Color[]{new Color(0, 0, 0, 0), new Color(0, 0, 0, 130)}));
                bg.fillRect(0, 0, w, h);
            } else {
                scenic(bg, w, h);
            }
        }

        static class Particle {

            private static final Map<Integer, Font> FONT_CACHE = new HashMap<>();
            private static final java.awt.Canvas METRICS_CANVAS = new java.awt.Canvas();

            private final String text;
            private final Random rnd;
            private BufferedImage image;
            private int ox;
            private int oy;
            private float baseX;
            private float baseY;
            private float x;
            private float y;
            private float vx;
            private float vy;
            private float perpX;
            private float perpY;
            private float t;
            private float wobblePhase;
            private float wobbleFreq;
            private float wobbleAmp;
            private int size;
            private Color color;

            Particle(char letter, Random rnd) {
                this.text = String.valueOf(letter);
                this.rnd = rnd;
                reset(1600, 2200);
            }

            private float marginX() {
                return size + (int) wobbleAmp + 24;
            }

            private float marginY() {
                return size + (int) wobbleAmp + 24;
            }

            void advance(float dt, int width, int height) {
                t += dt;
                baseX += vx * dt;
                baseY += vy * dt;
                float marginX = marginX();
                float marginY = marginY();
                float spanX = width + 2f * marginX;
                float spanY = height + 2f * marginY;
                if (baseX < -marginX) {
                    baseX += spanX;
                } else if (baseX > width + marginX) {
                    baseX -= spanX;
                }
                if (baseY < -marginY) {
                    baseY += spanY;
                } else if (baseY > height + marginY) {
                    baseY -= spanY;
                }
                float wobble = (float) (Math.sin(t * wobbleFreq + wobblePhase) * wobbleAmp);
                x = baseX + perpX * wobble;
                y = baseY + perpY * wobble;
            }

            void bounds(Rectangle out) {
                int w = (int) Math.ceil(size * 0.95f) + 8;
                int h = (int) Math.ceil(size * 1.25f) + 8;
                out.setBounds((int) Math.floor(x) - 4, (int) Math.floor(y - size) - 4, w, h);
            }

            void reset(int width, int height) {
                size = 24 + rnd.nextInt(25);
                wobbleAmp = 8f + rnd.nextFloat() * 20f;
                float mx = marginX();
                baseX = -mx + rnd.nextFloat() * (width + 2f * mx);
                baseY = -mx + rnd.nextFloat() * (height + 2f * mx);
                float angle = rnd.nextFloat() * 6.2831855f;
                float speed = 60f + rnd.nextFloat() * 120f;
                vx = (float) Math.cos(angle) * speed;
                vy = (float) Math.sin(angle) * speed;
                float len = (float) Math.hypot(vx, vy);
                perpX = -vy / len;
                perpY = vx / len;
                t = 0f;
                wobblePhase = rnd.nextFloat() * 6.2831855f;
                wobbleFreq = 0.5f + rnd.nextFloat() * 1.0f;
                Color[] palette = {TEAL, CORAL, GOLD, VIOLET, PINK, GREEN, ICE};
                Color base = palette[rnd.nextInt(palette.length)];
                int alpha = 110 + rnd.nextInt(135);
                color = new Color(base.getRed(), base.getGreen(), base.getBlue(), alpha);
                buildImage();
            }

            private void buildImage() {
                Font font = fontFor(size);
                FontMetrics fm = METRICS_CANVAS.getFontMetrics(font);
                int tw = fm.stringWidth(text);
                int th = fm.getHeight();
                int ascent = fm.getAscent();
                image = new BufferedImage(Math.max(1, tw + 12), Math.max(1, th + 12),
                        BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2 = image.createGraphics();
                quality(g2);
                g2.setFont(font);
                g2.setColor(color);
                g2.drawString(text, 6, ascent + 6);
                g2.dispose();
                ox = -6;
                oy = -(ascent + 6);
            }

            private static Font fontFor(int size) {
                return FONT_CACHE.computeIfAbsent(size, sz -> displayFont(Font.BOLD, sz));
            }
        }
    }
    public static JLabel sectionTitle(String text, int size) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(displayFont(Font.BOLD, size));
        label.setForeground(TEXT);
        return label;
    }

    public static JPanel screenHeader(JButton backButton, String title, int titleSize) {
        JPanel header = new JPanel(new BorderLayout(12, 0));
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(10, 14, 8, 14));
        if (backButton != null) {
            header.add(backButton, BorderLayout.WEST);
        }
        JLabel label = title(title, titleSize);
        header.add(label, BorderLayout.CENTER);
        if (backButton != null) {
            JPanel spacer = new JPanel();
            spacer.setOpaque(false);
            Dimension d = backButton.getPreferredSize();
            spacer.setPreferredSize(d);
            spacer.setMinimumSize(d);
            spacer.setMaximumSize(d);
            header.add(spacer, BorderLayout.EAST);
        }
        return header;
    }

    public static JPanel roundedBar() {
        JPanel bar = new JPanel(new BorderLayout(12, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                paintQuality(g2);
                int w = getWidth();
                int h = getHeight();
                int r = capsule(h);
                g2.setColor(new Color(255, 255, 255, 20));
                g2.fillRoundRect(0, 0, w, h, r, r);
                g2.setColor(new Color(255, 255, 255, 34));
                g2.drawRoundRect(1, 1, w - 3, h - 3, r, r);
                g2.dispose();
            }
        };
        bar.setOpaque(false);
        bar.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        return bar;
    }

    public static JLabel badge(String text, Color accent) {
        return new Badge(text, accent);
    }

    public static JComponent letterTilesRow(String letters, int size) {
        JPanel row = new JPanel();
        row.setOpaque(false);
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        Color[] colors = {TEAL, GOLD, CORAL, VIOLET, PINK, GOLD, TEAL, CORAL};
        for (int i = 0; i < letters.length(); i++) {
            row.add(new LetterTile(letters.charAt(i), colors[i % colors.length], size));
            if (i < letters.length() - 1) {
                row.add(Box.createHorizontalStrut(5));
            }
        }
        return row;
    }

    public static class RoundBorder implements javax.swing.border.Border {
        private final Color color;
        private final int radius;
        private final int thickness;

        public RoundBorder(Color color, int radius, int thickness) {
            this.color = color;
            this.radius = radius;
            this.thickness = thickness;
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(0, 0, 0, 0);
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            quality(g2);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            int r = radius == CAPSULE ? capsule(h) : radius;
            g2.drawRoundRect(x, y, w - thickness, h - thickness, r, r);
            g2.dispose();
        }
    }

    public static class StyleField extends JTextField {
        private boolean focused;

        public StyleField() {
            super();
            addFocusListener(new java.awt.event.FocusAdapter() {
                @Override
                public void focusGained(java.awt.event.FocusEvent e) {
                    focused = true;
                    repaint();
                }

                @Override
                public void focusLost(java.awt.event.FocusEvent e) {
                    focused = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            paintQuality(g2);
            int w = getWidth();
            int h = getHeight();
            int r = capsule(h);
            g2.setColor(FIELD_BG);
            g2.fillRoundRect(0, 0, w, h, r, r);
            if (focused) {
                g2.setColor(new Color(32, 211, 194, 52));
                g2.setStroke(new BasicStroke(3.5f));
                g2.drawRoundRect(1, 1, w - 3, h - 3, r, r);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static class StylePasswordField extends JPasswordField {
        private boolean focused;

        public StylePasswordField() {
            super();
            addFocusListener(new java.awt.event.FocusAdapter() {
                @Override
                public void focusGained(java.awt.event.FocusEvent e) {
                    focused = true;
                    repaint();
                }

                @Override
                public void focusLost(java.awt.event.FocusEvent e) {
                    focused = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            paintQuality(g2);
            int w = getWidth();
            int h = getHeight();
            int r = capsule(h);
            g2.setColor(FIELD_BG);
            g2.fillRoundRect(0, 0, w, h, r, r);
            if (focused) {
                g2.setColor(new Color(32, 211, 194, 52));
                g2.setStroke(new BasicStroke(3.5f));
                g2.drawRoundRect(1, 1, w - 3, h - 3, r, r);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static class PlaceholderField extends StyleField {
        private final String placeholder;

        public PlaceholderField(String placeholder) {
            super();
            this.placeholder = placeholder;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (placeholder != null && getText().isEmpty() && !isFocusOwner()) {
                Graphics2D g2 = (Graphics2D) g.create();
                quality(g2);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.setColor(new Color(184, 199, 221, 130));
                g2.drawString(placeholder, 14, (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
                g2.dispose();
            }
        }
    }

    public static class PlaceholderPasswordField extends StylePasswordField {
        private final String placeholder;

        public PlaceholderPasswordField(String placeholder) {
            super();
            this.placeholder = placeholder;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (placeholder != null && getPassword().length == 0 && !isFocusOwner()) {
                Graphics2D g2 = (Graphics2D) g.create();
                quality(g2);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.setColor(new Color(184, 199, 221, 130));
                g2.drawString(placeholder, 14, (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
                g2.dispose();
            }
        }
    }

    public static class Badge extends JLabel {
        private final Color accent;

        public Badge(String text, Color accent) {
            super(text == null ? null : text.toUpperCase());
            this.accent = accent;
            setOpaque(false);
            setFont(displayFont(Font.BOLD, FONT_BADGE));
            setForeground(Color.WHITE);
            setBorder(BorderFactory.createEmptyBorder(5, 13, 5, 13));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            quality(g2);
            int w = getWidth();
            int h = getHeight();
            int r = h / 2;
            g2.setColor(alpha(accent, 42));
            g2.fillRoundRect(0, 0, w, h, r, r);
            g2.setColor(alpha(accent, 120));
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawRoundRect(1, 1, w - 3, h - 3, r, r);
            g2.dispose();
            super.paintComponent(g);
        }

        private static Color alpha(Color color, int a) {
            return new Color(color.getRed(), color.getGreen(), color.getBlue(), a);
        }
    }

    public static JPanel accentBar() {
        JPanel bar = new JPanel();
        bar.setOpaque(true);
        bar.setBackground(TEAL);
        bar.setPreferredSize(new Dimension(Integer.MAX_VALUE, 4));
        bar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 4));
        return bar;
    }

    public static JLabel chipLabel(String text) {
        JLabel chip = new JLabel(text);
        chip.setFont(displayFont(Font.BOLD, FONT_CARD_TITLE));
        chip.setForeground(ICE);
        chip.setBorder(BorderFactory.createCompoundBorder(
                new PillBorder(PANEL_BORDER, 1),
                BorderFactory.createEmptyBorder(6, 16, 6, 16)));
        return chip;
    }

    public static class PillBorder implements javax.swing.border.Border {
        private final Color color;
        private final int thickness;

        public PillBorder(Color color, int thickness) {
            this.color = color;
            this.thickness = thickness;
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(0, 0, 0, 0);
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            quality(g2);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            g2.drawRoundRect(x, y, w - thickness, h - thickness, h, h);
            g2.dispose();
        }
    }

    /* ==================== Neon components ==================== */

    public static final class Anim {
        private final javax.swing.Timer timer;
        private final Runnable onUpdate;
        private float value;
        private float target;
        private float ease = 0.22f;

        public Anim(Runnable onUpdate) {
            this.onUpdate = onUpdate;
            this.timer = new javax.swing.Timer(16, e -> tick());
            timer.setRepeats(true);
        }

        public void setTarget(float target) {
            if (Math.abs(this.target - target) < 0.0001f && !timer.isRunning()) {
                return;
            }
            this.target = target;
            if (!timer.isRunning()) {
                timer.start();
            }
        }

        public void setTarget(float target, float ease) {
            this.ease = ease;
            setTarget(target);
        }

        public float value() {
            return value;
        }

        public boolean running() {
            return timer.isRunning();
        }

        private void tick() {
            value += (target - value) * ease;
            if (Math.abs(target - value) < 0.001f) {
                value = target;
                timer.stop();
            }
            onUpdate.run();
        }
    }

    public static JPanel glowCard(LayoutManager layout) {
        return new GlowCard(layout);
    }

    public static class GlowCard extends JPanel {

        private static final Color[] RING = {TEAL, VIOLET, PINK, GOLD, TEAL};
        private final javax.swing.Timer timer;
        private BufferedImage chrome;
        private float phase;

        public GlowCard(LayoutManager layout) {
            super(layout);
            setOpaque(false);
            timer = new javax.swing.Timer(16, e -> {
                phase += 0.045f;
                repaint();
            });
            timer.setRepeats(true);
        }

        @Override
        public void addNotify() {
            super.addNotify();
            timer.start();
        }

        @Override
        public void removeNotify() {
            timer.stop();
            super.removeNotify();
        }

        private void buildChrome(int w, int h) {
            chrome = new BufferedImage(Math.max(1, w), Math.max(1, h), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = chrome.createGraphics();
            quality(g2);
            int radius = CARD_RADIUS;
            softShadow(g2, 0, 0, w, h, 26);

            g2.setPaint(new LinearGradientPaint(0, 0, 0, h,
                    new float[]{0f, 0.5f, 1f},
                    new Color[]{new Color(26, 50, 80, 218),
                            new Color(16, 32, 52, 228),
                            new Color(9, 20, 34, 240)}));
            g2.fillRoundRect(0, 0, w, h, radius, radius);

            Shape body = new RoundRectangle2D.Float(1, 1, w - 3, h - 3, radius, radius);
            g2.setClip(body);
            g2.setPaint(new LinearGradientPaint(w * 0.12f, 0, w * 0.9f, h,
                    new float[]{0f, 0.45f, 1f},
                    new Color[]{new Color(32, 211, 194, 34),
                            new Color(255, 255, 255, 6),
                            new Color(155, 93, 229, 34)}));
            g2.fillRect(0, 0, w, h);

            g2.setColor(new Color(255, 255, 255, 12));
            g2.fillRoundRect(3, 3, w - 6, h - 6, radius - 3, radius - 3);

            g2.setPaint(new RadialGradientPaint(
                    new Point2D.Float(radius, radius), radius * 1.4f,
                    new float[]{0f, 1f},
                    new Color[]{new Color(32, 211, 194, 70), new Color(32, 211, 194, 0)}));
            g2.fillRect(0, 0, w, h);
            g2.setPaint(new RadialGradientPaint(
                    new Point2D.Float(w - radius, h - radius), radius * 1.4f,
                    new float[]{0f, 1f},
                    new Color[]{new Color(155, 93, 229, 80), new Color(155, 93, 229, 0)}));
            g2.fillRect(0, 0, w, h);
            g2.setClip(null);

            g2.setClip(0, 0, w, 6);
            g2.setStroke(new BasicStroke(1.8f));
            g2.setColor(new Color(255, 255, 255, 84));
            g2.draw(new RoundRectangle2D.Float(1.1f, 1.1f, w - 2.2f, h - 2.2f, radius, radius));
            g2.setClip(null);
            g2.dispose();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            paintQuality(g2);
            int w = getWidth();
            int h = getHeight();
            if (w <= 0 || h <= 0) {
                g2.dispose();
                return;
            }
            if (chrome == null || chrome.getWidth() != w || chrome.getHeight() != h) {
                buildChrome(w, h);
            }
            g2.drawImage(chrome, 0, 0, null);

            int radius = CARD_RADIUS;
            float cx = w / 2f;
            float cy = h / 2f;
            float dx = (float) Math.cos(phase);
            float dy = (float) Math.sin(phase);
            float rad = (float) Math.hypot(w, h) / 2f;
            Point2D p1 = new Point2D.Float(cx - dx * rad, cy - dy * rad);
            Point2D p2 = new Point2D.Float(cx + dx * rad, cy + dy * rad);
            float pulse = 0.5f + 0.5f * (float) Math.sin(phase * 1.6f);

            Shape ring = new RoundRectangle2D.Float(1.2f, 1.2f, w - 2.4f, h - 2.4f, radius - 1, radius - 1);
            g2.setComposite(AlphaComposite.SrcOver.derive(0.28f + 0.15f * pulse));
            g2.setPaint(new LinearGradientPaint(p1, p2, new float[]{0f, 0.25f, 0.5f, 0.75f, 1f}, RING));
            g2.setStroke(new BasicStroke(7f));
            g2.draw(ring);
            g2.setComposite(AlphaComposite.SrcOver.derive(1f));
            g2.setStroke(new BasicStroke(2.4f));
            g2.draw(ring);

            g2.setClip(new RoundRectangle2D.Float(1, 1, w - 2, h - 2, radius, radius));
            float sweep = ((phase * 0.35f * (w + h)) % (w + 220f)) - 110f;
            g2.setPaint(new LinearGradientPaint(sweep - 130f, 0, sweep + 130f, 0,
                    new float[]{0f, 0.5f, 1f},
                    new Color[]{new Color(255, 255, 255, 0),
                            new Color(255, 255, 255, 26),
                            new Color(255, 255, 255, 0)}));
            g2.fillRect(0, 0, w, h);
            g2.setClip(null);
            g2.dispose();
        }
    }

    public static JPanel segmentTabs(String[] labels, IntConsumer onSelect) {
        return new SegmentTabs(labels, onSelect);
    }

    public static class SegmentTabs extends JPanel {

        private final JButton[] buttons;
        private final Anim slide = new Anim(() -> repaint());
        private final IntConsumer onSelect;
        private int selected;
        private int hovered = -1;
        private Rectangle from;
        private Rectangle to;
        private boolean pillReady;

        public SegmentTabs(String[] labels, IntConsumer onSelect) {
            this.onSelect = onSelect;
            setOpaque(false);
            setLayout(new GridLayout(1, labels.length, 8, 0));
            buttons = new JButton[labels.length];
            for (int i = 0; i < labels.length; i++) {
                final int index = i;
                JButton b = new JButton(labels[i]) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        paintQuality(g2);
                        g2.setFont(getFont());
                        FontMetrics fm = g2.getFontMetrics();
                        String text = getText();
                        int tx = (getWidth() - fm.stringWidth(text)) / 2;
                        int ty = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                        if (index == SegmentTabs.this.selected) {
                            g2.setColor(Color.WHITE);
                        } else if (index == hovered) {
                            g2.setColor(new Color(0xe8fbff));
                        } else {
                            g2.setColor(TEXT_MUTED);
                        }
                        g2.drawString(text, tx, ty);
                        g2.dispose();
                    }
                };
                b.setContentAreaFilled(false);
                b.setBorder(BorderFactory.createEmptyBorder(11, 18, 11, 18));
                b.setFocusPainted(false);
                b.setOpaque(false);
                b.setFont(displayFont(Font.BOLD, 16));
                b.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        hovered = index;
                        repaint();
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        if (hovered == index) {
                            hovered = -1;
                            repaint();
                        }
                    }
                });
                b.addActionListener(e -> {
                    SoundUtil.playClick();
                    select(index);
                    onSelect.accept(index);
                });
                buttons[i] = b;
                add(b);
            }
            selected = 0;
        }

        public void select(int index) {
            if (index < 0 || index >= buttons.length) {
                return;
            }
            if (pillReady) {
                from = pillRect();
            }
            selected = index;
            to = buttons[index].getBounds();
            if (!pillReady) {
                if (to.width > 0) {
                    pillReady = true;
                    from = to;
                }
                repaint();
                return;
            }
            slide.setTarget(0f);
            slide.setTarget(1f, 0.2f);
        }

        public int selectedIndex() {
            return selected;
        }

        private Rectangle pillRect() {
            float v = slide.value();
            if (from == null || to == null) {
                return new Rectangle();
            }
            int x = Math.round(from.x + (to.x - from.x) * v);
            int y = Math.round(from.y + (to.y - from.y) * v);
            int w = Math.round(from.width + (to.width - from.width) * v);
            int h = Math.round(from.height + (to.height - from.height) * v);
            return new Rectangle(x, y, w, h);
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (!pillReady && buttons.length > 0) {
                Rectangle r = buttons[selected].getBounds();
                if (r.width > 0) {
                    from = to = r;
                    pillReady = true;
                }
            }
            Graphics2D g2 = (Graphics2D) g.create();
            paintQuality(g2);
            int w = getWidth();
            int h = getHeight();
            if (w <= 0 || h <= 0) {
                g2.dispose();
                return;
            }
            g2.setColor(new Color(255, 255, 255, 26));
            g2.fillRoundRect(0, 0, w, h, capsule(h), capsule(h));
            g2.setColor(new Color(255, 255, 255, 38));
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(1, 1, w - 3, h - 3, capsule(h), capsule(h));

            Rectangle r = pillRect();
            if (r.width > 0 && r.height > 0) {
                g2.setPaint(new LinearGradientPaint(r.x, r.y, r.x + r.width, r.y + r.height,
                        new float[]{0f, 0.5f, 1f},
                        new Color[]{TEAL, VIOLET, PINK}));
                g2.setComposite(AlphaComposite.SrcOver.derive(0.34f));
                g2.fillRoundRect(r.x - 2, r.y - 2, r.width + 4, r.height + 4, capsule(r.height), capsule(r.height));
                g2.setComposite(AlphaComposite.SrcOver.derive(1f));
                g2.fillRoundRect(r.x, r.y, r.width, r.height, capsule(r.height), capsule(r.height));
                g2.setPaint(new GradientPaint(0, r.y, new Color(255, 255, 255, 66),
                        0, r.y + r.height / 2f, new Color(255, 255, 255, 0)));
                g2.fillRoundRect(r.x + 1, r.y + 1, r.width - 2, Math.max(1, r.height / 2 - 1), capsule(r.height), capsule(r.height));
                g2.setColor(new Color(255, 255, 255, 80));
                g2.drawRoundRect(r.x + 1, r.y + 1, r.width - 3, r.height - 3, capsule(r.height), capsule(r.height));
            }
            g2.dispose();
        }
    }

    public static JButton glowButton(String text, Color top, Color bottom) {
        return new GlowButton(text, top, bottom);
    }

    public static class GlowButton extends JButton {

        private final Color top;
        private final Color bottom;
        private final SmoothHover hover;
        private final javax.swing.Timer pulse;
        private float phase;
        private boolean pressed;

        public GlowButton(String text, Color top, Color bottom) {
            super(text);
            this.top = top;
            this.bottom = bottom;
            this.hover = new SmoothHover(this, this::repaint);
            setContentAreaFilled(false);
            setOpaque(false);
            setFocusPainted(false);
            setFont(displayFont(Font.BOLD, 17));
            setBorder(BorderFactory.createEmptyBorder(12, 26, 12, 26));
            pulse = new javax.swing.Timer(16, e -> {
                phase += 0.08f;
                repaint();
            });
            pulse.setRepeats(true);
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hover.enter();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hover.exit();
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    pressed = true;
                    SoundUtil.playClick();
                    repaint();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    pressed = false;
                    repaint();
                }
            });
        }

        @Override
        public void addNotify() {
            super.addNotify();
            pulse.start();
        }

        @Override
        public void removeNotify() {
            pulse.stop();
            super.removeNotify();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            paintQuality(g2);
            int w = getWidth();
            int h = getHeight();
            int radius = capsule(h);
            float hv = hover.value();

            if (pressed) {
                double s = 0.965;
                g2.translate(w / 2.0, h / 2.0);
                g2.scale(s, s);
                g2.translate(-w / 2.0, -h / 2.0);
            }

            if (!isEnabled()) {
                g2.setColor(new Color(255, 255, 255, 20));
                g2.fillRoundRect(0, 0, w, h, radius, radius);
            } else {
                float drift = 0.5f + 0.5f * (float) Math.sin(phase);
                Color cTop = lerp(top, lerp(top, bottom, 0.5f), 0.25f * drift);
                Color cBottom = lerp(bottom, lerp(bottom, top, 0.5f), 0.25f * (1f - drift));

                g2.setComposite(AlphaComposite.SrcOver.derive(0.35f + 0.25f * hv));
                g2.setPaint(new LinearGradientPaint(0, 0, 0, h, new float[]{0f, 1f},
                        new Color[]{cTop, cBottom}));
                g2.fillRoundRect(-3, 2, w + 6, h + 2, radius + 4, radius + 4);
                g2.setComposite(AlphaComposite.SrcOver.derive(1f));

                Color gTop = lerp(cTop, cTop.brighter(), hv);
                Color gBottom = pressed ? cBottom.darker() : cBottom;
                g2.setPaint(new LinearGradientPaint(0, 0, w, h,
                        new float[]{0f, 0.5f, 1f},
                        new Color[]{gTop, lerp(gTop, gBottom, 0.5f), gBottom}));
                g2.fillRoundRect(0, 0, w, h, radius, radius);

                Shape clip = new RoundRectangle2D.Float(0, 0, w, h, radius, radius);
                g2.setClip(clip);
                g2.setColor(new Color(255, 255, 255, 34 + Math.round(26 * hv)));
                g2.fillRoundRect(2, 2, w - 4, (int) (h * 0.45f), radius, radius);
                if (hv > 0.02f) {
                    float sweep = ((phase * 60f) % (w + 240f)) - 120f;
                    g2.setPaint(new LinearGradientPaint(sweep - 80f, 0, sweep + 80f, 0,
                            new float[]{0f, 0.5f, 1f},
                            new Color[]{new Color(255, 255, 255, 0),
                                    new Color(255, 255, 255, 120),
                                    new Color(255, 255, 255, 0)}));
                    g2.fillRoundRect(0, 0, w, h, radius, radius);
                }
                g2.setClip(null);

                g2.setPaint(new LinearGradientPaint(0, 0, w, h, new float[]{0f, 1f},
                        new Color[]{cTop.brighter(), cBottom}));
                g2.setStroke(new BasicStroke(1.6f));
                g2.drawRoundRect(1, 1, w - 3, h - 3, radius, radius);
            }

            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            String label = getText();
            int tx = (w - fm.stringWidth(label)) / 2;
            int ty = (h - fm.getHeight()) / 2 + fm.getAscent();
            g2.setColor(isEnabled() ? Color.WHITE : new Color(0x8fa2bd));
            g2.drawString(label, tx, ty);
            g2.dispose();
        }
    }

    public static JButton linkButton(String text) {
        JButton button = new JButton(text);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        button.setFont(displayFont(Font.BOLD, 14));
        button.setForeground(TEAL);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setForeground(new Color(0x9af2e8));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setForeground(TEAL);
            }
        });
        return button;
    }

    public static JTextField pillField(String placeholder, String icon) {
        return new PillField(placeholder, icon);
    }

    public static JPasswordField pillPassword(String placeholder, String icon) {
        return new PillPasswordField(placeholder, icon);
    }

    /** Static capsule field: fully-rounded chrome, optional leading icon and
     *  simple placeholder. The chrome is cached to an off-screen image so
     *  every keystroke only blits cached pixels + text = lag-free 60+ FPS. */
    public static class PillField extends JTextField {
        private static final Color BG = new Color(255, 255, 255, 16);
        private static final Color FILL = new Color(255, 255, 255, 10);
        private static final Color BORDER = new Color(255, 255, 255, 60);
        private static final Color MUTED = new Color(255, 255, 255, 120);

        private final String placeholder;
        private final String icon;
        private final int pad;
        private boolean focused;
        private BufferedImage chrome;
        private int chromeW = -1;
        private int chromeH = -1;

        public PillField(String placeholder, String icon) {
            this.placeholder = placeholder;
            this.icon = icon;
            this.pad = icon != null ? 46 : 16;
            setOpaque(false);
            setForeground(TEXT);
            setCaretColor(TEXT);
            setSelectionColor(TEAL);
            setSelectedTextColor(Color.WHITE);
            setFont(bodyFont(Font.PLAIN, 19));
            setBorder(BorderFactory.createEmptyBorder(0, pad, 0, 16));
            addFocusListener(new FocusAdapter() {
                @Override public void focusGained(FocusEvent e) { focused = true; repaint(); }
                @Override public void focusLost(FocusEvent e) { focused = false; repaint(); }
            });
        }

        private void rebuildChrome(int w, int h) {
            chrome = new BufferedImage(Math.max(1, w), Math.max(1, h), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = chrome.createGraphics();
            paintQuality(g2);
            int r = capsule(h);
            g2.setColor(BG);
            g2.fillRoundRect(0, 0, w, h, r, r);
            g2.setColor(FILL);
            g2.fillRoundRect(2, 2, w - 4, h - 4, Math.max(1, r - 2), Math.max(1, r - 2));
            if (icon != null) {
                g2.setFont(bodyFont(Font.PLAIN, 18));
                FontMetrics im = g2.getFontMetrics();
                g2.setColor(MUTED);
                g2.drawString(icon, 15, (h - im.getHeight()) / 2 + im.getAscent());
            }
            g2.setClip(2, 2, w - 4, Math.max(1, (h - 4) / 2));
            g2.setColor(new Color(255, 255, 255, 16));
            g2.fillRoundRect(2, 2, w - 4, h - 4, Math.max(1, r - 2), Math.max(1, r - 2));
            g2.setClip(null);
            g2.dispose();
            chromeW = w;
            chromeH = h;
        }

        @Override
        protected void paintComponent(Graphics g) {
            int w = getWidth(), h = getHeight();
            if (w > 0 && h > 0) {
                if (chrome == null || chromeW != w || chromeH != h) {
                    rebuildChrome(w, h);
                }
                Graphics2D g2 = (Graphics2D) g.create();
                paintQuality(g2);
                g2.drawImage(chrome, 0, 0, null);
                int r = capsule(h);
                if (placeholder != null && !placeholder.isEmpty() && getText().isEmpty() && !focused) {
                    g2.setFont(getFont());
                    FontMetrics fm = g2.getFontMetrics();
                    g2.setColor(MUTED);
                    g2.drawString(placeholder, pad, (h - fm.getHeight()) / 2 + fm.getAscent());
                }
                if (focused) {
                    g2.setStroke(new BasicStroke(5f));
                    g2.setColor(new Color(32, 211, 194, 42));
                    g2.drawRoundRect(2, 2, w - 5, h - 5, Math.max(1, r - 1), Math.max(1, r - 1));
                }
                g2.setStroke(new BasicStroke(focused ? 2.2f : 1.2f));
                g2.setColor(focused ? TEAL : BORDER);
                g2.drawRoundRect(1, 1, w - 3, h - 3, r, r);
                g2.dispose();
            }
            super.paintComponent(g);
        }
    }

    /** Password variant of PillField. */
    public static class PillPasswordField extends JPasswordField {
        private static final Color BG = new Color(255, 255, 255, 16);
        private static final Color FILL = new Color(255, 255, 255, 10);
        private static final Color BORDER = new Color(255, 255, 255, 60);
        private static final Color MUTED = new Color(255, 255, 255, 120);

        private final String placeholder;
        private final String icon;
        private final int pad;
        private boolean focused;
        private BufferedImage chrome;
        private int chromeW = -1;
        private int chromeH = -1;

        public PillPasswordField(String placeholder, String icon) {
            this.placeholder = placeholder;
            this.icon = icon;
            this.pad = icon != null ? 46 : 16;
            setOpaque(false);
            setForeground(TEXT);
            setCaretColor(TEXT);
            setSelectionColor(TEAL);
            setSelectedTextColor(Color.WHITE);
            setFont(bodyFont(Font.PLAIN, 19));
            setEchoChar('\u2022');
            setBorder(BorderFactory.createEmptyBorder(0, pad, 0, 16));
            addFocusListener(new FocusAdapter() {
                @Override public void focusGained(FocusEvent e) { focused = true; repaint(); }
                @Override public void focusLost(FocusEvent e) { focused = false; repaint(); }
            });
        }

        private void rebuildChrome(int w, int h) {
            chrome = new BufferedImage(Math.max(1, w), Math.max(1, h), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = chrome.createGraphics();
            paintQuality(g2);
            int r = capsule(h);
            g2.setColor(BG);
            g2.fillRoundRect(0, 0, w, h, r, r);
            g2.setColor(FILL);
            g2.fillRoundRect(2, 2, w - 4, h - 4, Math.max(1, r - 2), Math.max(1, r - 2));
            if (icon != null) {
                g2.setFont(bodyFont(Font.PLAIN, 18));
                FontMetrics im = g2.getFontMetrics();
                g2.setColor(MUTED);
                g2.drawString(icon, 15, (h - im.getHeight()) / 2 + im.getAscent());
            }
            g2.setClip(2, 2, w - 4, Math.max(1, (h - 4) / 2));
            g2.setColor(new Color(255, 255, 255, 16));
            g2.fillRoundRect(2, 2, w - 4, h - 4, Math.max(1, r - 2), Math.max(1, r - 2));
            g2.setClip(null);
            g2.dispose();
            chromeW = w;
            chromeH = h;
        }

        @Override
        protected void paintComponent(Graphics g) {
            int w = getWidth(), h = getHeight();
            if (w > 0 && h > 0) {
                if (chrome == null || chromeW != w || chromeH != h) {
                    rebuildChrome(w, h);
                }
                Graphics2D g2 = (Graphics2D) g.create();
                paintQuality(g2);
                g2.drawImage(chrome, 0, 0, null);
                int r = capsule(h);
                if (placeholder != null && !placeholder.isEmpty() && getPassword().length == 0 && !focused) {
                    g2.setFont(getFont());
                    FontMetrics fm = g2.getFontMetrics();
                    g2.setColor(MUTED);
                    g2.drawString(placeholder, pad, (h - fm.getHeight()) / 2 + fm.getAscent());
                }
                if (focused) {
                    g2.setStroke(new BasicStroke(5f));
                    g2.setColor(new Color(32, 211, 194, 42));
                    g2.drawRoundRect(2, 2, w - 5, h - 5, Math.max(1, r - 1), Math.max(1, r - 1));
                }
                g2.setStroke(new BasicStroke(focused ? 2.2f : 1.2f));
                g2.setColor(focused ? TEAL : BORDER);
                g2.drawRoundRect(1, 1, w - 3, h - 3, r, r);
                g2.dispose();
            }
            super.paintComponent(g);
        }
    }

    /** Container that slides and fades its children in. */
    public static class SlidePanel extends JPanel {
        private final Anim slide = new Anim(() -> repaint());

        public SlidePanel(LayoutManager layout) {
            super(layout);
            setOpaque(false);
        }

        public void play() {
            slide.setTarget(0f);
            slide.setTarget(1f, 0.2f);
        }

        @Override
        protected void paintChildren(Graphics g) {
            if (slide.running()) {
                float v = slide.value();
                Graphics2D g2 = (Graphics2D) g.create();
                quality(g2);
                g2.setComposite(AlphaComposite.SrcOver.derive(Math.max(0f, Math.min(1f, v))));
                g2.translate(0, (1f - v) * 26f);
                super.paintChildren(g2);
                g2.dispose();
            } else {
                super.paintChildren(g);
            }
        }
    }

    public static JPanel fadeCards(JComponent[] cards) {
        return new FadeCards(cards);
    }

    public static class FadeCards extends JPanel {

        private static final Color COVER_TOP = new Color(22, 43, 70, 212);
        private static final Color COVER_MID = new Color(15, 30, 48, 224);
        private static final Color COVER_BOTTOM = new Color(9, 20, 34, 238);

        private final CardLayout cards;
        private final JPanel deck;
        private final Anim fade = new Anim(() -> repaint());
        private int next;
        private int phase;
        private boolean active;

        public FadeCards(JComponent[] cards) {
            setOpaque(false);
            setLayout(new BorderLayout());
            this.cards = new CardLayout();
            this.deck = new JPanel(this.cards);
            deck.setOpaque(false);
            for (int i = 0; i < cards.length; i++) {
                deck.add(cards[i], "c" + i);
            }
            add(deck, BorderLayout.CENTER);
        }

        public void show(int index) {
            if (active || index < 0 || index >= deck.getComponentCount()) {
                return;
            }
            next = index;
            phase = 0;
            active = true;
            fade.setTarget(0f);
            fade.setTarget(1f, 0.2f);
        }

        @Override
        protected void paintChildren(Graphics g) {
            super.paintChildren(g);
            if (!active) {
                return;
            }
            int w = getWidth();
            int h = getHeight();
            if (w <= 0 || h <= 0) {
                return;
            }
            float p = fade.value();
            float alpha = p < 0.5f ? p / 0.5f : (1f - p) / 0.5f;
            if (alpha > 0.002f) {
                Graphics2D g2 = (Graphics2D) g.create();
                quality(g2);
                g2.setComposite(AlphaComposite.SrcOver.derive(Math.max(0f, Math.min(1f, alpha))));
                g2.setPaint(new LinearGradientPaint(0, 0, 0, h,
                        new float[]{0f, 0.55f, 1f},
                        new Color[]{COVER_TOP, COVER_MID, COVER_BOTTOM}));
                g2.fillRect(0, 0, w, h);
                g2.dispose();
            }
            if (phase == 0 && p >= 0.5f) {
                cards.show(deck, "c" + next);
                phase = 1;
            }
            if (p >= 0.999f) {
                active = false;
            }
        }
    }

    /* ==================== Decorative components ==================== */

    public static JComponent logoRow() {
        return logoRow(42, 24);
    }

    public static JComponent logoRow(int tileSize, int textSize) {
        JPanel row = new JPanel();
        row.setOpaque(false);
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));

        char[] letters = {'W', 'o', 'r', 'l', 'd'};
        Color[] colors = {TEAL, CORAL, GOLD, VIOLET, PINK};
        for (int i = 0; i < letters.length; i++) {
            row.add(new LetterTile(letters[i], colors[i % colors.length], tileSize));
            if (i < letters.length - 1) {
                row.add(Box.createHorizontalStrut(6));
            }
        }
        row.add(Box.createHorizontalStrut(16));

        JComponent divider = dividerLine();
        divider.setPreferredSize(new Dimension(2, tileSize));
        divider.setMaximumSize(new Dimension(2, tileSize));
        row.add(divider);
        row.add(Box.createHorizontalStrut(12));

        JLabel sub = new JLabel("of Wonder");
        sub.setFont(displayFont(Font.BOLD, textSize));
        sub.setForeground(ICE);
        row.add(sub);

        return row;
    }

    public static JPanel tabBar() {
        JPanel bar = new JPanel(new GridLayout(1, 2, 10, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                paintQuality(g2);
                int r = capsule(getHeight());
                g2.setColor(new Color(255, 255, 255, 28));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), r, r);
                g2.setColor(new Color(255, 255, 255, 38));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, r, r);
                g2.dispose();
            }
        };
        bar.setOpaque(false);
        return bar;
    }

    public static JPanel divider(String text) {
        return divider(text, 11);
    }

    public static JPanel divider(String text, int fontSize) {
        JPanel row = new JPanel();
        row.setOpaque(false);
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.add(dividerLine());
        row.add(Box.createHorizontalStrut(10));
        JLabel label = new JLabel(text);
        label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, fontSize));
        label.setForeground(TEXT_MUTED);
        row.add(label);
        row.add(Box.createHorizontalStrut(10));
        row.add(dividerLine());
        return row;
    }

    private static JComponent dividerLine() {
        return new JComponent() {
            {
                setOpaque(false);
                setPreferredSize(new Dimension(10, 1));
                setMinimumSize(new Dimension(8, 1));
                setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
            }

            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(new Color(255, 255, 255, 36));
                g.fillRect(0, getHeight() / 2, getWidth(), 1);
            }
        };
    }

    public static JComponent avatar(String initial) {
        JComponent avatar = new JComponent() {
            {
                setPreferredSize(new Dimension(44, 44));
                setMaximumSize(new Dimension(44, 44));
                setMinimumSize(new Dimension(44, 44));
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                quality(g2);
                g2.setPaint(new GradientPaint(0, 0, CORAL, getWidth(), getHeight(), PINK));
                g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 17));
                FontMetrics fm = g2.getFontMetrics();
                String s = initial == null || initial.isEmpty() ? "?" : initial.substring(0, 1).toUpperCase();
                int x = (getWidth() - fm.stringWidth(s)) / 2;
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(s, x, y);
                g2.dispose();
            }
        };
        return avatar;
    }

    public static class RoundedButton extends JButton {

        private Color base;
        private Color fg;
        private Color top;
        private Color bottom;
        private boolean outline;
        private boolean glass;
        private boolean gradient;
        private boolean pressed;
        private final SmoothHover hover;

        RoundedButton(String text, Color base, Color fg) {
            this(text, base, base, fg, false);
        }

        RoundedButton(String text, Color top, Color bottom, Color fg, boolean gradient) {
            super(text);
            this.top = top;
            this.bottom = bottom;
            this.base = top;
            this.fg = fg;
            this.gradient = gradient;
            this.hover = new SmoothHover(this, this::repaint);
            setFont(displayFont(Font.BOLD, 15));
            setBorder(BorderFactory.createEmptyBorder(10, 22, 10, 22));
            setContentAreaFilled(false);
            setOpaque(false);
            setFocusPainted(false);
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hover.enter();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hover.exit();
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    pressed = true;
                    SoundUtil.playClick();
                    repaint();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    pressed = false;
                    repaint();
                }
            });
        }

        public void setPalette(Color base, Color fg, boolean outline) {
            this.base = base;
            this.fg = fg;
            this.outline = outline;
            this.glass = false;
            this.gradient = false;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            paintQuality(g2);
            int w = getWidth();
            int h = getHeight();
            float hv = hover.value();
            int radius = capsule(h);

            if (pressed) {
                double s = 0.965;
                g2.translate(w / 2.0, h / 2.0);
                g2.scale(s, s);
                g2.translate(-w / 2.0, -h / 2.0);
            }

            if (!isEnabled()) {
                g2.setColor(DISABLED_FILL);
                g2.fillRoundRect(0, 0, w, h, radius, radius);
                g2.setColor(new Color(0x8fa2bd));
            } else if (gradient) {
                if (hv > 0.02f) {
                    g2.setComposite(AlphaComposite.SrcOver.derive(0.30f + 0.35f * hv));
                    g2.setPaint(new GradientPaint(0, 0, lerp(top, top.brighter(), hv),
                            h, h, pressed ? bottom.darker() : bottom));
                    g2.fillRoundRect(-3, 2, w + 6, h + 2, radius + 4, radius + 4);
                    g2.setComposite(AlphaComposite.SrcOver.derive(1f));
                }
                g2.setPaint(new GradientPaint(0, 0, lerp(top, top.brighter(), hv),
                        pressed ? 0 : h, h, pressed ? bottom.darker() : bottom));
                g2.fillRoundRect(0, 0, w, h, radius, radius);
                g2.setPaint(new GradientPaint(0, 0, new Color(255, 255, 255, 60), 0, h / 2f, new Color(255, 255, 255, 0)));
                g2.fillRoundRect(0, 0, w, h / 2, radius, radius);
                g2.setColor(fg);
            } else if (glass) {
                g2.setColor(pressed
                        ? new Color(255, 255, 255, 38)
                        : new Color(255, 255, 255, 22 + Math.round(10 * hv)));
                g2.fillRoundRect(0, 0, w, h, radius, radius);
                g2.setColor(lerp(new Color(255, 255, 255, 72), base, hv));
                g2.setStroke(new BasicStroke(1.4f + 0.6f * hv));
                g2.drawRoundRect(1, 1, w - 3, h - 3, radius, radius);
                g2.setColor(lerp(fg, base, hv));
            } else if (outline) {
                g2.setColor(lerp(new Color(0, 0, 0, 0), alpha(base, 40), hv));
                g2.fillRoundRect(0, 0, w, h, radius, radius);
                g2.setColor(lerp(base, base.brighter(), hv));
                g2.setStroke(new BasicStroke(2f + 0.5f * hv));
                g2.drawRoundRect(1, 1, w - 3, h - 3, radius, radius);
                g2.setColor(fg);
            } else {
                if (hv > 0.02f) {
                    g2.setComposite(AlphaComposite.SrcOver.derive(0.35f + 0.35f * hv));
                    g2.setPaint(new RadialGradientPaint(
                            new Point2D.Float(w / 2f, h / 2f), w * 0.7f,
                            new float[]{0f, 1f},
                            new Color[]{base, base.darker()}));
                    g2.fillRoundRect(-3, 2, w + 6, h + 2, radius + 4, radius + 4);
                    g2.setComposite(AlphaComposite.SrcOver.derive(1f));
                }
                g2.setColor(pressed ? base.darker() : lerp(base, base.brighter(), hv));
                g2.fillRoundRect(0, 0, w, h, radius, radius);
                g2.setPaint(new GradientPaint(0, 0, new Color(255, 255, 255, 50), 0, h / 2f, new Color(255, 255, 255, 0)));
                g2.fillRoundRect(0, 0, w, h / 2, radius, radius);
                g2.setColor(fg);
            }

            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            int x = (w - fm.stringWidth(getText())) / 2;
            int y = (h - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(getText(), x, y);
            g2.dispose();
        }

        private static Color alpha(Color color, int a) {
            return new Color(color.getRed(), color.getGreen(), color.getBlue(), a);
        }
    }

    public static class TileButton extends JButton {

        private final String title;
        private final String subtitle;
        private final String icon;
        private final Color accent;
        private Color subtitleColor = TEXT_MUTED;
        private boolean dark;
        private final SmoothHover hover;
        private boolean pressed;
        private BufferedImage chrome;
        private int chromeW = -1;
        private int chromeH = -1;
        private int chromeRadius = -1;
        private boolean chromeDark;

        public TileButton(String title, String subtitle, Color accent) {
            this(title, subtitle, accent, null);
        }

        public TileButton(String title, String subtitle, Color accent, String icon) {
            super();
            this.title = title;
            this.subtitle = subtitle;
            this.accent = accent;
            this.icon = icon;
            this.hover = new SmoothHover(this, this::repaint);
            setContentAreaFilled(false);
            setOpaque(false);
            setFocusPainted(false);
            setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hover.enter();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hover.exit();
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    pressed = true;
                    SoundUtil.playClick();
                    repaint();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    pressed = false;
                    repaint();
                }
            });
        }

        public void setSubtitleColor(Color color) {
            this.subtitleColor = color;
            repaint();
        }

        public void setDark(boolean dark) {
            this.dark = dark;
            chromeW = -1;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            paintQuality(g2);
            int w = getWidth();
            int h = getHeight();
            float hv = hover.value();

            int radius = capsule(h);
            if (chrome == null || chromeW != w || chromeH != h || chromeRadius != radius || chromeDark != dark) {
                chrome = new BufferedImage(Math.max(1, w), Math.max(1, h), BufferedImage.TYPE_INT_ARGB);
                Graphics2D cg = chrome.createGraphics();
                quality(cg);
                softShadow(cg, 0, 2, w, h - 2, Math.min(24, radius));
                if (dark) {
                    cg.setColor(new Color(14, 26, 42, 160));
                } else {
                    cg.setColor(PANEL_BG);
                }
                cg.fillRoundRect(0, 0, w, h, radius, radius);
                if (dark) {
                    cg.setColor(new Color(255, 255, 255, 28));
                } else {
                    cg.setColor(PANEL_BORDER);
                }
                cg.setStroke(new BasicStroke(1.4f));
                cg.drawRoundRect(1, 1, w - 3, h - 3, radius, radius);
                cg.dispose();
                chromeW = w;
                chromeH = h;
                chromeRadius = radius;
                chromeDark = dark;
            }
            g2.drawImage(chrome, 0, 0, null);

            if (!pressed && hv > 0.02f) {
                if (dark) {
                    g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(),
                            Math.round(38 * hv)));
                } else {
                    g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(),
                            Math.round(26 * hv)));
                }
                g2.fillRoundRect(0, 0, w, h, radius, radius);
                g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(),
                        Math.round(170 * hv)));
                g2.setStroke(new BasicStroke(1.4f + 0.8f * hv));
                g2.drawRoundRect(1, 1, w - 3, h - 3, radius, radius);
                g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(),
                        Math.round(55 * hv)));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(3, 3, w - 7, h - 7, Math.max(1, radius - 2), Math.max(1, radius - 2));
            } else if (pressed) {
                g2.setColor(new Color(0, 0, 0, 40));
                g2.fillRoundRect(0, 0, w, h, radius, radius);
            }

            int lift = Math.round(3f * hv);
            int hasIcon = icon != null && !icon.isEmpty() ? 1 : 0;
            int tf = Math.max(17, (int) Math.round(h * 0.17));
            int sf = Math.max(13, (int) Math.round(h * 0.13));
            int iconSize = Math.max(26, (int) Math.round(h * 0.32));
            Font titleFont = displayFont(Font.BOLD, tf);
            Font subFont = bodyFont(Font.PLAIN, sf);
            FontMetrics tm = g2.getFontMetrics(titleFont);
            FontMetrics sm = g2.getFontMetrics(subFont);
            FontMetrics im = g2.getFontMetrics(displayFont(Font.BOLD, iconSize));

            int iconH = hasIcon == 1 ? iconSize + 4 : 0;
            int subHeight = subtitle == null || subtitle.isEmpty() ? 0 : sm.getHeight() + 6;
            int total = (hasIcon == 1 ? im.getHeight() + 4 : 0) + tm.getHeight() + subHeight;
            int startY = (h - total) / 2 - lift;

            if (hasIcon == 1) {
                int iy = startY + im.getAscent();
                g2.setFont(displayFont(Font.BOLD, iconSize));
                int ix = (w - im.stringWidth(icon)) / 2;
                g2.drawString(icon, ix, iy);
                startY += im.getHeight() + 4;
            }

            int titleY = startY + tm.getAscent();
            g2.setFont(titleFont);
            g2.setColor(isEnabled() ? TEXT : new Color(0x8fa2bd));
            g2.drawString(title, (w - tm.stringWidth(title)) / 2, titleY);

            if (subHeight > 0) {
                g2.setFont(subFont);
                g2.setColor(isEnabled() ? subtitleColor : new Color(0x8fa2bd));
                int subY = titleY + tm.getDescent() + 6 + sm.getAscent();
                int availSubW = w - 36;
                String subClipped = subtitle;
                if (sm.stringWidth(subClipped) > availSubW) {
                    while (subClipped.length() > 1 && sm.stringWidth(subClipped + "...") > availSubW) {
                        subClipped = subClipped.substring(0, subClipped.length() - 1);
                    }
                    subClipped = subClipped + "...";
                }
                g2.drawString(subClipped, (w - sm.stringWidth(subClipped)) / 2, subY);
            }
            g2.dispose();
        }
    }

    public static class OptionButton extends JButton {

        private final String letter;
        private final String label;
        private State state = State.NEUTRAL;
        private final SmoothHover hover;
        private final Anim enter = new Anim(() -> repaint());
        private final Anim pop = new Anim(() -> repaint());
        private final Anim shake = new Anim(() -> repaint());
        private boolean pressed;

        public OptionButton(String letter, String label) {
            super();
            this.letter = letter;
            this.label = label;
            this.hover = new SmoothHover(this, this::repaint);
            setContentAreaFilled(false);
            setOpaque(false);
            setFocusPainted(false);
            setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hover.enter();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hover.exit();
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    pressed = true;
                    SoundUtil.playClick();
                    repaint();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    pressed = false;
                    repaint();
                }
            });
        }

        public void reset() {
            state = State.NEUTRAL;
            repaint();
        }

        public void setSelected() {
            state = State.SELECTED;
            repaint();
        }

        public void setCorrect() {
            state = State.CORRECT;
            pop.setTarget(0f);
            pop.setTarget(1f, 0.16f);
            repaint();
        }

        public void setWrong() {
            state = State.WRONG;
            shake.setTarget(0f);
            shake.setTarget(1f, 0.22f);
            repaint();
        }

        public void playEnter(int delay) {
            javax.swing.Timer t = new javax.swing.Timer(delay, e -> {
                enter.setTarget(0f);
                enter.setTarget(1f, 0.18f);
            });
            t.setRepeats(false);
            t.start();
        }

        public String getLetter() {
            return letter;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            paintQuality(g2);
            int w = getWidth();
            int h = getHeight();

            float ev = enter.value();
            if (enter.running()) {
                g2.setComposite(AlphaComposite.SrcOver.derive(Math.max(0f, Math.min(1f, ev))));
                g2.translate(0, (1f - ev) * 22f);
            }
            float pv = pop.value();
            if (pop.running()) {
                double bump = 1.0 + 0.07 * Math.sin(pv * Math.PI);
                g2.translate(w / 2.0, h / 2.0);
                g2.scale(bump, bump);
                g2.translate(-w / 2.0, -h / 2.0);
            }
            float shv = shake.value();
            if (shake.running()) {
                double off = Math.sin(shv * 30.0) * 5.0 * (1.0 - shv);
                g2.translate(off, 0);
            }

            float hv = hover.value();
            int radius = capsule(h);

            Color fill;
            Color border;
            Color bubble;
            switch (state) {
                case SELECTED:
                    fill = new Color(32, 211, 194, 46);
                    border = TEAL;
                    bubble = TEAL;
                    break;
                case CORRECT:
                    fill = new Color(61, 220, 151, 46);
                    border = GREEN;
                    bubble = GREEN;
                    break;
                case WRONG:
                    fill = new Color(255, 93, 93, 40);
                    border = UITheme.ERROR;
                    bubble = UITheme.ERROR;
                    break;
                default:
                    fill = pressed ? PANEL_BG : lerp(PANEL_BG, new Color(32, 211, 194, 44), hv);
                    border = lerp(PANEL_BORDER, TEAL, hv);
                    bubble = GOLD;
            }

            g2.setColor(fill);
            g2.fillRoundRect(0, 0, w, h, radius, radius);
            g2.setColor(border);
            g2.setStroke(new BasicStroke(1.4f + 0.8f * hv));
            g2.drawRoundRect(1, 1, w - 3, h - 3, radius, radius);
            if (hv > 0.02f) {
                g2.setColor(new Color(32, 211, 194, Math.round(60 * hv)));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(3, 3, w - 7, h - 7, radius - 2, radius - 2);
            }

            int lift = Math.round(2f * hv);
            int slide = Math.round(5f * hv);
            int bubbleSize = Math.max(30, (int) Math.round(h * 0.55));
            int bx = 14 + slide;
            int by = (h - bubbleSize) / 2 - lift;
            int bubbleRadius = capsule(bubbleSize);
            g2.setColor(bubble);
            g2.fillRoundRect(bx, by, bubbleSize, bubbleSize, bubbleRadius, bubbleRadius);
            g2.setColor(new Color(255, 255, 255, 45));
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(bx, by, bubbleSize, bubbleSize, bubbleRadius, bubbleRadius);

            int letterFont = Math.max(15, (int) Math.round(bubbleSize * 0.5));
            g2.setFont(displayFont(Font.BOLD, letterFont));
            FontMetrics bm = g2.getFontMetrics();
            int lx = bx + (bubbleSize - bm.stringWidth(letter)) / 2;
            int ly = by + (bubbleSize - bm.getHeight()) / 2 + bm.getAscent();
            g2.setColor(Color.WHITE);
            g2.drawString(letter, lx, ly);

            int labelFont = Math.max(15, (int) Math.round(h * 0.27));
            g2.setFont(bodyFont(Font.BOLD, labelFont));
            FontMetrics tm = g2.getFontMetrics();
            int tx = bx + bubbleSize + 14;
            int ty = (h - tm.getHeight()) / 2 + tm.getAscent() - lift;
            g2.setColor(isEnabled() ? TEXT : new Color(0x8fa2bd));
            String clipped = clip(tm, label, w - tx - 12);
            g2.drawString(clipped, tx, ty);
            g2.dispose();
        }

        private String clip(FontMetrics fm, String text, int maxWidth) {
            if (fm.stringWidth(text) <= maxWidth) {
                return text;
            }
            String ellipsis = "...";
            int ellipsisWidth = fm.stringWidth(ellipsis);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < text.length(); i++) {
                char ch = text.charAt(i);
                if (fm.stringWidth(sb.toString() + ch) + ellipsisWidth > maxWidth) {
                    break;
                }
                sb.append(ch);
            }
            return sb.toString() + ellipsis;
        }

        private enum State {
            NEUTRAL, SELECTED, CORRECT, WRONG
        }
    }

    public enum GameIcon {
        QUIZ, WORDSEARCH, CUPS, WORDS
    }

    public static class GameModeCard extends JButton {

        private final GameIcon icon;
        private final String title;
        private final String subtitle;
        private final Color accent;
        private Color bandTop;
        private Color bandBottom;
        private boolean pressed;
        private final SmoothHover hover;
        private BufferedImage chrome;
        private int chromeW = -1;
        private int chromeH = -1;

        public GameModeCard(GameIcon icon, String title, String subtitle, Color accent) {
            super();
            this.icon = icon;
            this.title = title;
            this.subtitle = subtitle;
            this.accent = accent;
            this.bandTop = accent;
            this.bandBottom = accent.darker();
            this.hover = new SmoothHover(this, this::repaint);
            setContentAreaFilled(false);
            setOpaque(false);
            setFocusPainted(false);
            setBorder(BorderFactory.createEmptyBorder(10, 14, 14, 14));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hover.enter();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hover.exit();
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    pressed = true;
                    SoundUtil.playClick();
                    repaint();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    pressed = false;
                    repaint();
                }
            });
        }

        public void setBand(Color top, Color bottom) {
            this.bandTop = top;
            this.bandBottom = bottom;
            chrome = null;
            chromeW = -1;
            chromeH = -1;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            paintQuality(g2);
            int w = getWidth();
            int h = getHeight();
            float hv = hover.value();
            int lift = Math.round(4f * hv);
            int radius = Math.min(CARD_RADIUS, capsule(h));
            int bandH = Math.max(84, (int) (h * 0.32f));

            if (chrome == null || chromeW != w || chromeH != h) {
                chrome = new BufferedImage(Math.max(1, w), Math.max(1, h), BufferedImage.TYPE_INT_ARGB);
                buildChrome(chrome, w, h, radius, bandH);
                chromeW = w;
                chromeH = h;
            }

            if (hv > 0.02f) {
                int glowR = Math.round(Math.max(w, h) * 0.95f);
                g2.setPaint(new RadialGradientPaint(
                        new Point2D.Float(w / 2f, h * 0.38f), glowR,
                        new float[]{0f, 0.5f, 1f},
                        new Color[]{new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), Math.round(55 * hv)),
                                new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), Math.round(25 * hv)),
                                new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 0)}));
                g2.fillRoundRect(0, 0, w, h, radius, radius);
            }
            g2.drawImage(chrome, 0, 0, null);

            if (pressed) {
                g2.setColor(new Color(0, 0, 0, 50));
                g2.fillRoundRect(0, 0, w, h, radius, radius);
            }

            if (hv > 0.02f) {
                g2.setPaint(new LinearGradientPaint(0, 0, w, h,
                        new float[]{0f, 0.5f, 1f},
                        new Color[]{new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), (int) (150 * hv + 40)),
                                new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), (int) (100 * hv + 30)),
                                new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), (int) (60 * hv + 20))}));
                g2.setStroke(new BasicStroke(1.6f + 1.0f * hv));
                g2.drawRoundRect(1, 1, w - 3, h - 3, radius, radius);
            }

            int yOff = -lift;

            int pillW = Math.max(130, (int) Math.round(w * 0.52f));
            int pillH = Math.max(42, (int) Math.round(h * 0.11f));
            int pillY = h - pillH - 18 + yOff;

            int titleSize = Math.max(22, (int) Math.round(h * 0.060f));
            g2.setFont(displayFont(Font.BOLD, titleSize));
            FontMetrics tm = g2.getFontMetrics();
            String titleClipped = clip(tm, title, w - 20);
            int titleY = bandH + 20 + tm.getAscent() + yOff;
            g2.setColor(isEnabled() ? new Color(0xf0f6ff) : new Color(0x8fa2bd));
            g2.drawString(titleClipped, (w - tm.stringWidth(titleClipped)) / 2, titleY);

            int subSize = Math.max(15, (int) Math.round(h * 0.043f));
            g2.setFont(bodyFont(Font.PLAIN, subSize));
            FontMetrics sm = g2.getFontMetrics();
            int subBaseline = titleY + tm.getDescent() + 7 + sm.getAscent();
            g2.setColor(isEnabled() ? new Color(0xb8c8e8) : new Color(0x8fa2bd));
            drawSubtitle(g2, sm, subtitle, w - 24, subBaseline, sm.getHeight() + 3, pillY - 16);

            int px = (w - pillW) / 2;

            if (hv > 0.02f) {
                int glowR = Math.round(pillH * 2.2f);
                g2.setPaint(new RadialGradientPaint(
                        new Point2D.Float(px + pillW / 2f, pillY + pillH / 2f), glowR,
                        new float[]{0f, 0.5f, 1f},
                        new Color[]{new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), Math.round(80 * hv)),
                                new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), Math.round(30 * hv)),
                                new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 0)}));
                g2.fillOval(px - pillH, pillY - pillH, pillW + pillH * 2, pillH * 3);
            }

            g2.setPaint(new GradientPaint(0, 0, lerp(accent, accent.brighter(), hv),
                    0, pillH, isEnabled() ? accent.darker() : new Color(0x8fa2bd)));
            g2.fillRoundRect(px, pillY, pillW, pillH, pillH / 2, pillH / 2);
            g2.setPaint(new GradientPaint(0, pillY, new Color(255, 255, 255, 60),
                    0, pillY + pillH / 2f, new Color(255, 255, 255, 0)));
            g2.fillRoundRect(px, pillY, pillW, pillH / 2, pillH / 2, pillH / 2);
            int pillSize = Math.max(16, (int) Math.round(h * 0.043f));
            g2.setFont(displayFont(Font.BOLD, pillSize));
            FontMetrics pf = g2.getFontMetrics();
            String play = "Play Now";
            g2.setColor(Color.WHITE);
            g2.drawString(play, px + (pillW - pf.stringWidth(play)) / 2,
                    pillY + (pillH - pf.getHeight()) / 2 + pf.getAscent());
            g2.dispose();
        }

        private void buildChrome(BufferedImage img, int w, int h, int radius, int bandH) {
            Graphics2D g2 = img.createGraphics();
            quality(g2);
            softShadow(g2, 0, 2, w, h - 2, radius);
            g2.setColor(PANEL_BG);
            g2.fillRoundRect(0, 0, w, h, radius, radius);

            Shape cardBody = new RoundRectangle2D.Float(0, 0, w, h, radius, radius);
            g2.setClip(cardBody);
            g2.setPaint(new LinearGradientPaint(w * 0.2f, 0, w * 0.8f, h,
                    new float[]{0f, 0.55f, 1f},
                    new Color[]{new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 26),
                            new Color(255, 255, 255, 0),
                            new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 30)}));
            g2.fillRect(0, 0, w, h);
            g2.setPaint(new RadialGradientPaint(
                    new Point2D.Float(w * 0.5f, h), Math.max(w, h) * 0.7f,
                    new float[]{0f, 1f},
                    new Color[]{new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 26),
                            new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 0)}));
            g2.fillRect(0, 0, w, h);
            g2.setClip(null);

            g2.setStroke(new BasicStroke(1.2f));
            g2.setPaint(new LinearGradientPaint(0, 0, w, h,
                    new float[]{0f, 0.5f, 1f},
                    new Color[]{new Color(255, 255, 255, 90),
                            new Color(255, 255, 255, 26),
                            new Color(255, 255, 255, 60)}));
            g2.drawRoundRect(1, 1, w - 3, h - 3, radius, radius);

            Shape clip = new RoundRectangle2D.Float(0, 0, w, h, radius, radius);
            g2.setClip(clip);
            g2.setPaint(new GradientPaint(0, 0, bandTop, 0, bandH, bandBottom));
            g2.fillRect(0, 0, w, bandH);

            int bandGlowR = Math.round(Math.max(w, bandH) * 0.95f);
            g2.setPaint(new RadialGradientPaint(
                    new Point2D.Float(w / 2f, bandH * 0.1f), bandGlowR,
                    new float[]{0f, 1f},
                    new Color[]{new Color(255, 255, 255, 70), new Color(255, 255, 255, 0)}));
            g2.fillRect(0, 0, w, bandH);
            g2.setPaint(new RadialGradientPaint(
                    new Point2D.Float(w * 0.88f, bandH * 0.8f), bandGlowR * 0.8f,
                    new float[]{0f, 1f},
                    new Color[]{new Color(255, 255, 255, 55), new Color(255, 255, 255, 0)}));
            g2.fillRect(0, 0, w, bandH);
            g2.setPaint(new RadialGradientPaint(
                    new Point2D.Float(w * 0.12f, bandH * 0.7f), bandGlowR * 0.75f,
                    new float[]{0f, 1f},
                    new Color[]{new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 80),
                            new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 0)}));
            g2.fillRect(0, 0, w, bandH);

            g2.setColor(new Color(0, 0, 0, 70));
            g2.fillRect(0, bandH - 2, w, 2);
            g2.setPaint(new LinearGradientPaint(0, bandH - 1, w, bandH - 1,
                    new float[]{0f, 0.5f, 1f},
                    new Color[]{new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 60),
                            new Color(255, 255, 255, 40),
                            new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 70)}));
            g2.fillRect(0, bandH - 1, w, 1);
            g2.setClip(null);

            int iconPad = Math.max(14, Math.round(bandH * 0.13f));
            int iconSize = Math.min(w - 2 * iconPad, bandH - 2 * iconPad);
            g2.setClip(new RoundRectangle2D.Float(0, 0, w, bandH, radius, radius));
            int iconCx = w / 2;
            int iconCy = bandH / 2 + Math.round(iconSize * 0.02f);
            g2.setPaint(new RadialGradientPaint(
                    new Point2D.Float(iconCx, iconCy), iconSize * 0.75f,
                    new float[]{0f, 1f},
                    new Color[]{new Color(255, 255, 255, 40), new Color(255, 255, 255, 0)}));
            g2.fillOval(iconCx - iconSize / 2, iconCy - iconSize / 2, iconSize, iconSize);
            paintIcon(g2, icon, iconCx, iconCy, iconSize);
            g2.setClip(null);
            g2.dispose();
        }

        private void paintIcon(Graphics2D g2, GameIcon icon, int cx, int cy, int size) {
            switch (icon) {
                case QUIZ:
                    paintGlobeIcon(g2, cx, cy, size);
                    break;
                case WORDSEARCH:
                    paintSearchIcon(g2, cx, cy, size);
                    break;
                case CUPS:
                    paintCupsIcon(g2, cx, cy, size);
                    break;
                case WORDS:
                    paintWordsIcon(g2, cx, cy, size);
                    break;
                default:
                    break;
            }
        }

        private void paintGlobeIcon(Graphics2D g2, int cx, int cy, int size) {
            float r = size / 2f;
            g2.setPaint(new RadialGradientPaint(
                    new Point2D.Float(cx - r * 0.35f, cy - r * 0.42f), r * 1.35f,
                    new float[]{0f, 1f},
                    new Color[]{new Color(0xbfefff), new Color(0x166a8f)}));
            g2.fillOval((int) (cx - r), (int) (cy - r), size, size);

            g2.setColor(new Color(255, 255, 255, 120));
            g2.setStroke(new BasicStroke(Math.max(1.5f, size * 0.028f)));
            g2.drawOval((int) (cx - r * 0.5f), (int) (cy - r), size, size);
            g2.drawOval((int) (cx - r), (int) (cy - r * 0.5f), size, size);
            g2.drawLine(cx, (int) (cy - r), cx, (int) (cy + r));
            g2.drawLine((int) (cx - r), cy, (int) (cx + r), cy);

            g2.setPaint(new RadialGradientPaint(
                    new Point2D.Float(cx - r * 0.42f, cy - r * 0.48f), r * 0.75f,
                    new float[]{0f, 1f},
                    new Color[]{new Color(255, 255, 255, 200), new Color(255, 255, 255, 0)}));
            g2.fillOval((int) (cx - r * 0.6f), (int) (cy - r * 0.65f),
                    (int) (r * 0.85f), (int) (r * 0.5f));

            g2.setStroke(new BasicStroke(Math.max(2.5f, size * 0.055f)));
            g2.setPaint(new LinearGradientPaint(cx - r, cy - r, cx + r, cy + r,
                    new float[]{0f, 1f},
                    new Color[]{new Color(0x9fe7ff), new Color(0xffc93c)}));
            g2.drawOval((int) (cx - r), (int) (cy - r), size, size);
        }

        private void paintSearchIcon(Graphics2D g2, int cx, int cy, int size) {
            int gridSize = Math.round(size * 0.55f);
            int gx = cx - gridSize / 2;
            int gy = cy - gridSize / 2;
            int n = 3;
            float cell = gridSize / (float) n;
            Color[] tileColors = {TEAL, VIOLET, GOLD, PINK, ICE};
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    int tx = Math.round(gx + j * cell);
                    int ty = Math.round(gy + i * cell);
                    int cw = (int) cell - 2;
                    Color c = tileColors[(i + j) % tileColors.length];
                    g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 120));
                    g2.fillRoundRect(tx, ty, cw, cw, Math.max(3, cw / 3), Math.max(3, cw / 3));
                    g2.setColor(new Color(255, 255, 255, 90));
                    g2.setStroke(new BasicStroke(1f));
                    g2.drawRoundRect(tx, ty, cw, cw, Math.max(3, cw / 3), Math.max(3, cw / 3));
                    if ((i + j) % 3 == 0) {
                        int dot = Math.max(2, cw / 4);
                        g2.setColor(new Color(255, 255, 255, 150));
                        g2.fillOval(tx + cw / 2 - dot / 2, ty + cw / 2 - dot / 2, dot, dot);
                    }
                }
            }

            float mr = size * 0.24f;
            float mx = cx + size * 0.20f;
            float my = cy + size * 0.22f;
            float mw = mr * 2f;
            g2.setPaint(new RadialGradientPaint(
                    new Point2D.Float(mx - mr * 0.4f, my - mr * 0.4f), mr * 1.4f,
                    new float[]{0f, 1f},
                    new Color[]{new Color(255, 255, 255, 190), new Color(0x9fe7ff)}));
            g2.setStroke(new BasicStroke(Math.max(3f, size * 0.085f)));
            g2.drawOval((int) (mx - mr), (int) (my - mr), (int) mw, (int) mw);
            float hx = mx + mr * 0.72f;
            float hy = my + mr * 0.72f;
            float ex = mx + mr * 1.55f;
            float ey = my + mr * 1.55f;
            g2.setStroke(new BasicStroke(Math.max(3f, size * 0.085f)));
            g2.drawLine((int) hx, (int) hy, (int) ex, (int) ey);
            g2.setPaint(new RadialGradientPaint(
                    new Point2D.Float(mx, my), mr * 0.9f,
                    new float[]{0f, 1f},
                    new Color[]{new Color(255, 255, 255, 60), new Color(255, 255, 255, 0)}));
            g2.fillOval((int) (mx - mr * 0.6f), (int) (my - mr * 0.6f),
                    (int) (mr * 1.2f), (int) (mr * 1.2f));
        }

        private void paintCupsIcon(Graphics2D g2, int cx, int cy, int size) {
            float u = size / 100f;
            int tubeW = Math.round(34 * u);
            int tubeH = Math.round(56 * u);

            int rx = cx + Math.round(24 * u);
            int ry = cy - Math.round(20 * u);
            drawTestTube(g2, rx, ry, tubeW, tubeH, 0.72f,
                    new Color(0x4be3ff), new Color(0x0fa896));

            int lx = cx - Math.round(30 * u);
            int ly = cy - Math.round(34 * u);
            g2.translate(lx, ly);
            g2.rotate(-0.85f);
            drawTestTube(g2, -tubeW / 2, -tubeH / 2, tubeW, tubeH, 0.55f,
                    new Color(0xffd166), new Color(0xf5a623));
            g2.rotate(0.85f);
            g2.translate(-lx, -ly);

            float sx = lx + tubeW * 0.15f;
            float sy = ly - tubeH * 0.42f;
            float ex = rx - tubeW * 0.18f;
            float ey = ry + tubeH * 0.30f;
            g2.setStroke(new BasicStroke(Math.max(2.5f, size * 0.030f)));
            g2.setColor(new Color(0xffd166));
            java.awt.geom.Path2D stream = new java.awt.geom.Path2D.Float();
            stream.moveTo(sx, sy);
            stream.quadTo(sx + (ex - sx) * 0.55f, sy + (ey - sy) * 0.9f, ex, ey);
            g2.draw(stream);
            g2.setColor(new Color(255, 201, 60, 140));
            g2.fillOval((int) ex, (int) (ey - 3 * u), Math.max(3, Math.round(6 * u)), Math.max(3, Math.round(6 * u)));
        }

        private void paintWordsIcon(Graphics2D g2, int cx, int cy, int size) {
            float r = size * 0.34f;
            int nodeR = Math.max(5, Math.round(size * 0.09f));

            g2.setPaint(new RadialGradientPaint(
                    new Point2D.Float(cx, cy), r * 1.6f,
                    new float[]{0f, 1f},
                    new Color[]{new Color(32, 211, 194, 50), new Color(32, 211, 194, 0)}));
            g2.fillOval((int) (cx - r * 1.6f), (int) (cy - r * 1.6f),
                    (int) (r * 3.2f), (int) (r * 3.2f));

            g2.setColor(new Color(255, 255, 255, 80));
            g2.setStroke(new BasicStroke(Math.max(1.5f, size * 0.025f)));
            g2.drawOval((int) (cx - r), (int) (cy - r), (int) (r * 2), (int) (r * 2));

            String letters = "WORD";
            Color[] nc = {TEAL, GOLD, CORAL, VIOLET};
            int n = letters.length();
            for (int i = 0; i < n; i++) {
                float a = (float) (Math.PI * 2 * i / n - Math.PI / 2);
                int nx = (int) (cx + r * Math.cos(a));
                int ny = (int) (cy + r * Math.sin(a));
                float na = (float) (Math.PI * 2 * ((i + 1) % n) / n - Math.PI / 2);
                g2.setColor(new Color(255, 255, 255, 50));
                g2.setStroke(new BasicStroke(Math.max(1f, size * 0.015f)));
                g2.drawLine(nx, ny, (int) (cx + r * Math.cos(na)), (int) (cy + r * Math.sin(na)));
                g2.setColor(nc[i % nc.length]);
                g2.fillOval(nx - nodeR, ny - nodeR, nodeR * 2, nodeR * 2);
                g2.setColor(new Color(255, 255, 255, 80));
                g2.setStroke(new BasicStroke(1f));
                g2.drawOval(nx - nodeR, ny - nodeR, nodeR * 2, nodeR * 2);
                int fs = Math.max(8, Math.round(size * 0.11f));
                g2.setFont(displayFont(Font.BOLD, fs));
                FontMetrics fm = g2.getFontMetrics();
                String s = String.valueOf(letters.charAt(i));
                g2.setColor(Color.WHITE);
                g2.drawString(s, nx - fm.stringWidth(s) / 2, ny + fm.getAscent() / 2 - 1);
            }
        }

        private void drawTestTube(Graphics2D g2, int x, int y, int w, int h,
                                  float liquidFrac, Color liquidTop, Color liquidBottom) {
            int r = Math.max(6, w / 2);
            g2.setColor(new Color(255, 255, 255, 46));
            g2.fillRoundRect(x, y, w, h, r, r);

            int liqH = Math.round(h * liquidFrac);
            Shape clip = new RoundRectangle2D.Float(x + 2, y + 2, w - 4, h - 4, Math.max(4, r - 2), Math.max(4, r - 2));
            g2.setClip(clip);
            g2.setPaint(new GradientPaint(0, y + h - liqH, liquidTop, 0, y + h, liquidBottom));
            g2.fillRoundRect(x + 2, y + h - liqH, w - 4, liqH, Math.max(4, r - 2), Math.max(4, r - 2));
            g2.setColor(new Color(255, 255, 255, 70));
            g2.fillRect(x + 3, y + 3, 3, h - 6);
            g2.setClip(null);

            g2.setColor(new Color(255, 255, 255, 140));
            g2.setStroke(new BasicStroke(Math.max(1.5f, w * 0.10f)));
            g2.drawRoundRect(x, y, w, h, r, r);
            g2.drawLine(x, y, x + w, y);
        }

        private void drawSubtitle(Graphics2D g2, FontMetrics fm, String text, int maxWidth,
                                  int baseline, int lineHeight) {
            String[] words = text == null ? new String[0] : text.split(" ");
            List<String> lines = new ArrayList<>();
            StringBuilder cur = new StringBuilder();
            for (String word : words) {
                String next = cur.length() == 0 ? word : cur + " " + word;
                if (fm.stringWidth(next) <= maxWidth) {
                    cur = new StringBuilder(next);
                } else {
                    lines.add(cur.toString());
                    cur = new StringBuilder(word);
                }
            }
            if (cur.length() > 0) {
                lines.add(cur.toString());
            }
            int limit = Math.min(lines.size(), 3);
            for (int i = 0; i < limit; i++) {
                String line = lines.get(i);
                if (i == 1 && lines.size() > 2) {
                    line = clip(fm, line, maxWidth);
                }
                g2.drawString(line, (getWidth() - fm.stringWidth(line)) / 2, baseline + i * lineHeight);
            }
        }

        private void drawSubtitle(Graphics2D g2, FontMetrics fm, String text, int maxWidth,
                                  int baseline, int lineHeight, int maxBaseline) {
            String[] words = text == null ? new String[0] : text.split(" ");
            List<String> lines = new ArrayList<>();
            StringBuilder cur = new StringBuilder();
            for (String word : words) {
                String next = cur.length() == 0 ? word : cur + " " + word;
                if (fm.stringWidth(next) <= maxWidth) {
                    cur = new StringBuilder(next);
                } else {
                    lines.add(cur.toString());
                    cur = new StringBuilder(word);
                }
            }
            if (cur.length() > 0) {
                lines.add(cur.toString());
            }
            int limit = Math.min(lines.size(), 3);
            for (int i = 0; i < limit; i++) {
                if (baseline + i * lineHeight + fm.getDescent() > maxBaseline) {
                    break;
                }
                String line = lines.get(i);
                if (i == 1 && lines.size() > 2) {
                    line = clip(fm, line, maxWidth);
                }
                g2.drawString(line, (getWidth() - fm.stringWidth(line)) / 2, baseline + i * lineHeight);
            }
        }

        private String clip(FontMetrics fm, String text, int maxWidth) {
            if (fm.stringWidth(text) <= maxWidth) {
                return text;
            }
            String ellipsis = "...";
            int ellipsisWidth = fm.stringWidth(ellipsis);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < text.length(); i++) {
                char ch = text.charAt(i);
                if (fm.stringWidth(sb.toString() + ch) + ellipsisWidth > maxWidth) {
                    break;
                }
                sb.append(ch);
            }
            return sb.toString() + ellipsis;
        }
    }

    /* ==================== Web-style components ==================== */

    public static class ProgressBar extends JComponent {

        private final Anim anim = new Anim(() -> repaint());
        private double target;

        public ProgressBar() {
            setOpaque(false);
        }

        public void setProgress(double p) {
            target = Math.max(0.0, Math.min(1.0, p));
            anim.setTarget((float) target, 0.14f);
        }

        public double getProgress() {
            return target;
        }

        @Override
        protected void paintComponent(Graphics g) {
            double progress = anim.value();
            Graphics2D g2 = (Graphics2D) g.create();
            paintQuality(g2);
            int w = getWidth();
            int h = getHeight();
            int trackH = Math.max(10, h - 4);
            int ty = (h - trackH) / 2;

            g2.setColor(new Color(255, 255, 255, 30));
            g2.fillRoundRect(0, ty, w, trackH, trackH, trackH);
            g2.setColor(PANEL_BORDER);
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(0, ty, w, trackH, trackH, trackH);

            if (progress > 0.01) {
                int fw = Math.max(trackH, (int) Math.round(w * progress));
                g2.setClip(0, ty, fw, trackH);
                g2.setPaint(new GradientPaint(0, 0, TEAL, w, 0, GOLD));
                g2.fillRoundRect(0, ty, w, trackH, trackH, trackH);
                g2.setClip(null);
                g2.setColor(new Color(32, 211, 194, 90));
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, ty, fw, trackH, trackH, trackH);
            }

            int sx = (int) Math.round(w * progress);
            int star = trackH + 6;
            g2.setColor(new Color(255, 201, 60, 70));
            g2.fillOval(sx - star / 2 - 3, ty - 3, star + 6, star + 6);
            g2.setPaint(new RadialGradientPaint(
                    new Point2D.Float(sx, ty + trackH / 2f), star / 2f + 1,
                    new float[]{0f, 1f},
                    new Color[]{new Color(0xffffff), new Color(0xf5a623)}));
            g2.fillOval(sx - star / 2, ty, star, star);
            g2.setColor(new Color(255, 255, 255, 120));
            g2.setStroke(new BasicStroke(1f));
            g2.drawOval(sx - star / 2, ty, star, star);
            g2.dispose();
        }
    }

    public static class HintBox extends JLabel {

        public HintBox(String text) {
            super(text);
            setOpaque(false);
            setFont(bodyFont(Font.PLAIN, 15));
            setForeground(ICE);
            setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            quality(g2);
            int w = getWidth();
            int h = getHeight();
            int r = Math.max(14, Math.min(20, h / 2));
            g2.setColor(new Color(255, 201, 60, 26));
            g2.fillRoundRect(0, 0, w, h, r, r);
            g2.setColor(new Color(255, 201, 60, 120));
            g2.setStroke(new BasicStroke(4f));
            g2.drawRoundRect(0, 0, 4, h, 2, 2);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static class GradientTextLabel extends JLabel {

        private final Color from;
        private final Color to;

        public GradientTextLabel(String text, int size, Color from, Color to) {
            super(text, SwingConstants.CENTER);
            this.from = from;
            this.to = to;
            setOpaque(false);
            setFont(displayFont(Font.BOLD, size));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            quality(g2);
            String text = getText();
            if (text != null && !text.isEmpty()) {
                FontMetrics fm = g2.getFontMetrics(getFont());
                int w = fm.stringWidth(text);
                int x = (getWidth() - w) / 2;
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.setFont(getFont());
                g2.setColor(new Color(0, 0, 0, 110));
                g2.drawString(text, x + 1, y + 2);
                g2.setPaint(new LinearGradientPaint(
                        new Point2D.Float(x, 0), new Point2D.Float(x + w, 0),
                        new float[]{0f, 1f}, new Color[]{from, to}));
                g2.drawString(text, x, y);
            }
            g2.dispose();
        }
    }

    public static JComponent coin(int size) {
        JComponent coin = new JComponent() {
            {
                setPreferredSize(new Dimension(size, size));
                setMaximumSize(new Dimension(size, size));
                setMinimumSize(new Dimension(size, size));
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                quality(g2);
                int s = getWidth();
                g2.setPaint(new RadialGradientPaint(
                        new Point2D.Float(s * 0.35f, s * 0.30f), s * 0.8f,
                        new float[]{0f, 1f},
                        new Color[]{new Color(0xfff7d6), new Color(0xf5a623)}));
                g2.fillOval(0, 0, s, s);
                g2.setColor(new Color(0, 0, 0, 60));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawOval(0, 0, s, s);
                g2.setFont(displayFont(Font.BOLD, Math.max(10, (int) (s * 0.6f))));
                FontMetrics fm = g2.getFontMetrics();
                g2.setColor(Color.WHITE);
                g2.drawString("\u2B50", (s - fm.stringWidth("\u2B50")) / 2,
                        (s - fm.getHeight()) / 2 + fm.getAscent());
                g2.dispose();
            }
        };
        return coin;
    }

    public static final class Confetti {

        private static final Color[] COLORS = {
                new Color(0xffc93c), new Color(0x20d3c2), new Color(0xff6b6b),
                new Color(0x9b5de5), new Color(0xf15bb5), new Color(0x3ddc97),
        };

        private final List<Particle> particles = new ArrayList<>();
        private final javax.swing.Timer timer;
        private final JComponent host;

        public Confetti(JComponent host) {
            this.host = host;
            this.timer = new javax.swing.Timer(16, e -> tick());
            timer.setRepeats(true);
        }

        public void launch() {
            Random rnd = new Random();
            int w = Math.max(host.getWidth(), 600);
            int h = Math.max(host.getHeight(), 400);
            particles.clear();
            for (int i = 0; i < 170; i++) {
                particles.add(new Particle(rnd, w, h));
            }
            if (!timer.isRunning()) {
                timer.start();
            }
        }

        public void paint(Graphics2D g2) {
            int h = Math.max(host.getHeight(), 400);
            for (Particle p : particles) {
                if (p.y > h + 40) {
                    continue;
                }
                g2.rotate(p.rot, p.x, p.y);
                g2.setColor(p.color);
                if (p.circle) {
                    g2.fillOval((int) (p.x - p.w / 2f), (int) (p.y - p.w / 2f), p.w, p.w);
                } else {
                    g2.fillRect((int) (p.x - p.w / 2f), (int) (p.y - p.h / 2f), p.w, p.h);
                }
                g2.rotate(-p.rot, p.x, p.y);
            }
        }

        private void tick() {
            int h = Math.max(host.getHeight(), 400);
            particles.removeIf(p -> p.y > h + 40);
            boolean any = false;
            for (Particle p : particles) {
                p.y += p.vy;
                p.x += p.vx;
                p.vy += 0.03f;
                p.rot += p.vrot;
                any = true;
            }
            if (any) {
                host.repaint();
            }
            if (particles.isEmpty()) {
                timer.stop();
            }
        }

        private static class Particle {
            final float vx;
            float x;
            float y;
            float vy;
            float rot;
            final float vrot;
            final int w;
            final int h;
            final Color color;
            final boolean circle;

            Particle(Random rnd, int width, int height) {
                x = rnd.nextFloat() * width;
                y = -24 - rnd.nextFloat() * height * 0.4f;
                w = 6 + rnd.nextInt(6);
                h = 8 + rnd.nextInt(8);
                color = COLORS[rnd.nextInt(COLORS.length)];
                vx = (rnd.nextFloat() - 0.5f) * 2.6f;
                vy = 2.2f + rnd.nextFloat() * 3.4f;
                rot = rnd.nextFloat() * (float) Math.PI;
                vrot = (rnd.nextFloat() - 0.5f) * 0.22f;
                circle = rnd.nextBoolean();
            }
        }
    }

    public static class LetterTile extends JComponent {

        private final char letter;
        private final Color color;

        LetterTile(char letter, Color color, int size) {
            this.letter = letter;
            this.color = color;
            setPreferredSize(new Dimension(size, size));
            setMinimumSize(new Dimension(size, size));
            setMaximumSize(new Dimension(size, size));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            quality(g2);
            int w = getWidth();
            int h = getHeight();

            int r = capsule(h);
            g2.setColor(color.darker());
            g2.fillRoundRect(0, 3, w, h - 3, r, r);
            g2.setColor(color);
            g2.fillRoundRect(0, 0, w, h - 3, r, r);

            g2.setColor(new Color(0, 0, 0, 40));
            g2.drawRoundRect(0, 3, w - 1, h - 3, r, r);

            g2.setColor(Color.WHITE);
            g2.setFont(displayFont(Font.BOLD, Math.max(16, (int) (h * 0.5f))));
            FontMetrics fm = g2.getFontMetrics();
            String s = String.valueOf(letter);
            int x = (w - fm.stringWidth(s)) / 2;
            int y = ((h - 3) - fm.getHeight()) / 2 + fm.getAscent() + 1;
            g2.drawString(s, x, y);
            g2.dispose();
        }
    }
}
