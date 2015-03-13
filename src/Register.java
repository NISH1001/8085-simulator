public class Register {

    protected byte[] data;

    public Register(int width_bytes) {
        data = new byte[width_bytes];
    }

    public void setFromBool(boolean[] values) {
        int j=0;
        for (int i=0; i<data.length; i++) {
            byte setbyte = 0; int index = 1;
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
            bvalues[i] = (mask==1)?true:false;
            mask*=2;
        }
        return bvalues;
    }

    public void setFromInt(int value) {
        for (int i=0; i<data.length; i++) {
            data[i] = (byte)(value%256);
            value /= 256;
        }
    }

    public int getAsInt() {
        int value = 0;
        int power = 1;
        for (int i=0; i<data.length; i++) {
            value += power*data[i];
            power *= 256;
        }
        return value;
   }
}
