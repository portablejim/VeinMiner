package portablejim.veinminer.configuration;

/**
 * Created with IntelliJ IDEA.
 * User: james
 * Date: 14/06/13
 * Time: 3:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConfigurationValues {

    public String PICKAXE_BLOCK_ID_LIST;
    public static final String PICKAKE_BLOCK_ID_LIST_DEFAULT = "14,15,16,21,24:0,21,24:0,24:1,24:2,48,56,73,89,129";
    public static final String PICKAXE_BLOCK_ID_LIST_CONFIGNAME = "blockList.pickaxe";
    public static final String PICKAXE_BLOCK_ID_LIST_DESCRIPTION = "Block ids to auto-mine when using a configured pickaxe. [default: '14,15,16,21,24:0,21,24:0,24:1,24:2,48,56,73,89,129']";

    public String SHOVEL_BLOCK_ID_LIST;
    public static final String SHOVEL_BLOCK_ID_LIST_DEFAULT = "82";
    public static final String SHOVEL_BLOCK_ID_LIST_CONFIGNAME = "blockList.shovel";
    public static final String SHOVEL_BLOCK_ID_LIST_DESCRIPTION = "Block ids to auto-mine when using a configured shovel. [default: '82']";

    public String AXE_BLOCK_ID_LIST;
    public static final String AXE_BLOCK_ID_LIST_DEFAULT = "17:0,17:1,17:2,17:3,17:3,18:0,18:1,18:2,18:3";
    public static final String AXE_BLOCK_ID_LIST_CONFIGNAME = "blockList.axe";
    public static final String AXE_BLOCK_ID_LIST_DESCRIPTION = "Block ids to auto-mine when using a configured axe. [default '17:0,17:1,17:2,17:3,17:3,18:0,18:1,18:2,18:3']";

    public String BLOCK_EQUIVALENCY_LIST;
    public static final String BLOCK_EQUIVALENCY_LIST_DEFAULT = "73-74";
    public static final String BLOCK_EQUIVALENCY_LIST_CONFIGNAME = "equalBlocks";
    public static final String BLOCK_EQUIVALENCY_LIST_DESCRIPTION = "Block IDs to consider equivalent. Separate blocks with '-'. Separate groups with ',' [default: '73-74']";

    public int BLOCK_LIMIT;
    public static final int BLOCK_LIMIT_DEFAULT = 800;
    public static final String BLOCK_LIMIT_CONFIGNAME = "limit.blocks";
    public static final String BLOCK_LIMIT_DESCRIPTION = "Limit of blocks to be destroyed at once. Use -1 for infinite. [range: -1 to 2147483647, default: '800']";

    public int RADIUS_LIMIT;
    public static final int RADIUS_LIMIT_DEFAULT = 6;
    public static final String RADIUS_LIMIT_CONFIGNAME = "limit.radius";
    public static final String RADIUS_LIMIT_DESCRIPTION = "Maximum distance from the first block to search for blocks to destroy. [range: -1 to 1000, default: '6']";

    public int BLOCKS_PER_TICK;
    public static final int BLOCKS_PER_TICK_DEFAULT = 50;
    public static final String BLOCKS_PER_TICK_CONFIGNAME = "limit.blocksPerTick";
    public static final String BLOCKS_PER_TICK_DESCRIPTION = "Maximum number of blocks to be removed per game tick (1/20 seconds). Using a low number will keep the game from getting huge performance drops but also decreases the speed at which blocks are destroyed. [range: 1 ~ 1000, default: 50]";
}
