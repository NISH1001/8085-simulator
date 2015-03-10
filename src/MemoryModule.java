public class MemoryModule implements Device {

    private short start_addr,addr_size;
    private byte data[];

    public MemoryModule(int start, int size) {
        start_addr = start;
        addr_size = size;
        data = new byte[size];
    }

    public boolean hasAddress(int address) {
        if (address>=start && address<start+size)
            return true;
        else
            return false;
    }

    public void writeByte(int address, int databyte) {
        data[address-start_addr] = (byte)databyte;
    }

    public byte readByte(int address) {
        return data[address-start_addr];
    }
}
