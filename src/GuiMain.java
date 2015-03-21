import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class GuiMain extends JFrame {
    public GuiMain() {
        initUI();
    }

    public final void initUI() {
        JPanel mainpanel = new JPanel();
        mainpanel.setLayout(new BoxLayout(mainpanel,
                    BoxLayout.Y_AXIS));
        mainpanel.setBorder(BorderFactory.createEmptyBorder(
                    20,20,20,20));
        JTextArea codearea = new JTextArea();
        mainpanel.add(codearea);
        add(mainpanel);

        setTitle("8085 Simulator");
        setSize(933,700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }
}
