package portablejim.veinminer.network;

import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;

/**
 * Vein Miner
 *
 * Handles packets.
 *
 * Thanks to Equivalent-Exchange-3 by Pahimar.
 *
 * @author Portablejim, Pahimar
 */
public class PacketHandler implements IPacketHandler{
    @Override
    public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {

    }
}
