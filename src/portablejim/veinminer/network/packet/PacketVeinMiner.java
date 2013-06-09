package portablejim.veinminer.network.packet;

import portablejim.veinminer.network.PacketTypeHandler;

/**
 * Created with IntelliJ IDEA.
 * User: james
 * Date: 8/06/13
 * Time: 5:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class PacketVeinMiner {
    public PacketTypeHandler packetType;

    public PacketVeinMiner(PacketTypeHandler packetType) {
        this.packetType = packetType;
    }
}
