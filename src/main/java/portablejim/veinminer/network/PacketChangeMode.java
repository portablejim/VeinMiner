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
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import portablejim.veinminer.VeinMiner;
import portablejim.veinminer.server.MinerServer;
import portablejim.veinminer.util.PlayerStatus;
import portablejim.veinminer.util.PreferredMode;

import java.util.UUID;

/**
 * Packet that the client sends to the server to say it has the client mod.
 */

public class PacketChangeMode implements IMessage {
    public short mode;

    @SuppressWarnings("UnusedDeclaration")
    public PacketChangeMode() {
        mode = 0;
    }

    public PacketChangeMode(int clientMode) {
        switch(clientMode) {
            case PreferredMode.DISABLED:
            case PreferredMode.PRESSED:
            case PreferredMode.RELEASED:
            case PreferredMode.SNEAK_ACTIVE:
            case PreferredMode.SNEAK_INACTIVE:
                mode = (short) clientMode;
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

    public static class Handler extends GenericHandler<PacketChangeMode> {
        @Override
        public void processMessage(PacketChangeMode packetClientPresent, MessageContext context) {
            if(context.side == Side.CLIENT) {
                VeinMiner.instance.currentMode = packetClientPresent.mode;
                VeinMiner.instance.logger.info(String.format("Received mode change %d", packetClientPresent.mode));
                switch(packetClientPresent.mode) {
                    case PreferredMode.DISABLED:
                    case PreferredMode.SNEAK_ACTIVE:
                    case PreferredMode.SNEAK_INACTIVE:
                        Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("command.veinminerc.set.disabled"));
                        break;
                    case PreferredMode.PRESSED:
                        Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("command.veinminerc.set.pressed"));
                        break;
                    case PreferredMode.RELEASED:
                        Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation("command.veinminerc.set.released"));
                        break;
                }
            }
            else if (context.side == Side.SERVER) { // Server Side
                EntityPlayerMP player = context.getServerHandler().playerEntity;
                UUID playerName = player.getUniqueID();

                MinerServer minerServer = VeinMiner.instance.minerServer;
                minerServer.addClientPlayer(playerName);
                switch(packetClientPresent.mode) {
                    case PreferredMode.DISABLED:
                    case PreferredMode.PRESSED:
                    case PreferredMode.RELEASED:
                        minerServer.setPlayerStatus(playerName, PlayerStatus.INACTIVE);
                        player.sendMessage(new TextComponentTranslation("mod.veinminer.preferredmode.auto"));
                        break;
                    case PreferredMode.SNEAK_ACTIVE:
                        minerServer.setPlayerStatus(playerName, PlayerStatus.SNEAK_ACTIVE);
                        player.sendMessage(new TextComponentTranslation("mod.veinminer.preferredmode.sneak"));
                        break;
                    case PreferredMode.SNEAK_INACTIVE:
                        minerServer.setPlayerStatus(playerName, PlayerStatus.SNEAK_INACTIVE);
                        player.sendMessage(new TextComponentTranslation("mod.veinminer.preferredmode.nosneak"));
                        break;

                }
            }
        }
    }
}
