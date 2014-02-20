package portablejim.veinminer.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentTranslation;
import portablejim.veinminer.server.MinerServer;
import portablejim.veinminer.util.PlayerStatus;
import portablejim.veinminer.util.PreferredMode;

import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: james
 * Date: 19/02/14
 * Time: 4:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class PacketClientPresent implements IPacket {
    private int preferredMode;

    @SuppressWarnings("UnusedDeclaration")
    public PacketClientPresent(){}

    public PacketClientPresent(int preferredMode) {
        this.preferredMode = preferredMode;
    }

    @Override
    public void readBytes(ByteBuf bytes) {
        preferredMode = bytes.readInt();
    }

    @Override
    public void writeBytes(ByteBuf bytes) {
        bytes.writeInt(preferredMode);
    }

    @Override
    public void executeClient() { }

    @Override
    public void executeServer(EntityPlayerMP thePlayer) {
        UUID playerName = thePlayer.getUniqueID();

        MinerServer.instance.addClientPlayer(playerName);
        switch (preferredMode) {
            case PreferredMode.AUTO:
                MinerServer.instance.setPlayerStatus(playerName, PlayerStatus.INACTIVE);
                thePlayer.addChatMessage(new ChatComponentTranslation("mod.veinminer.preferredmode.auto"));
                break;
            case PreferredMode.SNEAK:
                MinerServer.instance.setPlayerStatus(playerName, PlayerStatus.SNEAK_ACTIVE);
                thePlayer.addChatMessage(new ChatComponentTranslation("mod.veinminer.preferredmode.sneak"));
                break;
            case PreferredMode.NO_SNEAK:
                MinerServer.instance.setPlayerStatus(playerName, PlayerStatus.SNEAK_INACTIVE);
                thePlayer.addChatMessage(new ChatComponentTranslation("mod.veinminer.preferredmode.nosneak"));
        }
    }
}
