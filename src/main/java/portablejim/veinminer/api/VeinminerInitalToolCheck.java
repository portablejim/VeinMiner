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

import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Event to configure or disallow the Veinmining of all tools. This takes place before item/block filtering
 */

public class VeinminerInitalToolCheck extends Event {
    public Permission allowVeinminerStart;
    public final EntityPlayer player;
    public final int radiusLimitConfig;
    public final int blockLimitConfig;
    public final Point breakPont;
    public int radiusLimit;
    public int blockLimit;

    public VeinminerInitalToolCheck(EntityPlayer player, Point breakPont, int radiusLimit, int blockLimit, int radiusLimitConfig, int blockLimitConfig) {
        this.breakPont = breakPont;
        this.allowVeinminerStart = Permission.ALLOW;
        this.player = player;
        this.radiusLimitConfig = radiusLimitConfig;
        this.blockLimitConfig = blockLimitConfig;
        this.radiusLimit = radiusLimit;
        this.blockLimit = blockLimit;
    }
}
