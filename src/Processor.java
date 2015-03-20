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

    public Register getReg(String name) {
        return registers.get(name);
    }

    public int getRegI(String name) {
        return registers.get(name).getAsInt();
    }

    public void setRegI(String name, int value) {
        registers.get(name).setFromInt(value);
    }

    protected int memRead(int address) {
        for (Device d : devices) {
            if (!d.IO_Mbar() && d.hasAddress(address))
                return d.readByte(address);
            if (!d.hasAddress(address))
                System.out.println("fuckup");
        }
        return (int)0x00;
    }

    protected int ioRead(int address) {
        for (Device d : devices)
            if (d.IO_Mbar() && d.hasAddress(address))
                return d.readByte(address);
        return (int)0x00;
    }

    protected void memWrite(int address, int b) {
        for (Device d : devices)
            if (!d.IO_Mbar() && d.hasAddress(address))
                d.writeByte(address,(int)b);
    }

    protected void ioWrite(int address, int b) {
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

    protected int getRegFromCodeI(int code) {
        return getRegFromCode(code).getAsInt();
    }

    protected void regMov(int dest, int src) {
        getRegFromCode(dest).setFromReg(getRegFromCode(src));
    }

    public void run() {
        while (true) {
            fetch();

            boolean[] irBits = registers.get("IR").getAsBool();
            Register ir = registers.get("IR");
            if (ir.getAsInt()==0)
                break;

            // If starts with 01, it is a MOV instruction
            if (!irBits[7] && irBits[6]) {
                int dst = ir.getBitrangeAsInt(3,5);
                int src = ir.getBitrangeAsInt(0,2);
                regMov(dst,src);
            // If starts with 10, it is an arith. or logical
            } else if (irBits[7] && !irBits[6]) {
                // If 10000 xxx, then ADD or ADC , xxx = register binary code
                if (!irBits[5] && !irBits[4]) {
                    int reg = ir.getBitrangeAsInt(0,2);
                    if (irBits[3])
                        setRegI("A",alu.addWithCarry(
                            getRegI("A"),getRegFromCodeI(reg)));
                    else
                        setRegI("A",alu.add(getRegI("A"),
                                    getRegFromCodeI(reg)));
                }
                // If 1001, then SUB or SBB
                else if (!irBits[5] && irBits[4]) {
                    int reg = ir.getBitrangeAsInt(0,2);
                    if (irBits[3])
                        setRegI("A",alu.subtractWithBorrow(
                            getRegI("A"),getRegFromCodeI(reg)));
                    else
                        setRegI("A",alu.subtract(getRegI("A"),
                                    getRegFromCodeI(reg)));
                }
                // If 1010 0XXX then ANA
                else if(irBits[5] && !irBits[4] && !irBits[3]) {
                    int reg = ir.getBitrangeAsInt(0,2);
                    setRegI("A",alu.and(getRegI("A"),
                                getRegFromCodeI(reg)));
                }
                // If 1010 1XXX then XRA
                else if(irBits[5] && !irBits[4] && irBits[3]) {
                    int reg = ir.getBitrangeAsInt(0,2);
                    setRegI("A",alu.xor(getRegI("A"),
                                getRegFromCodeI(reg)));
                }
                // If 1011 0XXX then ORA
                else if(irBits[5] && irBits[4] && !irBits[3]) {
                    int reg = ir.getBitrangeAsInt(0,2);
                    setRegI("A",alu.or(getRegI("A"),
                                getRegFromCodeI(reg)));
                }
                // If 1011 1XXX then CMP
                else if(irBits[5] && irBits[4] && irBits[3]) {
                    int reg = ir.getBitrangeAsInt(0,2);
                    setRegI("A",alu.cmp(getRegI("A"),
                                getRegFromCodeI(reg)));
                }
            }
            // Increment PC
            setRegI("PC",getRegI("PC")+1);
        }
    }

    public void showRegs() {
        for (String key : registers.keySet()) {
            String val =
                registers.get(key).getAsInt().toString();
            System.out.println(key+" : "+val);
        }
    }
}
