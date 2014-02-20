package portablejim.veinminer.network;

import portablejim.veinminer.network.packet.PacketClientPresent;
import portablejim.veinminer.network.packet.PacketMinerActivate;

/**
 * Created with IntelliJ IDEA.
 * User: james
 * Date: 20/02/14
 * Time: 11:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class PacketRegistrar {
    public static void registerPackets(PacketPipeline pipeline) {
        pipeline.registerPacket(PacketClientPresent.class);
        pipeline.registerPacket(PacketMinerActivate.class);
    }
}
