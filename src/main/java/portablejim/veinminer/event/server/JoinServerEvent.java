package portablejim.veinminer.event.server;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import portablejim.veinminer.VeinMiner;
import portablejim.veinminer.lib.MinerLogger;
import portablejim.veinminer.network.packet.PacketPingClient;

/**
 * Created with IntelliJ IDEA.
 * User: james
 * Date: 20/02/14
 * Time: 4:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class JoinServerEvent {

    public JoinServerEvent() {
        FMLCommonHandler.instance().bus().register(this);
    }

    @SuppressWarnings("UnusedDeclaration")
    @SubscribeEvent
    public void connected(PlayerLoggedInEvent event) {
        //new JoinServerTicker();
        PacketPingClient packet = new PacketPingClient();
        VeinMiner.instance.channelHandler.sendToPlayer(event.player, packet);
        MinerLogger.debug("Sent ping packet to client");
    }
}
