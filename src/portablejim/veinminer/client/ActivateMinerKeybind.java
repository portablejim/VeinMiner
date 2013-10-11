package portablejim.veinminer.client;

import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.network.PacketDispatcher;
import net.minecraft.client.settings.KeyBinding;
import portablejim.veinminer.lib.ModInfo;
import portablejim.veinminer.network.PacketTypeHandler;
import portablejim.veinminer.network.packet.PacketClientSettings;

import java.util.EnumSet;

/**
 * Created with IntelliJ IDEA.
 * User: james
 * Date: 10/10/13
 * Time: 4:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class ActivateMinerKeybind extends KeyBindingRegistry.KeyHandler {
    public ActivateMinerKeybind(KeyBinding[] keyBindings, boolean[] repeatings) {
        super(keyBindings, repeatings);
    }

    @Override
    public void keyDown(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd, boolean isRepeat) {
        PacketDispatcher.sendPacketToServer(PacketTypeHandler.populatePacket(new PacketClientSettings(true)));
    }

    @Override
    public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd) {
        PacketDispatcher.sendPacketToServer(PacketTypeHandler.populatePacket(new PacketClientSettings(false)));
    }

    @Override
    public EnumSet<TickType> ticks() {
        return EnumSet.of(TickType.CLIENT);
    }

    @Override
    public String getLabel() {
        return ModInfo.MOD_ID + ":" + this.getClass().getSimpleName();
    }
}
