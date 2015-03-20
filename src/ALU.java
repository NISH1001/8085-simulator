import java.util.HashMap;

public class ALU {

    public Flags flags;

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

    public int rlc(int num) {
        int firstbit = 0x80 & num;
        num = 0xFF & (num*2);
        if (firstbit!=0)
            num++;
        return flags.adjust(num);
    }

    public int rrc(int num) {
        int lastbit = 0x01 & num;
        num = 0xFF & (num/2);
        if (lastbit!=0)
            num = num | 0x80;
        return flags.adjust(num);
    }

    public int ral(int num) {
        int firstbit = 0x80 & num;
        boolean carry = flags.getFlag("carry");
        num = 0xFF & (num*2);
        if (carry)
            num = num++;
        flags.adjust(num);
        flags.setFlag("carry",(firstbit==0)?false:true);
        return num;
    }

    public int rar(int num) {
        int lastbit = 0x01 & num;
        boolean carry = flags.getFlag("carry");
        num = 0xFF & (num/2);
        if (carry)
            num = num|0x80;
        flags.adjust(num);
        flags.setFlag("carry",(lastbit==0)?false:true);
        return num;
    }
}
