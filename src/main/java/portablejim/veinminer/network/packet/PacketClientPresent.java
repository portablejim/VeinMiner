package portablejim.veinminer.network.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
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
public class PacketClientPresent extends AbstractPacket {
    int preferredMode;

    public PacketClientPresent() {}

    public PacketClientPresent(int preferredMode) {
        this.preferredMode = preferredMode;
    }

    @Override
    public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
        buffer.writeInt(preferredMode);
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
        preferredMode = buffer.readInt();
    }

    @Override
    public void handleClientSide(EntityPlayer player) { }

    @Override
    public void handleServerSide(EntityPlayerMP player) {
        UUID playerName = player.getUniqueID();

        MinerServer.instance.addClientPlayer(playerName);
        switch (preferredMode) {
            case PreferredMode.AUTO:
                MinerServer.instance.setPlayerStatus(playerName, PlayerStatus.INACTIVE);
                player.addChatMessage(new ChatComponentTranslation("mod.veinminer.preferredmode.auto"));
                break;
            case PreferredMode.SNEAK:
                MinerServer.instance.setPlayerStatus(playerName, PlayerStatus.SNEAK_ACTIVE);
                player.addChatMessage(new ChatComponentTranslation("mod.veinminer.preferredmode.sneak"));
                break;
            case PreferredMode.NO_SNEAK:
                MinerServer.instance.setPlayerStatus(playerName, PlayerStatus.SNEAK_INACTIVE);
                player.addChatMessage(new ChatComponentTranslation("mod.veinminer.preferredmode.nosneak"));
        }
    }
}
