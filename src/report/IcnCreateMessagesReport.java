package report;

import core.DTNHost;
import core.Message;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jim Wang on 2017/3/16.
 */
public class IcnCreateMessagesReport extends CreatedMessagesReport {
    public static String HEADER = "# time  ID  size  fromHost  toHost  TTL  " +
            "isResponse  " +"requestMsgName";
    private static Map<String,Integer> id_f = new HashMap<>();

    /**
     * Constructor.
     */
    public IcnCreateMessagesReport() {
        super();

    }

    public void newMessage(Message m) {
        if (isWarmup()) {
            return;
        }

        int ttl = m.getTtl();
        String id = m.getId();
        if(id.startsWith("I")){

            String responseMsgName =(String)m.getProperty("responseMsgName");
            Integer f = id_f.get(responseMsgName);
            if(f!=null){
                id_f.put(responseMsgName,f+1);
            }else{
                id_f.put(responseMsgName,1);
            }
            write(format(getSimTime()) + " " + id + " " +
                    m.getSize() + " " + m.getFrom() + " " + m.getTo() + " " +
                    (ttl != Integer.MAX_VALUE ? ttl : "n/a") +
                    (m.isResponse() ? " Y " : " N ")+responseMsgName);
        }
    }



    @Override
    public void done() {


        Object [] f= id_f.values().toArray();
        Arrays.sort(f);
        StringBuilder fStr = new StringBuilder();
        for(int i =f.length-1;i>=0;i--){
           fStr.append(" ").append(f[i]).append(" ");
        }
        write(fStr.toString());
        super.done();

    }
}
