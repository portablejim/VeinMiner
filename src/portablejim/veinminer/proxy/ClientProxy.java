package portablejim.veinminer.proxy;

import cpw.mods.fml.client.registry.KeyBindingRegistry;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import portablejim.veinminer.client.ActivateMinerKeybind;
import portablejim.veinminer.configuration.ConfigurationSettings;
import portablejim.veinminer.configuration.ConfigurationValues;

/**
 * Created with IntelliJ IDEA.
 * User: james
 * Date: 8/06/13
 * Time: 9:37 AM
 * To change this template use File | Settings | File Templates.
 */
public class ClientProxy implements CommonProxy {
    ConfigurationSettings configSettings;
    @Override
    public void registerKeybind() {
        KeyBinding enableKeyBinding = new KeyBinding("Activate Veinminer", Keyboard.KEY_LSHIFT);
        KeyBinding[] keyBindings = {enableKeyBinding};
        boolean[] repeats = {false};
        ActivateMinerKeybind enableKeyBind = new ActivateMinerKeybind(keyBindings, repeats);

        KeyBindingRegistry.registerKeyBinding(enableKeyBind);
    }

    @Override
    public void setupConfig(ConfigurationValues config) {
        configSettings = new ConfigurationSettings(config);
    }

    @Override
    public ConfigurationSettings getConfigSettings() {
        if(configSettings == null) {
            return null;
        }
        return configSettings;
    }
}
