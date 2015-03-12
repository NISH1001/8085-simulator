import java.io.*;
import java.util.*;
import java.lang.String;

public class Parser
{
    //where the parsed hexcodes gets stored as RAM
    private Memory memory;

    public Parser()
    {
        memory = new Memory();
    }

    public boolean Initialize(String filename)
    {
        //System.out.println(memory.onebyte.get(0));
        try
        {
            BufferedReader reader =
                new BufferedReader( new FileReader(filename));

            String line;

            int index = 0;
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
                        continue;

                    //else if line has got some comments at its end
                    int commentindex = line.indexOf(';');

                    if(commentindex > 0)
                    {
                        line = line.substring(0, commentindex);
                        line = line.trim();
                    }
                    
                    line = line.toUpperCase();
                    line  = ConvertToHex(line);

                    //now split the line by spaces
                    String[] splitted = line.split("\\s");
                    int len = splitted.length;

                    //to store codes as integers -> short = 16bit
                    short[] codes = new short[len];

                    //im assuming we dont have a single instruction whose
                    //length is greater than 3 bytes
                    try
                    {
                        if(len > 3)
                        {
                            throw new ParseException("Invalid insturcion line -> " + line);
                        }
                    }

                    catch(ParseException err)
                    {
                        System.out.println(err.getMessage());
                        reader.close();
                        return false;
                    }

                    //now check for genuine characters after splitting
                    for(int i=0; i<len; ++i)
                    {
                        splitted[i] = splitted[i].toUpperCase();
                        try
                        {
                            if(!splitted[i].matches("([0-9A-F][0-9A-F])|([0-9A-F])"))
                            {
                                String error = "Invalid code -> " + splitted[i] + " :: in line -> " + line;
                                throw new ParseException(error);
                            }
                        }

                        catch(ParseException err)
                        {
                            System.out.println(err.getMessage());
                            reader.close();
                            return false;
                        }

                        //convert to integer value if success
                        codes[i] = Short.parseShort(splitted[i],16);
                    }

                    //if single byte instruction check if genuine opcode
                    if(len == 1)
                    {
                        try
                        {
                            if((memory.onebyte.get((int)codes[0])) == null)
                                throw new ParseException("Invalid onebyte instruction -> " + line);
                        }

                        catch(ParseException err)
                        {
                            System.out.println(err.getMessage());
                            reader.close();
                            return false;
                        }

                    }

                    //if two byte instruction check if genuine opcode
                    if(len == 2)
                    {
                        try
                        {
                            if(memory.twobyte.get((int)codes[0]) == null)
                                throw new ParseException("Invalid twobyte instruction -> " + line);
                        }

                        catch(ParseException err)
                        {
                            System.out.println(err.getMessage());
                            reader.close();
                            return false;
                        }
                    }

                    //if three byte instructon check if genuine opcode
                    if(len == 3)
                    {
                        try
                        {
                            if(memory.threebyte.get((int)codes[0]) == null)
                                throw new ParseException("Invalid threebyte instruction -> " + line);
                        }

                        catch(ParseException err)
                        {
                            System.out.println(err.getMessage());
                            reader.close();
                            return false;
                        }
                    }

                    //if everthing is success store into RAM
                    for(int i=0; i<len; ++i)
                    {
                        memory.RAM[index++] = codes[i];
                    }

                }
            }

            reader.close();

        }
        catch ( IOException e)
        {
            return false;
        }

        return true;
    }

    public String ConvertToHex(String line)
    {
        Iterator <Map.Entry<Integer, String>> iter = memory.onebyte.entrySet().iterator();

        String converted = line;
        boolean matched = false;

        while(iter.hasNext())
        {
            Map.Entry<Integer, String> opcode = iter.next();
            String hexcode = Integer.toHexString(opcode.getKey());
            String engcode = opcode.getValue();

            matched = line.toUpperCase().contains(engcode.toUpperCase());

            if(matched)
            {
                converted = line.replaceAll(engcode, hexcode);
                break;
            }
        }

        if(!matched)
        {
            iter = memory.twobyte.entrySet().iterator();

            while(iter.hasNext())
            {
                Map.Entry<Integer, String> opcode = iter.next();
                String hexcode = Integer.toHexString(opcode.getKey());
                String engcode = opcode.getValue();

                line = line.replaceAll("\\s*,\\s*", " ");
                matched = line.toUpperCase().contains(engcode.toUpperCase());

                if(matched)
                {
                    converted = line.replaceAll(engcode, hexcode);
                    break;
                }
            }

        }

        if(!matched)
        {
            iter = memory.threebyte.entrySet().iterator();

            while(iter.hasNext())
            {
                Map.Entry<Integer, String> opcode = iter.next();
                String hexcode = Integer.toHexString(opcode.getKey());
                String engcode = opcode.getValue();

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

    public Memory GetMemory()
    {
        return this.memory;
    }
}
