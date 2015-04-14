package portablejim.veinminer.network;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Handler to redirect message processing.
 * Used to easily unify 1.7 and 1.8 code in a thread-safe way.
 */
public abstract class GenericHandler<REQ extends IMessage> implements IMessageHandler<REQ, IMessage> {
    @Override
    public IMessage onMessage(REQ message, MessageContext ctx) {
        processMessage(message, ctx);
        return null;
    }

    public abstract void processMessage(REQ message, MessageContext context);
}
