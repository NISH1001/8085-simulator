package pkg8085;

public interface Device {
    public boolean hasAddress(int address);
    public boolean IO_Mbar();
    public void writeByte(int address, int data);
    public int readByte(int address);
}
