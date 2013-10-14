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

import cpw.mods.fml.common.network.Player;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import portablejim.veinminer.network.PacketTypeHandler;
import portablejim.veinminer.server.MinerServer;
import portablejim.veinminer.server.PlayerStatus;
import portablejim.veinminer.util.PreferredMode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Packet that the client sends to the server on login. Uses the stored value
 * to set the mode for the player on the server.
 *
 * Execute() is performed server-side.
 */

public class PacketClientPresent extends PacketVeinMiner {
    private int preferredMode;

    public PacketClientPresent() {
        super(PacketTypeHandler.CLIENT_PRESENT, false);
        this.preferredMode = PreferredMode.AUTO;
    }
    public PacketClientPresent(int preferredMode) {
        super(PacketTypeHandler.CLIENT_PRESENT, false);
        this.preferredMode = preferredMode;
    }

    @Override
    public void readDataStream(DataInputStream dataInputStream) throws IOException {
        preferredMode = dataInputStream.readInt();
    }

    public void writeDataStream(DataOutputStream dataOutputStream) throws IOException{
        dataOutputStream.writeInt(preferredMode);
    }

    public void execute(INetworkManager manager, Player player) {
        if(player instanceof EntityPlayer) {
            EntityPlayer thePlayer = (EntityPlayer) player;
            String playerName = thePlayer.getEntityName();

            MinerServer.instance.addClientPlayer(playerName);
            switch (preferredMode) {
                case PreferredMode.AUTO:
                    // Already set to auto
                    thePlayer.addChatMessage("mod.veinminer.preferredmode.auto");
                    break;
                case PreferredMode.SNEAK:
                    MinerServer.instance.setPlayerStatus(playerName, PlayerStatus.SNEAK_ACTIVE);
                    thePlayer.addChatMessage("mod.veinminer.preferredmode.sneak");
                    break;
                case PreferredMode.NO_SNEAK:
                    MinerServer.instance.setPlayerStatus(playerName, PlayerStatus.SNEAK_INACTIVE);
                    thePlayer.addChatMessage("mod.veinminer.preferredmode.nosneak");
            }
        }
    }
}
