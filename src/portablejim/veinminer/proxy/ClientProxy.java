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

package portablejim.veinminer.proxy;

import cpw.mods.fml.client.registry.KeyBindingRegistry;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import portablejim.veinminer.client.ActivateMinerKeybind;
import portablejim.veinminer.configuration.ConfigurationSettings;
import portablejim.veinminer.configuration.ConfigurationValues;

/**
 * Client side implementation of proxy interface.
 */

public class ClientProxy implements CommonProxy {
    private ConfigurationSettings configSettings;
    @Override
    public void registerKeybind() {
        KeyBinding enableKeyBinding = new KeyBinding("Activate Veinminer", Keyboard.KEY_GRAVE);
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
