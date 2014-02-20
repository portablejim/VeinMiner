package portablejim.veinminer.network;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.FMLIndexedMessageToMessageCodec;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import portablejim.veinminer.lib.ModInfo;
import portablejim.veinminer.network.handler.HandlerClientPresent;
import portablejim.veinminer.network.packet.IPacket;
import portablejim.veinminer.network.packet.PacketClientPresent;
import portablejim.veinminer.network.packet.PacketMinerActivate;

/**
 * Created with IntelliJ IDEA.
 * User: james
 * Date: 19/02/14
 * Time: 3:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class ChannelManager {
    private SimpleNetworkWrapper channels;

    public ChannelManager() { }

    public void init(){
        channels = NetworkRegistry.INSTANCE.newSimpleChannel("TESTING");
        channels.registerMessage(HandlerClientPresent.class, PacketClientPresent.class, 1, Side.SERVER);
    }

    public void sendToServer(IMessage packet) {
        channels.sendToServer(packet);
    }
}
