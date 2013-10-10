package portablejim.veinminer.network.packet;

import cpw.mods.fml.common.network.Player;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import portablejim.veinminer.network.PacketTypeHandler;
import portablejim.veinminer.server.MinerServer;

/**
 * Created with IntelliJ IDEA.
 * User: james
 * Date: 9/06/13
 * Time: 12:12 AM
 * To change this template use File | Settings | File Templates.
 */
public class PacketClientPresent extends PacketVeinMiner {
    public PacketClientPresent() {
        super(PacketTypeHandler.CLIENT_PRESENT, false);
    }

    public void execute(INetworkManager manager, Player player) {
        if(player instanceof EntityPlayer) {
            EntityPlayer thePlayer = (EntityPlayer) player;
            String playerName = thePlayer.getEntityName();

            MinerServer.instance.addClientPlayer(playerName);
            thePlayer.sendChatToPlayer("VeinMiner set to use keybind ('auto')");
        }
    }
}
