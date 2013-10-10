package portablejim.veinminer.network.packet;

import cpw.mods.fml.common.network.Player;
import net.minecraft.network.INetworkManager;
import portablejim.veinminer.network.PacketTypeHandler;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: james
 * Date: 8/06/13
 * Time: 5:35 PM
 * To change this template use File | Settings | File Templates.
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
