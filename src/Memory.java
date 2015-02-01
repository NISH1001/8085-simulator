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

				onebyte.put(0x8F, "ADC A") ; 
				onebyte.put(0x88, "ADC B") ; 
				onebyte.put(0x89, "ADC C") ; 
				onebyte.put(0x8A, "ADC D") ; 
				onebyte.put(0x8B, "ADC E") ; 
				onebyte.put(0x8C, "ADC H") ; 
				onebyte.put(0x8D, "ADC L") ; 
				onebyte.put(0x8E, "ADC M") ; 
				onebyte.put(0x87, "ADD A") ; 
				onebyte.put(0x80, "ADD B") ; 
				onebyte.put(0x81, "ADD C") ; 
				onebyte.put(0x82, "ADD D") ; 
				onebyte.put(0x83, "ADD E") ; 
				onebyte.put(0x84, "ADD H") ; 
				onebyte.put(0x85, "ADD L") ; 
				onebyte.put(0x86, "ADD M") ; 
				onebyte.put(0xA7, "ANA A") ; 
				onebyte.put(0xA0, "ANA B") ; 
				onebyte.put(0xA1, "ANA C") ; 
				onebyte.put(0xA2, "ANA D") ; 
				onebyte.put(0xA3, "ANA E") ; 
				onebyte.put(0xA4, "ANA H") ; 
				onebyte.put(0xA5, "ANA L") ; 
				onebyte.put(0xA6, "ANA M") ; 
				onebyte.put(0x2F, "CMA"  ) ; 
				onebyte.put(0x3F, "CMC"  ) ; 
				onebyte.put(0xBF, "CMP A") ; 
				onebyte.put(0xB8, "CMP B") ; 
				onebyte.put(0xB9, "CMP C") ; 
				onebyte.put(0xBA, "CMP D") ; 
				onebyte.put(0xBB, "CMP E") ; 
				onebyte.put(0xBC, "CMP H") ; 
				onebyte.put(0xBD, "CMP L") ; 
				onebyte.put(0xBD, "CMP M") ; 
				onebyte.put(0x27, "DAA"  ) ;
				onebyte.put(0x09, "DAD B") ;
				onebyte.put(0x19, "DAD D") ;
				onebyte.put(0x29, "DAD H") ;
				onebyte.put(0x39, "DAD SP");
				onebyte.put(0x3D, "DCR A") ;
				onebyte.put(0x05, "DCR B") ;
				onebyte.put(0x0D, "DCR C") ;
				onebyte.put(0x15, "DCR D") ;
				onebyte.put(0x1D, "DCR E") ;
				onebyte.put(0x25, "DCR H") ;
				onebyte.put(0x2D, "DCR L") ;
				onebyte.put(0x35, "DCR M") ;
				onebyte.put(0x0B, "DCX B") ;
				onebyte.put(0x1B, "DCX D") ;
				onebyte.put(0x2B, "DCX H") ;
				onebyte.put(0x3B, "DCX SP" );
				onebyte.put(0xF3,"DI")  ;
				onebyte.put(0xFB,"EI" ) ;
				onebyte.put(0x76,"HLT") ;
				onebyte.put(0x3C, "INR A"); 
				onebyte.put(0x04, "INR B"); 
				onebyte.put(0x0C, "INR C"); 
				onebyte.put(0x14, "INR D"); 
				onebyte.put(0x1C, "INR E"); 
				onebyte.put(0x24, "INR H"); 
				onebyte.put(0x2C, "INR L"); 
				onebyte.put(0x34, "INR M"); 
				onebyte.put(0x03, "INX B") ; 
				onebyte.put(0x13, "INX D") ; 
				onebyte.put(0x23, "INX H") ; 
				onebyte.put(0x33, "INX SP") ;
				onebyte.put(0x0A, "LDAX B") ; 
				onebyte.put(0x1A, "LDAX D") ; 
				onebyte.put(0x7F, "MOV A, A"); 
				onebyte.put(0x78, "MOV A, B"); 
				onebyte.put(0x79, "MOV A, C"); 
				onebyte.put(0x7A, "MOV A, D"); 
				onebyte.put(0x7B, "MOV A, E"); 
				onebyte.put(0x7C, "MOV A, H"); 
				onebyte.put(0x7D, "MOV A, L"); 
				onebyte.put(0x7E, "MOV A, M"); 
				onebyte.put(0x47, "MOV B, A"); 
				onebyte.put(0x40, "MOV B, B"); 
				onebyte.put(0x41, "MOV B, C"); 
				onebyte.put(0x42, "MOV B, D"); 
				onebyte.put(0x43, "MOV B, E"); 
				onebyte.put(0x44, "MOV B, H"); 
				onebyte.put(0x45, "MOV B, L"); 
				onebyte.put(0x46, "MOV B, M"); 
				onebyte.put(0x4F, "MOV C, A"); 
				onebyte.put(0x48, "MOV C, B"); 
				onebyte.put(0x49, "MOV C, C"); 
				onebyte.put(0x4A, "MOV C, D"); 
				onebyte.put(0x4B, "MOV C, E"); 
				onebyte.put(0x4C, "MOV C, H"); 
				onebyte.put(0x4D, "MOV C, L"); 
				onebyte.put(0x4E, "MOV C, M"); 
				onebyte.put(0x57, "MOV D, A"); 
				onebyte.put(0x50, "MOV D, B"); 
				onebyte.put(0x51, "MOV D, C"); 
				onebyte.put(0x52, "MOV D, D"); 
				onebyte.put(0x53, "MOV D, E"); 
				onebyte.put(0x54, "MOV D, H"); 
				onebyte.put(0x55, "MOV D, L"); 
				onebyte.put(0x56, "MOV D, M"); 
				onebyte.put(0x5F, "MOV E, A"); 
				onebyte.put(0x58, "MOV E, B"); 
				onebyte.put(0x59, "MOV E, C"); 
				onebyte.put(0x5A, "MOV E, D"); 
				onebyte.put(0x5B, "MOV E, E"); 
				onebyte.put(0x5C, "MOV E, H"); 
				onebyte.put(0x5D, "MOV E, L"); 
				onebyte.put(0x5E, "MOV E, M"); 
				onebyte.put(0x67, "MOV H, A"); 
				onebyte.put(0x60, "MOV H, B"); 
				onebyte.put(0x61, "MOV H, C"); 
				onebyte.put(0x62, "MOV H, D"); 
				onebyte.put(0x63, "MOV H, E"); 
				onebyte.put(0x64, "MOV H, H"); 
				onebyte.put(0x65, "MOV H, L"); 
				onebyte.put(0x66, "MOV H, M"); 
				onebyte.put(0x6F, "MOV L, A"); 
				onebyte.put(0x68, "MOV L, B"); 
				onebyte.put(0x69, "MOV L, C"); 
				onebyte.put(0x6A, "MOV L, D"); 
				onebyte.put(0x6B, "MOV L, E"); 
				onebyte.put(0x6C, "MOV L, H"); 
				onebyte.put(0x6D, "MOV L, L"); 
				onebyte.put(0x6E, "MOV L, M"); 
				onebyte.put(0x77, "MOV M, A"); 
				onebyte.put(0x70, "MOV M, B"); 
				onebyte.put(0x71, "MOV M, C"); 
				onebyte.put(0x72, "MOV M, D"); 
				onebyte.put(0x73, "MOV M, E"); 
				onebyte.put(0x74, "MOV M, H"); 
				onebyte.put(0x75, "MOV M, L"); 
				onebyte.put(0x00, "NOP" ) ;
				onebyte.put(0xB7, "ORA A" ) ; 
				onebyte.put(0xB0, "ORA B" ) ; 
				onebyte.put(0xB1, "ORA C" ) ; 
				onebyte.put(0xB2, "ORA D" ) ; 
				onebyte.put(0xB3, "ORA E" ) ; 
				onebyte.put(0xB4, "ORA H" ) ; 
				onebyte.put(0xB5, "ORA L" ) ; 
				onebyte.put(0xB6, "ORA M" ) ; 
				onebyte.put(0xE9, "PCHL"  ) ;
				onebyte.put(0xC1, "POP B"  );
				onebyte.put(0xD1, "POP D"  );
				onebyte.put(0xE1, "POP H"  );
				onebyte.put(0xF1, "POP PSW");
				onebyte.put(0xC5 , "PUSH B" ); 
				onebyte.put(0xD5 , "PUSH D" );  
				onebyte.put(0xE5 , "PUSH H" ); 
				onebyte.put(0xF5 , "PUSH PSW");
				onebyte.put(0x17, "RAL"); 
				onebyte.put(0x1F, "RAR"); 
				onebyte.put(0xD8, "RC" );
				onebyte.put(0xC9, "RET"); 
				onebyte.put(0x20, "RIM"); 
				onebyte.put(0x07, "RLC"); 
				onebyte.put(0xF8, "RM" );
				onebyte.put(0xD0, "RNC"); 
				onebyte.put(0xC0, "RNZ"); 
				onebyte.put(0xF0, "RP" );
				onebyte.put(0xE8, "RPE"); 
				onebyte.put(0xE0, "RPO"); 
				onebyte.put(0x0F, "RRC"); 
				onebyte.put(0xC7, "RST 0") ; 
				onebyte.put(0xCF, "RST 1") ; 
				onebyte.put(0xD7, "RST 2") ; 
				onebyte.put(0xDF, "RST 3") ; 
				onebyte.put(0xE7, "RST 4") ; 
				onebyte.put(0xEF, "RST 5") ; 
				onebyte.put(0xF7, "RST 6") ; 
				onebyte.put(0xFF, "RST 7") ; 
				onebyte.put(0xC8, "RZ" ) ;
				onebyte.put(0x9F, "SBB A") ; 
				onebyte.put(0x98, "SBB B") ; 
				onebyte.put(0x99, "SBB C") ; 
				onebyte.put(0x9A, "SBB D") ; 
				onebyte.put(0x9B, "SBB E") ; 
				onebyte.put(0x9C, "SBB H") ; 
				onebyte.put(0x9D, "SBB L") ; 
				onebyte.put(0x9E, "SBB M") ; 
				onebyte.put(0x30, "SIM");
				onebyte.put(0xF9, "SPHL"); 
				onebyte.put(0x02, "STAX B"); 
				onebyte.put(0x12, "STAX D"); 
				onebyte.put(0x37, "STC");
				onebyte.put(0x97, "SUB A") ; 
				onebyte.put(0x90, "SUB B") ; 
				onebyte.put(0x91, "SUB C") ; 
				onebyte.put(0x92, "SUB D") ; 
				onebyte.put(0x93, "SUB E"); 
				onebyte.put(0x94, "SUB H"); 
				onebyte.put(0x95, "SUB L"); 
				onebyte.put(0x96, "SUB M"); 
				onebyte.put(0xEB, "XCHG" );  
				onebyte.put(0xAF, "XRA A"); 
				onebyte.put(0xA8, "XRA B"); 
				onebyte.put(0xA9, "XRA C"); 
				onebyte.put(0xAA, "XRA D"); 
				onebyte.put(0xAB, "XRA E"); 
				onebyte.put(0xAC, "XRA H"); 
				onebyte.put(0xAD, "XRA L"); 
				onebyte.put(0xAE, "XRA M"); 
				onebyte.put(0xE3, "XTHL" );
	}

	public static HashMap<Integer, String> twobyte;
	static
	{
		twobyte = new HashMap<Integer, String>();

			twobyte.put(0xCE, "ACI");  
			twobyte.put(0xC6, "ADI");
			twobyte.put(0xE6, "ANI");
			twobyte.put(0xFE, "CPI"); 
			twobyte.put(0xDB, "IN") ; 
			twobyte.put(0x3E, "MVI A");
			twobyte.put(0x06, "MVI B");
			twobyte.put(0x0E, "MVI C");
			twobyte.put(0x16, "MVI D");
			twobyte.put(0x1E, "MVI E");
			twobyte.put(0x26, "MVI H");
			twobyte.put(0x2E, "MVI L");
			twobyte.put(0x36, "MVI M");
			twobyte.put(0xF6, "ORI"); 
			twobyte.put(0xD3, "OUT");
			twobyte.put(0xDE, "SBI");
			twobyte.put(0xD6, "SUI");
			twobyte.put(0xEE, "XRI");
	}

	public static HashMap<Integer, String> threebyte;
	static
	{
		threebyte = new HashMap<Integer, String>();

			threebyte.put(0xCD, "CALL" );  
			threebyte.put(0xDC, "CC"   );
			threebyte.put(0xFC, "CM"  ); 
			threebyte.put(0xD4, "CNC");  
			threebyte.put(0xC4, "CNZ"); 
			threebyte.put(0xF4, "CP"); 
			threebyte.put(0xEC, "CPE") ;  
			threebyte.put(0xE4, "CPO") ;  
			threebyte.put(0xCC, "CZ");  
			threebyte.put(0xDA, "JC");  
			threebyte.put(0xFA, "JM");  
			threebyte.put(0xC3, "JMP");  
			threebyte.put(0xD2, "JNC");  
			threebyte.put(0xC2, "JNZ");  
			threebyte.put(0xF2, "JP");  
			threebyte.put(0xEA, "JPE");  
			threebyte.put(0xE2, "JPO");  
			threebyte.put(0xCA, "JZ");  
			threebyte.put(0x3A, "LDA"); 
			threebyte.put(0x2A, "LHLD"); 
			threebyte.put(0x01, "LXI B"); 
			threebyte.put(0x11, "LXI D"); 
			threebyte.put(0x21, "LXI H"); 
			threebyte.put(0x31, "LXI SP");  
			threebyte.put(0x22, "SHLD"); 
			threebyte.put(0x32, "STA" ); 
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