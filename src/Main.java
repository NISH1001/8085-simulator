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
        proc.run();
        //System.out.println(memory.readByte(0x8001));
        proc.showRegs();
   }
}
