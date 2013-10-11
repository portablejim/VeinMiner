package portablejim.veinminer.network.packet;

import cpw.mods.fml.common.network.Player;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import portablejim.veinminer.network.PacketTypeHandler;
import portablejim.veinminer.server.MinerServer;
import portablejim.veinminer.server.PlayerStatus;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: james
 * Date: 9/06/13
 * Time: 1:21 AM
 * To change this template use File | Settings | File Templates.
 */
public class PacketClientSettings extends PacketVeinMiner {
    public boolean isKeyDown;

    public PacketClientSettings() {
        super(PacketTypeHandler.CLIENT_SETTINGS, false);
    }

    public PacketClientSettings(boolean keyDown) {
        super(PacketTypeHandler.CLIENT_SETTINGS, false);

        this.isKeyDown = keyDown;
    }

    @Override
    public void readDataStream(DataInputStream dataInputStream) throws IOException {
        isKeyDown = dataInputStream.readBoolean();
    }

    public void writeDataStream(DataOutputStream dataOutputStream) throws IOException{
        dataOutputStream.writeBoolean(isKeyDown);
    }

    public void execute(INetworkManager manager, Player player) {
        if(player instanceof EntityPlayer) {
            EntityPlayer thePlayer = (EntityPlayer) player;
            String playerName = thePlayer.getEntityName();

            PlayerStatus status = MinerServer.instance.getPlayerStatus(playerName);
            if(isKeyDown) {
                if(status == PlayerStatus.INACTIVE) {
                    MinerServer.instance.setPlayerStatus(playerName, PlayerStatus.ACTIVE);
                }
            }
            else {
                if(status == PlayerStatus.ACTIVE) {
                    MinerServer.instance.setPlayerStatus(playerName, PlayerStatus.INACTIVE);
                }
            }
        }
    }
}
