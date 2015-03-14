import java.util.HashMap;

public class ALU {

    Flags flags;

    public ALU() {
        flags = new Flags();
    }

    public short add(short first, short second) {
        int f,s,r;
        f = first; s = second; r = first+second;
        return flags.adjust(r);
    }

    public short addWithCarry(short first, short second) {
        int r = (int)add(first, second);
        if (flags.getFlag("carry"))
            r++;
        return flags.adjust(r);
    }

    public short twoscomplement(short b) {
        int bt = b;
        bt = (0xFF-bt)+1;
        return flags.adjust(bt);
    }

    public short subtract(short first, short second) {
        return add(first,twoscomplement(second));
    }

    public short subtractWithBorrow(short first, short second) {
        boolean bor = flags.getFlag("carry");
        short r = subtract(first,second);
        if (bor)
            return subtract(r,(short)0x01);
        return r;
    }
}
