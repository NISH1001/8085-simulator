package pkg8085.core;


public class Register {

    protected boolean[] data;

    public Register(int width) {
        data = new boolean[width];
    }

    public void set(boolean[] values) {
        for (int i=0; i<data.length && i<values.length; i++) {
            data[i] = values[i];
        }
    }

    public boolean[] get() {
        return data;
    }

    public void setFromInt(int value) {
        for (int i=0; i<data.length; i++) {
            data[i] = (value%2==0)?false:true;
            value /= 2;
        }
    }

    public int getAsInt() {
        int value = 0;
        int power = 1;
        for (int i=0; i<data.length; i++) {
            if (data[i]==true)
                value += power;
            power *= 2;
        }
        return value;
   }
}
