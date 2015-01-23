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

import net.minecraftforge.fml.common.network.FMLEmbeddedChannel;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import portablejim.veinminer.lib.ModInfo;
import portablejim.veinminer.network.packet.IPacket;

import java.util.EnumMap;

import static net.minecraftforge.fml.common.network.FMLOutboundHandler.*;

/**
 * Manage network related stuff. Setup channel and send packets when asked.
 */

public class NetworkManager {
    private EnumMap<Side, FMLEmbeddedChannel> channels;

    public NetworkManager() {
        channels = NetworkRegistry.INSTANCE.newChannel(ModInfo.CHANNEL, new ChannelHandler(), new PacketHandler());
    }

    public void sendToServer(IPacket packet) {
        channels.get(Side.CLIENT).attr(FML_MESSAGETARGET).set(OutboundTarget.TOSERVER);
        channels.get(Side.CLIENT).writeOutbound(packet);
    }

    public void sendToPlayer(EntityPlayer player, IPacket packet) {
        channels.get(Side.SERVER).attr(FML_MESSAGETARGET).set(OutboundTarget.PLAYER);
        channels.get(Side.SERVER).attr(FML_MESSAGETARGETARGS).set(player);
        channels.get(Side.SERVER).writeOutbound(packet);
    }
}
