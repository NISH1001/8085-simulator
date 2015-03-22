package pkg8085;

import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.StringReader;
import java.io.IOException;
import java.io.FileNotFoundException;

public class MultipleParser {

    ArrayList<CodeSegment> segments;
    ArrayList<Parser> parsers;
    MemoryModule memory;
    int def_addr;

    public MultipleParser(MemoryModule mem) {
        segments = new ArrayList<CodeSegment>();
        parsers = new ArrayList<Parser>();
        memory = mem;
    }

    public boolean initializeFile(String filename,int defAddr)
    throws IOException, FileNotFoundException, ParseException{
        //try {
        def_addr = defAddr;
        split(new BufferedReader(new FileReader(filename)));
        return parse();
        /*} catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }*/
    }

    public boolean initializeString(String data, int defAddr)
    throws IOException,ParseException {
        //try {
        def_addr = defAddr;
        split(new BufferedReader(new StringReader(data)));
        return parse();
        /*} catch (Exception e) {
            throw e;
        }*/
    }

    private boolean parse() throws ParseException {
        boolean success = true;
        for (CodeSegment seg : segments) {
            Parser p = new Parser();
            if (p.InitializeString(seg.data,seg.address)) {

                if (p.size()>0) {
                    p.WriteToMemory(memory,seg.address);
                    Integer sa = seg.address;
                    p.ShowData();
                }
            } else {
                success = false;
                break;
            }
        }
        return success;
    }

    private void split(BufferedReader breader)
        throws IOException {

        CodeSegment curSeg = new CodeSegment();
        int curAddr = def_addr;
        String data = "",line;

        while ((line = breader.readLine())!=null) {
            if (line.length()>0 && line.charAt(0)=='@') {
                curSeg.address = curAddr; curSeg.data = data;
                segments.add(curSeg);
                curSeg = new CodeSegment();
                curAddr = Integer.parseInt(line.substring(1));
                data = "";
            } else
                data += line+String.format("%n");
        }
        curSeg.address = curAddr;
        curSeg.data = data;
        segments.add(curSeg);
    }

    public class CodeSegment {
        public int address;
        public String data;

        public CodeSegment() {
            address = 0;
            data = "";
        }
    }
}
