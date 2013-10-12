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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Packet client sends to the server on keybind being pressed. Sets the player
 * status based on the value of the status and whether the keybind is active.
 */

public class PacketClientSettings extends PacketVeinMiner {
    private boolean isKeyDown;

    public PacketClientSettings() {
        super(PacketTypeHandler.CLIENT_SETTINGS, false);
    }

    public PacketClientSettings(boolean keyDown) {
        super(PacketTypeHandler.CLIENT_SETTINGS, false);

        this.isKeyDown = keyDown;
    }

    @Override
    public void readDataStream(DataInputStream dataInputStream) throws IOException {
        isKeyDown = dataInputStream.readBoolean();
    }

    public void writeDataStream(DataOutputStream dataOutputStream) throws IOException{
        dataOutputStream.writeBoolean(isKeyDown);
    }

    public void execute(INetworkManager manager, Player player) {
        if(player instanceof EntityPlayer) {
            EntityPlayer thePlayer = (EntityPlayer) player;
            String playerName = thePlayer.getEntityName();

            PlayerStatus status = MinerServer.instance.getPlayerStatus(playerName);
            if(isKeyDown) {
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
}
