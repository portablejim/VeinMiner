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
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores the block ID and metadata of a block. Allows having no metadata value
 * to match blocks with any metadata value.
 *
 * This has been mostly copied from Bskprs Block ID he has in his bspkrsCore
 * mod, but some parts have been re-written.
 */

public class BlockID
{
    public int id, metadata;
    
    public BlockID(int id, int metadata)
    {
        this.id = id;
        this.metadata = metadata;
    }
    
    /**
     * String must be one or two integer values delimited with the delimiter string. ex "17|0","|"
     * 
     * @param format Two integer values delimited by the delimiter character
     * @param delimiter Character to split the two numbers declared in format
     */
    public BlockID(String format, String delimiter, int defaultVal)
    {
        String[] s = format.split(delimiter);

        try {
            id = Integer.parseInt(s[0]);
        }
        catch (NumberFormatException e) {
            id = defaultVal;
        }

        if (s.length < 2)
            metadata = -1;
        else {
            try {
                metadata = Integer.parseInt(s[1]);
            }
            catch (NumberFormatException e) {
                metadata = -1;
            }
        }
    }
    
    public BlockID(String format, String delimiter)
    {
        this(format, delimiter, 0);
    }
    
    /**
     * String must be one or two integer values delimited with a colon. ex "17:0"
     * 
     * @param format Two integers delimetered with a ':'
     */
    public BlockID(String format)
    {
        this(format, ":");
    }
    
    public BlockID(Block block, int metadata)
    {
        this(block.blockID, metadata);
    }
    
    public BlockID(Block block)
    {
        this(block.blockID, -1);
    }
    
    public BlockID(int id)
    {
        this(id, -1);
    }
    
    public BlockID(World world, int x, int y, int z)
    {
        this(world, x, y, z, world.getBlockMetadata(x, y, z));
    }

    public BlockID(World world, int x, int y, int z, int metadata)
    {
        this(world.getBlockId(x, y, z), metadata);
    }
    
    @Override
    public BlockID clone() throws CloneNotSupportedException {
        super.clone();
        return new BlockID(id, metadata);
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        
        if (!(obj instanceof BlockID))
            return false;
        
        BlockID o = (BlockID) obj;
        if (o.metadata == -1 || metadata == -1)
            return id == o.id;
        else
            return id == o.id && metadata == o.metadata;
    }
    
    @Override
    public int hashCode()
    {
        return (this.id << 8) + this.metadata;
    }
    
    @Override
    public String toString()
    {
        return (metadata == -1 ? id + "" : id + ":" + metadata);
    }
}
