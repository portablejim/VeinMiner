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

package portablejim.veinminer.util;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Stores the block ID and metadata of a block. Allows having no metadata value
 * to match blocks with any metadata value.
 *
 * This has been mostly copied from Bskprs Block ID he has in his bspkrsCore
 * mod, but some parts have been re-written.
 */

public class BlockID implements Comparable<BlockID>
{
    public String name;
    public int metadata;
    public IBlockState state = null;
    
    public BlockID(String fullDescription) {
        int preMeta = -1;
        Pattern p = Pattern.compile("([^/]+)(?:/([-]?\\d{1,2}))?");
        Matcher m = p.matcher(fullDescription);

        if(m.matches()) {
            name = m.group(1);
            if(m.group(2) != null) {
                try {
                    preMeta = Integer.parseInt(m.group(2));
                }
                catch (NumberFormatException e) {
                    preMeta = -1;
                }
            }
        }
        else {
            name = "";
        }
        metadata = preMeta >= -1 ? preMeta : -1;
    }

    public BlockID(String name, int meta) {
        this.name = name;
        this.metadata =  meta < -1 || meta == OreDictionary.WILDCARD_VALUE ? -1 : meta;
    }

    public BlockID(World world, BlockPos position) {
        this(world.getBlockState(position));
    }

    public BlockID(IBlockState state) {
        this((String)Block.blockRegistry.getNameForObject(state.getBlock()), state.getBlock().getMetaFromState(state));
        this.state = state;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        
        if (!(obj instanceof BlockID))
            return false;

        BlockID o = (BlockID) obj;
        return name.equals(o.name) && metadata == o.metadata;
    }

    /**
     * Compare the objects using the metadata value of -1 to be a wildcard that matches all.
     * @param obj The object to compare to this one
     * @return Whether this object and obj are equal, using the wildcard value.
     */
    public boolean wildcardEquals(Object obj)
    {
        if (this == obj)
            return true;

        if (!(obj instanceof BlockID))
            return false;

        BlockID o = (BlockID) obj;
        if (o.metadata == -1 || metadata == -1)
            return this.equals(obj);
        else
            return name.equals(o.name) && metadata == o.metadata;
    }
    
    @Override
    public int hashCode()
    {
        return (this.name.hashCode() << 6) + this.metadata;
    }
    
    @Override
    public String toString()
    {
        return (metadata == -1 ? name + "" : name + "/" + metadata);
    }

    @Override
    public int compareTo(BlockID blockID) {
        if(name != null && !name.equals(blockID.name)) {
            int result = name.compareTo(blockID.name);
            if(result > 0) return 1;
            else if (result < 0) return -1;
            return 0;
        }
        else if(metadata < blockID.metadata) {
            return -1;
        }
        else if(metadata > blockID.metadata) {
            return 1;
        }
        else {
            return 0;
        }
    }
}
