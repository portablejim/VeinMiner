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

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import portablejim.veinminer.VeinMiner;
import portablejim.veinminer.lib.MinerLogger;
import portablejim.veinminer.network.PacketPingClient;
import portablejim.veinminer.server.MinerServer;

/**
 * Class to hold events that happen on the server.
 */
public class PlayerServerEvent {
    MinerServer minerServer = null;

    public PlayerServerEvent() {
        FMLCommonHandler.instance().bus().register(this);
    }

    public void setServer(MinerServer server) {
        minerServer = server;
    }

    @SuppressWarnings("UnusedDeclaration")
    @SubscribeEvent
    public void connected(PlayerLoggedInEvent event) {
        //new JoinServerTicker();
        if(event.player instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) event.player;
            PacketPingClient packet = new PacketPingClient();
            VeinMiner.instance.networkWrapper.sendTo(packet, player);
            MinerLogger.debug("Sent ping packet to client");
        }
        else if(event.player != null){
            MinerLogger.debug("Somehow %s is not an EntityPlayerMP", event.player.toString());
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    @SubscribeEvent
    public void disconnected(PlayerLoggedOutEvent event) {
        minerServer.removeClientPlayer(event.player.getPersistentID());
    }
}
