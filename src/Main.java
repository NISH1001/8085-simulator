
public class Main
{
    public static void main(String[] args) 
	{
		Memory mem = new Memory();
		Parser p = new Parser();

		if(p.Initialize("test.txt"))
		{
			mem = p.GetMemory();
			mem.DisplayRAMHex();
		}

    }
}
