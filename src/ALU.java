import java.util.HashMap;

public class ALU {

    Flags flags;

    public ALU() {
        flags = new Flags();
    }

    public byte add(byte first, byte second) {
        int f,s,r;
        f = first; s = second; r = first+second;
        return flags.adjust(r);
    }

    public byte addWithCarry(byte first, byte second) {
        int r = (int)add(first, second);
        if (flags.getFlag("carry"))
            r++;
        return flags.adjust(r);
    }

    public byte twoscomplement(byte b) {
        int bt = b;
        bt = (0xFF-bt)+1;
        return flags.adjust(bt);
    }

    public byte subtract(byte first, byte second) {
        return add(first,twoscomplement(second));
    }

    public byte subtractWithBorrow(byte first, byte second) {
        boolean bor = flags.getFlag("carry");
        byte r = subtract(first,second);
        if (bor)
            return subtract(r,(byte)0x01);
        return r;
    }
}
