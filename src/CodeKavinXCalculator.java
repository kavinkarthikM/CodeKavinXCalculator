import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Objects;
import javax.imageio.ImageIO;

public class CodeKavinXCalculator {

    // THEME
    private static final Color BG_BLACK = new Color(0x0B0B0B);
    private static final Color WHITE = Color.WHITE;
    private static final Color NEON = new Color(0x00FF00); // #00ff00
    private static final String WATERMARK_TEXT = "codekavinx";

    // STATE
    private String displayText = "0";
    private double lastValue = 0.0;
    private String pendingOp = null;
    private boolean startNewNumber = true;

    private JLabel display;

    public static void main(String[] args) {
        // Use system look & feel where possible
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new CodeKavinXCalculator().createAndShow());
    }

    private void createAndShow() {
        JFrame f = new JFrame("CodeKavinX Calculator");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Try to set a window icon (optional)
        // Try to set a window icon
        try {
            // Load icon from resources folder (src/icon.png)
            ImageIcon appIcon = new ImageIcon(getClass().getResource("/logo.png"));
            f.setIconImage(appIcon.getImage());
        } catch (Exception e) {
            System.out.println("Icon not found!");
        }


        // Root panel with custom paint (neon glow + watermark)
        JPanel root = new NeonPanel();
        root.setLayout(new BorderLayout(12, 12));
        root.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        f.setContentPane(root);

        // Display
        display = new JLabel(displayText, SwingConstants.RIGHT);
        display.setOpaque(true);
        display.setBackground(BG_BLACK);
        display.setForeground(WHITE);
        display.setBorder(new LineBorder(NEON, 2, true));
        display.setFont(new Font("Consolas", Font.BOLD, 32));
        display.setPreferredSize(new Dimension(360, 60));
        root.add(display, BorderLayout.NORTH);

        // Buttons
        JPanel grid = new JPanel(new GridLayout(5, 4, 10, 10)) {
            @Override public boolean isOpaque() { return false; }
        };

        String[] keys = {
                "C", "DEL", "÷", "×",
                "7", "8", "9", "−",
                "4", "5", "6", "+",
                "1", "2", "3", "=",
                "±", "0", ".", "%"
        };

        for (String k : keys) grid.add(makeButton(k));
        root.add(grid, BorderLayout.CENTER);

        f.setSize(420, 520);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

    private JButton makeButton(String text) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setFont(new Font("Inter", Font.BOLD, 18));
        b.setForeground(isOperator(text) || text.equals("=") ? BG_BLACK : NEON);
        b.setBackground(isOperator(text) || text.equals("=") ? NEON : BG_BLACK);
        b.setBorder(new LineBorder(NEON, 1, true));
        b.setOpaque(true);
        b.addActionListener(new KeyHandler(text));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private boolean isOperator(String k) {
        return Objects.equals(k, "+") || Objects.equals(k, "−") ||
                Objects.equals(k, "×") || Objects.equals(k, "÷");
    }

    private class KeyHandler implements ActionListener {
        private final String key;
        KeyHandler(String key) { this.key = key; }

        @Override public void actionPerformed(ActionEvent e) {
            switch (key) {
                case "C" -> {
                    displayText = "0";
                    lastValue = 0;
                    pendingOp = null;
                    startNewNumber = true;
                }
                case "DEL" -> {
                    if (startNewNumber) return;
                    if (displayText.length() > 1) displayText = displayText.substring(0, displayText.length() - 1);
                    else displayText = "0";
                }
                case "±" -> {
                    if (!displayText.equals("0")) {
                        if (displayText.startsWith("-")) displayText = displayText.substring(1);
                        else displayText = "-" + displayText;
                    }
                }
                case "." -> {
                    if (startNewNumber) {
                        displayText = "0.";
                        startNewNumber = false;
                    } else if (!displayText.contains(".")) {
                        displayText += ".";
                    }
                }
                case "%" -> {
                    try {
                        double v = Double.parseDouble(displayText);
                        v = v / 100.0;
                        displayText = trim(v);
                        startNewNumber = true;
                    } catch (NumberFormatException ignored) {}
                }
                case "+" , "−" , "×" , "÷" -> handleOperator(key);
                case "=" -> handleEquals();
                default -> handleDigit(key);
            }
            display.setText(displayText);
        }
    }

    private void handleDigit(String d) {
        if (!d.chars().allMatch(Character::isDigit)) return;
        if (startNewNumber || displayText.equals("0")) {
            displayText = d;
            startNewNumber = false;
        } else {
            displayText += d;
        }
    }

    private void handleOperator(String op) {
        try {
            double current = Double.parseDouble(displayText);
            if (pendingOp == null) {
                lastValue = current;
            } else {
                lastValue = compute(lastValue, current, pendingOp);
                displayText = trim(lastValue);
            }
            pendingOp = op;
            startNewNumber = true;
        } catch (NumberFormatException ignored) {}
    }

    private void handleEquals() {
        if (pendingOp == null) return;
        try {
            double current = Double.parseDouble(displayText);
            double result = compute(lastValue, current, pendingOp);
            displayText = trim(result);
            pendingOp = null;
            startNewNumber = true;
        } catch (ArithmeticException ex) {
            JOptionPane.showMessageDialog(null, "Cannot divide by zero.", "Error", JOptionPane.ERROR_MESSAGE);
            displayText = "0";
            pendingOp = null;
            startNewNumber = true;
        } catch (NumberFormatException ignored) {}
    }

    private double compute(double a, double b, String op) {
        return switch (op) {
            case "+" -> a + b;
            case "−" -> a - b;
            case "×" -> a * b;
            case "÷" -> {
                if (b == 0.0) throw new ArithmeticException("/0");
                yield a / b;
            }
            default -> b;
        };
    }

    private String trim(double v) {
        String s = Double.toString(v);
        if (s.endsWith(".0")) s = s.substring(0, s.length() - 2);
        return s;
    }

    /**
     * Custom panel paints a soft neon green radial glow and a semi-transparent watermark.
     */
    private class NeonPanel extends JPanel {
        @Override public boolean isOpaque() { return true; }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            int w = getWidth(), h = getHeight();

            // Background
            g2.setColor(BG_BLACK);
            g2.fillRect(0, 0, w, h);

            // Neon radial glow
            Point2D center = new Point2D.Float(w * 0.25f, h * 0.25f);
            float radius = Math.max(w, h) * 0.9f;
            float[] dist = {0f, 1f};
            Color neonSoft = new Color(0, 255, 0, 140);
            RadialGradientPaint paint = new RadialGradientPaint(center, radius, dist, new Color[]{neonSoft, new Color(0, 0, 0, 0)});
            g2.setPaint(paint);
            g2.fillRect(0, 0, w, h);

            // Watermark (bottom-right)
            g2.setComposite(AlphaComposite.SrcOver.derive(0.12f)); // subtle
            g2.setColor(WHITE);
            g2.setFont(new Font("Consolas", Font.BOLD, 42));
            FontMetrics fm = g2.getFontMetrics();
            int textW = fm.stringWidth(WATERMARK_TEXT);
            int textH = fm.getAscent();
            int x = w - textW - 14;
            int y = h - 14;
            // simple neon edge by drawing twice
            g2.setColor(new Color(0, 255, 0, 90));
            g2.drawString(WATERMARK_TEXT, x+2, y+2);
            g2.setColor(WHITE);
            g2.drawString(WATERMARK_TEXT, x, y);

            g2.dispose();
        }
    }
}
