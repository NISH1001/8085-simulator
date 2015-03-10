public interface Device {

    public boolean hasAddress(int address);
    public void writeByte(int address, int data);
    public byte readByte(int address);
}
