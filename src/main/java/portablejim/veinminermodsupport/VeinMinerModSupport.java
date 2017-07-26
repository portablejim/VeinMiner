/* This file is part of VeinMiner Mod Support.
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

package portablejim.veinminermodsupport;

import bluedart.api.IBreakable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.common.config.Configuration;
import portablejim.veinminer.api.IMCMessage;
import portablejim.veinminer.api.Permission;
import portablejim.veinminer.api.VeinminerHarvestFailedCheck;
import portablejim.veinminer.api.VeinminerPostUseTool;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static net.minecraftforge.fml.common.Mod.EventHandler;
import static net.minecraftforge.fml.common.Mod.Instance;

/**
 * Main mod class to handle events from Veinminer and cancel events when
 * special mod support is wanted.
 */

@Mod(modid = ModInfo.MOD_ID,
        name = ModInfo.MOD_NAME,
        acceptedMinecraftVersions = "[1.9,1.11)")
public class VeinMinerModSupport {

    private boolean debugMode = false;

    @Instance(ModInfo.MOD_ID)
    public static VeinMinerModSupport instance;

    private boolean forceConsumerAvailable;

    private final static String[] FALSETOOLS_DEFAULT = {
            "excompressum:chicken_stick",
            "excompressum:compressed_hammer_wood",
            "excompressum:compressed_hammer_stone",
            "excompressum:compressed_hammer_iron",
            "excompressum:compressed_hammer_gold",
            "excompressum:compressed_hammer_diamond",
            "excompressum:double_compressed_diamond_hammer",
            "excompressum:compressed_crook",
            "redstonearsenal:tool.axe_flux",
            "redstonearsenal:tool.battlewrench_flux",
            "redstonearsenal:tool.hammer_flux",
            "redstonearsenal:tool.pickaxe_flux",
            "redstonearsenal:tool.shovel_flux",
            "redstonearsenal:tool.sickle_flux",
            "redstonearsenal:tool.sword_flux",
            "actuallyadditions:item_drill",
    };
    private Set<String> falseTools = new LinkedHashSet<String>();

    private final static String[] OVERRIDE_BLACKLIST_DEFAULT = {
            "EnderIO:blockConduitBundle",
    };
    private Set<String> overrideBlacklist = new LinkedHashSet<String>();

    private static final String CONFIG_AUTODETECT = "autodetect";
    private static final String CONFIG_AUTODETECT_COMMENT = "Autodetect items and blocks during game start-up.";

    private boolean AUTODETECT_TOOLS_TOGGLE;
    private static final boolean AUTODETECT_TOOLS_TOGGLE_DEFAULT = true;
    private static final String AUTODETECT_TOOLS_TOGGLE_CONFIGNAME = "autodetect.tools";
    private static final String AUTODETECT_TOOLS_TOGGLE_DESCRIPTION = "Autodetect tools on starting the game, adding the names to the list.";

    private final static String[] BADTOOLS_DEFAULT = {};
    private Set<String> badTools = new LinkedHashSet<String>();

    public VeinMinerModSupport(){

    }

    @SuppressWarnings("unused")
    @NetworkCheckHandler
    public boolean checkClientModVersion(Map<String, String> mods, Side side) {
        return true;
    }

