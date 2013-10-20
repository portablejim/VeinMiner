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

package portablejim.veinminer.configuration;

import com.google.common.base.Joiner;
import net.minecraft.item.ItemStack;
import portablejim.veinminer.util.BlockID;
import portablejim.veinminer.util.PreferredMode;

import java.util.*;

/**
 * Class to manage the config settings. It takes the raw values from
 * ConfigurationValues and stores the settings in much more useful types
 * and provides methods to retrieve the settings.
 */

public class ConfigurationSettings {

    private ConfigurationValues configValues;

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

        setPreferredMode(configValues.CLIENT_PREFERRED_MODE, ConfigurationValues.CLIENT_PREFERRED_MODE_DEFAULT);
    }

    public boolean getEnableAllBlocks() {
        return enableAllBlocks;
    }

    void setEnableAllBlocks(boolean enableAllBlocks) {
        this.enableAllBlocks = enableAllBlocks;
    }

    public boolean getEnableAllTools() {
        return enableAllTools;
    }

    void setEnableAllTools(boolean enableAllTools) {
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

    private int preferredMode;

    /**
     * Add the blocks mentioned in whitelist to the block whitelist for the specified tool.
     * @param whitelist String of blocks with metadata value to add to whitelist. Format is ':' to separate block id and
     *                  metadata and ',' to separate blocks. e.g. "[block id]:[metadata],[block id]".
     *                  See {@link ConfigurationValues}.
     * @param tool Tool to set the whitelist for.
     */
    void setBlockWhitelist(ToolType tool, String whitelist) {
        String[] blocksString = whitelist.split(",");

        for (String blockString : blocksString ) {
            BlockID newBlock = new BlockID(blockString, ":");
            blockWhitelist[tool.ordinal()].add(newBlock);
        }
    }

    public void addBlockToWhitelist(ToolType tool, BlockID block) {
        blockWhitelist[tool.ordinal()].add(block);
    }

    public void removeBlockFromWhitelist(ToolType tool, BlockID block) {
        blockWhitelist[tool.ordinal()].remove(block);
    }

    public void clearBlockWhitelist() {
        for (ToolType tool : ToolType.values()) {
            blockWhitelist[tool.ordinal()].clear();
        }
    }

    public String getBlockWhitelist(ToolType tool) {
        return Joiner.on(',').join(blockWhitelist[tool.ordinal()]);
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
    void setBlockCongruenceList(String congruenceList) {
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

    public String getBlockCongruenceList() {
        ArrayList<String> congruenceGroups = new ArrayList<String>();
        for(Set<BlockID> group : blockCongruenceList) {
            congruenceGroups.add(Joiner.on('-').join(group));
        }
        return Joiner.on(',').join(congruenceGroups);
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
            return blockCongruenceMap.get(block1).equals(blockCongruenceMap.get(block2));
        }

        return false;
    }

    void setToolIds(ToolType tool, String ids) {
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

    public void addTool(ToolType tool, int id) {
        toolIds[tool.ordinal()].add(id);
    }

    public void removeTool(ToolType tool, int id) {
        toolIds[tool.ordinal()].remove(id);
    }

    public String getToolIds(ToolType tool) {
        return Joiner.on(',').join(toolIds[tool.ordinal()]);
    }

    public int getBlockLimit() {
        return blockLimit;
    }

    public void setBlockLimit(int blockLimit) {
        if(blockLimit < -1) {
            radiusLimit = -1;
        }
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
        return tool == null || this.toolIds[type.ordinal()].contains(tool.itemID);
    }

    /**
     * Sets the preferred mode to the modeString if valid or fallback if modeString is not valid
     * @param modeString one of 'auto', 'sneak', 'no_sneak'
     * @param fallback one of 'auto', 'sneak', 'no_sneak'
     * @return If modestring is valid
     */
    boolean setPreferredMode(String modeString, String fallback) {
        if("auto".equals(modeString)) {
            preferredMode = PreferredMode.AUTO;
            return true;
        }
        else if("sneak".equals(modeString)) {
            preferredMode = PreferredMode.SNEAK;
            return true;
        }
        else if("no_sneak".equals(modeString)) {
            preferredMode = PreferredMode.NO_SNEAK;
            return true;
        }

        // No valid option.
        // Set to fallback
        if(fallback != null) {
            setPreferredMode(fallback, null);
        }
        return false;
    }

    /**
     * Returns the preferred mode.
     * @return One of PreferredMode.AUTO, PreferredMode.SNEAK, PreferredMode.NO_SNEAK
     */
    public int getPreferredMode() {
        return preferredMode;
    }

    public String getPreferredModeString() {
        switch (preferredMode) {
            case PreferredMode.AUTO:
                return "auto";
            case PreferredMode.SNEAK:
                return "sneak";
            case PreferredMode.NO_SNEAK:
                return "no_sneak";
        }
        return "";
    }

    public void saveConfigs() {
        configValues.AXE_BLOCK_ID_LIST = getBlockWhitelist(ToolType.AXE);
        configValues.PICKAXE_BLOCK_ID_LIST = getBlockWhitelist(ToolType.PICKAXE);
        configValues.SHOVEL_BLOCK_ID_LIST = getBlockWhitelist(ToolType.SHOVEL);
        configValues.AXE_ID_LIST = getToolIds(ToolType.AXE);
        configValues.PICKAXE_ID_LIST = getToolIds(ToolType.PICKAXE);
        configValues.SHOVEL_ID_LIST = getToolIds(ToolType.SHOVEL);

        configValues.BLOCK_LIMIT = getBlockLimit();
        configValues.BLOCKS_PER_TICK = getBlocksPerTick();
        configValues.RADIUS_LIMIT = getRadiusLimit();

        configValues.BLOCK_EQUIVALENCY_LIST = getBlockCongruenceList();

        configValues.ENABLE_ALL_BLOCKS = getEnableAllBlocks();
        configValues.ENABLE_ALL_TOOLS = getEnableAllTools();

        configValues.CLIENT_PREFERRED_MODE = getPreferredModeString();

        configValues.saveConfigFile();
    }
}
