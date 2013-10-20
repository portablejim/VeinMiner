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
 * Cancelable event that fires when VeinMiner is about to cancel an event
 * because tryHarvestBlock returned false;
 *
 * Cancel this event to allow VeinMiner to continue.
 */

@Cancelable
public class VeinminerCancelHarvest extends Event {
    public final EntityPlayerMP player;
    public final int blockId;
    public final int blockMetadata;

    public VeinminerCancelHarvest(EntityPlayerMP player, int blockId, int blockMetadata) {
        super();
        this.player = player;
        this.blockId = blockId;
        this.blockMetadata = blockMetadata;
    }
}
