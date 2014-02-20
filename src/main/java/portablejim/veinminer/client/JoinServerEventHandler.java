package portablejim.veinminer.client;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import portablejim.veinminer.VeinMiner;
import portablejim.veinminer.network.packet.PacketClientPresent;

/**
 * Created with IntelliJ IDEA.
 * User: james
 * Date: 20/02/14
 * Time: 4:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class JoinServerEventHandler {
    public JoinServerEventHandler() {
        FMLCommonHandler.instance().bus().register(this);
    }

    @SubscribeEvent
    public void joinServer(ClientConnectedToServerEvent event) {
        PacketClientPresent packet = new PacketClientPresent();
        VeinMiner.instance.channelManager.sendToServer(packet);
    }
}
