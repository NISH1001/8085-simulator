public class Register {

    protected int[] data;

    public Register(int width_bytes) {
        data = new int[width_bytes];
    }

    public void setFromBool(boolean[] values) {
        int j=0;
        for (int i=0; i<data.length; i++) {
            int setbyte = 0; int index = 1;
            do {
                if (values[j])
                    setbyte += index;
                index *= 2;
                j++;
            } while (j%8!=0 && j<values.length);
            data[i] = setbyte;
            if (j>=values.length)
                break;
        }
    }

    public boolean[] getAsBool() {
        boolean[] bvalues = new boolean[data.length*8];
        int mask = 1; int val;
        for (int i=0; i<data.length*8; i++) {
            val = data[i/8] & mask;
            bvalues[i] = (val==0)?false:true;
            mask*=2;
        }
        return bvalues;
    }

    public boolean getBit(int n) {
        boolean[] vals = getAsBool();
        if (n<vals.length)
            return vals[n];
        return false;
    }

    public void setBit(int n, boolean bitval) {
        boolean[] vals = getAsBool();
        if (n<vals.length)
            vals[n] = bitval;
        setFromBool(vals);
    }

    public void setFromInt(int value) {
        for (int i=0; i<data.length; i++) {
            data[i] = value%256;
            value /= 256;
        }
    }

    public Integer getAsInt() {
        Integer value = 0;
        int power = 1;
        for (int i=0; i<data.length; i++) {
            value += power*data[i];
            power *= 256;
        }
        return value;
   }

   public void setFromReg(Register another) {
       for (int i=0;i<data.length && i<another.data.length;i++)
           data[i] = another.data[i];
   }

   public int getBitrangeAsInt(int start, int end) {
       int res = 0;
       int index = 1;
       if (start>end) {
           int tmp = start;
           start = end;
           end = tmp;
       }
       boolean[] bits = getAsBool();
       for (int i=start; i<=end; i++) {
           if (bits[i])
               res += index;
           index *= 2;
       }
       return res;
   }
}
