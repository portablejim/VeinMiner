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

import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import portablejim.veinminer.network.packet.PacketVeinMiner;

/**
 * Vein Miner
 *
 * Handles packets.
 *
 * Thanks to Equivalent-Exchange-3 by Pahimar.
 *
 * @author Portablejim, Pahimar
 */

public class PacketHandler implements IPacketHandler{
    @Override
    public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
        PacketVeinMiner packetVeinMiner = PacketTypeHandler.generatePacket(packet.data);

        packetVeinMiner.execute(manager, player);
    }
}
