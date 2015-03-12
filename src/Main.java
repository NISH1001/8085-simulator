public class Main
{
    public static void main(String[] args)
    {
        //opcode object used by parser
        Opcode op = new Opcode();
        Parser p = new Parser();

        MemoryModule memory = new MemoryModule(0x0000, 0x10000);

        //if parser is successful get memory
        if(p.Initialize("test.txt"))
        {
            //p.ShowData();
            p.WriteToMemory(memory, 0x8000);
        }

        System.out.println(memory.readByte(0x8001));
   }

}
