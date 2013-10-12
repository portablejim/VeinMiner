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
import net.minecraft.network.INetworkManager;
import portablejim.veinminer.network.PacketTypeHandler;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * The base implementation of a packet.
 */

public class PacketVeinMiner {
    public PacketTypeHandler packetType;
    public boolean isChunkDataPacket;

    public PacketVeinMiner(PacketTypeHandler packetType, boolean isChunkDataPacket) {
        this.packetType = packetType;
        this.isChunkDataPacket = isChunkDataPacket;
    }

    public void execute(INetworkManager manager, Player player) { }

    public void populate(DataInputStream dataInputStream) {
        try {
            this.readDataStream(dataInputStream);
        }
        catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }

    public byte[] generateByteArray() {
        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteOutputStream);
        
        try {
            dataOutputStream.writeByte(packetType.ordinal());
            this.writeDataStream(dataOutputStream);
        }
        catch (IOException e) {
            e.printStackTrace(System.err);
        }

        return byteOutputStream.toByteArray();
    }

    public void readDataStream(DataInputStream dataInputStream) throws IOException{ }

    public void writeDataStream(DataOutputStream dataOutputStream) throws IOException{ }

}
