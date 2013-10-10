package portablejim.veinminer.network;

import cpw.mods.fml.common.network.IConnectionHandler;
import cpw.mods.fml.common.network.Player;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.NetLoginHandler;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet1Login;
import net.minecraft.server.MinecraftServer;
import portablejim.veinminer.network.packet.PacketClientPresent;

/**
 * Created with IntelliJ IDEA.
 * User: james
 * Date: 10/10/13
 * Time: 10:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConnectionHandler implements IConnectionHandler{
    @Override
    public void playerLoggedIn(Player player, NetHandler netHandler, INetworkManager manager) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String connectionReceived(NetLoginHandler netHandler, INetworkManager manager) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void connectionOpened(NetHandler netClientHandler, String server, int port, INetworkManager manager) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void connectionOpened(NetHandler netClientHandler, MinecraftServer server, INetworkManager manager) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void connectionClosed(INetworkManager manager) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void clientLoggedIn(NetHandler clientHandler, INetworkManager manager, Packet1Login login) {
        manager.addToSendQueue(PacketTypeHandler.populatePacket(new PacketClientPresent()));
    }
}
