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

package portablejim.veinminer.event.client;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import portablejim.veinminer.VeinMiner;
import portablejim.veinminer.network.packet.PacketMinerActivate;
import portablejim.veinminer.util.PreferredMode;

/**
 * Manages the keybind for the client side of VeinMiner.
 * Registers, keybind and sends packets on the key being pressed and released.
 */

public class ActivateMinerKeybindManager {
    public KeyBinding keyBinding = new KeyBinding("veinminer.key.enable", Keyboard.KEY_GRAVE, "veinminer.key.category");
    private static boolean statusEnabled = false;

    public ActivateMinerKeybindManager() {
        ClientRegistry.registerKeyBinding(keyBinding);
        FMLCommonHandler.instance().bus().register(this);
    }

    @SuppressWarnings("UnusedDeclaration")
    @SubscribeEvent
    public void KeyEvent(InputEvent event) {
        boolean sendPacket = false;

        int mode = VeinMiner.instance.configurationSettings.getPreferredMode();
        boolean pressed = keyBinding.getIsKeyPressed();
        if(mode == PreferredMode.DISABLED) {
            statusEnabled = false;
            sendPacket = true; // If enabled when changing, notify server that it is disabled.
        }
        else if ((pressed &&  mode == PreferredMode.PRESSED) || (!pressed && mode == PreferredMode.RELEASED) && !statusEnabled) {
            statusEnabled = true;
            sendPacket = true;
        } else if (((pressed &&  mode == PreferredMode.RELEASED) || (!pressed && mode == PreferredMode.PRESSED)) && statusEnabled) {
            statusEnabled = false;
            sendPacket = true;
        }
        if (sendPacket) {
            PacketMinerActivate packet = new PacketMinerActivate(statusEnabled);
            VeinMiner.instance.networkManager.sendToServer(packet);
        }
    }
}
