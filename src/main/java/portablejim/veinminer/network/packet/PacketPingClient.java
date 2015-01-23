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

package portablejim.veinminer.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayerMP;
import portablejim.veinminer.VeinMiner;

/**
 * Packet the server sends the client on logging in.
 * If the client has this mod, it will respond with a
 * packet in return
 * @see PacketClientPresent
 */

public class PacketPingClient implements IPacket {
    @Override
    public void writeBytes(ByteBuf buffer) { }

    @Override
    public void readBytes(ByteBuf buffer) { }

    @Override
    public void handleClientSide(EntityPlayerSP player) {
        PacketClientPresent packet = new PacketClientPresent(VeinMiner.instance.configurationSettings.getPreferredMode());
        VeinMiner.instance.networkManager.sendToServer(packet);
    }

    @Override
    public void handleServerSide(EntityPlayerMP player) { }
}
