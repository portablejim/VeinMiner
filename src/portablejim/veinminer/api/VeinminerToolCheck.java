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
import net.minecraftforge.event.Event;

/**
 * Event to check whether the current equipped tool can Veinmine.
 * allowTool == Permission.FORCE_ALLOW: Permit tool.
 * allowTool == Permission.ALLOW: Permit tool.
 * allowTool == Permission.DENY: Default. Allow tool if player can mine stone.
 * allowTool == Permission.FORCE_DENY: Don't allow tool at all.
 */

public class VeinminerToolCheck extends Event {
    public Permission allowTool;
    public final EntityPlayerMP player;

    public VeinminerToolCheck(EntityPlayerMP player) {
        allowTool = Permission.DENY;
        this.player = player;
    }
}
