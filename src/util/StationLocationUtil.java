package util;

import core.Coord;
import core.SettingsError;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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

    private static double minX = Double .MAX_VALUE;
    private static double minY = Double.MAX_VALUE;
    private static double maxX = Double.MIN_VALUE;
    private static double maxY = Double.MIN_VALUE;
    private static double offsetX = 2550186.32d;
    private static double offsetY = 6674388.39d;
    private static Random rng = new Random();
    private static List<Coord>  data = new ArrayList<>();


    public static void main(String[] args) {
//        txt2Wkt("basestation.txt",2);
//        txt2Wkt("basestation.txt",1);
        init();
        getStationLocation();
//        averageStation("basestation", 1);
//        averageStation("basestation", 2);
//        System.out.println("minX:"+minX);
//        System.out.println("minY:"+minY);
//        System.out.println("maxX:"+maxX);
//        System.out.println("maxY:"+maxY);

    }
    public static void txt2RouteFile(Scanner sc, FileWriter wktstream )throws IOException{
        while (sc.hasNext()){
            String  line = sc.nextLine();
            String[] location = line.split(" ");
            double x = Double.valueOf(location[0]);
            double y = Double.valueOf(location[1]);
            if ( x < minX)
                minX = x;
            if (y < minY)
                minY = y;
            if ( x> maxX)
                maxX = x;
            if (y > maxY)
                maxY = y;
            wktstream.append(WKT_TAG_BEGIN);
            wktstream.append((x + offsetX)+" "+(offsetY - y)+", "+(x + offsetX)+" "+(offsetY - y));
            wktstream.append(WKT_TAG_END + WKT_TAG_BREAK);



        }

    }
    public static void txt2MapFile(Scanner sc, FileWriter wktstream )throws IOException{
        wktstream.append(WKT_TAG_BEGIN);
        boolean first = true;
        while (sc.hasNext()){
            String  line = sc.nextLine();
            String[] location = line.split(" ");
            double x = Double.valueOf(location[0]);
            double y = Double.valueOf(location[1]);
            if (first){
                wktstream.append((x + offsetX)+" "+(offsetY - y));
                first = false;
            }else{
                wktstream.append(", "+(x + offsetX)+" "+(offsetY - y));
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

    public static void init(){
        int i = 0;
        while (i< 100) {
//            double x = offsetX + rng.nextDouble() * 4500;
//            double y = offsetY - rng.nextDouble() * 3400;
            double x =rng.nextDouble() * 4000;
            double y = rng.nextDouble() * 3200;
            Coord c = new Coord(x, y);
            data.add(c);
            i++;
        }
    }
    public static void getStationLocation() {
        FileWriter wktstream;
        try {
            File wkt = new File("RandomStationRoute.txt");
            if (!wkt.exists()) {
                wkt.createNewFile();
            }
            wktstream = new FileWriter(wkt, false);
         for (int i = 0;i < data.size();i++){
             Coord c = data.get(i);
             double x = c.getX();
             double y = c.getY();
             wktstream.append(x+" "+y);
             wktstream.append(WKT_TAG_BREAK);
         }
            wktstream.close();
        }catch(Exception e){

        }

    }

    public static void averageStation(String fileName, int type){
        FileWriter wktstream ;
        try{
            File wkt = new File("Random"+fileName+type+".wkt");
            if (!wkt.exists()) {
                wkt.createNewFile();
            }
            wktstream = new FileWriter(wkt, false);
            if (type == WKT_TYPE_MAP)
                wktstream.append(WKT_TAG_BEGIN);
            int i = 0;
            boolean first = true;
            while (i< data.size()){
                Coord c = data.get(i);
                double x = c.getX();
                double y = c.getY();

                if (type == WKT_TYPE_MAP){
                    if (first){
                        wktstream.append(x+" "+y);
                        first = false;
                    }else{
                        wktstream.append(", "+x+" "+y);
                    }
                }else if(type == WKT_TYPE_ROUTE){
                    wktstream.append(WKT_TAG_BEGIN);
                    wktstream.append(x+" "+y+", "+x+" "+y);
                    wktstream.append(WKT_TAG_END + WKT_TAG_BREAK);
                }
                i++;
            }
            if (type == WKT_TYPE_MAP)
                wktstream.append(WKT_TAG_END + WKT_TAG_BREAK);
            wktstream.close();
        }catch(Exception ew){
            ew.printStackTrace();
            return ;
        }
    }


}
