public class Test extends Synchronous {
    int t,awriteid,dwriteid;
    Bus adb, dtb;
    ControlLines cln;
    Register mar;
    Register dtr;

    public Test(Bus addrbus, Bus databus, ControlLines cl) {
        t = 0; adb = addrbus; dtb = databus; cln = cl;
        mar = new Register(16);
        dtr = new Register(8);
        awriteid = addrbus.registerWriter(mar);
        dwriteid = databus.registerWriter(dtr);
        databus.registerReader(dtr);
    }

    public void run() {
        while (true) {
            synchronized (ticks) { if (ticks>0) {
                if (t==0) {
                    cln.set("IO_M'",false);
                    cln.set("RD",false);
                    cln.set("WR",true);
                    mar.fromInt(0);
                    adb.select(awriteid);
                    dtr.fromInt(55);
                } else if (t==1) {
                    cln.set("WR",false);
                    adb.select(0);
                    dtb.select(0);
                } else if (t==2) {
                    cln.set("RD",true);
                    mar.fromInt(0);
                    adb.select(awriteid);
                } else if (t==3) {
                    if (dtb.tristated()) {
                        System.out.println("tristated");
                    } else {
                        System.out.println(dtr.asInt());
                    }
                    adb.select(0);
                    dtb.select(0);
                    cln.set("RD",false);
                }
                t++;
                if (t==4)
                    t = 0;
                ticks = 0;
            }}
        }
    }
}
