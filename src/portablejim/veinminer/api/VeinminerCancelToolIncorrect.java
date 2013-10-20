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

package portablejim.veinminer.api;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.Event;

/**
 * Cancelable event that fires when Veinminer is going to stop because the
 * player has an empty hand (no tool) and the override to allow all tools has
 * not been set.
 *
 * Cancel to regard the current tool as allowed.
 */

@Cancelable
public class VeinminerCancelToolIncorrect extends Event {
    public final EntityPlayerMP player;

    public VeinminerCancelToolIncorrect(EntityPlayerMP player) {
        super();
        this.player = player;
    }
}
