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

import net.minecraftforge.common.Configuration;

import java.io.File;

/**
 * Stores the raw values of configs which are stored in the config file.
 *
 * The Forge config file writer to write files.
 */

public class ConfigurationValues {

    private Configuration configFile;

    public static final String CONFIG_BLOCKLIST = "blocklist";
    public static final String CONFIG_BLOCKLIST_COMMENT = "IDs of blocks to auto-mine. Separate ids with ',' and metadata from ID with ':'.";
    public static final String CONFIG_ITEMLIST = "itemlist";
    public static final String CONFIG_ITEMLIST_COMMENT = "Tools to use to auto-mine with. Separate ids with ',' and metadata from ID with ':'.";
    public static final String CONFIG_LIMITS = "limit";
    public static final String CONFIG_MISC = "misc";
    public static final String CONFIG_OVERRIDE = "overrides";
    public static final String CONFIG_CLIENT = "client";
    public static final String CONFIG_CLIENT_COMMENT = "These settings are used client side, so they have no impact on servers.";

    public String PICKAXE_BLOCK_ID_LIST;
    public static final String PICKAXE_BLOCK_ID_LIST_DEFAULT = "14,15,16,21,24,48,56,73,74,89,129";
    public static final String PICKAXE_BLOCK_ID_LIST_CONFIGNAME = "blockList.pickaxe";
    public static final String PICKAXE_BLOCK_ID_LIST_DESCRIPTION = "Block ids to auto-mine when using a configured pickaxe. [default: '14,15,16,21,24,48,56,73,74,89,129']";

    public String SHOVEL_BLOCK_ID_LIST;
    public static final String SHOVEL_BLOCK_ID_LIST_DEFAULT = "82";
    public static final String SHOVEL_BLOCK_ID_LIST_CONFIGNAME = "blockList.shovel";
    public static final String SHOVEL_BLOCK_ID_LIST_DESCRIPTION = "Block ids to auto-mine when using a configured shovel. [default: '82']";

    public String AXE_BLOCK_ID_LIST;
    public static final String AXE_BLOCK_ID_LIST_DEFAULT = "17,18";
    public static final String AXE_BLOCK_ID_LIST_CONFIGNAME = "blockList.axe";
    public static final String AXE_BLOCK_ID_LIST_DESCRIPTION = "Block ids to auto-mine when using a configured axe. [default '17,18']";

    public String PICKAXE_ID_LIST;
    public static final String PICKAXE_ID_LIST_DEFAULT = "257,270,274,278,285";
    public static final String PICKAXE_ID_LIST_CONFIGNAME = "itemList.pickaxe";
    public static final String PICKAXE_ID_LIST_DESCRIPTION = "Item ids to use as a pickaxe. [default '257,270,274,278,285']";

    public String SHOVEL_ID_LIST;
    public static final String SHOVEL_ID_LIST_DEFAULT = "256,269,273,277,284";
    public static final String SHOVEL_ID_LIST_CONFIGNAME = "itemList.shovel";
    public static final String SHOVEL_ID_LIST_DESCRIPTION = "Item ids to use as a shovel. [default '256,269,273,277,284']";

    public String AXE_ID_LIST;
    public static final String AXE_ID_LIST_DEFAULT = "258,271,275,279,286";
    public static final String AXE_ID_LIST_CONFIGNAME = "itemList.axe";
    public static final String AXE_ID_LIST_DESCRIPTION = "Item ids to use as a axe. [default '258,271,275,279,286']";

    public String BLOCK_EQUIVALENCY_LIST;
    public static final String BLOCK_EQUIVALENCY_LIST_DEFAULT = "73:0=74:0,17:0=17:4=17:8,17:1=17:5=17:9,17:2=17:6=17:10,17:3=17:7=17:11";
    public static final String BLOCK_EQUIVALENCY_LIST_CONFIGNAME = "equalBlocks";
    public static final String BLOCK_EQUIVALENCY_LIST_DESCRIPTION = "Block IDs (with metadata) to consider equivalent.\nSeparate block IDs from metadata with ':'. Separate blocks with '='. Separate groups with ','\n[default: '73:0-74:0,17:0-17:4-17:8,17:1-17:5-17:9,17:2-17:6-17:10,17:3-17:7-17:11']";

    public int BLOCK_LIMIT;
    public static final int BLOCK_LIMIT_DEFAULT = 800;
    public static final String BLOCK_LIMIT_CONFIGNAME = "limit.blocks";
    public static final String BLOCK_LIMIT_DESCRIPTION = "Limit of blocks to be destroyed at once. Use -1 for infinite. [range: -1 to 2147483647, default: '800']";

