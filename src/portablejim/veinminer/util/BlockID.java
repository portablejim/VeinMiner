package portablejim.veinminer.util;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

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
     * @param format
     * @param delimiter
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
     * String must be one or two integer values delimited with a comma. ex "17,0"
     * 
     * @param format
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

    // Returns -1 if there is only one type of block and the metadata is 0
    private static int getMetaValue(World world, int x, int y, int z, int meta) {
        int blockId = world.getBlockId(x, y, z);
        List sameBlocks = new ArrayList();

        if(Block.blocksList[blockId] == null) {
            return meta;
        }

        Block.blocksList[blockId].getSubBlocks(blockId, CreativeTabs.tabAllSearch, sameBlocks);

        if(sameBlocks.size() == 1 && meta == 0) {
            return -1;
        }
        else {
            return meta;
        }
    }
    
    public BlockID(World world, int x, int y, int z, int metadata)
    {
        this(world.getBlockId(x, y, z), getMetaValue(world, x, y, z, metadata));
    }
    
    @Override
    public BlockID clone()
    {
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
