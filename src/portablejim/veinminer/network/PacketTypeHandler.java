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
 * Created with IntelliJ IDEA.
 * User: james
 * Date: 8/06/13
 * Time: 5:40 PM
 * To change this template use File | Settings | File Templates.
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
