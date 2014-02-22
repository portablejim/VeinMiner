package portablejim.veinminer.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayerMP;
import portablejim.veinminer.VeinMiner;

/**
 * Created with IntelliJ IDEA.
 * User: james
 * Date: 21/02/14
 * Time: 10:20 AM
 * To change this template use File | Settings | File Templates.
 */
public class PacketPingClient implements IPacket {
    @Override
    public void writeBytes(ByteBuf buffer) { }

    @Override
    public void readBytes(ByteBuf buffer) { }

    @Override
    public void handleClientSide(EntityClientPlayerMP player) {
        PacketClientPresent packet = new PacketClientPresent();
        VeinMiner.instance.channelHandler.sendToServer(packet);
    }

    @Override
    public void handleServerSide(EntityPlayerMP player) { }
}
