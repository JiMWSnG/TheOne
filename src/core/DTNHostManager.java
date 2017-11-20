package core;

import constant.HostTypeContanst;
import movement.MovementModel;
import routing.MessageRouter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jim Wang
 * @create 2017-11-20 18:33
 **/
public class DTNHostManager {
    /** Global message event listeners */
    private List<MessageListener> messageListeners;
    /** Global movement event listeners */
    private List<MovementListener> movementListeners;
    private String gid;
    private List<NetworkInterface> interfaces;
    private ModuleCommunicationBus comBus;
    private MovementModel mmProto;
    private MessageRouter mRouterProto;
    private World world;

    public DTNHostManager(){
        
    }

    public DTNHost createHost(){
        DTNHost host = new DTNHost(this.messageListeners,
                this.movementListeners,	gid, interfaces, comBus,
                mmProto, mRouterProto);
        world.getHosts().add(host);
        return host;
    }
    public void removeHost(){
        removeStaticHost();
    }
    //去掉精致的节点，基站除外
    private void removeStaticHost(){
        List<DTNHost> removeHosts = new ArrayList<>();
        for(DTNHost host : this.world.getHosts()){
            if (host.toString().startsWith(HostTypeContanst.CAR) && host.isStatic()){
                removeHosts.add(host);
            }
        }
        this.world.getHosts().removeAll(removeHosts);
    }


}
