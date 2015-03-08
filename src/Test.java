import java.util.Random;

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
        Random randgen = new Random();
        int ourint = 0;
        boolean error = false;
        while (true) {
            synchronized (ticks) { if (ticks>0) {
                if (t==0) {
                    cln.set("IO_M'",false);
                    cln.set("RD",false);
                    cln.set("WR",true);
                    mar.fromInt(2);
                    adb.select(awriteid);
                    ourint = randgen.nextInt(255);
                    dtr.fromInt(ourint);
                    dtb.select(dwriteid);
                } else if (t==1) {
                    //adb.select(0);
                } else if (t==2) {
                    cln.set("WR",false);
                    dtb.select(0);
                } else if (t==3) {
                    dtr.fromInt(111);
                    dtr.loadable.set(true);
                    cln.set("RD",true);
                    mar.fromInt(2);
                    adb.select(awriteid);
                    dtb.select(0);
                } else if (t==4) {
                } else if (t==5) {
                    while (dtb.tristated())
                        Thread.yield();
                    //System.out.println(dtr.asInt());
                    if (ourint!=dtr.asInt())
                        System.out.println("error");
                    adb.select(0);
                    dtb.select(0);
                    cln.set("RD",false);
                    dtr.loadable.set(false);
                }
                t++;
                if (t==6)
                    t = 0;
                ticks = 0;
            }}
        }
    }
}
