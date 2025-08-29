import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class Swingcalculator {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Swingcalculator::createUI);
    }

    private static void createUI() {
        JFrame f = new JFrame("Calculator");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(380, 150);
        f.setLocationRelativeTo(null);

        JTextField aField = new JTextField();
        JComboBox<String> opBox = new JComboBox<>(new String[]{"+", "-", "*", "/"});
        JTextField bField = new JTextField();
        JButton calcBtn = new JButton("Compute");
        JLabel result = new JLabel("Result: ");
        result.setFont(result.getFont().deriveFont(Font.BOLD));

        JPanel row = new JPanel(new GridLayout(1, 5, 6, 6));
        row.add(aField);
        row.add(opBox);
        row.add(bField);
        row.add(calcBtn);
        row.add(result);

        f.setLayout(new BorderLayout(6, 6));
        f.add(row, BorderLayout.CENTER);
        f.add(new JLabel("  Enter numbers, pick operator, press Compute or Enter"), BorderLayout.SOUTH);

        Action computeAction = new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) {
                try {
                    double a = Double.parseDouble(aField.getText().trim());
                    double b = Double.parseDouble(bField.getText().trim());
                    String op = (String) opBox.getSelectedItem();
                    if ("/".equals(op) && b == 0) {
                        JOptionPane.showMessageDialog(f, "Cannot divide by zero.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    double ans = switch (op) {
                        case "+" -> a + b;
                        case "-" -> a - b;
                        case "*" -> a * b;
                        default -> a / b;
                    };
                    result.setText("Result: " + ans);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(f, "Please enter valid numbers.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        calcBtn.addActionListener(computeAction);
        aField.addActionListener(computeAction);
        bField.addActionListener(computeAction);

        f.setVisible(true);
    }
}
