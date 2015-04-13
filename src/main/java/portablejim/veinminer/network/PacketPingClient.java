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

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import portablejim.veinminer.VeinMiner;

/**
 * Packet the server sends the client on logging in.
 * If the client has this mod, it will respond with a
 * packet in return
 * @see PacketClientPresent
 */

public class PacketPingClient implements IMessage {

    public PacketPingClient() {}

    @Override
    public void fromBytes(ByteBuf buf) { }

    @Override
    public void toBytes(ByteBuf buf) { }

    public static class Handler implements IMessageHandler<PacketPingClient, PacketClientPresent> {
        @Override
        public PacketClientPresent onMessage(PacketPingClient packetPingClient, MessageContext context) {
            return new PacketClientPresent(VeinMiner.instance.configurationSettings.getPreferredMode());
        }
    }
}
