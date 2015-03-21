import java.util.ArrayList;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Color;
import java.awt.BorderLayout;

import java.awt.EventQueue;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.UIManager;
import javax.swing.BoxLayout;
import javax.swing.BorderFactory;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;
import javax.swing.JFileChooser;
import javax.swing.JTabbedPane;
import javax.swing.JScrollPane;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;


public class GuiMain extends JFrame {
    private JTabbedPane tabbedpane;
    private ArrayList<TabItem> tabitems;

    public GuiMain() {
        initUI();
        tabitems = new ArrayList<TabItem>();
    }

    public final void initUI() {
        JPanel superpanel = panelBoxH();
        JPanel mainpanel = panelBoxV();
        mainpanel.setBorder(BorderFactory.createEmptyBorder(
                    5,5,5,5));
        tabbedpane = new JTabbedPane();
        tabbedpane.setBorder(BorderFactory.createEmptyBorder(
                    10,10,10,10));
        TabItem firstitem = new TabItem();
        firstitem.title = "New"; firstitem.addTo(tabbedpane);
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
        JLabel rega = new JLabel("A 00");
        row_rega.add(rega);
        JLabel regb = new JLabel("B 00");
        JLabel regc = new JLabel("C 00");
        row_regbc.add(regb);
        row_regbc.add(Box.createRigidArea(new Dimension(5,0)));
        row_regbc.add(regc);
        JLabel regd = new JLabel("D 00");
        JLabel rege = new JLabel("E 00");
        row_regde.add(regd);
        row_regde.add(Box.createRigidArea(new Dimension(5,0)));
        row_regde.add(rege);
        JLabel regh = new JLabel("H 00");
        JLabel regl = new JLabel("L 00");
        row_reghl.add(regh);
        row_reghl.add(Box.createRigidArea(new Dimension(5,0)));
        row_reghl.add(regl);
        JLabel regpc = new JLabel(
                "PC 0000");
        row_regpc.add(regpc);

        statuspanel.add(row_title);
        statuspanel.add(row_rega);
        statuspanel.add(row_regbc);
        statuspanel.add(row_regde);
        statuspanel.add(row_reghl);
        statuspanel.add(row_regpc);

        mainpanel.add(statuspanel);

        superpanel.add(mainpanel);
        add(superpanel);

        createMenuBar();
        createToolBar();
        setTitle("8085 Simulator");
        setSize(933,700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("Then find it");
        }
        setLocationRelativeTo(null);
    }

    private void createMenuBar() {
        JMenuBar menubar = new JMenuBar();

        JMenu file = new JMenu("File");
        file.setMnemonic(KeyEvent.VK_F);

        JMenuItem nMenuItem = new JMenuItem("New");
        nMenuItem.addActionListener(new NewFileAction());
        JMenuItem oMenuItem = new JMenuItem("Open");
        oMenuItem.addActionListener(new OpenFileAction());
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

        ImageIcon exitIcon = new ImageIcon(
                "icons/24/application-exit.png");
        JButton exitButton = new JButton(exitIcon);

        ImageIcon newIcon = new ImageIcon(
                "icons/24/tab-new.png");
        JButton newButton = new JButton(newIcon);

        ImageIcon openIcon = new ImageIcon(
                "icons/24/document-open.png");
        JButton openButton = new JButton(openIcon);

        toolbar.add(exitButton);
        toolbar.add(newButton);
        toolbar.add(openButton);
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                System.exit(0);
            }
        });
        newButton.addActionListener(new NewFileAction());
        openButton.addActionListener(new OpenFileAction());
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

    public String readFile(File file) {
        String content = "";
        try {
            content = new String(Files.readAllBytes(Paths.get(
                            file.getAbsolutePath())));
        } catch (IOException ex) {
            Logger.getLogger(GuiMain.class.getName())
                .log(Level.SEVERE,null,ex);
        }
        return content;
    }

    private class OpenFileAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fdia = new JFileChooser();
            FileFilter filter = new FileNameExtensionFilter(
                    "Assembly files", "asm");
            fdia.addChoosableFileFilter(filter);
            int ret = fdia.showDialog(getContentPane(),
                    "Open File");
            if (ret==JFileChooser.APPROVE_OPTION) {
                TabItem ti=new TabItem(fdia.getSelectedFile());
                ti.title = "File";
                ti.addTo(tabbedpane);
            }
        }
    }

    private class NewFileAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
                File file = null;
                TabItem ti = new TabItem();
                ti.title = "New";
                ti.addTo(tabbedpane);
        }
    }

    private class TabItem {
        public String title;
        public JTextArea codearea;
        public JScrollPane scrollpane;
        public File file;

        public TabItem() {
            codearea = new JTextArea();
            scrollpane = new JScrollPane(codearea);
        }

        public TabItem(File f) {
            file = f;
            codearea = new JTextArea();
            scrollpane = new JScrollPane(codearea);
            codearea.setText(readFile(file));
        }

        public void addTo(JTabbedPane jtp) {
            jtp.addTab(title,scrollpane);
        }
    }
}
