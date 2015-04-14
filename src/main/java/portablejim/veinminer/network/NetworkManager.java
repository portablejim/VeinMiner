package portablejim.veinminer.network;

import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayerMP;
import portablejim.veinminer.lib.MinerLogger;
import portablejim.veinminer.lib.ModInfo;

/**
 * Wraps networking code to allow method of packet sending to change.
 */
public class NetworkManager {
    public SimpleNetworkWrapper networkWrapper;

    public void setupNetworking() {
        networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(ModInfo.CHANNEL);
        networkWrapper.registerMessage(PacketPingClient.Handler.class, PacketPingClient.class, 0, Side.CLIENT);
        networkWrapper.registerMessage(PacketClientPresent.Handler.class, PacketClientPresent.class, 1, Side.SERVER);
        networkWrapper.registerMessage(PacketMinerActivate.Handler.class, PacketMinerActivate.class, 2, Side.SERVER);
    }

    public void sendToServer(IMessage packet) {
        if(networkWrapper == null) {
            MinerLogger.debug("ERROR: NetworkWrapper is not setup.");
            return;
        }

        networkWrapper.sendToServer(packet);
    }

    public void sendToPlayer(IMessage packet, EntityPlayerMP player) {
        if(networkWrapper == null) {
            MinerLogger.debug("ERROR: NetworkWrapper is not setup.");
            return;
        }
        if(player == null) {
            FMLLog.warning("Trying to send packet to null player");
        }

        networkWrapper.sendTo(packet, player);
    }
}
