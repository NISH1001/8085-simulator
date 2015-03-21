public class Main
{
    public static void main(String[] args)
    { //parser object
        Parser p = new Parser();

        MemoryModule memory = new MemoryModule(0x0000, 0x10000);
        int start_addr = (int)0x8000;

        //if parser is successful get memory
        if(p.Initialize("newtest.txt", start_addr))
        {
            p.ShowOriginalLines();
            System.out.println("--------------------------");
            p.ShowData();
            System.out.println("--------------------------");
            p.WriteToMemory(memory, start_addr);
        }
        Processor proc = new Processor();
        proc.addDevice(memory);
        proc.setRegI("PC",0x8000);
        Thread proc_thread = new Thread(proc);
        proc_thread.start();
        //System.out.println(memory.readByte(0x8001));
        GuiMain gui = new GuiMain();
        gui.setVisible(true);
        try {
            proc_thread.join();
        } catch (InterruptedException e) {
            System.out.println("interrupted");
        }
        proc.showRegs();
   }
}
