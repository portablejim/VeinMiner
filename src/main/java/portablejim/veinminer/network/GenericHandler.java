package portablejim.veinminer.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Handler to redirect message processing.
 * Used to easily unify 1.7 and 1.8 code in a thread-safe way.
 */
public abstract class GenericHandler<REQ extends IMessage> implements IMessageHandler<REQ, IMessage> {
    @Override
    public IMessage onMessage(final REQ message, final MessageContext ctx) {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                processMessage(message, ctx);
            }
        };
        if(ctx.side == Side.CLIENT) {
            Minecraft.getMinecraft().addScheduledTask(task);
        }
        else if(ctx.side == Side.SERVER) {
            EntityPlayerMP playerEntity = ctx.getServerHandler().playerEntity;
            if(playerEntity == null) {
                FMLLog.warning("onMessage-server: Player is null");
                return null;
            }
            playerEntity.getServer().addScheduledTask(task);
        }
        return null;
    }

    public abstract void processMessage(REQ message, MessageContext context);
}
