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

    protected short memRead(int address) {
        for (Device d : devices)
            if (!d.IO_Mbar() && d.hasAddress(address))
                return d.readByte(address);
        return (short)0x00;
    }

    protected short ioRead(int address) {
        for (Device d : devices)
            if (d.IO_Mbar() && d.hasAddress(address))
                return d.readByte(address);
        return (short)0x00;
    }

    protected void memWrite(int address, short b) {
        for (Device d : devices)
            if (!d.IO_Mbar() && d.hasAddress(address))
                d.writeByte(address,(int)b);
    }

    protected void ioWrite(int address, short b) {
        for (Device d : devices)
            if (d.IO_Mbar() && d.hasAddress(address))
                d.writeByte(address,(int)b);
    }

    protected void fetch() {
        registers.get("IR").setFromInt(memRead(
                    registers.get("PC").getAsInt()));
    }

    protected Register getRegFromCode(int code) {
        if (code==0)
            return registers.get("B");
        else if (code==1)
            return registers.get("C");
        else if (code==2)
            return registers.get("D");
        else if (code==3)
            return registers.get("E");
        else if (code==4)
            return registers.get("H");
        else if (code==5)
            return registers.get("L");
        else if (code==6)
            return registers.get("M");
        else if (code==7)
            return registers.get("A");
        else
            return new Register(1);
    }

    protected void regMov(int dest, int src) {
        getRegFromCode(dest).setFromReg(getRegFromCode(src));
    }

    public void run() {
        while (true) {
            fetch();

            boolean[] irBits = registers.get("IR").getAsBool();
            Register ir = registers.get("IR");

            // If starts with 01, it is a MOV instruction
            if (!irBits[7] && irBits[6]) {
                int dst = ir.getBitrangeAsInt(3,5);
                int src = ir.getBitrangeAsInt(0,2);
                regMov(dst,src);
            }
        }
    }
}
