package portablejim.veinminer.network.packet;

import cpw.mods.fml.common.network.Player;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import portablejim.veinminer.network.PacketTypeHandler;
import portablejim.veinminer.server.MinerServer;
import portablejim.veinminer.server.PlayerStatus;
import portablejim.veinminer.util.PreferredMode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: james
 * Date: 9/06/13
 * Time: 12:12 AM
 * To change this template use File | Settings | File Templates.
 */
public class PacketClientPresent extends PacketVeinMiner {
    int preferredMode;

    public PacketClientPresent() {
        super(PacketTypeHandler.CLIENT_PRESENT, false);
        this.preferredMode = PreferredMode.AUTO;
    }
    public PacketClientPresent(int preferredMode) {
        super(PacketTypeHandler.CLIENT_PRESENT, false);
        this.preferredMode = preferredMode;
    }

    @Override
    public void readDataStream(DataInputStream dataInputStream) throws IOException {
        preferredMode = dataInputStream.readInt();
    }

    public void writeDataStream(DataOutputStream dataOutputStream) throws IOException{
        dataOutputStream.writeInt(preferredMode);
    }

    public void execute(INetworkManager manager, Player player) {
        if(player instanceof EntityPlayer) {
            EntityPlayer thePlayer = (EntityPlayer) player;
            String playerName = thePlayer.getEntityName();

            MinerServer.instance.addClientPlayer(playerName);
            switch (preferredMode) {
                case PreferredMode.AUTO:
                    // Already set to auto
                    thePlayer.sendChatToPlayer("VeinMiner set to use keybind ('auto')");
                    break;
                case PreferredMode.SNEAK:
                    MinerServer.instance.setPlayerStatus(playerName, PlayerStatus.SNEAK_ACTIVE);
                    thePlayer.sendChatToPlayer("Veinminer set to activate on sneak.");
                    break;
                case PreferredMode.NO_SNEAK:
                    MinerServer.instance.setPlayerStatus(playerName, PlayerStatus.SNEAK_INACTIVE);
                    thePlayer.sendChatToPlayer("Veinminer set to deactivate on sneak.");
            }
        }
    }
}
