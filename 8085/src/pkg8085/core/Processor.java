package pkg8085.core;


import java.util.ArrayList;

public class Processor {

    private ALU alu;
    private Register a,b,c,d,e,h,l;
    private Register pc,ir,mar,dr;
    private ArrayList<Device> devices;

    public Processor() {
        alu = new ALU(); a = new Register(8);
        b = new Register(8); c = new Register(8);
        d = new Register(8); e = new Register(8);
        h = new Register(8); l = new Register(8);
        pc = new Register(16); ir = new Register(8);
        mar = new Register(16); dr = new Register(8);
    }

    public void addDevice(Device d) {
        devices.add(d);
    }

    protected byte memRead(int address) {
        for (Device d : devices)
            if (!d.IO_Mbar() && d.hasAddress(address))
                return d.readByte(address);
        return (byte)0x00;
    }

    protected byte ioRead(int address) {
        for (Device d : devices)
            if (d.IO_Mbar() && d.hasAddress(address))
                return d.readByte(address);
        return (byte)0x00;
    }

    protected void memWrite(int address, byte b) {
        for (Device d : devices)
            if (!d.IO_Mbar() && d.hasAddress(address))
                d.writeByte(address,(int)b);
    }

    protected void ioWrite(int address, byte b) {
        for (Device d : devices)
            if (d.IO_Mbar() && d.hasAddress(address))
                d.writeByte(address,(int)b);
    }

    protected void fetch() {
        ir.setFromInt(memRead(pc.getAsInt()));
    }

    public void run() {
    }
}
