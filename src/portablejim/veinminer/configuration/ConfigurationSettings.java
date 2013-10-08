package portablejim.veinminer.configuration;

import net.minecraft.item.ItemStack;
import portablejim.veinminer.util.BlockID;

import java.io.File;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: james
 * Date: 12/06/13
 * Time: 9:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConfigurationSettings {

    ConfigurationValues configValues;

    public ConfigurationSettings(ConfigurationValues configValues) {
        this.configValues = configValues;
        toolIds = new Set[ToolType.values().length];
        for (ToolType tool : ToolType.values()) {
            toolIds[tool.ordinal()] = new HashSet<Integer>();
        }
        blockWhitelist = new ArrayList[ToolType.values().length];
        for (ToolType tool : ToolType.values()) {
            blockWhitelist[tool.ordinal()] = new ArrayList<BlockID>();
        }
        blockCongruenceList = new ArrayList<Set<BlockID>>();
        blockCongruenceMap = new HashMap<BlockID, Integer>();

        parseConfigValues();
    }

    private void parseConfigValues() {
        setBlockWhitelist(ToolType.AXE, configValues.AXE_BLOCK_ID_LIST);
        setBlockWhitelist(ToolType.PICKAXE, configValues.PICKAXE_BLOCK_ID_LIST);
        setBlockWhitelist(ToolType.SHOVEL, configValues.SHOVEL_BLOCK_ID_LIST);
        setToolIds(ToolType.AXE, configValues.AXE_ID_LIST);
        setToolIds(ToolType.PICKAXE, configValues.PICKAXE_ID_LIST);
        setToolIds(ToolType.SHOVEL, configValues.SHOVEL_ID_LIST);

        setBlockLimit(configValues.BLOCK_LIMIT);
        setBlocksPerTick(configValues.BLOCKS_PER_TICK);
        setRadiusLimit(configValues.RADIUS_LIMIT);

        setBlockCongruenceList(configValues.BLOCK_EQUIVALENCY_LIST);

        setEnableAllBlocks(configValues.ENABLE_ALL_BLOCKS);
        setEnableAllTools(configValues.ENABLE_ALL_TOOLS);
    }

    public boolean getEnableAllBlocks() {
        return enableAllBlocks;
    }

    public void setEnableAllBlocks(boolean enableAllBlocks) {
        this.enableAllBlocks = enableAllBlocks;
    }

    public boolean getEnableAllTools() {
        return enableAllTools;
    }

    public void setEnableAllTools(boolean enableAllTools) {
        this.enableAllTools = enableAllTools;
    }

    public enum ToolType { PICKAXE, SHOVEL, AXE  }

    /**
     * List of block IDs to whitelist for each tool.
     */
    private List<BlockID>[] blockWhitelist;

    /**
     * Groups congruent block IDs together in an array.
     * Congruent blocks are blocks that should be treated as the same block.
     */
    private ArrayList<Set<BlockID>> blockCongruenceList;

    /**
     * A map to easily get the list of congruent block IDS for a specified block ID.
     */
    private HashMap<BlockID, Integer> blockCongruenceMap;

    /**
     * The items specified as each tool.
     */
    private Set<Integer>[] toolIds;

    private int blockLimit;

    private int radiusLimit;

    private int blocksPerTick;

    private boolean enableAllBlocks;

    private boolean enableAllTools;

    /**
     * Add the blocks mentioned in whitelist to the block whitelist for the specified tool.
     * @param whitelist String of blocks with metadata value to add to whitelist. Format is ':' to separate block id and
     *                  metadata and ',' to separate blocks. e.g. "[block id]:[metadata],[block id]".
     *                  See {@link ConfigurationValues}.
     * @param tool
     */
    public void setBlockWhitelist(ToolType tool, String whitelist) {
        String[] blocksString = whitelist.split(",");

        for (String blockString : blocksString ) {
            BlockID newBlock = new BlockID(blockString, ":");
            blockWhitelist[tool.ordinal()].add(newBlock);
        }
    }

    public void clearBlockWhitelist() {
        for (ToolType tool : ToolType.values()) {
            blockWhitelist[tool.ordinal()].clear();
        }
    }

    public List<BlockID> getBlockWhitelist(ToolType tool) {
        return blockWhitelist[tool.ordinal()];
    }

    public boolean whiteListHasBlockId(ToolType tool, BlockID targetBlock) {
        return blockWhitelist[tool.ordinal()].contains(targetBlock);
    }

    /**
     * Add groups of blocks mentioned in congruenceList to a set of blocks to be considered equal.
     * @param congruenceList String of groups of blocks with metadata value to be added to congruence list. Format is
     *                       ':' to separate block id and metadata, '-' to separate blocks in groups and ',' to separate
     *                       groups. e.g. "[block id]:[metadata]-[block id];[block id]-[block id]-[block id]".
     *                       See {@link ConfigurationValues}
     */
    public void setBlockCongruenceList(String congruenceList) {
        String[] blocksString = congruenceList.split(",");

        for (String congruentBlocks : blocksString) {
            int newId = blockCongruenceList.size();
            //List<BlockID> newCongruentBlocks = new ArrayList<BlockID>();
            Set<BlockID> newCongruentBlocks = new HashSet<BlockID>();

            if (!congruentBlocks.contains("-")) {
                 continue;
            }

            for (String blockString : congruentBlocks.split("-")) {
                BlockID newBlockId = new BlockID(blockString);

                newCongruentBlocks.add(newBlockId);

                if (blockCongruenceMap.containsKey(newBlockId)) {
                    newId = blockCongruenceMap.get(newBlockId);
                }

                blockCongruenceMap.put(newBlockId, newId);
            }

            newCongruentBlocks.addAll(newCongruentBlocks);
            if(newId < blockCongruenceList.size()) {
                blockCongruenceList.get(newId).addAll(newCongruentBlocks);
            }
            else {
                blockCongruenceList.add(newId, newCongruentBlocks);
            }
        }
    }

    public ArrayList<Set<BlockID>> getBlockCongruenceList() {
        return blockCongruenceList;
    }

    public HashMap<BlockID, Integer> getBlockCongruenceMap() {
        return  blockCongruenceMap;
    }

    public Set<BlockID> getCongruentBlocks(BlockID targetBlock) {
        int listId = blockCongruenceMap.get(targetBlock);
        return blockCongruenceList.get(listId);
    }

    public boolean areBlocksCongruent(BlockID block1, BlockID block2) {

        if (blockCongruenceMap.containsKey(block1) && blockCongruenceMap.containsKey(block2)) {
            return  blockCongruenceMap.get(block1) == blockCongruenceMap.get(block2);
        }

        return false;
    }

    public void setToolIds(ToolType tool, String ids) {
        String[] toolsString = ids.split(",");

        for (String idString : toolsString) {
            int newToolId;

            try {
                newToolId = Integer.parseInt(idString);
            }
            catch (NumberFormatException e) {
                // Invalid, skip value
                continue;
            }

            toolIds[tool.ordinal()].add(newToolId);
        }
    }

    public void clearToolIds() {
        for (ToolType tool : ToolType.values()) {

        }
    }

    public Set<Integer>[] getToolIds() {
        return toolIds;
    }

    public int getBlockLimit() {
        return blockLimit;
    }

    public void setBlockLimit(int blockLimit) {
        this.blockLimit = blockLimit;
    }

    public int getRadiusLimit() {
        return radiusLimit;
    }

    public void setRadiusLimit(int radiusLimit) {
        if (radiusLimit < -1) {
            radiusLimit = -1;
        }
        else if (radiusLimit > 1000) {
            radiusLimit = 100;
        }

        this.radiusLimit = radiusLimit;
    }

    public int getBlocksPerTick() {
        return blocksPerTick;
    }

    public void setBlocksPerTick(int blocksPerTick) {
        if (blocksPerTick < -1) {
            blocksPerTick = -1;
        }
        else if (blocksPerTick > 1000) {
            blocksPerTick = 100;
        }

        this.blocksPerTick = blocksPerTick;
    }

    public boolean toolIsOfType(ItemStack tool, ToolType type) {
        return this.toolIds[type.ordinal()].contains(tool.itemID);
    }
}
