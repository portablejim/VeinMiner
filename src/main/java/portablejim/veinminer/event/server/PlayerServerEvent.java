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

package portablejim.veinminer.event.server;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import portablejim.veinminer.VeinMiner;
import portablejim.veinminer.lib.MinerLogger;
import portablejim.veinminer.network.packet.PacketPingClient;
import portablejim.veinminer.server.MinerServer;

/**
 * Class to hold events that happen on the server.
 */
public class PlayerServerEvent {

    public PlayerServerEvent() {
        FMLCommonHandler.instance().bus().register(this);
    }

    @SuppressWarnings("UnusedDeclaration")
    @SubscribeEvent
    public void connected(PlayerLoggedInEvent event) {
        //new JoinServerTicker();
        PacketPingClient packet = new PacketPingClient();
        VeinMiner.instance.channelHandler.sendToPlayer(event.player, packet);
        MinerLogger.debug("Sent ping packet to client");
    }

    @SuppressWarnings("UnusedDeclaration")
    @SubscribeEvent
    public void disconnected(PlayerLoggedOutEvent event) {
        MinerServer.instance.removeClientPlayer(event.player.getPersistentID());
    }
}
