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
import net.minecraft.util.text.TextComponentTranslation;
import portablejim.veinminer.VeinMiner;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import portablejim.veinminer.lib.MinerLogger;
import portablejim.veinminer.server.MinerServer;
import portablejim.veinminer.util.PlayerStatus;
import portablejim.veinminer.util.PreferredMode;

import java.util.UUID;

/**
 * Packet that the client sends to the server to say it has the client mod.
 */

public class PacketClientPresent implements IMessage {
    public short mode;

    @SuppressWarnings("UnusedDeclaration")
    public PacketClientPresent() {
        mode = 0;
    }

    public PacketClientPresent(int clientMode) {
        switch(clientMode) {
            case PreferredMode.PRESSED:
                mode = 1;
                break;
            case PreferredMode.RELEASED:
                mode = 2;
                break;
            case PreferredMode.SNEAK_ACTIVE:
                mode = 3;
                break;
            case PreferredMode.SNEAK_INACTIVE:
                mode = 4;
                break;
            default:
                mode = 0;
        }
    }

    @Override
    public void toBytes(ByteBuf buffer) {
        buffer.writeShort(mode);
    }

    @Override
    public void fromBytes(ByteBuf buffer) {
        mode = buffer.readShort();
    }

    public static class Handler extends GenericHandler<PacketClientPresent> {
        @Override
        public void processMessage(PacketClientPresent packetClientPresent, MessageContext context) {
            EntityPlayerMP player = context.getServerHandler().playerEntity;
            MinerLogger.debug("Received a PacketClientPresent");
            UUID playerName = player.getUniqueID();

            MinerServer minerServer = VeinMiner.instance.minerServer;
            minerServer.addClientPlayer(playerName);
            switch (packetClientPresent.mode) {
                case 3:
                    minerServer.setPlayerStatus(playerName, PlayerStatus.SNEAK_ACTIVE);
                    player.addChatMessage(new TextComponentTranslation("mod.veinminer.preferredmode.sneak"));
                    break;
                case 4:
                    minerServer.setPlayerStatus(playerName, PlayerStatus.SNEAK_INACTIVE);
                    player.addChatMessage(new TextComponentTranslation("mod.veinminer.preferredmode.nosneak"));
                    break;
                case 1:
                case 2:
                    player.addChatMessage(new TextComponentTranslation("mod.veinminer.preferredmode.auto"));
                default:
                    minerServer.setPlayerStatus(playerName, PlayerStatus.INACTIVE);
            }

            VeinMiner.instance.networkWrapper.sendTo(new PacketChangeMode(packetClientPresent.mode), player);
        }
    }
}