    public int RADIUS_LIMIT;
    public static final int RADIUS_LIMIT_DEFAULT = 20;
    public static final String RADIUS_LIMIT_CONFIGNAME = "limit.radius";
    public static final String RADIUS_LIMIT_DESCRIPTION = "Maximum distance from the first block to search for blocks to destroy. [range: -1 to 1000, default: '20]";

    public int BLOCKS_PER_TICK;
    public static final int BLOCKS_PER_TICK_DEFAULT = 10;
    public static final String BLOCKS_PER_TICK_CONFIGNAME = "limit.blocksPerTick";
    public static final String BLOCKS_PER_TICK_DESCRIPTION = "Maximum number of blocks to be removed per game tick (1/20 seconds). Using a low number will keep the game from getting huge performance drops but also decreases the speed at which blocks are destroyed. [range: 1 ~ 1000, default: 10]";

    public boolean ENABLE_ALL_BLOCKS;
    public static final boolean ENABLE_ALL_BLOCKS_DEFAULT = false;
    public static final String ENABLE_ALL_BLOCKS_CONFIGNAME = "override.allBlocks";
    public static final String ENABLE_ALL_BLOCKS_DESCRIPTION = "Mine all blocks with all registered tools that can harvest blocks.";

    public boolean ENABLE_ALL_TOOLS;
    public static final boolean ENABLE_ALL_TOOLS_DEFAULT = false;
    public static final String ENABLE_ALL_TOOLS_CONFIGNAME = "override.allTools";
    public static final String ENABLE_ALL_TOOLS_DESCRIPTION = "Allow all tools, including the open hand, to be used to mine blocks.";

    public String CLIENT_PREFERRED_MODE;
    public static final String CLIENT_PREFERRED_MODE_DEFAULT = "sneak";
    public static final String CLIENT_PREFERRED_MODE_CONFIGNAME = "client.preferredMode";
    public static final String CLIENT_PREFERRED_MODE_DESCRIPTION = "What mode should the client use when joining a game.\nValid modes: [default: sneak\n  'auto' = enables on press of keybind\n  'sneak' = enables on sneaking\n  'no_sneak' = disables on sneaking";

    public ConfigurationValues(File file) {
        configFile = new Configuration(file);
        loadConfigFile();
    }

    public void loadConfigFile() {
        configFile.load();

        configFile.addCustomCategoryComment(CONFIG_BLOCKLIST, CONFIG_BLOCKLIST_COMMENT);
        PICKAXE_BLOCK_ID_LIST = configFile.get(CONFIG_BLOCKLIST, PICKAXE_BLOCK_ID_LIST_CONFIGNAME, PICKAXE_BLOCK_ID_LIST_DEFAULT, PICKAXE_BLOCK_ID_LIST_DESCRIPTION).getString();
        SHOVEL_BLOCK_ID_LIST = configFile.get(CONFIG_BLOCKLIST, SHOVEL_BLOCK_ID_LIST_CONFIGNAME, SHOVEL_BLOCK_ID_LIST_DEFAULT, SHOVEL_BLOCK_ID_LIST_DESCRIPTION).getString();
        AXE_BLOCK_ID_LIST = configFile.get(CONFIG_BLOCKLIST, AXE_BLOCK_ID_LIST_CONFIGNAME, AXE_BLOCK_ID_LIST_DEFAULT, AXE_BLOCK_ID_LIST_DESCRIPTION).getString();
        configFile.addCustomCategoryComment(CONFIG_ITEMLIST, CONFIG_ITEMLIST_COMMENT);
        PICKAXE_ID_LIST = configFile.get(CONFIG_ITEMLIST, PICKAXE_ID_LIST_CONFIGNAME, PICKAXE_ID_LIST_DEFAULT, PICKAXE_ID_LIST_DESCRIPTION).getString();
        SHOVEL_ID_LIST = configFile.get(CONFIG_ITEMLIST, SHOVEL_ID_LIST_CONFIGNAME, SHOVEL_ID_LIST_DEFAULT, SHOVEL_ID_LIST_DESCRIPTION).getString();
        AXE_ID_LIST = configFile.get(CONFIG_ITEMLIST, AXE_ID_LIST_CONFIGNAME, AXE_ID_LIST_DEFAULT, AXE_ID_LIST_DESCRIPTION).getString();

        BLOCK_LIMIT = configFile.get(CONFIG_LIMITS, BLOCK_LIMIT_CONFIGNAME, BLOCK_LIMIT_DEFAULT, BLOCK_LIMIT_DESCRIPTION).getInt(BLOCK_LIMIT_DEFAULT);
        RADIUS_LIMIT = configFile.get(CONFIG_LIMITS, RADIUS_LIMIT_CONFIGNAME, RADIUS_LIMIT_DEFAULT, RADIUS_LIMIT_DESCRIPTION).getInt(RADIUS_LIMIT_DEFAULT);
        BLOCKS_PER_TICK = configFile.get(CONFIG_LIMITS, BLOCKS_PER_TICK_CONFIGNAME, BLOCKS_PER_TICK_DEFAULT, BLOCKS_PER_TICK_DESCRIPTION).getInt(BLOCKS_PER_TICK_DEFAULT);

        BLOCK_EQUIVALENCY_LIST = configFile.get(CONFIG_MISC, BLOCK_EQUIVALENCY_LIST_CONFIGNAME, BLOCK_EQUIVALENCY_LIST_DEFAULT, BLOCK_EQUIVALENCY_LIST_DESCRIPTION).getString();

        ENABLE_ALL_BLOCKS = configFile.get(CONFIG_OVERRIDE, ENABLE_ALL_BLOCKS_CONFIGNAME, ENABLE_ALL_BLOCKS_DEFAULT, ENABLE_ALL_BLOCKS_DESCRIPTION).getBoolean(ENABLE_ALL_BLOCKS_DEFAULT);
        ENABLE_ALL_TOOLS = configFile.get(CONFIG_OVERRIDE, ENABLE_ALL_TOOLS_CONFIGNAME, ENABLE_ALL_TOOLS_DEFAULT, ENABLE_ALL_TOOLS_DESCRIPTION).getBoolean(ENABLE_ALL_TOOLS_DEFAULT);

        configFile.addCustomCategoryComment(CONFIG_CLIENT, CONFIG_CLIENT_COMMENT);
        CLIENT_PREFERRED_MODE = configFile.get(CONFIG_CLIENT, CLIENT_PREFERRED_MODE_CONFIGNAME, CLIENT_PREFERRED_MODE_DEFAULT, CLIENT_PREFERRED_MODE_DESCRIPTION).getString();

        configFile.save();
    }

