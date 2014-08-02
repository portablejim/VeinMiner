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
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import portablejim.veinminer.api.ToolType;
import portablejim.veinminer.configuration.json.ToolStruct;
import portablejim.veinminer.util.BlockID;
import portablejim.veinminer.util.PreferredMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Class to manage the config settings. It takes the raw values from
 * ConfigurationValues and stores the settings in much more useful types
 * and provides methods to retrieve the settings.
 */

public class ConfigurationSettings {

    private ConfigurationValues configValues;

    public ConfigurationSettings(ConfigurationValues configValues) {
        this.configValues = configValues;
        autoDetectBlocksToggle = new boolean[ToolType.values().length];
        //noinspection unchecked
        autoDetectBlocksList = new HashSet[ToolType.values().length];
        for (ToolType tool : ToolType.values()) {
            autoDetectBlocksToggle[tool.ordinal()] = false;
            autoDetectBlocksList[tool.ordinal()] = new HashSet<String>();
        }
        blockCongruenceList = new ArrayList<Set<BlockID>>();
        blockCongruenceMap = new HashMap<BlockID, Integer>();

        toolsAndBlocks = new HashMap<String, Tool>();

        parseConfigValues();
    }

    private void parseConfigValues() {
        try {
            JsonObject toolsJson = configValues.toolsAndBlocks.getAsJsonObject().getAsJsonObject("tools");
            for(Map.Entry<String, JsonElement> entry: toolsJson.entrySet()) {
                String toolName = entry.getKey();
                Gson gson = new Gson();
                ToolStruct toolInstance = gson.fromJson(entry.getValue().getAsJsonObject(), ToolStruct.class);
                toolsAndBlocks.put(toolName, new Tool(toolInstance));
            }
        }
        catch (Exception ignored) {

        }

        for(ToolType toolType : ToolType.values()) {
            setAutodetectBlocksToggle(toolType, configValues.toolConfig.get(toolType).autodetectToggle.value);
            setAutodetectBlocksList(toolType, configValues.toolConfig.get(toolType).autodetectList.value);
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
     * Groups congruent block IDs together in an array.
     * Congruent blocks are blocks that should be treated as the same block.
     */
    private ArrayList<Set<BlockID>> blockCongruenceList;

    /**
     * A map to easily get the list of congruent block IDS for a specified block ID.
     */
    private HashMap<BlockID, Integer> blockCongruenceMap;

    private int blockLimit;

    private int radiusLimit;

    private int blocksPerTick;

    private boolean enableAllBlocks;

    private boolean enableAllTools;

    private int preferredMode;

    private Map<String, Tool> toolsAndBlocks;

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
    void setBlockWhitelist(String tool, String whitelist) {
        String[] blocksString = whitelist.split(",");

        for (String blockString : blocksString ) {
            BlockID newBlock = new BlockID(blockString);
            if(!newBlock.name.isEmpty()) {
                addBlockToWhitelist(tool, newBlock);
            }
        }
    }

    public void addBlockToWhitelist(String tool, BlockID block) {
        if(!block.name.isEmpty()) {
            BlockID testBlock = new BlockID(block.name, -1);
            if(!toolsAndBlocks.get(tool).blocklist.contains(block) && !toolsAndBlocks.get(tool).blocklist.contains(testBlock)) {
                block.metadata = block.metadata == OreDictionary.WILDCARD_VALUE ? -1 : block.metadata;
                toolsAndBlocks.put(tool, toolsAndBlocks.get(tool).addBlock(block));
            }
        }
    }

    public void removeBlockFromWhitelist(String tool, BlockID block) {
        BlockID blockNoMeta = new BlockID(block.name, block.metadata);
        blockNoMeta.metadata = -1;
        if(toolsAndBlocks.get(tool).blocklist.contains(block)) {
            toolsAndBlocks.put(tool, toolsAndBlocks.get(tool).removeBlock(block));
        }
        else if(toolsAndBlocks.get(tool).blocklist.contains(blockNoMeta)) {
            toolsAndBlocks.put(tool, toolsAndBlocks.get(tool).removeBlock(blockNoMeta));
        }
    }

    public String getBlockWhitelist(String tool) {
        return Joiner.on(',').join(toolsAndBlocks.get(tool).blocklist);
    }

    public ArrayList<String> getBlockIDArray(String toolType) {
        ArrayList<String> output = new ArrayList<String>();
        for(BlockID blockID : toolsAndBlocks.get(toolType).blocklist) {
            output.add(blockID.toString());
        }
        return output;
    }

    public boolean whiteListHasBlockId(String tool, BlockID targetBlock) {
        BlockID targetBlockNoMeta = new BlockID(targetBlock.name, targetBlock.metadata);
        targetBlockNoMeta.metadata = -1;
        return toolsAndBlocks.get(tool).blocklist.contains(targetBlock) || toolsAndBlocks.get(tool).blocklist.contains(targetBlockNoMeta);
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

                // TODO: Ensure congruence works properly.
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

    public void addTool(String tool, String name) {
        toolsAndBlocks.put(tool, toolsAndBlocks.get(tool).addTool(name));
    }

    public void removeTool(String tool, String name) {
        toolsAndBlocks.put(tool, toolsAndBlocks.get(tool).removeTool(name));
    }

    public String getToolIds(String tool) {
        return Joiner.on(',').join(toolsAndBlocks.get(tool).toollist);
    }

    public ArrayList<String> getToolIdArray(String tool) {
        return new ArrayList<String>(toolsAndBlocks.get(tool).toollist);
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

    public boolean toolIsOfType(ItemStack tool, String type) {
        return tool == null || toolsAndBlocks.get(type).toollist.contains(Item.itemRegistry.getNameForObject(tool.getItem()));
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
        else if("sneak".equals(modeString)) {
            preferredMode = PreferredMode.SNEAK_ACTIVE;
            return true;
        }
        else if("no_sneak".equals(modeString)) {
            preferredMode = PreferredMode.SNEAK_INACTIVE;
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
            case PreferredMode.SNEAK_ACTIVE:
                return "sneak";
            case PreferredMode.SNEAK_INACTIVE:
                return "no_sneak";
        }
        return "";
    }

    public void saveConfigs() {
        configValues.toolsAndBlocks = getToolsAndBlocksJson();

        configValues.BLOCK_LIMIT = getBlockLimit();
        configValues.BLOCKS_PER_TICK = getBlocksPerTick();
        configValues.RADIUS_LIMIT = getRadiusLimit();

        configValues.BLOCK_EQUIVALENCY_LIST = getBlockCongruenceList();

        configValues.ENABLE_ALL_BLOCKS = getEnableAllBlocks();
        configValues.ENABLE_ALL_TOOLS = getEnableAllTools();

        configValues.CLIENT_PREFERRED_MODE = getPreferredModeString();

        configValues.saveConfigFile();
    }

    public JsonObject getToolsAndBlocksJson() {
        JsonObject jsonTools = new JsonObject();

        for(Map.Entry<String, Tool> toolEntry : toolsAndBlocks.entrySet()) {
            String toolName = toolEntry.getKey();
            Tool tool = toolEntry.getValue();

            JsonArray toolList = new JsonArray();
            for(String whiteListTool : tool.toollist) {
                toolList.add(new JsonPrimitive(whiteListTool));
            }
            JsonArray blockList = new JsonArray();
            for(BlockID whitelistBlock : tool.blocklist) {
                blockList.add(new JsonPrimitive(whitelistBlock.toString()));
            }

            JsonObject jsonTool = new JsonObject();
            jsonTool.add("name", new JsonPrimitive(tool.name));
            jsonTool.add("icon", new JsonPrimitive(tool.icon));
            jsonTool.add("toollist", toolList);
            jsonTool.add("blocklist", blockList);

            jsonTools.add(toolName, jsonTool);
        }

        JsonObject jsonObject = new JsonObject();
        jsonObject.add("tools", jsonTools);

        return jsonObject;
    }
}
