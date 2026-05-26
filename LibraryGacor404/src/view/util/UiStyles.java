package view.util;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.*;

public final class UiStyles {

    // Palet warna modern
    public static final Color PRIMARY       = new Color(37, 99, 235);
    public static final Color PRIMARY_DARK  = new Color(29, 78, 216);
    public static final Color SUCCESS       = new Color(22, 163, 74);
    public static final Color DANGER        = new Color(220, 38, 38);
    public static final Color WARNING       = new Color(234, 179,  8);
    public static final Color BG            = new Color(248, 250, 252);
    public static final Color CARD_BG       = Color.WHITE;
    public static final Color HEADER_BG     = new Color(30,  64, 175);
    public static final Color HEADER_FG     = Color.WHITE;
    public static final Color TEXT_MAIN     = new Color( 15,  23,  42);
    public static final Color TEXT_MUTED    = new Color(100, 116, 139);
    public static final Color BORDER_COLOR  = new Color(226, 232, 240);
    public static final Color ROW_ALT       = new Color(241, 245, 249);
    public static final Color SEL_BG        = new Color(219, 234, 254);

    public static final Font FONT_TITLE    = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_LABEL    = new Font("Segoe UI", Font.BOLD,  13);
    public static final Font FONT_BODY     = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL    = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_TABLE    = new Font("Segoe UI", Font.PLAIN, 12);

    private UiStyles() {}

    public static void applyGlobalDefaults() {
        UIManager.put("Panel.background",        BG);
        UIManager.put("OptionPane.background",   CARD_BG);
        UIManager.put("Button.font",             FONT_BODY);
        UIManager.put("Label.font",              FONT_BODY);
        UIManager.put("TextField.font",          FONT_BODY);
        UIManager.put("PasswordField.font",      FONT_BODY);
        UIManager.put("ComboBox.font",           FONT_BODY);
        UIManager.put("TabbedPane.font",         new Font("Segoe UI", Font.BOLD, 12));
        UIManager.put("TabbedPane.selected",     CARD_BG);
        UIManager.put("Table.font",              FONT_TABLE);
        UIManager.put("TableHeader.font",        new Font("Segoe UI", Font.BOLD, 12));
    }

    public static JButton primaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(PRIMARY);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        return btn;
    }

    public static JButton dangerButton(String text) {
        JButton btn = primaryButton(text);
        btn.setBackground(DANGER);
        return btn;
    }

    public static JButton successButton(String text) {
        JButton btn = primaryButton(text);
        btn.setBackground(SUCCESS);
        return btn;
    }

    public static JButton outlineButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(CARD_BG);
        btn.setForeground(PRIMARY);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createLineBorder(PRIMARY, 1, true));
        return btn;
    }

    public static JTextField styledField(int cols) {
        JTextField f = new JTextField(cols);
        f.setFont(FONT_BODY);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        return f;
    }

    public static JPasswordField styledPasswordField(int cols) {
        JPasswordField f = new JPasswordField(cols);
        f.setFont(FONT_BODY);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        return f;
    }

    public static Border cardBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                BorderFactory.createEmptyBorder(12, 14, 12, 14));
    }

    public static void styleTable(JTable table) {
        table.setFont(FONT_TABLE);
        table.setRowHeight(30);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setGridColor(BORDER_COLOR);
        table.setSelectionBackground(SEL_BG);
        table.setSelectionForeground(TEXT_MAIN);
        table.setBackground(CARD_BG);
        table.setFillsViewportHeight(true);

        JTableHeader header = table.getTableHeader();
        header.setBackground(HEADER_BG);
        header.setForeground(HEADER_FG);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(header.getWidth(), 36));
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean focus, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, val, sel, focus, row, col);
                lbl.setBackground(HEADER_BG);
                lbl.setForeground(Color.WHITE);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
                lbl.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                lbl.setOpaque(true);
                return lbl;
            }
        });
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean focus, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, val, sel, focus, row, col);
                lbl.setBackground(HEADER_BG);
                lbl.setForeground(Color.WHITE);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
                lbl.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                lbl.setOpaque(true);
                return lbl;
            }
        });

        // Alternating row colors
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean focus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, sel, focus, row, col);
                if (!sel) {
                    c.setBackground(row % 2 == 0 ? CARD_BG : ROW_ALT);
                    c.setForeground(TEXT_MAIN);
                } else {
                    c.setForeground(TEXT_MAIN);
                }
                ((JLabel)c).setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return c;
            }
        });
    }

    public static JScrollPane scrollPane(JTable table) {
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        sp.getViewport().setBackground(CARD_BG);
        return sp;
    }

    public static JLabel sectionHeader(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbl.setForeground(TEXT_MAIN);
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        return lbl;
    }
}
