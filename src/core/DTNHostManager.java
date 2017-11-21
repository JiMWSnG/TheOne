package core;

import movement.MovementModel;
import routing.MessageRouter;

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
    private MovementModel mmProto;
    private MessageRouter mRouterProto;
    private World world;

    public DTNHostManager( List<MessageListener> messageListeners,
                           List<MovementListener> movementListeners,	String gid, List<NetworkInterface> interfaces,
                           MovementModel  mmProto, MessageRouter mRouterProto,World world){
        this.messageListeners = messageListeners;
        this.movementListeners = movementListeners;
        this.gid = gid;
        this.interfaces = interfaces;
        this.mmProto = mmProto;
        this.mRouterProto = mRouterProto;
        this.world = world;

    }

    public DTNHost createHost(){
        ModuleCommunicationBus comBus = new ModuleCommunicationBus();
        DTNHost host = new DTNHost(this.messageListeners,
                this.movementListeners,	gid, interfaces, comBus,
                mmProto, mRouterProto);
        world.getHosts().add(host);
        return host;
    }
    public void removeHost(){

    }

    public List<MessageListener> getMessageListeners() {
        return messageListeners;
    }

    public List<MovementListener> getMovementListeners() {
        return movementListeners;
    }

    public String getGid() {
        return gid;
    }

    public List<NetworkInterface> getInterfaces() {
        return interfaces;
    }

    public MovementModel getMmProto() {
        return mmProto;
    }

    public MessageRouter getmRouterProto() {
        return mRouterProto;
    }

    public World getWorld() {
        return world;
    }
}
