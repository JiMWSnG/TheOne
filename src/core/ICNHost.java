package core;

import movement.MovementModel;
import routing.MessageRouter;

import java.util.*;

/**
 * Created by Jim Wang on 2016/12/23.
 * not used
 */
public class ICNHost  extends DTNHost{
    /**
     * PIT表的改进，head：dataName，rquestNodeName，popularity，timestamp
     * rquestNodeName可以有多个，ArrayList<String>，作聚合作用*/
    private Set<Map<String,Object>> PIT;

    public ICNHost(List<MessageListener> msgLs,
                   List<MovementListener> movLs,
                   String groupId, List<NetworkInterface> interf,
                   ModuleCommunicationBus comBus,
                   MovementModel mmProto, MessageRouter mRouterProto){
        super(msgLs,movLs,groupId,interf,comBus,mmProto,mRouterProto);
        this.PIT = new HashSet<Map<String,Object>>();
    }
    @Deprecated
    public boolean addRecord( String dataName,String rquestNodeName,int popularity,String timestamp ){
//        Map<String,Object> record = new HashMap<String ,Object>();
//        record.put("dataName",dataName);
//        record.put("rquestNodeName",rquestNodeName);
//        record.put("popularity",popularity);
//        record.put("timestamp",timestamp);
//        for(Map i :PIT){
//            if( i.containsValue(dataName)){
//                Object   rquestNodeNameValue =   i.get("rquestNodeName");
//                if(rquestNodeNameValue instanceof List){
//                    List<String> nodeNames = (List<String>)rquestNodeNameValue;
//                    nodeNames.add(rquestNodeName);
//                }
//                i.put("timestamp",timestamp);
//                //TODO:jisuan popularity
//                return true;
//            }
//        }
//        return PIT.add(record);
        return true;
    }

    public boolean deleteRecord(){
        return false;
    }
    /**
     * 获取PIT中的一条记录
     * @param dataName 请求的数据的名字
     * @return
     */
    public Map<String ,Object> getRecord(String dataName){
        if(dataName ==null||"".equals(dataName) ){
            return null;
        }
        for(Map<String,Object> oneRecord : PIT){
            if(dataName.equals(oneRecord.get("dataname"))){
               return oneRecord;
            }
        }
        return null;
    }

    @Override
    public List<Connection> getConnections() {
        List<Connection> lc =super.getConnections();
        List<Connection> cons = new ArrayList<Connection>();
        for(Connection con :lc){
            if(con.isUp()){
                cons.add(con);
            }
        }
        return cons;
    }
}
