package util;

import core.SettingsError;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * @author Jim Wang
 * @create 2017-11-16 22:01
 **/
public class MovementScanner {
    public static Map<String, Boolean > ids = new HashMap<>();
    public static int num = 0;
    public static void main(String[] args) {
        locationStatic("koln.tr6.txt");
    }

    public static void getMovementByTime(int num, String fileName){
        long unit = 86400/num;
        //TODO：把文件按时间分成若干份，以此来减小节点数量，降低仿真压力
        //不行的话修改one节点管理机制

        File inFile = new File(fileName);
        Scanner sc ;
        try {
            sc = new Scanner(inFile);
        } catch (FileNotFoundException e) {
            throw new SettingsError("Couldn't find external movement input " +
                    "file " + inFile);
        }
        FileWriter[] wktstreams = new FileWriter[num] ;
        for (int j = 0;j<num;j++){
            try{
                File wkt = new File(fileName+j+".txt");
                if (!wkt.exists()) {
                    wkt.createNewFile();
                }
                 wktstreams[j] = new FileWriter(wkt, false);

            }catch(Exception ew){
                ew.printStackTrace();
                return ;
            }
        }
        try{
            while(sc.hasNext()){
                String line = sc.nextLine();
                String[] strs = line.split(" ");
                long time = Long.valueOf(strs[0]);
                for (int i = 0;i<num;i++){
                    if (time >=i*unit && time < (i+1)*unit){
                        wktstreams[i].append(line);
                        wktstreams[i].append("\n");
                    }
                }
            }
            for (int i = 0;i<num;i++){

                wktstreams[i].close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void locationStatic(String fileName){
        File inFile = new File(fileName);
        Scanner sc ;
        try {
            sc = new Scanner(inFile);
        } catch (FileNotFoundException e) {
            throw new SettingsError("Couldn't find external movement input " +
                    "file " + inFile);
        }

        double minX= Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double minY= Double.MAX_VALUE;
        double maxY = Double.MIN_VALUE;

        while (sc.hasNext()){
            String line = sc.nextLine();
            String[] strs = line.split(" ");
            String name = strs[1];
            double x = Double.valueOf(strs[2]);
            double y = Double.valueOf(strs[3]);
            if (!ids.containsKey(name)){
                num++;
                ids.put(name,true);
            }
            minX = minX <= x?minX:x;
            maxX = maxX <= x?x:maxX;
            minY = minY <= y?minY:y;
            maxY = maxY <= y ? y:maxY;
        }
        System.out.println("minX ="+minX+" maxX ="+maxX+" minY = "+ minY+ " maxY = "+maxY);
        System.out.println("num = "+num);
    }
}
