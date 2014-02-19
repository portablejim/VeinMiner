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

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * Event to check if Veinmine-ing should start, even though tryHarvestBlock returned false.
 * allowVeinminer == Permission.FORCE_ALLOW || allowVeinMiner.ALLOW: Allow Veinminer to start.
 * allowVeinminer == Permission.FORCE_DENY || allowVeinMiner.DENY: Don't allow Veinminer to start.
 */

public class VeinminerHarvestFailedCheck extends Event {
    public Permission allowContinue;
    public final EntityPlayerMP player;
    public final int blockId;
    public final int blockMetadata;

    public VeinminerHarvestFailedCheck(EntityPlayerMP player, int id, int metadata) {
        allowContinue = Permission.DENY;
        this.player = player;
        this.blockId = id;
        this.blockMetadata = metadata;
    }
}
