public class MemoryModule extends Synchronous {
    // Holds incoming address
    Register raddress;
    // Holds incoming or outgoing data
    Register rdata;
    int datawriteid;

    // IO/M'
    BooleanMutable IoMbar;
    // (Read')'
    BooleanMutable Rd;
    // (Write')'
    BooleanMutable Wr;

    // Data
    int[] data;

    // References to the buses
    Bus baddress, bdata;
    ControlLines control;

    public MemoryModule(Bus addrbus, Bus databus,
            ControlLines clines) {
        // Initialize 64K of RAM
        data = new int[65536];
        // create the registers
        raddress = new Register(16);
        rdata = new Register(8);
        IoMbar = new BooleanMutable();
        Rd = new BooleanMutable();
        Wr = new BooleanMutable();
        addrbus.registerReader(raddress);
        databus.registerReader(rdata);
        datawriteid = databus.registerWriter(rdata);
        clines.reflect("IO_M'",IoMbar);
        clines.reflect("!IO_M'",raddress.loadable);
        clines.reflect("!IO_M'&WR",rdata.loadable);
        clines.reflect("RD",Rd);
        clines.reflect("WR",Wr);
        baddress = addrbus;
        bdata = databus;
        control = clines;
    }

    public void run() {
        boolean dataout = false;
        while (true) {
            synchronized (ticks) { if (ticks>0) {
                if (Rd.get()==true && IoMbar.get()==false) {
                    /*System.out.println("read");
                    System.out.println(data[raddress.asInt()]);*/
                    rdata.fromInt(data[raddress.asInt()]);
                    bdata.select(datawriteid);
                    dataout = true;
                } else if (Wr.get()==true &&
                        IoMbar.get()==false) {
                    /*System.out.println("write");
                    System.out.println(rdata.asInt());*/
                    data[raddress.asInt()] = rdata.asInt();
                }
                if (dataout && Rd.get()==false) {
                    bdata.select(0);
                    dataout = false;
                }
                ticks = 0;
            }}
        }
    }
}
