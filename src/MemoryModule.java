public class MemoryModule implements Device {

    private short start_addr,addr_size;
    private short data[];

    public MemoryModule(int start, int size) {
        start_addr = (short)start;
        addr_size = (short)size;
        data = new short[size];
    }

    public boolean hasAddress(int address) {
        if(address>=start_addr && address<start_addr+addr_size)
            return true;
        else
            return false;
    }

    public boolean IO_Mbar() {
        return false;
    }

    public void writeByte(int address, int databyte) {
        data[address-start_addr] = (short)databyte;
    }

    public short readByte(int address) {
        return data[address-start_addr];
   }
}
