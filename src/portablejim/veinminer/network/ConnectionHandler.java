/* This file is part of VeinMiner.
 *
 *    VeinMiner is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU Lesser General Public License as
 *    published by the Free Software Foundation, either version 3 of
 *     the License, or (at your option) any later version.
 *
 *    VeinMiner is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with VeinMiner.
 *    If not, see <http://www.gnu.org/licenses/>.
 */

package portablejim.veinminer.network;

import cpw.mods.fml.common.network.IConnectionHandler;
import cpw.mods.fml.common.network.Player;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.NetLoginHandler;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet1Login;
import net.minecraft.server.MinecraftServer;
import portablejim.veinminer.VeinMiner;
import portablejim.veinminer.configuration.ConfigurationSettings;
import portablejim.veinminer.network.packet.PacketClientPresent;

/**
 * Client side. Handles when the client connects to the server and sends a
 * packet to the server with the preference specified in the config file.
 */

public class ConnectionHandler implements IConnectionHandler{
    @Override
    public void playerLoggedIn(Player player, NetHandler netHandler, INetworkManager manager) { }

    @Override
    public String connectionReceived(NetLoginHandler netHandler, INetworkManager manager) {
        return null;
    }

    @Override
    public void connectionOpened(NetHandler netClientHandler, String server, int port, INetworkManager manager) { }

    @Override
    public void connectionOpened(NetHandler netClientHandler, MinecraftServer server, INetworkManager manager) { }

    @Override
    public void connectionClosed(INetworkManager manager) { }

    @Override
    public void clientLoggedIn(NetHandler clientHandler, INetworkManager manager, Packet1Login login) {
        ConfigurationSettings settings = VeinMiner.proxy.getConfigSettings();
        int mode = settings.getPreferredMode();
        manager.addToSendQueue(PacketTypeHandler.populatePacket(new PacketClientPresent(mode)));
    }
}
