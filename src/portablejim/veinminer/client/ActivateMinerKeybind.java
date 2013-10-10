package portablejim.veinminer.client;

import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.common.TickType;
import net.minecraft.client.settings.KeyBinding;
import portablejim.veinminer.lib.ModInfo;

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
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd) {
        //To change body of implemented methods use File | Settings | File Templates.
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
