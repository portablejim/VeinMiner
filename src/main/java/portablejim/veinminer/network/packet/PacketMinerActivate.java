package portablejim.veinminer.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.*;
import portablejim.veinminer.server.MinerServer;
import portablejim.veinminer.util.*;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: james
 * Date: 19/02/14
 * Time: 5:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class PacketMinerActivate implements IPacket {
    private boolean keyActive;

    @SuppressWarnings("UnusedDeclaration")
    public PacketMinerActivate(){}
    public PacketMinerActivate(boolean active) {
        this.keyActive = active;
    }

    @Override
    public void readBytes(ByteBuf bytes) {
        keyActive = bytes.readBoolean();
    }

    @Override
    public void writeBytes(ByteBuf bytes) {
        bytes.writeBoolean(keyActive);
    }

    @Override
    public void executeClient() { }

    @Override
    public void executeServer(EntityPlayerMP player) {
        UUID playerName = player.getUniqueID();

        PlayerStatus status = MinerServer.instance.getPlayerStatus(playerName);
        if(keyActive) {
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
