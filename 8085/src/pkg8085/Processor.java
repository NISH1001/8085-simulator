package pkg8085;

import java.util.ArrayList;
import java.util.HashMap;

public class Processor implements Runnable {

    public ALU alu;
    private HashMap<String,Register> registers;
    private ArrayList<Device> devices;
    private Boolean enable_intr,stopped,running;
    private Boolean trap,r7,r6,r5;
    private Boolean mr7,mr6,mr5;
    private Boolean sid,sod;

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
        setRegI("SP",0xFFFF);
        enable_intr = false; stopped = false; running = false;
        trap = false; r7 = false; r6 = false; r5 = false;
        mr7 = true; mr6 = true; mr5 = true;
        sid = false; sod = false;
    }

    // Send an interrupt externally. code means:
    // 0:TRAP, 1:7.5 2:6.5 3:5.5
    public void inter(int code) {
        if (code==0)
            synchronized(trap) { trap = true; }
        else if (code==1)
            synchronized(r7) { r7 = true; }
        else if (code==2)
            synchronized(r6) { r6 = true; }
        else if (code==3)
            synchronized(r5) { r5 = true; }
    }

    public void stop() {
        synchronized(stopped) {
            stopped = true;
        }
    }

    public boolean isRunning() {
        synchronized (running) {
        return running; }
    }

    public void addDevice(Device d) {
        devices.add(d);
    }

    public Register getReg(String name) {
        return registers.get(name);
    }

    public Integer getRegI(String name) {
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
        else if (code==6) {
            Register memreg = new Register(1);
            memreg.setFromInt(memRead(
                        getRegI("H")*0x100+getRegI("L")));
            return memreg;
        } else if (code==7)
            return registers.get("A");
        else
            return new Register(1);
    }

    protected Integer getRegFromCodeI(int code) {
        return getRegFromCode(code).getAsInt();
    }

    protected void setRegFromCodeI(int code, int value) {
        // Special treatment for the M 'register'
        if (code==6)
            memWrite(getRegI("H")*0x100+getRegI("L"),value);
        else
            getRegFromCode(code).setFromInt(value);
    }

    protected void regMov(int dest, int src) {
        setRegFromCodeI(dest,getRegFromCodeI(src));
    }

    protected int twoBytesFromMem() {
        int val = memRead(getRegI("PC"));
        setRegI("PC",getRegI("PC")+1);
        val += 0x100*memRead(getRegI("PC"));
        setRegI("PC",getRegI("PC")+1);
        return val;
    }

    protected int oneByteFromMem() {
        int res = memRead(getRegI("PC"));
        setRegI("PC",getRegI("PC")+1);
        return res;
    }

    public void run() {
        Register pc = registers.get("PC");
        synchronized(running) { running = true; }

    while (true) {

        synchronized(stopped) { if (stopped) {
                stopped = false;
                break;
        }}
        synchronized(trap) { if (trap) {
            push(pc.getAsInt());
            enable_intr = false;
            pc.setFromInt(0x24);
        }}
        synchronized(r7) { if (r7 && !mr7 && enable_intr) {
            push(pc.getAsInt());
            mr6 = true; mr5 = true;
            pc.setFromInt(0x3C);
        }}
        synchronized(r6) { if (r6 && !mr6 && enable_intr) {
            push(pc.getAsInt());
            mr5 = true;
            pc.setFromInt(0x34);
        }}
        synchronized(r5) { if (r5 && !mr5 && enable_intr) {
            push(pc.getAsInt());
            pc.setFromInt(0x2C);
        }}

        fetch();
        pc.setFromInt(pc.getAsInt()+1);

        boolean[] irBits = registers.get("IR").getAsBool();
        Register ir = registers.get("IR");

        // Break on HLT
        if (ir.getAsInt() == 0x76)
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
            else if (ir.getAsInt()==0x27) {
                if (getRegI("A")%0x10>9 ||
                        alu.flags.getFlag("auxcarry"))
                    setRegI("A",alu.add(getRegI("A"),6));
                if (getRegI("A")/0x10>9 ||
                        alu.flags.getFlag("carry"))
                    setRegI("A",alu.add(getRegI("A"),0x60));
            }

            // 2F is CMA
            else if (ir.getAsInt()==0x2F)
                setRegI("A",alu.complement(getRegI("A")));

            // 37 is STC
            else if (ir.getAsInt()==0x37)
                alu.flags.setFlag("carry",true);

            // 3F is CMC
            else if (ir.getAsInt()==0x3F)
                alu.flags.setFlag("carry",
                        !alu.flags.getFlag("carry"));

            // 00XX X100 is INR
            else if (ir.getBitrangeAsInt(0,2)==4) {
                int reg = ir.getBitrangeAsInt(3,5);
                // INR leaves CY flag unaffected.
                // hence this workaround.
                boolean cyflag = alu.flags.getFlag("carry");
                setRegFromCodeI(reg,
                        alu.add(getRegFromCodeI(reg),1));
                alu.flags.setFlag("carry",cyflag);
            }

            // 00XX X101 is DCR
            else if (ir.getBitrangeAsInt(0,2)==5) {
                int reg = ir.getBitrangeAsInt(3,5);
                // DCR leaves CY flag unaffected.
                // hence this workaround.
                boolean cyflag = alu.flags.getFlag("carry");
                setRegFromCodeI(reg,alu.subtract(
                            getRegFromCodeI(reg),1));
                alu.flags.setFlag("carry",cyflag);
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
            else if (ir.getAsInt()==0x3A) {
                setRegI("A",memRead(twoBytesFromMem()));
            }

            // 31 is LXI SP
            else if (ir.getAsInt()==0x31)
                setRegI("SP",twoBytesFromMem());

            // 39 is DAD SP
            else if (ir.getAsInt()==0x39) {
                int sum = getRegI("SP"), res;
                sum += getRegI("H")*0x100 + getRegI("L");
                res = sum & 0xFFFF;
                setRegI("L",res%0x100);
                setRegI("H",res/0x100);
                if (res<sum)
                    alu.flags.setFlag("carry",true);
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

            // 30 is SIM
            else if (ir.getAsInt()==0x30) {
                int acc = getRegI("A");
                // If Serial Data enable, output SOD
                if ((acc&0x40)!=0)
                    sod = (acc&0x80)!=0;

                // If bit D4 is set, reset R7.5
                if ((acc&0x10)!=0)
                    r7 = false;

                // If mask set enabled, mask the interrupts
                if ((acc&0x08)!=0) {
                    if ((acc&0x04)!=0) mr7 = true;
                    else mr7 = false;
                    if ((acc&0x02)!=0) mr6 = true;
                    else mr6 = false;
                    if ((acc&0x01)!=0) mr5 = true;
                    else mr5 = false;
                }
            }

            // 20 is RIM
            else if (ir.getAsInt()==0x20) {
                int sttus = 0;
                // Read SiD to D7
                sttus = sttus | (sid?0x80:0x00);
                // Read interrupt bits
                sttus = sttus | (r7?0x40:0x00);
                sttus = sttus | (r6?0x20:0x00);
                sttus = sttus | (r5?0x10:0x00);
                // Read IE
                sttus = sttus | (enable_intr?0x08:0x00);
                // Read interrupt mask bits
                sttus = sttus | (mr7?0x04:0x00);
                sttus = sttus | (mr6?0x02:0x00);
                sttus = sttus | (mr5?0x01:0x00);
                // Put to Accumulator
                setRegI("A",sttus);
            }
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
                // CY is reset and AC is set after ANA
                alu.flags.setFlag("carry",false);
                alu.flags.setFlag("auxcarry",true);
            }

            // If 1010 1XXX then XRA
            else if(irBits[5] && !irBits[4] && irBits[3]) {
                int reg = ir.getBitrangeAsInt(0,2);
                setRegI("A",alu.xor(getRegI("A"),
                            getRegFromCodeI(reg)));
                // XRA resets flags CY and AC
                alu.flags.setFlag("carry",false);
                alu.flags.setFlag("auxcarry",false);
            }

            // If 1011 0XXX then ORA
            else if(irBits[5] && irBits[4] && !irBits[3]) {
                int reg = ir.getBitrangeAsInt(0,2);
                setRegI("A",alu.or(getRegI("A"),
                            getRegFromCodeI(reg)));
                // ORA resets AC and CY
                alu.flags.setFlag("auxcarry",false);
                alu.flags.setFlag("carry",false);
            }

            // If 1011 1XXX then CMP
            else if(irBits[5] && irBits[4] && irBits[3]) {
                int reg = ir.getBitrangeAsInt(0,2);
                alu.cmp(getRegI("A"),getRegFromCodeI(reg));
            }
        }
        else if (irBits[7] && irBits[6]) {

            // C9 is RET
            if (ir.getAsInt()==0xC9) {
                int retaddr = pop();
                pc.setFromInt(retaddr);
            }

            // 11XX X000 is RX (conditional return)
            else if (ir.getAsInt()%0x8==0) {
                int cond = ir.getBitrangeAsInt(3,5);
                if (alu.flags.check(cond)) {
                    int retaddr = pop();
                    pc.setFromInt(retaddr);
                }
            }

            // C3 is JMP
            else if (ir.getAsInt()==0xC3) {
                    int retaddr = twoBytesFromMem();
                    pc.setFromInt(retaddr);
            }

            // 11XX X010 is JX (conditional jump)
            else if (ir.getAsInt()%0x8==2) {
                int cond = ir.getBitrangeAsInt(3,5);
                int retaddr = twoBytesFromMem();
                if (alu.flags.check(cond)) {
                    pc.setFromInt(retaddr);
                }
            }

            // CD is CALL
            else if (ir.getAsInt()==0xCD) {
                int addr = twoBytesFromMem();
                push(addr); pc.setFromInt(addr);
            }

            // 11XX X100 is CX (conditional call)
            else if (ir.getAsInt()%0x8==4) {
                int cond = ir.getBitrangeAsInt(3,5);
                int addr = twoBytesFromMem();
                if (alu.flags.check(cond)) {
                    push(addr);
                    pc.setFromInt(addr);
                }
            }

            // D3 is OUT
            else if (ir.getAsInt()==0xD3) {
                int addr = oneByteFromMem();
                ioWrite(addr,getRegI("A"));
            }

            // DB is IN
            else if (ir.getAsInt()==0xDB) {
                int addr = oneByteFromMem();
                setRegI("A",ioRead(addr));
            }

            // E3 is XTHL
            else if (ir.getAsInt()==0xEB) {
                int bh,bl;
                bl = memRead(getRegI("SP"));
                bh = memRead(getRegI("SP")+1);
                memWrite(getRegI("SP"),getRegI("L"));
                memWrite(getRegI("SP")+1,getRegI("H"));
                setRegI("L",bl); setRegI("H",bh);
            }

            // F3 is DI
            else if (ir.getAsInt()==0xEB) {
                enable_intr = false;
            }

            // 11XX 0001 is POP Rp
            else if (ir.getAsInt()%0x10==1) {
                int rp = ir.getBitrangeAsInt(4,5);
                int val = pop();
                if (rp==0) {
                    setRegI("B",val/0x100);
                    setRegI("C",val%0x100);
                } else if (rp==1) {
                    setRegI("D",val/0x100);
                    setRegI("E",val%0x100);
                } else if (rp==2) {
                    setRegI("H",val/0x100);
                    setRegI("L",val%0x100);
                } else if (rp==3) {
                    setRegI("A",val/0x100);
                    alu.flags.setFromInt(val%0x100);
                }
            }

            // 11XX 0101 is PUSH Rp
            else if (ir.getAsInt()%0x10==5) {
                int rp = ir.getBitrangeAsInt(4,5); int val = 0;
                if (rp==0) {
                    val = getRegI("B")*0x100+getRegI("C");
                } else if (rp==1) {
                    val = getRegI("D")*0x100+getRegI("E");
                } else if (rp==2) {
                    val = getRegI("H")*0x100+getRegI("L");
                } else if (rp==3) {
                    val = getRegI("A")*0x100+
                        alu.flags.getAsInt();
                }
                push(val);
            }

            // 11XX X110 are immediate arithmetic/logic.
            // ADI,SUI,ANI,ORI,ACI,SBI,XRI,CPI
            else if (ir.getAsInt()%0x8==6) {
                int val = oneByteFromMem();
                int code = ir.getBitrangeAsInt(3,5);
                if (code==0)  // ADI
                    setRegI("A",alu.add(getRegI("A"),val));
                else if (code==1)  // ACI
                    setRegI("A",
                            alu.addWithCarry(getRegI("A"),val));
                else if (code==2)  // SUI
                    setRegI("A",alu.subtract(getRegI("A"),val));
                else if (code==3)  // SBI
                    setRegI("A",alu.subtractWithBorrow(
                                getRegI("A"),val));
                else if (code==4) { // ANI
                    setRegI("A",alu.and(getRegI("A"),val));
                    alu.flags.setFlag("carry",false);
                    alu.flags.setFlag("auxcarry",true);
                } else if (code==5) { // XRI
                    setRegI("A",alu.xor(getRegI("A"),val));
                    alu.flags.setFlag("carry",false);
                    alu.flags.setFlag("auxcarry",false);
                } else if (code==6) { // ORI
                    setRegI("A",alu.or(getRegI("A"),val));
                    alu.flags.setFlag("carry",false);
                    alu.flags.setFlag("auxcarry",false);
                } else if (code==7)  // CPI
                    alu.cmp(getRegI("A"),val);
            }

            // 11XX X111 are RST X
            else if (ir.getAsInt()%0x8==7) {
                int code = ir.getBitrangeAsInt(3,5);
                pc.setFromInt(code*0x8);
            }

            // E9 is PCHL
            else if (ir.getAsInt()==0xE9) {
                pc.setFromInt(getRegI("H")*0x100+getRegI("L"));
            }

            // F9 is SPHL
            else if (ir.getAsInt()==0xF9) {
                registers.get("SP").setFromInt(
                        getRegI("H")*0x100+getRegI("L"));
            }

            // EB is XCHG
            else if (ir.getAsInt()==0xEB) {
                int e = getRegI("E"); int d = getRegI("D");
                setRegI("E",getRegI("L"));
                setRegI("D",getRegI("H"));
                setRegI("H",d); setRegI("L",e);
            }

            // FB is EI
            else if (ir.getAsInt()==0xFB) {
                enable_intr = true;
            }
        }

    } // end while
        synchronized(running) { running = false; }
    }

    public void showRegs() {
        for (String key : registers.keySet()) {
            String val = Integer.toHexString(
                    registers.get(key).getAsInt());
            System.out.println(key+" : "+val);
        }
    }

    protected void push(int value) {
        int bl,bh,sp;
        bl = value%0x100; bh = value/0x100;
        sp = getRegI("SP");
        if (sp==0) sp = 0xFFFF;
        else sp--;
        memWrite(sp,bh);
        if (sp==0) sp = 0xFFFF;
        else sp--;
        memWrite(sp,bl);
        setRegI("SP",sp);
    }

    protected int pop() {
        int bl,bh,sp;
        sp = getRegI("SP");
        bl = memRead(sp);
        if (sp==0xFFFF) sp = 0;
        else sp++;
        bh = memRead(sp);
        if (sp==0xFFFF) sp = 0;
        else sp++;
        setRegI("SP",sp);
        return bh*0x100+bl;
    }
}
