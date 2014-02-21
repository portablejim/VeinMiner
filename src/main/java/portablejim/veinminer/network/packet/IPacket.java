package portablejim.veinminer.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * Created with IntelliJ IDEA.
 * User: james
 * Date: 20/02/14
 * Time: 11:19 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IPacket {
    public void writeBytes(ByteBuf buffer);
    public void readBytes(ByteBuf buffer);
    public void handleClientSide(EntityClientPlayerMP player);
    public void handleServerSide(EntityPlayerMP player);
}
