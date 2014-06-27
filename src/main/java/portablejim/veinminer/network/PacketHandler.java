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

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.network.NetHandlerPlayServer;
import portablejim.veinminer.network.packet.IPacket;

import static io.netty.channel.ChannelHandler.Sharable;

/**
 * Handle actions when packet is received. Split action depending on side.
 */
@Sharable
public class PacketHandler extends SimpleChannelInboundHandler<IPacket> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, IPacket msg) throws Exception {
        if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
            NetHandlerPlayServer handler = (NetHandlerPlayServer) ctx.channel().attr(NetworkRegistry.NET_HANDLER).get();
            msg.handleServerSide(handler.playerEntity);
        } else if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            msg.handleClientSide(Minecraft.getMinecraft().thePlayer);
        }
    }
}
