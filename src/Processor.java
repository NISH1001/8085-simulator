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
        registers.put("SP",new Register(2));
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

    protected void setRegFromCodeI(int code, int value) {
        getRegFromCode(code).setFromInt(value);
    }

    protected void regMov(int dest, int src) {
        getRegFromCode(dest).setFromReg(getRegFromCode(src));
    }

    protected int twoBytesFromMem() {
        setRegI("PC",getRegI("PC")+1);
        int val = memRead(getRegI("PC"));
        setRegI("PC",getRegI("PC")+1);
        val += 0x100*memRead(getRegI("PC"));
        return val;
    }

    protected int oneByteFromMem() {
        setRegI("PC",getRegI("PC")+1);
        return memRead(getRegI("PC"));
    }

    public void run() {
    while (true) {
        fetch();

        boolean[] irBits = registers.get("IR").getAsBool();
        Register ir = registers.get("IR");
        Register pc = registers.get("PC");
        if (ir.getAsInt()==0)
            break;

        // If starts with 00, group 1 instruction
        if (!irBits[7] && !irBits[6]) {

            // 07 is RLC
            if (ir.getAsInt()==7)
                setRegI("A",alu.rlc(getRegI("A")));

            // 0F is RRC
            else if (ir.getAsInt()==0x0F)
                setRegI("A",alu.rrc(getRegI("A")));

            // 17 is RAL
            else if (ir.getAsInt()==0x17)
                setRegI("A",alu.ral(getRegI("A")));

            // 1F is RAR
            else if (ir.getAsInt()==0x1F)
                setRegI("A",alu.rar(getRegI("A")));

            // 27 is DAA
            else if (ir.getAsInt()==0x27)
                ;

            // 2F is CMA
            else if (ir.getAsInt()==0x2F)
                setRegI("A",alu.xor(0xFF,getRegI("A")));

            // 37 is STC
            else if (ir.getAsInt()==0x37)
                alu.flags.setFlag("carry",true);

            // 3F is CTC
            else if (ir.getAsInt()==0x3F)
                alu.flags.setFlag("carry",
                        !alu.flags.getFlag("carry"));

            // 00XX X100 is INR
            else if (ir.getBitrangeAsInt(0,2)==4) {
                int reg = ir.getBitrangeAsInt(3,5);
                setRegFromCodeI(reg,
                        alu.add(getRegFromCodeI(reg),1));
            }

            // 00XX X101 is DCR
            else if (ir.getBitrangeAsInt(0,2)==5) {
                int reg = ir.getBitrangeAsInt(3,5);
                setRegFromCodeI(reg,alu.subtract(
                            getRegFromCodeI(reg),1));
            }

            // 00XX X110 is MVI
            else if (ir.getBitrangeAsInt(0,2)==6) {
                int reg = ir.getBitrangeAsInt(3,5);
                setRegFromCodeI(reg,oneByteFromMem());
            }

            // 33 is INX SP
            else if (ir.getAsInt()==0x33)
                setRegI("SP",0xFFFF&(getRegI("SP")+1));

            // 3B is DCX SP
            else if (ir.getAsInt()==0x3B) {
                if (getRegI("SP")==0)
                    setRegI("SP",0xFFFF);
                else
                    setRegI("SP",getRegI("SP")-1);
            }

            // 32 is STA
            else if (ir.getAsInt()==0x32)
                memWrite(twoBytesFromMem(),getRegI("A"));

            // 3A is LDA
            else if (ir.getAsInt()==0x3A)
                setRegI("A",twoBytesFromMem());

            // 31 is LXI SP
            else if (ir.getAsInt()==0x31)
                setRegI("SP",twoBytesFromMem());

            // 39 is DAD SP
            else if (ir.getAsInt()==0x39) {
                int sum = getRegI("SP");
                sum += getRegI("H")*0x100 + getRegI("L");
                sum = sum & 0xFFFF;
                setRegI("L",sum%0x100);
                setRegI("H",sum/0x100);
            }

            // 22 is SHLD
            else if (ir.getAsInt()==0x22) {
                int addr = twoBytesFromMem();
                memWrite(addr,getRegI("L"));
                memWrite(addr,getRegI("H"));
            }

            // 2A is LHLD
            else if (ir.getAsInt()==0x2A) {
                int addr = twoBytesFromMem();
                setRegI("L",memRead(addr));
                setRegI("H",memRead(addr+1));
            }

            // 00XX 0001 is LXI Rp
            else if (ir.getAsInt()%0x10==1) {
                int rp = ir.getBitrangeAsInt(4,5);
                int bl = oneByteFromMem();
                int bh = oneByteFromMem();
                if (rp==0) {
                    setRegI("C",bl); setRegI("B",bh);
                } else if (rp==1) {
                    setRegI("E",bl); setRegI("D",bh);
                } else if (rp==2) {
                    setRegI("L",bl); setRegI("H",bh);
                }
                // Case of rp==3 is already covered above in
                // LXI SP
            }

            // 00XX 0010 is STAX Rp
            else if (ir.getAsInt()%0x10==2) {
                int rp = ir.getBitrangeAsInt(4,5); int addr;
                if (rp==0)
                    addr = getRegI("B")*0x100+getRegI("C");
                else // rp == 1
                    addr = getRegI("D")*0x100+getRegI("E");
                // Cases rp==2 [SHLD] and rp==3 [STA]
                // are already dealt with
                memWrite(addr,getRegI("A"));
            }

            // 00XX 1010 is LDAX Rp
            else if (ir.getAsInt()%0x10==0xA) {
                int rp = ir.getBitrangeAsInt(4,5); int addr;
                if (rp==0)
                    addr = getRegI("B")*0x100+getRegI("C");
                else // rp == 1
                    addr = getRegI("D")*0x100+getRegI("E");
                // Cases rp==2 [LHLD] and rp==3 [LDA]
                // are already dealt with
                setRegI("A",memRead(addr));
            }

            // 00XX 1001 is DAD Rp
            else if (ir.getAsInt()%0x10==0x9) {
                int rp = ir.getBitrangeAsInt(4,5); int sum=0;
                if (rp==0)
                    sum = getRegI("B")*0x100+getRegI("C");
                else if (rp==1)
                    sum = getRegI("D")*0x100+getRegI("E");
                else if (rp==2)
                    sum = getRegI("H")*0x100+getRegI("L");
                // The case of rp==3 is DAD SP, already dealt
                sum += getRegI("H")*0x100+getRegI("L");
                sum = sum & 0xFFFF;
                setRegI("L",sum%0x100);
                setRegI("H",sum/0x100);
            }

            // 00XX 0011 is INX Rp
            else if (ir.getAsInt()%0x10==0x3) {
                int rp = ir.getBitrangeAsInt(4,5); int num;
                if (rp==0) {
                    num = getRegI("B")*0x100+getRegI("C")+1;
                    num = num & 0xFFFF;
                    setRegI("C",num%0x100);
                    setRegI("B",num/0x100);
                } else if (rp==1) {
                    num = getRegI("D")*0x100+getRegI("E")+1;
                    num = num & 0xFFFF;
                    setRegI("E",num%0x100);
                    setRegI("D",num/0x100);
                } else if (rp==2) {
                    num = getRegI("H")*0x100+getRegI("L")+1;
                    num = num & 0xFFFF;
                    setRegI("L",num%0x100);
                    setRegI("H",num/0x100);
                }
                // The case of rp==3 is INX SP, already dealt
            }

            // 00XX 1011 is DCX Rp
            else if (ir.getAsInt()%0x10==0xB) {
                int rp = ir.getBitrangeAsInt(4,5); int num;
                if (rp==0) {
                    num = getRegI("B")*0x100+getRegI("C")-1;
                    if (num<0)
                        num = 0xFFFF;
                    num = num & 0xFFFF;
                    setRegI("C",num%0x100);
                    setRegI("B",num/0x100);
                } else if (rp==1) {
                    num = getRegI("D")*0x100+getRegI("E")-1;
                    if (num<0)
                        num = 0xFFFF;
                    num = num & 0xFFFF;
                    setRegI("E",num%0x100);
                    setRegI("D",num/0x100);
                } else if (rp==2) {
                    num = getRegI("H")*0x100+getRegI("L")-1;
                    if (num<0)
                        num = 0xFFFF;
                    num = num & 0xFFFF;
                    setRegI("L",num%0x100);
                    setRegI("H",num/0x100);
                }
                // The case of rp==3 is INX SP, already dealt
            }

            // TODO DAA, RIM, SIM
        }


        // If starts with 01, it is a MOV instruction
        else if (!irBits[7] && irBits[6]) {
            int dst = ir.getBitrangeAsInt(3,5);
            int src = ir.getBitrangeAsInt(0,2);
            regMov(dst,src);


        // If starts with 10, it is an arith. or logical
        } else if (irBits[7] && !irBits[6]) {

            // If 1000, then ADD or ADC
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
                alu.cmp(getRegI("A"),getRegFromCodeI(reg));
            }

        }
        // Increment PC
        setRegI("PC",getRegI("PC")+1);
    } // end while
    }

    public void showRegs() {
        for (String key : registers.keySet()) {
            String val =
                registers.get(key).getAsInt().toString();
            System.out.println(key+" : "+val);
        }
    }
}