    @SuppressWarnings("UnusedDeclaration")
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        File configDir = new File(event.getModConfigurationDirectory(), "veinminer");
        File loadedFile = new File(configDir, "modSupport.cfg");
        try {
            Configuration config = new Configuration(loadedFile);
            config.load();

            config.addCustomCategoryComment(CONFIG_AUTODETECT, CONFIG_AUTODETECT_COMMENT);
            AUTODETECT_TOOLS_TOGGLE = config.get(CONFIG_AUTODETECT, AUTODETECT_TOOLS_TOGGLE_CONFIGNAME, AUTODETECT_TOOLS_TOGGLE_DEFAULT, AUTODETECT_TOOLS_TOGGLE_DESCRIPTION).getBoolean(AUTODETECT_TOOLS_TOGGLE_DEFAULT);

            config.setCategoryComment("advanced", "You probably don't want to touch these");

            String[] badTools_array = config.getStringList("bad_tools", "advanced", BADTOOLS_DEFAULT, "Tools that break veinminer.");
            String[] falseTools_array = config.getStringList("special_snowflake_tools", "advanced", FALSETOOLS_DEFAULT, "Tools that need to be treated as special snowflakes\n");
            String[] overrideBlacklist_array = config.getStringList("override_blacklist_blocks", "advanced", OVERRIDE_BLACKLIST_DEFAULT, "Blocks to not override success for\n");
            falseTools = new LinkedHashSet<String>(Arrays.asList(falseTools_array));
            overrideBlacklist = new LinkedHashSet<String>(Arrays.asList(overrideBlacklist_array));
            badTools = new LinkedHashSet<String>(Arrays.asList(badTools_array));

            config.save();

        }
        catch(Exception e) {
            event.getModLog().error("Error writing config file");
        }
    }

    @SuppressWarnings("unused")
    @EventHandler
    public void init(@SuppressWarnings("UnusedParameters") FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);

        ModContainer thisMod = Loader.instance().getIndexedModList().get(ModInfo.MOD_ID);
        if(thisMod != null) {
            String fileName = thisMod.getSource().getName();
            if(fileName.contains("-dev") || !fileName.contains(".jar")) {
                debugMode = true;
                devLog("DEV VERSION");
            }
        }
        forceConsumerAvailable = false;

        if(AUTODETECT_TOOLS_TOGGLE) {
            addTools();
        }
    }

    private void addTools() {
        if(Loader.isModLoaded("tconstruct")) {
            devLog("Tinkers support loaded");
        }
        if(Loader.isModLoaded("exnihilo")) {
            devLog("Ex Nihilo support loaded");
            IMCMessage.addToolType("crook", "Crook", "exnihilo:crook");
            IMCMessage.addToolType("hammer", "Hammer", "exnihilo:hammer_stone");


        }
        else if(Loader.isModLoaded("excompressum")) {
            IMCMessage.addToolType("hammer", "Hammer", "excompressum:chickenStick");
        }
        this.addAllTool(Lists.Tools.shears_array, "shears");
        this.addAllTool(Lists.Tools.sickle_array, "hoe");
        this.addAllTool(Lists.Tools.hammer_array, "pickaxe");
        this.addAllTool(Lists.Tools.drill_array, "pickaxe");
        this.addAllTool(Lists.Tools.drill_array, "shovel");
        this.addAllTool(Lists.Tools.pickaxe_array, "pickaxe");
        this.addAllTool(Lists.Tools.shovel_array, "shovel");
        this.addAllTool(Lists.Tools.hoe_array, "hoe");
        this.addAllTool(Lists.Tools.axe_array, "axe");
        this.addAllTool(Lists.Tools.multitool_array, "axe");
        this.addAllTool(Lists.Tools.multitool_array, "shovel");
        this.addAllTool(Lists.Tools.multitool_array, "pickaxe");
        this.addAllTool(Lists.Tools.multitool_array, "axe");
        this.addAllTool(Lists.Tools.nihilo_hammer_array, "hammer");
        this.addAllTool(Lists.Tools.nihilo_crook_array, "crook");

        this.addAllBlock(Lists.Blocks.nihilo_hammer_array, "hammer");
        this.addAllBlock(Lists.Blocks.nihilo_crook_array, "crook");
    }

    private void addAllTool(String[] list, String category) {
        for (String item : list) {
            ResourceLocation id = new ResourceLocation(item);
            if (Loader.isModLoaded(id.getResourceDomain())) {
                IMCMessage.addTool(category, item);
            }
        }
    }

    private void addAllBlock(String[] list, String category) {
        for(String item : list) {
            ResourceLocation id = new ResourceLocation(item);
            if(Loader.isModLoaded(id.getResourceDomain())) {
                IMCMessage.addBlock(category, item);
            }
        }

    }

    @SuppressWarnings("UnusedDeclaration")
    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        if(Loader.isModLoaded("DartCraft")) {
            devLog("Testing for dartcraft classes and functions.");
            try {
                Object obj = Class.forName("bluedart.api.IForceConsumer").getMethod("attemptRepair", ItemStack.class);

                // Class present.
                forceConsumerAvailable = true;
            } catch (ClassNotFoundException e) {
                devLog("Failed to find Dartcraft force consumer. Disabling repair support");
            } catch (NoSuchMethodException e) {
                devLog("Failed to find Dartcraft force consumer function. Disabling repair support");
            }
        }
    }

    private void devLog(String string) {
        if(debugMode) {
            FMLLog.getLogger().info("[" + ModInfo.MOD_ID + "] " + string);
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    @SubscribeEvent
    public void banBadTools(VeinminerHarvestFailedCheck event) {
        ItemStack currentEquipped = event.player.getHeldItemMainhand();

        if(currentEquipped != null && currentEquipped.getItem() != null && Item.itemRegistry.getNameForObject(currentEquipped.getItem()) != null) {
            String item_name = Item.itemRegistry.getNameForObject(currentEquipped.getItem()).toString();
            if(badTools.contains(item_name)) {
                event.allowContinue = Permission.FORCE_DENY;
            }
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    @SubscribeEvent
    public void makeToolsWork(VeinminerHarvestFailedCheck event) {
        ItemStack currentEquipped = event.player.getHeldItemMainhand();

        if(currentEquipped == null) {
            return;
        }

        if(event.allowContinue == Permission.DENY) {
            if(overrideBlacklist.contains(event.blockName)) {
                devLog("Denied with block: " + event.blockName);
                event.allowContinue = Permission.FORCE_DENY;
            }
            else {
                devLog("Not Denied with block: " + event.blockName);
            }
        }

        Item currentEquippedItem = event.player.getHeldItemMainhand().getItem();
        if(Loader.isModLoaded("DartCraft")) {
            devLog("Dartcraft detected");
            if(currentEquippedItem instanceof IBreakable && event.allowContinue == Permission.DENY) {
                devLog("Allowed start");
                event.allowContinue = Permission.ALLOW;
            }
        }
        if(Loader.isModLoaded("tconstruct")) {
            devLog("Tinkers Construct detected");
            tinkersConstructToolEvent(event);
        }

        if(event.allowContinue == Permission.DENY) {
            String item_name = Item.itemRegistry.getNameForObject(currentEquippedItem).toString();
            if(falseTools.contains(item_name)) {
                devLog("Allowed start with " + item_name);
                event.allowContinue = Permission.ALLOW;
            }
        }

        if(Loader.isModLoaded("exnihilo")) {
            devLog("Ex Nihilo detected");
            if(currentEquippedItem != null) {
                if (currentEquipped.getClass() != null && currentEquipped.getClass().getCanonicalName() != null
                        && currentEquippedItem.getClass().getCanonicalName().startsWith("exnihilo.items.hammers") && event.allowContinue == Permission.DENY) {
                    devLog("Allowed hammer start");
                    event.allowContinue = Permission.ALLOW;
                }
                else {
                    devLog(currentEquippedItem.getClass().getCanonicalName());
                }
                Block testLeaves = Block.getBlockFromName(event.blockName);
                if(Block.getBlockFromName(event.blockName).isLeaves(testLeaves.getStateFromMeta(event.blockMetadata), event.player.getEntityWorld(), event.player.getPosition())
                        && event.allowContinue == Permission.DENY) {
                    String item_name = currentEquippedItem.getRegistryName().toString();
                    if("exnihilo:crook".equals(item_name)) event.allowContinue = Permission.ALLOW;
                    if("exnihilo:crook_bone".equals(item_name)) event.allowContinue = Permission.ALLOW;
                    if("exastris:crook_rf".equals(item_name)) event.allowContinue = Permission.ALLOW;
                }
            }

            try {
                Class<?> hammerBase = Class.forName("exnihilo.items.hammers.ItemHammerBase");
                if(currentEquippedItem != null && hammerBase.isAssignableFrom(currentEquippedItem.getClass())) {
                    if(event.allowContinue == Permission.DENY) {
                        devLog("Allowed generic Ex Nihilo hammer start");
                        event.allowContinue = Permission.ALLOW;
                    }
                }

            } catch (ClassNotFoundException e) {
                // Class doesn't exist. Do nothing.
                devLog("Ex Nihilo generic hammer support failed.");
            }
            try {
                Class<?> hammerBase = Class.forName("ExAstris.Item.ItemHammerRF");
                if(currentEquippedItem != null && hammerBase.isAssignableFrom(currentEquippedItem.getClass())) {
                    if(event.allowContinue == Permission.DENY) {
                        devLog("Allowed Ex Astris hammer start");
                        event.allowContinue = Permission.ALLOW;
                    }
                }

            } catch (ClassNotFoundException e) {
                // Class doesn't exist. Do nothing.
                devLog("Ex Astris hammer support failed.");
            }
        }
    }

    private void tinkersConstructToolEvent(VeinminerHarvestFailedCheck event) {
        ItemStack currentItem = event.player.getHeldItemMainhand();

        if(currentItem == null) {
            devLog("ERROR: Item is null");
            return;
        }

        if(!currentItem.hasTagCompound()) {
            devLog("ERROR: No NBT data");
            return;
        }
        NBTTagCompound toolTags = currentItem.getTagCompound().getCompoundTag("Stats");
        if(toolTags == null || toolTags.hasNoTags()) {
            devLog("ERROR: Not Tinkers Construct Tool");
            return;
        }

        Block block = Block.getBlockFromName(event.blockName);
        if(block == null) {
            devLog("ERROR: Block id wrong.");
            return;
        }

        /*if(toolTags.hasKey("Broken")) {
            devLog("DENY: Tool broken");
            if(event.allowContinue == Permission.ALLOW) {
                event.allowContinue = Permission.DENY;
            }
            return;
        }*/

        devLog("Allowing event");
        if(event.allowContinue == Permission.DENY) {
            event.allowContinue = Permission.ALLOW;
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    @SubscribeEvent
    public void fixFalseNegatives(VeinminerHarvestFailedCheck event) {
        // Some blocks return false when they shouldn't.
        if(event.allowContinue == Permission.DENY) {
            if("IC2:blockRubWood".equals(event.blockName)) event.allowContinue = Permission.ALLOW;
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    @SubscribeEvent
    public void applyForce(VeinminerPostUseTool event) {
        ItemStack currentEquippedItemStack = event.player.getHeldItemMainhand();

        // Pre-compute if avaliable to short circuit logic if not found.
        // Method called lots (many times a second, possibly thousand times total).
        // Reflection is slow, and you'll probably feel it.
        if(forceConsumerAvailable && currentEquippedItemStack != null && Loader.isModLoaded("DartCraft")) {
            devLog("Reflecting on Dartcraft run repair method.");
            try {
                Class IForceConsumer = Class.forName("bluedart.api.IForceConsumer");
                if(IForceConsumer != null && IForceConsumer.isInstance(currentEquippedItemStack.getItem())) {
                    //noinspection unchecked
                    Method attemptRepair = IForceConsumer.getMethod("attemptRepair", ItemStack.class);
                    attemptRepair.invoke(currentEquippedItemStack.getItem(), currentEquippedItemStack);
                    devLog("Repairing dartcraft force consumer");
                }
            } catch (ClassNotFoundException e) {
                devLog("Strange, I thought we already found the Dartcraft class.");
                forceConsumerAvailable = false;
            } catch (NoSuchMethodException e) {
                devLog("Strange, I thought we already found the Dartcraft class and correct method.");
                forceConsumerAvailable = false;
            } catch (InvocationTargetException e) {
                devLog("Trying to repair Dartcraft tools didn't work. It threw a InvocationTargetException.");
                forceConsumerAvailable = false;
            } catch (IllegalAccessException e) {
                devLog("Trying to repair Dartcraft tools didn't work. It threw a IllegalAccessException.");
                forceConsumerAvailable = false;
            }
        }
    }
}
