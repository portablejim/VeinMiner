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
import net.minecraft.util.ChatComponentTranslation;
import portablejim.veinminer.server.MinerServer;
import portablejim.veinminer.util.PlayerStatus;
import portablejim.veinminer.util.PreferredMode;

import java.util.UUID;

/**
 * Packet that the client sends to the server to say it has the client mod.
 */

public class PacketClientPresent implements IPacket {
    public short mode;

    @SuppressWarnings("UnusedDeclaration")
    public PacketClientPresent() {
        mode = 0;
    }

    public PacketClientPresent(int clientMode) {
        switch(clientMode) {
            case PreferredMode.PRESSED:
            case PreferredMode.RELEASED:
                mode = 1;
                break;
            case PreferredMode.SNEAK_ACTIVE:
                mode = 2;
                break;
            case PreferredMode.SNEAK_INACTIVE:
                mode = 3;
                break;
            default:
                mode = 0;
        }
    }

    @Override
    public void writeBytes(ByteBuf buffer) {
        buffer.writeShort(mode);
    }

    @Override
    public void readBytes(ByteBuf buffer) {
        mode = buffer.readShort();
    }

    @Override
    public void handleClientSide(EntityPlayerSP player) { }

    @Override
    public void handleServerSide(EntityPlayerMP player) {
        UUID playerName = player.getUniqueID();

        MinerServer.instance.addClientPlayer(playerName);
        switch(mode) {
            case 2:
                MinerServer.instance.setPlayerStatus(playerName, PlayerStatus.SNEAK_ACTIVE);
                player.addChatMessage(new ChatComponentTranslation("mod.veinminer.preferredmode.sneak"));
                break;
            case 3:
                MinerServer.instance.setPlayerStatus(playerName, PlayerStatus.SNEAK_INACTIVE);
                player.addChatMessage(new ChatComponentTranslation("mod.veinminer.preferredmode.nosneak"));
                break;
            case 1:
                player.addChatMessage(new ChatComponentTranslation("mod.veinminer.preferredmode.auto"));
            default:
                MinerServer.instance.setPlayerStatus(playerName, PlayerStatus.INACTIVE);
        }
    }
}
