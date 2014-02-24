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

package portablejim.veinminer.lib;

import net.minecraft.block.Block;
import portablejim.veinminer.util.BlockID;

/**
 * Provides extra functions dealing with blocks.
 */
public class BlockLib {

    /**
     * Uses the hooks from Block.getPickBlock() to check if the first and
     * second blocks give different metadata.
     * @param first BlockID of the first block.
     * @param second BlockID of the secound block.
     * @return If pick block on both blocks are the same.
     */
    public static boolean arePickBlockEqual(BlockID first, BlockID second) {
        if(first == null || second == null) {
            return false;
        }

        Block firstBlock = Block.getBlockFromName(first.name);
        Block secondBlock = Block.getBlockFromName(second.name);

        int firstResultMeta = firstBlock.damageDropped(first.metadata);
        int secondResultMeta = secondBlock.damageDropped(second.metadata);

        return first.name.equals(second.name) && firstResultMeta == secondResultMeta;
    }


}
