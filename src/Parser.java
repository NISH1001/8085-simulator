import java.io.*;
import java.util.*;
import java.lang.String;

public class Parser
{
    //where the parsed hexcodes gets stored as RAM
    private Opcode opcode;

    //to store the parsed hexcodes
    private ArrayList<Integer> data;

    //store m_label and their correspoinding address
    private HashMap<String,Integer> m_label;

    //store lines to be used in second pass
    private HashMap<Integer,String> m_parsedLines;

    //m_offset for m_labels
    private int m_offset;

    public Parser()
    {
        m_offset = (int)0;
        m_parsedLines = new HashMap<Integer,String>();
        m_label = new HashMap<String, Integer>();
        data = new ArrayList<Integer>();
        opcode = new Opcode();
    }

    public boolean Initialize(String filename, int start_addr)
    {
        boolean first = FirstPass(filename, start_addr);
        if(!first)
            return false;

        boolean second = SecondPass();
        if(!second)
            return false;
        return true;
    }


    public boolean FirstPass(String filename, int start_address)
    {
        int start_addr = (int)start_address;
        int linenumber = 1;
        try
        {
            BufferedReader reader = new BufferedReader( new FileReader(filename));

            String line;
            
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
                        line = line.substring(0, 1);
                        m_parsedLines.put(linenumber, line);
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
                    String label = "";

                    //check if there is a label
                    if(line.indexOf(":")>0)
                    {
                        String[] labelsplitted = line.split(":");
                        String l1 = labelsplitted[0].trim();
                        String l2 = labelsplitted[1].trim();

                        if(!l1.matches("[A-Z][0-9A-Z]*"))
                        {
                            throw new ParseException("Invalid label in line -> " + copy + " :: linenumber -> " + linenumber);
                        }
                        label = l1;
                        line = l2;
                    }
                    
                    // get the opcode -> first byte of our line is always an opcode
                    String op = (line.split(" "))[0];
                    int code = Integer.parseInt(op,16);
                    int off = m_offset;
                    
                    //to calculate offset increment
                    if(opcode.onebyte.get(code) != null)
                    {
                        m_offset++;
                    }
                    if(opcode.twobyte.get(code) != null)
                    {
                        m_offset += 2;
                    }
                    if(opcode.threebyte.get(code) != null)
                    {
                        m_offset += 3;
                    }

                    if(m_label.get(label) != null && label.length()>0)
                    {
                        throw new ParseException("possible label duplication :: " + copy + " :: line ->" + linenumber);
                    }

                    m_label.put(label, start_addr + off);

                    //System.out.println(start_addr + m_offset + line);
                }
                
                m_parsedLines.put(linenumber, line);
                //System.out.println(m_parsedLines.get(linenumber));
                ++linenumber;
            }

            reader.close();
        }
        catch ( IOException e)
        {
            return false;
        }
        catch (NumberFormatException e)
        {
            System.out.println("invalid line : " + linenumber);
            return false;
        }
        catch (ParseException err)
        {
            System.out.println(err.getMessage());
            return false;
        }

        return true;
    }

    public boolean SecondPass()
    {
        try
        {
            Iterator <Map.Entry<Integer, String>> iter = m_parsedLines.entrySet().iterator();
            while(iter.hasNext())
            {
                Map.Entry<Integer, String> lineiter = iter.next();
                int linenumber = lineiter.getKey();
                String  line = lineiter.getValue();

                if(line.length() == 0)
                {
                    continue;
                }

                line = SubstituteLabel(line);

                //now split the line by spaces
                String[] splitted = line.split("\\s");
                int len = splitted.length;

                //to store codes as integers -> int = 16bit
                int[] codes = new int[len];

                //im assuming we dont have a single instruction whose
                //length is greater than 3 bytes
                if(len > 3)
                {
                    throw new ParseException("Invalid insturcion line -> " + line + " :: linenumber -> " + linenumber);
                }


                //now check for genuine characters after splitting
                for(int i=0; i<len; ++i)
                {
                    splitted[i] = splitted[i].toUpperCase();
                    if(!splitted[i].matches("([0-9A-F][0-9A-F])|([0-9A-F])"))
                    {
                        String error = "Invalid code -> " + splitted[i] + " :: in line -> " + line + " :: linenumber -> " + linenumber ;
                        throw new ParseException(error);
                    }

                    //convert to integer value if success
                    codes[i] = Integer.parseInt(splitted[i],16);
                }

                //if single byte instruction check if genuine opcode
                if(len == 1)
                {
                    if((opcode.onebyte.get((int)codes[0])) == null)
                        throw new ParseException("Invalid insturcion line -> " + line  + " :: linenumber -> " + linenumber);


                }

                //if two byte instruction check if genuine opcode
                if(len == 2)
                {
                    if(opcode.twobyte.get((int)codes[0]) == null)
                        throw new ParseException("Invalid insturcion line -> " + line + " :: linenumber -> " + linenumber);

                }

                //if three byte instructon check if genuine opcode
                if(len == 3)
                {
                    if(opcode.threebyte.get((int)codes[0]) == null)
                        throw new ParseException("Invalid insturcion line -> " + line + " :: linenumber -> " + linenumber);

                }

                //if everthing is success store into RAM
                for(int i=0; i<len; ++i)
                {
                    data.add(codes[i]);
                }

            }

        }
        catch (ParseException err)
        {
            System.out.println(err.getMessage());
            return false;
        }
        return true;
    }

    //to substitute label by address
    public String SubstituteLabel(String line)
    {
        Iterator <Map.Entry<String, Integer>> iter = m_label.entrySet().iterator();

        boolean matched = false;
        String converted = line;

        while(iter.hasNext())
        {
            Map.Entry<String, Integer> labeliter = iter.next();
            String label = labeliter.getKey();
            if(label.length() == 0)
                continue;

            Integer label_addr = labeliter.getValue();

            matched = line.contains(label);
            
            //if line contains a label
            if(matched)
            {
                //convert integer address to hex string
                String hexcode = Integer.toHexString(label_addr);
                int len = hexcode.length();
                String temp = "";

                //add leading zeros to maximum length -> 4 for now
                for(int i=0; i<(4-len); ++i)
                {
                    temp += "0";
                }

                hexcode = temp + hexcode;
                hexcode = hexcode.charAt(0) + "" + hexcode.charAt(1) + " " + hexcode.charAt(2) + ""  + hexcode.charAt(3);
                converted = line.replaceAll(label, hexcode);
            }
        }
        return converted;
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
        for (Integer d : data)
        {
            System.out.println(Integer.toHexString(d));
        }
    }

    //to write to memory
    public void WriteToMemory(MemoryModule mem, int start_addr)
    {
       int i = 0 ; 
       for(Integer d : data)
       {
           //mem.writeByte(start_addr+i, d);
           i++;
       }
    }
}
