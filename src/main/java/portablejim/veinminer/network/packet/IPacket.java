package portablejim.veinminer.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.*;

/**
 * Created with IntelliJ IDEA.
 * User: james
 * Date: 19/02/14
 * Time: 4:03 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IPacket {
    public void readBytes(ByteBuf bytes);
    public void writeBytes(ByteBuf bytes);
    public void executeClient();
    public void executeServer(EntityPlayerMP player);
}
