public class Flags extends Register {

    public Flags() {
        super((short)1);
    }

    public boolean getFlag(String flag) {
        if (flag=="carry")
            return getBit(0);
        else if (flag=="parity")
            return getBit(1);
        else if (flag=="auxcarry")
            return getBit(3);
        else if (flag=="zero")
            return getBit(5);
        else if (flag=="sign")
            return getBit(7);
        else
            return false;
    }

    public void setFlag(String flag, boolean value) {
        if (flag=="carry")
            setBit(0,value);
        else if (flag=="parity")
            setBit(2,value);
        else if (flag=="auxcarry")
            setBit(4,value);
        else if (flag=="zero")
            setBit(6,value);
        else if (flag=="sign")
            setBit(7,value);
    }

    public short adjust(int result) {
        short res = (short)(0xFF & result);
        if (result==0)
            setBit(6,true);
        else if (result>0x7f)
            setBit(7,true);
        if (result>res)
            setBit(0,true);
        boolean parity = false;
        while (result!=0) {
            if (result%2==1)
                parity=!parity;
            result/=2;
        }
        setBit(2,parity);
        return res;
    }
}
