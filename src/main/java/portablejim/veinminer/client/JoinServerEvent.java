package portablejim.veinminer.client;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import portablejim.veinminer.VeinMiner;
import portablejim.veinminer.network.packet.PacketClientPresent;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created with IntelliJ IDEA.
 * User: james
 * Date: 20/02/14
 * Time: 4:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class JoinServerEvent {
    boolean loggedIn = false;

    public JoinServerEvent() {
        FMLCommonHandler.instance().bus().register(this);
        loggedIn = false;
    }

    @SuppressWarnings("UnusedDeclaration")
    @SubscribeEvent
    public void connected(ClientConnectedToServerEvent event) {
        /*
         * Logged in, but can't send packets yet, so just flag that we need to
         * send the packet. (if PlayerLoggedInEvent is called multiple times
         * while connected to the server, which I am not sure if it happens or
         * not)
         */
        loggedIn = false;
    }

    @SuppressWarnings("UnusedDeclaration")
    @SubscribeEvent
    public void joinServer(PlayerLoggedInEvent event) {
        if(!loggedIn) {
            PacketClientPresent packet = new PacketClientPresent(VeinMiner.instance.configurationSettings.getPreferredMode());
            VeinMiner.PACKET_PIPELINE.sendToServer(packet);
            loggedIn = true;
        }
    }
}
