package com.worldofwonder.ui;

import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.FlowLayout;

public final class UIUtil {

    private UIUtil() {
    }

    public static void fixedSize(JComponent component, int width, int height) {
        Dimension size = new Dimension(width, height);
        component.setPreferredSize(size);
        component.setMinimumSize(size);
        component.setMaximumSize(size);
    }

    public static void fullWidth(JComponent component, int height) {
        component.setPreferredSize(new Dimension(component.getPreferredSize().width, height));
        component.setMinimumSize(new Dimension(0, height));
        component.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
    }

    public static JPanel centered(JComponent child) {
        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        wrapper.setOpaque(false);
        wrapper.add(child);
        return wrapper;
    }

    public static void flexWidth(JComponent component, int height) {
        component.setPreferredSize(new Dimension(component.getPreferredSize().width, height));
        component.setMinimumSize(new Dimension(140, height));
        component.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
    }

    public static void fixedHeight(JComponent component, int height) {
        Dimension cur = component.getPreferredSize();
        component.setPreferredSize(new Dimension(cur.width, height));
        component.setMinimumSize(new Dimension(cur.width, height));
        component.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
    }

    /** Set a minimum width while letting the component stretch to fill available space. */
    public static void minWidth(JComponent component, int width, int height) {
        component.setPreferredSize(new Dimension(width, height));
        component.setMinimumSize(new Dimension(width, height));
        component.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
    }

    /** Set a flexible width that grows with the container, with a max cap. */
    public static void flex(JComponent component, int prefWidth, int height, int maxWidth) {
        component.setPreferredSize(new Dimension(prefWidth, height));
        component.setMinimumSize(new Dimension(prefWidth, height));
        component.setMaximumSize(new Dimension(maxWidth, height));
    }

    /** Wrap a component in a GridBagLayout-centered panel for use inside a card. */
    public static JPanel wrapCentered(JComponent child) {
        JPanel wrapper = new JPanel(new java.awt.GridBagLayout());
        wrapper.setOpaque(false);
        wrapper.add(child);
        return wrapper;
    }

    /** Set min+preferred+maximum to the same flexible dimensions (pref w, min, max). */
    public static void flexSize(JComponent component, int prefW, int prefH, int minW, int maxW) {
        component.setPreferredSize(new Dimension(prefW, prefH));
        component.setMinimumSize(new Dimension(minW, prefH));
        component.setMaximumSize(new Dimension(maxW, prefH));
    }
}
