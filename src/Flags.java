public class Flags extends Register {

    public Flags() {
        super((short)8);
    }

    public boolean getFlag(String flag) {
        if (flag=="carry")
            return data[0];
        else if (flag=="parity")
            return data[1];
        else if (flag=="auxcarry")
            return data[3];
        else if (flag=="zero")
            return data[5];
        else if (flag=="sign")
            return data[7];
        else
            return false;
    }

    public void setFlag(String flag, boolean value) {
        if (flag=="carry")
            data[0] = value;
        else if (flag=="parity")
            data[1] = value;
        else if (flag=="auxcarry")
            data[3] = value;
        else if (flag=="zero")
            data[5] = value;
        else if (flag=="sign")
            data[7] = value;
    }

    public byte adjust(int result) {
        byte res = (byte)(0xFF & result);
        if (result==0)
            data[5]=true;
        else if (result>0x7f)
            data[7]=true;
        if (result>res)
            data[0]=true;
        boolean parity = false;
        while (result!=0) {
            if (result%2==1)
                parity=!parity;
            result/=2;
        }
        data[1] = parity;
        return res;
    }
}
