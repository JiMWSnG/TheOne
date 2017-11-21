package report;

import core.DTNHost;
import core.Message;
import core.MessageListener;
import core.Settings;

/**
 * @author Jim Wang
 * @create 2017-11-19 1:16
 **/
public class TrafficReport extends Report implements MessageListener {
    public static String TITLE = "# host  ";
    public static String HEADER = "# time  upstreamTraffic  downstreamTraffic ";

    public static final String DTNHOST_NAME = "host";
    public static final String INTERVAL = "samplingInterval";
    public static int num = 0;
    private double time;
    private double upstreamTraffic;
    private double downstreamTraffic;
    private double lastUp;
    private double lastDown;
    private double lastTime;
    private String hostName;
    private int interval;


    public TrafficReport(){
        super();
        Settings s = getSettings();
        if (s.contains(DTNHOST_NAME)) {
            this.hostName = s.getCsvSetting(DTNHOST_NAME)[num++];
            String outFileName = getOutFileName();
            outFileName = outFileName.substring(0, outFileName.length()-4);
            setOutFileName(outFileName+this.hostName+OUT_SUFFIX);
        }
        this.interval = s.getInt(INTERVAL);
        init();
    }

    @Override
    protected void init() {
        super.init();
        this.upstreamTraffic = this.lastUp = 0;
        this.downstreamTraffic = this.lastDown = 0;
        this.time = this.lastTime = getSimTime();
        write(TITLE);
        write(hostName);
        write(HEADER);
    }

    @Override
    public void messageTransferStarted(Message m, DTNHost from, DTNHost to) {
        //下行
        if (this.hostName.equals(from.toString())) {
            double size = m.getSize();
            downstreamTraffic += size;
        }
        //上行
        if (this.hostName.equals(to.toString())) {
            double size = m.getSize();
            upstreamTraffic += size;
        }
        reportValues();

    }

    @Override
    public void messageTransferred(Message m, DTNHost from, DTNHost to, boolean firstDelivery) {
    }

    @Override
    public void messageDeleted(Message m, DTNHost where, boolean dropped) {
    }

    @Override
    public void messageTransferAborted(Message m, DTNHost from, DTNHost to) {
    }

    @Override
    public void newMessage(Message m) {
    }

    private void reportValues() {
        double now = getSimTime();
        double det = now - lastTime;
        if (det > interval) {
            write(now + "  " + (upstreamTraffic - lastUp) + "  " + (downstreamTraffic - lastDown));
            this.lastTime = now;
            this.lastDown = downstreamTraffic;
            this.lastUp = upstreamTraffic;
        }

    }

    @Override
    public void done() {
        write("done----------------------");
        reportValues();
        super.done();
    }


}
