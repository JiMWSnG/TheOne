package util;

import core.SettingsError;

import java.io.File;
import java.io.FileNotFoundException;
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
        File inFile = new File("F:/研究生/osm2wkt-master/koln.tr");
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
