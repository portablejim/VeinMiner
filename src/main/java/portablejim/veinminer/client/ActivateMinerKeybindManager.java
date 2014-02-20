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

package portablejim.veinminer.client;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import portablejim.veinminer.VeinMiner;
import portablejim.veinminer.network.packet.PacketMinerActivate;

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
    public void KeyEvent(InputEvent.KeyInputEvent event) {
        boolean sendPacket = false;
        if (keyBinding.getIsKeyPressed() && !statusEnabled) {
            statusEnabled = true;
            sendPacket = true;
        } else if (!keyBinding.getIsKeyPressed() && statusEnabled) {
            statusEnabled = false;
            sendPacket = true;
        }
        if (sendPacket) {
            PacketMinerActivate packet = new PacketMinerActivate(statusEnabled);
            VeinMiner.instance.channelHandler.sendToServer(packet);
        }
    }
}
