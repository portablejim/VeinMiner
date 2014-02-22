package portablejim.veinminer.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayerMP;
import portablejim.veinminer.server.MinerServer;
import portablejim.veinminer.util.PlayerStatus;

import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: james
 * Date: 19/02/14
 * Time: 5:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class PacketMinerActivate implements IPacket {
    public boolean keyActive;

    @SuppressWarnings("UnusedDeclaration")
    public PacketMinerActivate() {}

    public PacketMinerActivate(boolean keyActive) {
        this.keyActive = keyActive;
    }

    @Override
    public void writeBytes(ByteBuf buffer) {
        buffer.writeBoolean(keyActive);
    }

    @Override
    public void readBytes(ByteBuf buffer) {
        keyActive = buffer.readBoolean();
    }

    @Override
    public void handleClientSide(EntityClientPlayerMP player) { }

    @Override
    public void handleServerSide(EntityPlayerMP player) {
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
