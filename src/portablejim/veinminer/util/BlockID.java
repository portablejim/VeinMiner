package portablejim.veinminer.util;

import net.minecraft.block.Block;
import net.minecraft.world.World;

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
        
        id = CommonUtils.parseInt(s[0].trim(), defaultVal);
        if (s.length < 2)
            metadata = -1;
        else
            metadata = CommonUtils.parseInt(s[1].trim(), -1);
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
        this(format, ",");
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
        int result = 23;
        result = HashCodeUtil.hash(result, id);
        result = HashCodeUtil.hash(result, metadata);
        return result;
    }
    
    @Override
    public String toString()
    {
        return (metadata == -1 ? id + "" : id + ", " + metadata);
    }
}
