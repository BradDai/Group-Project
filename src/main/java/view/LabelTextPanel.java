package view;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * A panel containing a label and a text field.
 */
class LabelTextPanel extends JPanel {
    LabelTextPanel(final JLabel label, final JTextField textField) {
        this.add(label);
        this.add(textField);
    }
}
