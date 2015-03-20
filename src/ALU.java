import java.util.HashMap;

public class ALU {

    Flags flags;

    public ALU() {
        flags = new Flags();
    }

    public int add(int first, int second) {
        return flags.adjust(first+second);
    }

    public int addWithCarry(int first, int second) {
        int r = (int)add(first, second);
        if (flags.getFlag("carry"))
            r++;
        return flags.adjust(r);
    }

    public int twoscomplement(int b) {
        int bt = b;
        bt = (0xFF-bt)+1;
        return flags.adjust(bt);
    }

    public int subtract(int first, int second) {
        return add(first,twoscomplement(second));
    }

    public int subtractWithBorrow(int first, int second) {
        boolean bor = flags.getFlag("carry");
        int r = subtract(first,second);
        if (bor)
            return subtract(r,(int)0x01);
        return r;
    }

    public void cmp(int first, int second) {
        flags.adjust(first-second);
    }

    public int and(int first, int second) {
        return flags.adjust(0xFF & (first & second));
    }

    public int xor(int first, int second) {
        return flags.adjust(0xFF & (first ^ second));
    }

    public int or(int first, int second) {
        return flags.adjust(0xFF & (first | second));
    }
}
