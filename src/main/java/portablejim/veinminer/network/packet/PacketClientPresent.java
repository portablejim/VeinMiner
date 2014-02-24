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
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentTranslation;
import portablejim.veinminer.server.MinerServer;
import portablejim.veinminer.util.PlayerStatus;

import java.util.UUID;

/**
 * Packet that the client sends to the server to say it has the client mod.
 */

public class PacketClientPresent implements IPacket {

    public PacketClientPresent() {}

    @Override
    public void writeBytes(ByteBuf buffer) { }

    @Override
    public void readBytes(ByteBuf buffer) { }

    @Override
    public void handleClientSide(EntityClientPlayerMP player) { }

    @Override
    public void handleServerSide(EntityPlayerMP player) {
        UUID playerName = player.getUniqueID();

        MinerServer.instance.addClientPlayer(playerName);
        MinerServer.instance.setPlayerStatus(playerName, PlayerStatus.INACTIVE);
        player.addChatMessage(new ChatComponentTranslation("mod.veinminer.preferredmode.auto"));
    }
}
