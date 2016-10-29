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

import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import portablejim.veinminer.VeinMiner;
import portablejim.veinminer.network.PacketMinerActivate;
import portablejim.veinminer.util.PreferredMode;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages the keybind for the client side of VeinMiner.
 * Registers, keybind and sends packets on the key being pressed and released.
 */

public class ActivateMinerKeybindManager {
    public KeyBinding keyBinding = new KeyBinding("veinminer.key.enable", Keyboard.KEY_GRAVE, "veinminer.key.category");
    private static boolean statusEnabled = false;
    private  int[] count = {0, 0, 0};
    private final int PACKET_COUNT = 5;

    public ActivateMinerKeybindManager() {
        ClientRegistry.registerKeyBinding(keyBinding);
        FMLCommonHandler.instance().bus().register(this);
    }

    @SuppressWarnings("UnusedDeclaration")
    @SubscribeEvent
    public void KeyEvent(InputEvent.KeyInputEvent event) {
        boolean sendPacket = false;

        int mode = VeinMiner.instance.currentMode;
        boolean pressed = keyBinding.isKeyDown();
        if(mode == PreferredMode.DISABLED) {
            statusEnabled = false;
            if(count[0] < PACKET_COUNT) {
                sendPacket = true; // If enabled when changing, notify server that it is disabled.

                count[0]++;
                count[1] = 0;
                count[2] = 0;
            }
        }
        else if ((pressed &&  mode == PreferredMode.PRESSED) || (!pressed && mode == PreferredMode.RELEASED) && !statusEnabled) {
            statusEnabled = true;
            if(count[1] < PACKET_COUNT) {
                sendPacket = true; // If enabled when changing, notify server that it is disabled.

                count[0] = 0;
                count[1]++;
                count[2] = 0;
            }
        } else if (((pressed &&  mode == PreferredMode.RELEASED) || (!pressed && mode == PreferredMode.PRESSED)) && statusEnabled) {
            statusEnabled = false;
            if(count[2] < PACKET_COUNT) {
                sendPacket = true; // If enabled when changing, notify server that it is disabled.

                count[0] = 0;
                count[1] = 0;
                count[2]++;
            }
        }
        if (sendPacket) {
            PacketMinerActivate packet = new PacketMinerActivate(statusEnabled);
            VeinMiner.instance.networkWrapper.sendToServer(packet);
        }
    }

    public void resetCount() {
        count[0] = 0;
        count[1] = 0;
        count[2] = 0;
    }
}
