package portablejim.veinminer.network.handler;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.entity.player.EntityPlayerMP;
import portablejim.veinminer.network.packet.PacketMinerActivate;
import portablejim.veinminer.server.MinerServer;
import portablejim.veinminer.util.PlayerStatus;

import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: james
 * Date: 20/02/14
 * Time: 6:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class HandlerMinerActive implements IMessageHandler<PacketMinerActivate, IMessage> {
    @Override
    public IMessage onMessage(PacketMinerActivate message, MessageContext ctx) {
        EntityPlayerMP thePlayer = ctx.getServerHandler().playerEntity;
        UUID playerName = thePlayer.getUniqueID();

        PlayerStatus status = MinerServer.instance.getPlayerStatus(playerName);
        if(message.keyActive) {
            if(status == PlayerStatus.INACTIVE) {
                MinerServer.instance.setPlayerStatus(playerName, PlayerStatus.ACTIVE);
            }
        }
        else {
            if(status == PlayerStatus.ACTIVE) {
                MinerServer.instance.setPlayerStatus(playerName, PlayerStatus.INACTIVE);
            }
        }
        return null;
    }
}
