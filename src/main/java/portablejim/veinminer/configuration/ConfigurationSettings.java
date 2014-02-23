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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import portablejim.veinminer.api.ToolType;
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
        //noinspection unchecked
        toolIds = new Set[ToolType.values().length];
        for (ToolType tool : ToolType.values()) {
            toolIds[tool.ordinal()] = new HashSet<String>();
        }
        //noinspection unchecked
        blockWhitelist = new ArrayList[ToolType.values().length];
        autoDetectBlocksToggle = new boolean[ToolType.values().length];
        //noinspection unchecked
        autoDetectBlocksList = new HashSet[ToolType.values().length];
        for (ToolType tool : ToolType.values()) {
            blockWhitelist[tool.ordinal()] = new ArrayList<BlockID>();
            autoDetectBlocksToggle[tool.ordinal()] = false;
            autoDetectBlocksList[tool.ordinal()] = new HashSet<String>();
        }
        blockCongruenceList = new ArrayList<Set<BlockID>>();
        blockCongruenceMap = new HashMap<BlockID, Integer>();

        parseConfigValues();
    }

    private void parseConfigValues() {
        for(ToolType toolType : ToolType.values()) {
            setAutodetectBlocksToggle(toolType, configValues.toolConfig.get(toolType).autodetectToggle.value);
            setAutodetectBlocksList(toolType, configValues.toolConfig.get(toolType).autodetectList.value);
            setBlockWhitelist(toolType, configValues.toolConfig.get(toolType).blockIdList.value);
            setToolIds(toolType, configValues.toolConfig.get(toolType).toolIdList.value);
        }

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

    private boolean[] autoDetectBlocksToggle;
    private HashSet<String>[] autoDetectBlocksList;

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
    private Set<String>[] toolIds;

    private int blockLimit;

    private int radiusLimit;

    private int blocksPerTick;

    private boolean enableAllBlocks;

    private boolean enableAllTools;

    private int preferredMode;

    void setAutodetectBlocksToggle(ToolType tool, boolean value) {
        autoDetectBlocksToggle[tool.ordinal()] = value;
    }

    public boolean getAutodetectBlocksToggle(ToolType tool) {
        return autoDetectBlocksToggle[tool.ordinal()];
    }

    void setAutodetectBlocksList(ToolType tool, String list) {
        String[] parts = list.split(",");

        for(String part : parts) {
            if(!part.isEmpty()) {
                autoDetectBlocksList[tool.ordinal()].add(part);
            }
        }
    }

    public Set<String> getAutodetectBlocksList(ToolType tool) {
        return autoDetectBlocksList[tool.ordinal()];
    }

    /**
     * Add the blocks mentioned in whitelist to the block whitelist for the specified tool.
     * @param whitelist String of blocks with metadata value to add to whitelist. Format is
     *                  'modName:block_name/metadata'. 'minecraft' is the modName for vanilla.
     *                  Use ',' to separate blocks in whitelist.
     *                  See {@link ConfigurationValues}.
     * @param tool Tool to set the whitelist for.
     */
    void setBlockWhitelist(ToolType tool, String whitelist) {
        String[] blocksString = whitelist.split(",");

        for (String blockString : blocksString ) {
            BlockID newBlock = new BlockID(blockString);
            if(!newBlock.name.isEmpty()) {
                blockWhitelist[tool.ordinal()].add(newBlock);
            }
        }
    }

    public void addBlockToWhitelist(ToolType tool, BlockID block) {
        if(!block.name.isEmpty()) {
            BlockID testBlock = new BlockID(block.name, -1);
            if(!blockWhitelist[tool.ordinal()].contains(block) && !blockWhitelist[tool.ordinal()].contains(testBlock)) {
                block.metadata = block.metadata == OreDictionary.WILDCARD_VALUE ? -1 : block.metadata;
                blockWhitelist[tool.ordinal()].add(block);
            }
        }
    }

    public void removeBlockFromWhitelist(ToolType tool, BlockID block) {
        blockWhitelist[tool.ordinal()].remove(block);
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
     *                       'modName:block_name/metadata'. 'minecraft' is the modName for vanilla.
     *                       Use ',' to separate blocks in whitelist.
     *                       See {@link ConfigurationValues}
     */
    void setBlockCongruenceList(String congruenceList) {
        String[] blocksString = congruenceList.split(",");

        for (String congruentBlocks : blocksString) {
            int newId = blockCongruenceList.size();
            //List<BlockID> newCongruentBlocks = new ArrayList<BlockID>();
            Set<BlockID> newCongruentBlocks = new HashSet<BlockID>();

            if (!congruentBlocks.contains("=")) {
                 continue;
            }

            for (String blockString : congruentBlocks.split("=")) {
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
            congruenceGroups.add(Joiner.on('=').join(group));
        }
        return Joiner.on(',').join(congruenceGroups);
    }

    @SuppressWarnings("UnusedDeclaration")
    public Set<BlockID> getCongruentBlocks(BlockID targetBlock) {
        int listId = blockCongruenceMap.get(targetBlock);
        return blockCongruenceList.get(listId);
    }

    public boolean areBlocksCongruent(BlockID block1, BlockID block2) {
        BlockID block1NoMeta = new BlockID(block1.name, block1.metadata);
        block1NoMeta.metadata = -1;
        BlockID block2NoMeta = new BlockID(block2.name, block2.metadata);
        block2NoMeta.metadata = -1;

        int targetBlock1;
        int targetBlock2;

        if(blockCongruenceMap.containsKey(block1) || blockCongruenceMap.containsKey(block1NoMeta)) {
            targetBlock1 = blockCongruenceMap.containsKey(block1) ? blockCongruenceMap.get(block1) : blockCongruenceMap.get(block1NoMeta);
        }
        else {
            return false;
        }
        if(blockCongruenceMap.containsKey(block2) || blockCongruenceMap.containsKey(block2NoMeta)) {
            targetBlock2 = blockCongruenceMap.containsKey(block2) ? blockCongruenceMap.get(block2) : blockCongruenceMap.get(block2NoMeta);
        }
        else {
            return false;
        }

        return targetBlock1 == targetBlock2;
    }

    void setToolIds(ToolType tool, String ids) {
        String[] toolsString = ids.split(",");

        for (String nameString : toolsString) {
            toolIds[tool.ordinal()].add(nameString);
        }
    }

    public void addTool(ToolType tool, String name) {
        toolIds[tool.ordinal()].add(name);
    }

    public void removeTool(ToolType tool, String name) {
        toolIds[tool.ordinal()].remove(name);
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
        return tool == null || this.toolIds[type.ordinal()].contains(Item.itemRegistry.getNameForObject(tool.getItem()));
    }

    /**
     * Sets the preferred mode to the modeString if valid or fallback if modeString is not valid
     * @param modeString one of 'auto', 'sneak', 'no_sneak'
     * @param fallback one of 'auto', 'sneak', 'no_sneak'
     * @return If modestring is valid
     */
    boolean setPreferredMode(String modeString, String fallback) {
        if("disabled".equals(modeString)) {
            preferredMode = PreferredMode.DISABLED;
            return true;
        }
        else if("pressed".equals(modeString)) {
            preferredMode = PreferredMode.PRESSED;
            return true;
        }
        else if("released".equals(modeString)) {
            preferredMode = PreferredMode.RELEASED;
            return true;
        }

        // No valid option.
        // Set to fallback
        if(fallback != null) {
            setPreferredMode(fallback, null);
        }
        return false;
    }

    public void setPreferredMode(int mode) {
        preferredMode = mode;
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
            case PreferredMode.DISABLED:
                return "disabled";
            case PreferredMode.PRESSED:
                return "pressed";
            case PreferredMode.RELEASED:
                return "released";
        }
        return "";
    }

    public void saveConfigs() {
        for(ToolType toolType : ToolType.values()) {
            configValues.toolConfig.get(toolType).blockIdList.value = getBlockWhitelist(toolType);
            configValues.toolConfig.get(toolType).toolIdList.value = getToolIds(toolType);
        }

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
