import java.io.*;
import java.util.*;

/*
	Our Memory class :  has both ROM and RAM
	ROM -> contains hashtable for onebyteinstruction, twobyte, threebytes
	RAM -> holds the bytevalues as integer , of instructions provided by the user 
*/

public class Memory
{
	
	public Memory()
	{
		PC = 0;
		SP = 0xFFFF;
		RAM = new short[MAXMEMSIZE];
		Arrays.fill(RAM, (short)-1);
		IO = new short[MAXIOSIZE];
	}

	public void DisplayRAMDec()
	{
		for(int i=0; i<RAM.length; ++i)
		{
			if(RAM[i] != -1)
			{
				System.out.println(RAM[i]);
			}
		}
	}

	public void DisplayRAMHex()
	{
		for(int i=0; i<RAM.length; ++i)
		{
			if(RAM[i] != -1)
			{
				System.out.println(Integer.toHexString(RAM[i]));
			}
		}
	}

	public void DisplayHashMap()
	{
		Iterator <Map.Entry<Integer, String>> iter = onebyte.entrySet().iterator();

		/*
		for(Map.Entry<Integer, String> entry : onebyte.entrySet())
		{
			System.out.println(entry.getKey() + " : " + entry.getValue() + "\n");
		}
		*/

		while(iter.hasNext())
		{
			Map.Entry<Integer, String> opcode = iter.next();
			String hexcode = Integer.toHexString(opcode.getKey());
			String engcode = opcode.getValue();
			
			System.out.println(hexcode + " " + engcode + "\n");
		}
	}

	/*store onebyte instuction hashmap ie integer value (hex) and corresponding instruction
		Static {} is used for initializing a hashmap in rip JAVA
	*/
	public static HashMap<Integer, String> onebyte;
	static
	{
		onebyte = new HashMap<Integer, String>();

		//nop instruction
		onebyte.put(0x00, "NOP");

		//generate all the MOV Rd Rs instruction
		char[] registers = {'B', 'C', 'D', 'E', 'H', 'L', 'M', 'A'};
		String mov = "MOV ";
		
		for(int index = 0; index<8; ++index)
		{
			for(int i=0; i<8; ++i)
			{
				onebyte.put((i+64) + 8*index, mov + registers[index] + " " +  registers[i]);
			}
		}

		//any onebyte instruction -> add here

		onebyte.put(0x3D, "DCR A");
		onebyte.put(0x05, "DCR B");
		onebyte.put(0x0D, "DCR C");
		onebyte.put(0x15, "DCR D");
		onebyte.put(0x1D, "DCR E");
		onebyte.put(0x25, "DCR H");
		onebyte.put(0x2D, "DCR L");
		onebyte.put(0x35, "DCR M");

		onebyte.put(0x3C, "INR A");
		onebyte.put(0x04, "INR B");
		onebyte.put(0x0C, "INR C");
		onebyte.put(0x14, "INR D");
		onebyte.put(0x1C, "INR E");
		onebyte.put(0x24, "INR H");
		onebyte.put(0x2C, "INR L");
		onebyte.put(0x34, "INR M");
	}

	public static HashMap<Integer, String> twobyte;
	static
	{
		twobyte = new HashMap<Integer, String>();

		twobyte.put(0x3E, "MVI A");
		twobyte.put(0x06, "MVI B");
		twobyte.put(0x0E, "MVI C");
		twobyte.put(0x16, "MVI D");
		twobyte.put(0x1E, "MVI E");
		twobyte.put(0x26, "MVI H");
		twobyte.put(0x2E, "MVI L");
	}

	public static HashMap<Integer, String> threebyte;
	static
	{
		threebyte = new HashMap<Integer, String>();

		threebyte.put(0x01, "LXI B");
		threebyte.put(0x11, "LXI D");
		threebyte.put(0x21, "LXI H");
		threebyte.put(0x31, "LXI SP");
	}

	public static HashMap<String, Short> REGISTER;
	static
	{
		REGISTER = new HashMap<String, Short>();
		REGISTER.put("B", (short)0);
		REGISTER.put("C", (short)0);
		REGISTER.put("D", (short)0);
		REGISTER.put("E", (short)0);
		REGISTER.put("H", (short)0);
		REGISTER.put("L", (short)0);
		REGISTER.put("A", (short)0);
		REGISTER.put("AF", (short)0);
	}

	private static final int MAXMEMSIZE = 0x10000;
	private static final int MAXIOSIZE = 0x10000;
	public short[] RAM;
	public short[] IO;
	public int SP, PC;
}