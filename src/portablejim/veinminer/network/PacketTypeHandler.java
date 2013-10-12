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

import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import portablejim.veinminer.lib.ModInfo;
import portablejim.veinminer.network.packet.PacketClientPresent;
import portablejim.veinminer.network.packet.PacketClientSettings;
import portablejim.veinminer.network.packet.PacketServerDetected;
import portablejim.veinminer.network.packet.PacketVeinMiner;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

/**
 * Handles the packet types to simply generate packets of different types.
 */

public enum PacketTypeHandler {
    CLIENT_PRESENT(PacketClientPresent.class),
    CLIENT_SETTINGS(PacketClientSettings.class),
    SERVER_DETECTED(PacketServerDetected.class);

    private Class<? extends PacketVeinMiner> subClassType;

     PacketTypeHandler(Class <? extends PacketVeinMiner> subClassType) {
         this.subClassType = subClassType;
    }

    public static PacketVeinMiner generatePacket(byte[] data) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        int selector = inputStream.read();
        DataInputStream dataInputStream = new DataInputStream(inputStream);

        PacketVeinMiner packet = null;

        try {
            packet = values()[selector].subClassType.newInstance();
        }
        catch(Exception e) {
            e.printStackTrace(System.err);
        }

        packet.populate(dataInputStream);

        return packet;
    }

    public static PacketVeinMiner generatePacket(PacketTypeHandler type) {
        PacketVeinMiner packet = null;

        try {
            packet = values()[type.ordinal()].subClassType.newInstance();
        }
        catch(Exception e) {
            e.printStackTrace(System.err);
        }

        return packet;
    }

    public static Packet populatePacket(PacketVeinMiner packetVeinMiner) {
        byte[] data = packetVeinMiner.generateByteArray();

        Packet250CustomPayload newPacket = new Packet250CustomPayload();
        newPacket.channel = ModInfo.CHANNEL;
        newPacket.data = data;
        newPacket.length = data.length;
        newPacket.isChunkDataPacket = packetVeinMiner.isChunkDataPacket;

        return newPacket;
    }
}
