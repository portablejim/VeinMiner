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
import portablejim.veinminer.server.MinerServer;
import portablejim.veinminer.util.PlayerStatus;

import java.util.UUID;

/**
 * Packet the client sends to the server to tell it to activate or deactivate
 * for the player that sent it.
 */

public class PacketMinerActivate implements IPacket {
    public boolean keyActive;

    @SuppressWarnings("UnusedDeclaration")
    public PacketMinerActivate() {}

    public PacketMinerActivate(boolean keyActive) {
        this.keyActive = keyActive;
    }

    @Override
    public void writeBytes(ByteBuf buffer) {
        buffer.writeBoolean(keyActive);
    }

    @Override
    public void readBytes(ByteBuf buffer) {
        keyActive = buffer.readBoolean();
    }

    @Override
    public void handleClientSide(EntityPlayerSP player) { }

    @Override
    public void handleServerSide(EntityPlayerMP player) {
        UUID playerName = player.getUniqueID();

        PlayerStatus status = MinerServer.instance.getPlayerStatus(playerName);
        if(keyActive) {
            if(status == PlayerStatus.INACTIVE) {
                MinerServer.instance.setPlayerStatus(playerName, PlayerStatus.ACTIVE);
            }
        }
        else {
            if(status == PlayerStatus.ACTIVE) {
                MinerServer.instance.setPlayerStatus(playerName, PlayerStatus.INACTIVE);
            }
        }
    }
}
