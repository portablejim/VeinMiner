package portablejim.veinminer.network.packet;

import portablejim.veinminer.network.PacketTypeHandler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: james
 * Date: 9/06/13
 * Time: 1:21 AM
 * To change this template use File | Settings | File Templates.
 */
public class PacketClientSettings extends PacketVeinMiner {
    public boolean isKeyDown;

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
}
