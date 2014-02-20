package portablejim.veinminer.client;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
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
public class JoinServerEventHandler {
    public JoinServerEventHandler() {
        FMLCommonHandler.instance().bus().register(this);
    }

    @SubscribeEvent
    public void joinServer(ClientConnectedToServerEvent event) {
        final PacketClientPresent packet = new PacketClientPresent(VeinMiner.instance.configurationSettings.getPreferredMode());
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                //To change body of implemented methods use File | Settings | File Templates.
                VeinMiner.PACKET_PIPELINE.sendToServer(packet);
            }
        }, 1000);
        VeinMiner.PACKET_PIPELINE.sendToServer(packet);
    }

    @SubscribeEvent
    public void custom(FMLNetworkEvent e) {
        VeinMiner.instance.logger.info(String.format("TESTING NETWORK EVENT: %s", e.toString()));
    }
}
