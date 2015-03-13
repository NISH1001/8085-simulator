import java.io.*;
import java.util.*;
import java.lang.String;

public class Parser
{
    //where the parsed hexcodes gets stored as RAM
    private Opcode opcode;

    //to store the parsed hexcodes
    private ArrayList<Short> data;

    public Parser()
    {
        data = new ArrayList<Short>();
        opcode = new Opcode();
    }

    public boolean Initialize(String filename)
    {
        try
        {
            BufferedReader reader = new BufferedReader( new FileReader(filename));

            String line;
            
            int linenumber = 1;
            while((line = reader.readLine()) != null)
            {
                //trim leading and trailing spaces
                line = line.trim();

                if(line.length()>0)
                {
                    //reduce multiple spaces to single
                    line = line.replaceAll("\\s+", " ");

                    //if whole line is a comment
                    if(line.charAt(0) == ';')
                    {
                        linenumber++;
                        continue;
                    }

                    //else if line has got some comments at its end
                    int commentindex = line.indexOf(';');

                    if(commentindex > 0)
                    {
                        line = line.substring(0, commentindex);
                        line = line.trim();
                    }

                    String copy = line;                     
                    line = line.toUpperCase();
                    
                    //convert to hex opcodes
                    line  = ConvertToHex(line);

                    //now split the line by spaces
                    String[] splitted = line.split("\\s");
                    int len = splitted.length;

                    //to store codes as integers -> short = 16bit
                    short[] codes = new short[len];

                    //im assuming we dont have a single instruction whose
                    //length is greater than 3 bytes
                    if(len > 3)
                    {
                        throw new ParseException("Invalid insturcion line -> " + copy + " :: linenumber -> " + linenumber);
                    }


                    //now check for genuine characters after splitting
                    for(int i=0; i<len; ++i)
                    {
                        splitted[i] = splitted[i].toUpperCase();
                        if(!splitted[i].matches("([0-9A-F][0-9A-F])|([0-9A-F])"))
                        {
                            String error = "Invalid code -> " + splitted[i] + " :: in line -> " + copy + " :: linenumber -> " + linenumber ;
                            throw new ParseException(error);
                        }

                        //convert to integer value if success
                        codes[i] = Short.parseShort(splitted[i],16);
                    }

                    //if single byte instruction check if genuine opcode
                    if(len == 1)
                    {
                        if((opcode.onebyte.get((int)codes[0])) == null)
                            throw new ParseException("Invalid insturcion line -> " + copy + " :: linenumber -> " + linenumber);


                    }

                    //if two byte instruction check if genuine opcode
                    if(len == 2)
                    {
                        if(opcode.twobyte.get((int)codes[0]) == null)
                            throw new ParseException("Invalid insturcion line -> " + copy + " :: linenumber -> " + linenumber);

                    }

                    //if three byte instructon check if genuine opcode
                    if(len == 3)
                    {
                        if(opcode.threebyte.get((int)codes[0]) == null)
                            throw new ParseException("Invalid insturcion line -> " + copy + " :: linenumber -> " + linenumber);

                    }

                    //if everthing is success store into RAM
                    for(int i=0; i<len; ++i)
                    {
                        data.add(codes[i]);
                    }

                }
                ++linenumber;
            }

            reader.close();

        }
        catch ( IOException e)
        {
            return false;
        }

        catch (ParseException err)
        {
            System.out.println(err.getMessage());
            return false;
        }

        return true;
    }

    // convert the line to our primitive hex code
    public String ConvertToHex(String line)
    {
        //first iterate over onebyte instruction
        Iterator <Map.Entry<Integer, String>> iter = opcode.onebyte.entrySet().iterator();

        //for converted line into hexcode
        String converted = line;
        boolean matched = false;

        //loop through onebyte insturction
        while(iter.hasNext())
        {
            Map.Entry<Integer, String> opcode = iter.next();
            String hexcode = Integer.toHexString(opcode.getKey());
            String engcode = opcode.getValue();

            //search for substring like MOV A,B
            matched = line.toUpperCase().contains(engcode.toUpperCase());

            //if line contains instruction then replace with hex value
            if(matched)
            {
                converted = line.replaceAll(engcode, hexcode);
                break;
            }
        }

        //if one byte instruciton failed -> do same for two byte instruction
        if(!matched)
        {
            iter = opcode.twobyte.entrySet().iterator();

            while(iter.hasNext())
            {
                Map.Entry<Integer, String> opcode = iter.next();
                String hexcode = Integer.toHexString(opcode.getKey());
                String engcode = opcode.getValue();

                //replae *,* with just a space
                line = line.replaceAll("\\s*,\\s*", " ");
                matched = line.toUpperCase().contains(engcode.toUpperCase());

                if(matched)
                {
                    converted = line.replaceAll(engcode, hexcode);
                    break;
                }
            }

        }

        //if two  byte instruciton failed -> do same for three  byte instruction
        if(!matched)
        {
            iter = opcode.threebyte.entrySet().iterator();

            while(iter.hasNext())
            {
                Map.Entry<Integer, String> opcode = iter.next();
                String hexcode = Integer.toHexString(opcode.getKey());
                String engcode = opcode.getValue();

                //replae *,* with just a space
                line = line.replaceAll("\\s*,\\s*", " ");
                matched = line.toUpperCase().contains(engcode.toUpperCase());

                if(matched)
                {
                    converted = line.replaceAll(engcode, hexcode);
                    break;
                }
            }

        }

        return converted;
    }

    //to show parsed data
    public void ShowData()
    {
        for (Short d : data)
        {
            System.out.println(Integer.toHexString(d));
        }
    }

    //to write to memory
    public void WriteToMemory(MemoryModule mem, int start_addr)
    {
       int i = 0 ; 
       for(Short d : data)
       {
           mem.writeByte(start_addr+i, d);
           i++;
       }
    }
}
