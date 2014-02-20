package portablejim.veinminer.network.packet;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
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
public class PacketMinerActivate implements IMessage {
    public boolean keyActive;

    @SuppressWarnings("UnusedDeclaration")
    public PacketMinerActivate(){}
    public PacketMinerActivate(boolean active) {
        this.keyActive = active;
    }

    @Override
    public void fromBytes(ByteBuf bytes) {
        keyActive = bytes.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf bytes) {
        bytes.writeBoolean(keyActive);
    }
}