    public void saveConfigFile() {
        //configFile.load();

        configFile.getCategory(CONFIG_BLOCKLIST).get(PICKAXE_BLOCK_ID_LIST_CONFIGNAME).set(PICKAXE_BLOCK_ID_LIST);
        configFile.getCategory(CONFIG_BLOCKLIST).get(SHOVEL_BLOCK_ID_LIST_CONFIGNAME).set(SHOVEL_BLOCK_ID_LIST);
        configFile.getCategory(CONFIG_BLOCKLIST).get(AXE_BLOCK_ID_LIST_CONFIGNAME).set(AXE_BLOCK_ID_LIST);
        configFile.getCategory(CONFIG_ITEMLIST).get(PICKAXE_ID_LIST_CONFIGNAME).set(PICKAXE_ID_LIST);
        configFile.getCategory(CONFIG_ITEMLIST).get(SHOVEL_ID_LIST_CONFIGNAME).set(SHOVEL_ID_LIST);
        configFile.getCategory(CONFIG_ITEMLIST).get(AXE_ID_LIST_CONFIGNAME).set(AXE_ID_LIST);

        configFile.getCategory(CONFIG_LIMITS).get(BLOCK_LIMIT_CONFIGNAME).set(BLOCK_LIMIT);
        configFile.getCategory(CONFIG_LIMITS).get(RADIUS_LIMIT_CONFIGNAME).set(RADIUS_LIMIT);
        configFile.getCategory(CONFIG_LIMITS).get(BLOCKS_PER_TICK_CONFIGNAME).set(BLOCKS_PER_TICK);

        configFile.getCategory(CONFIG_MISC).get(BLOCK_EQUIVALENCY_LIST_CONFIGNAME).set(BLOCK_EQUIVALENCY_LIST);

        configFile.getCategory(CONFIG_OVERRIDE).get(ENABLE_ALL_BLOCKS_CONFIGNAME).set(ENABLE_ALL_BLOCKS);
        configFile.getCategory(CONFIG_OVERRIDE).get(ENABLE_ALL_TOOLS_CONFIGNAME).set(ENABLE_ALL_TOOLS);

        configFile.getCategory(CONFIG_CLIENT).get(CLIENT_PREFERRED_MODE_CONFIGNAME).set(CLIENT_PREFERRED_MODE);

        if(configFile.hasChanged()) {
            configFile.save();
        }
    }
}
