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

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import portablejim.veinminer.VeinMiner;
import portablejim.veinminer.server.MinerServer;
import portablejim.veinminer.util.PlayerStatus;

import java.util.UUID;

/**
 * Packet the client sends to the server to tell it to activate or deactivate
 * for the player that sent it.
 */

public class PacketMinerActivate implements IMessage {
    public boolean keyActive;

    @SuppressWarnings("UnusedDeclaration")
    public PacketMinerActivate() {}

    public PacketMinerActivate(boolean keyActive) {
        this.keyActive = keyActive;
    }

    @Override
    public void toBytes(ByteBuf buffer) {
        buffer.writeBoolean(keyActive);
    }

    @Override
    public void fromBytes(ByteBuf buffer) {
        keyActive = buffer.readBoolean();
    }

    public static class Handler extends GenericHandler<PacketMinerActivate> {
        @Override
        public void processMessage(PacketMinerActivate packetMinerActivate, MessageContext context) {
            EntityPlayerMP player = context.getServerHandler().player;
            UUID playerName = player.getUniqueID();

            PlayerStatus status = VeinMiner.instance.minerServer.getPlayerStatus(playerName);
            if (packetMinerActivate.keyActive) {
                if (status == PlayerStatus.INACTIVE) {
                    VeinMiner.instance.minerServer.setPlayerStatus(playerName, PlayerStatus.ACTIVE);
                }
            } else {
                if (status == PlayerStatus.ACTIVE) {
                    VeinMiner.instance.minerServer.setPlayerStatus(playerName, PlayerStatus.INACTIVE);
                }
            }
        }
    }
}
