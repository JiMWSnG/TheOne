package util;

import core.SettingsError;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * @author Jim Wang
 * @create 2017-11-16 22:22
 **/
public class StationLocationUtil {

    private final static String WKT_TAG_BEGIN = "LINESTRING (";
    private final static String WKT_TAG_END = ")";
    private final static String WKT_TAG_BREAK = "\n";

    private final static int WKT_TYPE_MAP = 1;
    private final static int WKT_TYPE_ROUTE = 2;

    public static void main(String[] args) {
        txt2Wkt("basestation.txt",2);
        txt2Wkt("basestation.txt",1);

    }
    public static void txt2RouteFile(Scanner sc, FileWriter wktstream )throws IOException{
        while (sc.hasNext()){
            String  line = sc.nextLine();
            String[] location = line.split(" ");
            String x = location[1];
            String y = location[2];
            wktstream.append(WKT_TAG_BEGIN);
            wktstream.append(x+" "+y+", "+x+" "+y);
            wktstream.append(WKT_TAG_END + WKT_TAG_BREAK);
        }

    }
    public static void txt2MapFile(Scanner sc, FileWriter wktstream )throws IOException{
        wktstream.append(WKT_TAG_BEGIN);
        boolean first = true;
        while (sc.hasNext()){
            String  line = sc.nextLine();
            String[] location = line.split(" ");
            String x = location[1];
            String y = location[2];
            if (first){
                wktstream.append(x+" "+y);
                first = false;
            }else{

                wktstream.append(", "+x+" "+y);
            }
        }
        wktstream.append(WKT_TAG_END + WKT_TAG_BREAK);
    }
    public static  void txt2Wkt(String fileName, int type){
        File inFile = new File(fileName);
        Scanner sc ;
        try {
            sc = new Scanner(inFile);
        } catch (FileNotFoundException e) {
            throw new SettingsError("Couldn't find external movement input " +
                    "file " + inFile);
        }
        FileWriter wktstream ;
        try{
            File wkt = new File(fileName+type+".wkt");
            if (!wkt.exists()) {
                wkt.createNewFile();
            }
            wktstream = new FileWriter(wkt, false);
            if (type == WKT_TYPE_MAP){
                txt2MapFile(sc, wktstream);
            }else if(type == WKT_TYPE_ROUTE){
                txt2RouteFile(sc, wktstream);
            }
            wktstream.close();
        }catch(Exception ew){
            ew.printStackTrace();
            return ;
        }


    }
}
