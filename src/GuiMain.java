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
import javax.swing.JViewport;
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


public class GuiMain extends JFrame implements Runnable {
    private JTabbedPane tabbedpane;
    private ArrayList<TabItem> tabitems;
    private Processor proc;

    // Register status labels
    private JLabel rega,regb,regc,regd,rege,regh,regl,regpc;

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
        rega = new JLabel("A 00");
        row_rega.add(rega);
        regb = new JLabel("B 00"); regc = new JLabel("C 00");
        row_regbc.add(regb);
        row_regbc.add(Box.createRigidArea(new Dimension(5,0)));
        row_regbc.add(regc);
        regd = new JLabel("D 00"); rege = new JLabel("E 00");
        row_regde.add(regd);
        row_regde.add(Box.createRigidArea(new Dimension(5,0)));
        row_regde.add(rege);
        regh = new JLabel("H 00"); regl = new JLabel("L 00");
        row_reghl.add(regh);
        row_reghl.add(Box.createRigidArea(new Dimension(5,0)));
        row_reghl.add(regl);
        regpc = new JLabel("PC 0000");
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

        JMenuItem item_f_new = new JMenuItem("New");
        item_f_new.addActionListener(new NewFileAction());
        JMenuItem item_f_open = new JMenuItem("Open");
        item_f_open.addActionListener(new OpenFileAction());
        JMenuItem item_f_save = new JMenuItem("Save");
        JMenuItem item_f_exit = new JMenuItem("Exit");
        item_f_exit.setMnemonic(KeyEvent.VK_E);
        item_f_exit.setToolTipText("Exit application");
        item_f_exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                System.exit(0);
            }
        });

        file.add(item_f_new);
        file.add(item_f_open);
        file.add(item_f_save);
        file.addSeparator();
        file.add(item_f_exit);

        JMenu run = new JMenu("Run");
        run.setMnemonic(KeyEvent.VK_R);

        JMenuItem item_r_exec = new JMenuItem("Execute");
        item_r_exec.addActionListener(new ExecuteAction());
        JMenuItem item_r_stop = new JMenuItem("Stop");
        //item_r_stop.addActionListener();

        run.add(item_r_exec);
        run.add(item_r_stop);

        menubar.add(file);
        menubar.add(run);
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

    private class ExecuteAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            JViewport activevp =
            ((JScrollPane)tabbedpane.getSelectedComponent())
                .getViewport();
            JTextArea active = (JTextArea)activevp.getView();
            String code = active.getText();

            MemoryModule memory =
                new MemoryModule(0x0000, 0x10000);
            int start_addr = (int)0x8000;
            MultipleParser mp = new MultipleParser(memory);
            mp.initializeString(code,start_addr);
            proc = new Processor();
            proc.addDevice(memory);
            proc.setRegI("PC",0x8000);
            Thread proc_thread = new Thread(proc);
            proc_thread.start();
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

    public void run() {
        try {
        while (true) {
            if (proc!=null) {
                rega.setText("A "+Integer.toHexString(
                        proc.getRegI("A")));
                regb.setText("B "+Integer.toHexString(
                        proc.getRegI("B")));
                regc.setText("C "+Integer.toHexString(
                        proc.getRegI("C")));
                regd.setText("D "+Integer.toHexString(
                        proc.getRegI("D")));
                rege.setText("E "+Integer.toHexString(
                        proc.getRegI("E")));
                regh.setText("H "+Integer.toHexString(
                        proc.getRegI("H")));
                regl.setText("L "+Integer.toHexString(
                        proc.getRegI("L")));
                regpc.setText("PC "+Integer.toHexString(
                        proc.getRegI("PC")));
            }
            Thread.sleep(75);
        } } catch (Exception e) {
        }
    }
}
