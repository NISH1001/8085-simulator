import java.util.ArrayList;
import java.util.HashMap;

public class Processor {

    private ALU alu;
    private HashMap<String,Register> registers;
    private ArrayList<Device> devices;

    public Processor() {
        alu = new ALU();
        devices = new ArrayList<Device>();
        registers = new HashMap<String,Register>();
        registers.put("A",new Register(1));
        registers.put("B",new Register(1));
        registers.put("C",new Register(1));
        registers.put("D",new Register(1));
        registers.put("E",new Register(1));
        registers.put("H",new Register(1));
        registers.put("L",new Register(1));
        registers.put("IR",new Register(1));
        registers.put("DR",new Register(1));
        registers.put("PC",new Register(2));
        registers.put("MAR",new Register(2));
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
        registers.get("IR").setFromInt(memRead(
                    registers.get("PC").getAsInt()));
    }

    public void run() {
    }
}
