package portablejim.veinminer.network.handler;

import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentTranslation;
import portablejim.veinminer.network.packet.PacketClientPresent;
import portablejim.veinminer.server.MinerServer;
import portablejim.veinminer.util.PlayerStatus;
import portablejim.veinminer.util.PreferredMode;

import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: james
 * Date: 20/02/14
 * Time: 6:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class HandlerClientPresent implements IMessageHandler<PacketClientPresent, PacketClientPresent> {

    public HandlerClientPresent(){}

    @Override
    public PacketClientPresent onMessage(PacketClientPresent message, MessageContext ctx) {
        return null;
    }
}
