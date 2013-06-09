package portablejim.veinminer.network;

import portablejim.veinminer.network.packet.PacketClientPresent;
import portablejim.veinminer.network.packet.PacketClientSettings;
import portablejim.veinminer.network.packet.PacketServerDetected;
import portablejim.veinminer.network.packet.PacketVeinMiner;

/**
 * Created with IntelliJ IDEA.
 * User: james
 * Date: 8/06/13
 * Time: 5:40 PM
 * To change this template use File | Settings | File Templates.
 */
public enum PacketTypeHandler {
    CLIENT_PRESENT(PacketClientPresent.class),
    CLIENT_SETTINGS(PacketClientSettings.class),
    SERVER_DETECTED(PacketServerDetected.class);

    private Class<? extends PacketVeinMiner> subClassType;

     PacketTypeHandler(Class <? extends PacketVeinMiner> subClassType) {
         this.subClassType = subClassType;
    }
}
