public class Main
{
    public static void main(String[] args)
    {
        /*memory object
          if parsing is successful the memory gets passed to
          (to-be-build ) simulator class
          */
        Memory mem = new Memory();
        Parser p = new Parser();

        //if parser is successful get memory
        if(p.Initialize("test.txt"))
        {
            mem = p.GetMemory();
            mem.DisplayRAMHex();
        }

        Timer tmr = new Timer(0.000001);
        Bus address_bus = new Bus(16);
        Bus data_bus = new Bus(8);
        ControlLines clines = new ControlLines();
        clines.set("IO_M",false);
        clines.set("RD",false);
        clines.set("WR",false);

        MemoryModule m = new MemoryModule(address_bus,
                data_bus, clines);
        Test tst = new Test(address_bus, data_bus, clines);
        tmr.register(m);
        tmr.register(tst);
        tmr.start();
        m.start();
        tst.start();
    }
}
