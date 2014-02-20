package portablejim.veinminer.network.packet;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

/**
 * Created with IntelliJ IDEA.
 * User: james
 * Date: 19/02/14
 * Time: 4:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class PacketClientPresent implements IMessage {
    public int preferredMode;

    @SuppressWarnings("UnusedDeclaration")
    public PacketClientPresent(){}

    public PacketClientPresent(int preferredMode) {
        this.preferredMode = preferredMode;
    }

    @Override
    public void fromBytes(ByteBuf bytes) {
        preferredMode = bytes.readInt();
    }

    @Override
    public void toBytes(ByteBuf bytes) {
        bytes.writeInt(preferredMode);
    }
}
