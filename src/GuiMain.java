import java.awt.Dimension;
import java.awt.Font;
import java.awt.Color;
import java.awt.BorderLayout;

import java.awt.EventQueue;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;
import javax.swing.JTabbedPane;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

public class GuiMain extends JFrame {
    public GuiMain() {
        initUI();
    }

    public final void initUI() {
        JPanel mainpanel = panelBoxV();
        mainpanel.setBorder(BorderFactory.createEmptyBorder(
                    20,20,20,20));
        JTabbedPane tabbedpane = new JTabbedPane();
        JTextArea codearea = new JTextArea();
        tabbedpane.setBorder(BorderFactory.createEmptyBorder(
                    10,10,10,10));
        tabbedpane.addTab("Tab1",codearea);
        mainpanel.add(tabbedpane);

        JPanel statuspanel = panelBoxV();
        statuspanel.setBorder(BorderFactory.createEmptyBorder(
                    10,10,10,10));
        JPanel row_title = panelBoxH();
        JPanel row_rega = panelBoxH();
        JPanel row_regbc = panelBoxH();
        JPanel row_regde = panelBoxH();
        JPanel row_reghl = panelBoxH();
        JPanel row_regpc = panelBoxH();

        JLabel title = new JLabel("Processor Status");
        title.setFont(new Font("Serif",Font.PLAIN,14));
        row_title.add(title);
        JLabel rega = new JLabel("A 0000 0000 [00]");
        row_rega.add(rega);
        JLabel regb = new JLabel("B 0000 0000 [00]");
        JLabel regc = new JLabel("C 0000 0000 [00]");
        row_regbc.add(regb);
        row_regbc.add(Box.createRigidArea(new Dimension(5,0)));
        row_regbc.add(regc);
        JLabel regd = new JLabel("D 0000 0000 [00]");
        JLabel rege = new JLabel("E 0000 0000 [00]");
        row_regde.add(regd);
        row_regde.add(Box.createRigidArea(new Dimension(5,0)));
        row_regde.add(rege);
        JLabel regh = new JLabel("H 0000 0000 [00]");
        JLabel regl = new JLabel("L 0000 0000 [00]");
        row_reghl.add(regh);
        row_reghl.add(Box.createRigidArea(new Dimension(5,0)));
        row_reghl.add(regl);
        JLabel regpc = new JLabel(
                "PC 0000 0000 0000 0000 [00 00]");
        row_regpc.add(regpc);

        statuspanel.add(row_title);
        statuspanel.add(row_rega);
        statuspanel.add(row_regbc);
        statuspanel.add(row_regde);
        statuspanel.add(row_reghl);
        statuspanel.add(row_regpc);

        mainpanel.add(statuspanel);

        add(mainpanel);

        createMenuBar();
        createToolBar();
        setTitle("8085 Simulator");
        setSize(933,700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void createMenuBar() {
        JMenuBar menubar = new JMenuBar();

        JMenu file = new JMenu("File");
        file.setMnemonic(KeyEvent.VK_F);

        JMenuItem nMenuItem = new JMenuItem("New");
        JMenuItem oMenuItem = new JMenuItem("Open");
        JMenuItem sMenuItem = new JMenuItem("Save");
        JMenuItem eMenuItem = new JMenuItem("Exit");
        eMenuItem.setMnemonic(KeyEvent.VK_E);
        eMenuItem.setToolTipText("Exit application");
        eMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                System.exit(0);
            }
        });

        file.add(nMenuItem);
        file.add(oMenuItem);
        file.add(sMenuItem);
        file.addSeparator();
        file.add(eMenuItem);
        menubar.add(file);
        setJMenuBar(menubar);
    }

    private void createToolBar() {
        JToolBar toolbar = new JToolBar();

        ImageIcon icon = new ImageIcon(
                "icons/24/application-exit.png");
        JButton exitButton = new JButton(icon);
        toolbar.add(exitButton);
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                System.exit(0);
            }
        });
        add(toolbar, BorderLayout.NORTH);
    }

    private JPanel panelBoxH() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p,BoxLayout.X_AXIS));
        return p;
    }

    private JPanel panelBoxV() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));
        return p;
    }
}
