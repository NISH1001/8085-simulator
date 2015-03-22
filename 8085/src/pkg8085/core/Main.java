public class Main
{
    public static void main(String[] args)
    {
        GuiMain gui = new GuiMain();
        gui.setVisible(true);
        Thread guithread = new Thread(gui);
        guithread.start();
    }
}
