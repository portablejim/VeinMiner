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

import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.network.PacketDispatcher;
import net.minecraft.client.settings.KeyBinding;
import portablejim.veinminer.lib.ModInfo;
import portablejim.veinminer.network.PacketTypeHandler;
import portablejim.veinminer.network.packet.PacketClientSettings;

import java.util.EnumSet;

/**
 * Client only. Adds a keybind that sends packets to the server when the
 * keybind is pressed and when it is unpressed.
 */

public class ActivateMinerKeybind extends KeyBinding {
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
