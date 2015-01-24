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
import portablejim.veinminer.configuration.json.ToolStruct;
import portablejim.veinminer.util.BlockID;
import portablejim.veinminer.util.PreferredMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
                if(toolInstance.name == null) toolInstance.name = toolName;
                if(toolInstance.icon == null) toolInstance.icon = "";
                if(toolInstance.blocklist == null) toolInstance.blocklist = new String[]{};
                if(toolInstance.toollist == null) toolInstance.toollist = new String[]{};
                toolsAndBlocks.put(toolName, new Tool(toolInstance));
            }
        }
        catch (Exception ignored) {

        }

        boolean defaultsAdded = false;
        for(String defaultTool : configValues.defaultTools.keySet()) {
            if(!toolsAndBlocks.containsKey(defaultTool)) {
                toolsAndBlocks.put(defaultTool, configValues.defaultTools.get(defaultTool));
                defaultsAdded = true;
            }
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

        if(defaultsAdded) {
            saveConfigs();
        }
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

    public void addToolType(String newType, String name, String icon) {
        if(!newType.isEmpty() && !toolsAndBlocks.containsKey(newType)) {
            Tool newTool = new Tool(name, icon, new String[]{}, new String[]{});
            toolsAndBlocks.put(newType, newTool);
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

    public void addCongruentBlocks(String existingBlock, String newBlock) {
        setBlockCongruenceList(String.format("%s=%s", existingBlock, newBlock));
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

    public void addTool(String tool, String name) {
        toolsAndBlocks.put(tool, toolsAndBlocks.get(tool).addTool(name));
    }

    public void removeTool(String tool, String name) {
        toolsAndBlocks.put(tool, toolsAndBlocks.get(tool).removeTool(name));
    }

    public Set<String> getToolTypeNames() {
        return toolsAndBlocks.keySet();
    }

    public String getToolTypeName(String toolType) {
        return toolsAndBlocks.get(toolType).name;
    }

    public String getToolTypeIcon(String toolType) {
        return toolsAndBlocks.get(toolType).icon;
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
            radiusLimit = 1000;
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
        return tool == null || toolsAndBlocks.get(type).toollist.contains(Item.itemRegistry.getNameForObject(tool.getItem()).toString());
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

        List<String> sortedToolNames = new ArrayList<String>(toolsAndBlocks.keySet());
        Collections.sort(sortedToolNames);
        for(String toolName : sortedToolNames) {
            Tool tool = toolsAndBlocks.get(toolName);

            JsonArray toolList = new JsonArray();
            List<String> sortedToolList = new ArrayList<String>(tool.toollist);
            Collections.sort(sortedToolList);
            for(String whiteListTool : sortedToolList) {
                toolList.add(new JsonPrimitive(whiteListTool));
            }
            JsonArray blockList = new JsonArray();
            List<BlockID> sortedBlockList= new ArrayList<BlockID>(tool.blocklist);
            Collections.sort(sortedBlockList);
            for(BlockID whitelistBlock : sortedBlockList) {
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
