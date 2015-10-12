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
import com.google.common.io.Files;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.NetworkCheckHandler;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import portablejim.veinminer.api.IMCMessage;
import portablejim.veinminer.api.Permission;
import portablejim.veinminer.api.VeinminerHarvestFailedCheck;
import portablejim.veinminer.api.VeinminerPostUseTool;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.Map;

import static cpw.mods.fml.common.Mod.EventHandler;
import static cpw.mods.fml.common.Mod.Instance;

/**
 * Main mod class to handle events from Veinminer and cancel events when
 * special mod support is wanted.
 */

@Mod(modid = ModInfo.MOD_ID,
        name = ModInfo.MOD_NAME)
public class VeinMinerModSupport {

    private boolean debugMode = false;

    @Instance(ModInfo.MOD_ID)
    public static VeinMinerModSupport instance;

    public boolean forceConsumerAvailable;

    private Boolean configLoaded = false;

    @NetworkCheckHandler
    public boolean checkClientModVersion(Map<String, String> mods, Side side) {
        return true;
    }

    @SuppressWarnings("UnusedDeclaration")
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        File configDir = new File(event.getModConfigurationDirectory(), "veinminer");
        File loadedFile = new File(configDir, "modSupport.cfg");
        if(loadedFile.exists()) {
            configLoaded = true;
        }
        else {
            try {
                //noinspection ResultOfMethodCallIgnored
                loadedFile.createNewFile();
                Files.write("#Nothing to see here!\n", loadedFile, Charset.defaultCharset());
            } catch (IOException ignored) { }
        }
    }

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

        if(!configLoaded) {
            addTools();
        }
    }

    private void addTools() {
        if(Loader.isModLoaded("IC2")) {
            IMCMessage.addTool("axe", "IC2:itemToolBronzeAxe");
            IMCMessage.addTool("axe", "IC2:itemToolChainsaw");
            IMCMessage.addTool("hoe", "IC2:itemToolBronzeHoe");
            IMCMessage.addTool("pickaxe", "IC2:itemToolBronzePickaxe");
            IMCMessage.addTool("pickaxe", "IC2:itemToolDrill");
            IMCMessage.addTool("pickaxe", "IC2:itemToolDDrill");
            IMCMessage.addTool("pickaxe", "IC2:itemToolIridiumDrill");
            IMCMessage.addTool("shears", "IC2:itemToolBronzeHoe");
            IMCMessage.addTool("shovel", "IC2:itemToolBronzeSpade");
        }
        if(Loader.isModLoaded("appliedenergistics2")) {
            IMCMessage.addTool("axe", "appliedenergistics2:item.ToolCertusQuartzAxe");
            IMCMessage.addTool("hoe", "appliedenergistics2:item.ToolCertusQuartzHoe");
            IMCMessage.addTool("pickaxe", "appliedenergistics2:item.ToolCertusQuartzPickaxe");
            IMCMessage.addTool("shovel", "appliedenergistics2:item.ToolCertusQuartzSpade");
            IMCMessage.addTool("axe", "appliedenergistics2:item.ToolNetherQuartzAxe");
            IMCMessage.addTool("hoe", "appliedenergistics2:item.ToolNetherQuartzHoe");
            IMCMessage.addTool("pickaxe", "appliedenergistics2:item.ToolNetherQuartzPickaxe");
            IMCMessage.addTool("shovel", "appliedenergistics2:item.ToolNetherQuartzSpade");
        }
        if(Loader.isModLoaded("BiomesOPlenty")) {
            IMCMessage.addTool("axe", "BiomesOPlenty:axeMud");
            IMCMessage.addTool("hoe", "BiomesOPlenty:hoeMud");
            IMCMessage.addTool("pickaxe", "BiomesOPlenty:pickaxeMud");
            IMCMessage.addTool("shovel", "BiomesOPlenty:shovelMud");
            IMCMessage.addTool("axe", "BiomesOPlenty:axeAmethyst");
            IMCMessage.addTool("hoe", "BiomesOPlenty:hoeAmethyst");
            IMCMessage.addTool("pickaxe", "BiomesOPlenty:pickaxeAmethyst");
            IMCMessage.addTool("shovel", "BiomesOPlenty:shovelAmethyst");
            IMCMessage.addTool("shears", "BiomesOPlenty:scytheWood");
            IMCMessage.addTool("shears", "BiomesOPlenty:scytheStone");
            IMCMessage.addTool("shears", "BiomesOPlenty:scytheIron");
            IMCMessage.addTool("shears", "BiomesOPlenty:scytheGold");
            IMCMessage.addTool("shears", "BiomesOPlenty:scytheDiamond");
            IMCMessage.addTool("shears", "BiomesOPlenty:scytheMud");
            IMCMessage.addTool("shears", "BiomesOPlenty:scytheAmethyst");
        }
        if(Loader.isModLoaded("TConstruct")) {
            devLog("Tinkers support loaded");
            IMCMessage.addTool("axe", "TConstruct:hatchet");
            IMCMessage.addTool("hoe", "TConstruct:mattock");
            IMCMessage.addTool("pickaxe", "TConstruct:pickaxe");
            IMCMessage.addTool("shovel", "TConstruct:shovel");
            IMCMessage.addTool("shovel", "TConstruct:mattock");
        }
        if(Loader.isModLoaded("exnihilo")) {
            devLog("Ex Nihilo support loaded");
            IMCMessage.addToolType("crook", "Crook", "exnihilo:crook");
            IMCMessage.addTool("crook", "exnihilo:crook");
            IMCMessage.addTool("crook", "exnihilo:crook_bone");
            IMCMessage.addToolType("hammer", "Hammer", "exnihilo:hammer_stone");
            IMCMessage.addTool("hammer", "exnihilo:hammer_wood");
            IMCMessage.addTool("hammer", "exnihilo:hammer_stone");
            IMCMessage.addTool("hammer", "exnihilo:hammer_iron");
            IMCMessage.addTool("hammer", "exnihilo:hammer_gold");
            IMCMessage.addTool("hammer", "exnihilo:hammer_diamond");

            IMCMessage.addBlock("crook", "minecraft:leaves");
            IMCMessage.addBlock("crook", "minecraft:leaves2");
            IMCMessage.addBlock("crook", "minecraft:tallgrass");
            IMCMessage.addBlock("crook", "minecraft:vine");
            IMCMessage.addBlock("crook", "minecraft:web");
            IMCMessage.addBlock("crook", "minecraft:wool");
            IMCMessage.addBlock("hammer", "exnihilo:aluminum_dust");
            IMCMessage.addBlock("hammer", "exnihilo:aluminum_gravel");
            IMCMessage.addBlock("hammer", "exnihilo:aluminum_sand");
            IMCMessage.addBlock("hammer", "exnihilo:copper_dust");
            IMCMessage.addBlock("hammer", "exnihilo:copper_gravel");
            IMCMessage.addBlock("hammer", "exnihilo:copper_sand");
            IMCMessage.addBlock("hammer", "exnihilo:dust");
            IMCMessage.addBlock("hammer", "exnihilo:ender_lead_gravel");
            IMCMessage.addBlock("hammer", "exnihilo:ender_platinum_gravel");
            IMCMessage.addBlock("hammer", "exnihilo:ender_silver_gravel");
            IMCMessage.addBlock("hammer", "exnihilo:ender_tin_gravel");
            IMCMessage.addBlock("hammer", "exnihilo:exnihilo.gravel_ender");
            IMCMessage.addBlock("hammer", "exnihilo:exnihilo.gravel_nether");
            IMCMessage.addBlock("hammer", "exnihilo:gold_dust");
            IMCMessage.addBlock("hammer", "exnihilo:gold_gravel");
            IMCMessage.addBlock("hammer", "exnihilo:gold_sand");
            IMCMessage.addBlock("hammer", "exnihilo:iron_dust");
            IMCMessage.addBlock("hammer", "exnihilo:iron_gravel");
            IMCMessage.addBlock("hammer", "exnihilo:iron_sand");
            IMCMessage.addBlock("hammer", "exnihilo:lead_dust");
            IMCMessage.addBlock("hammer", "exnihilo:lead_gravel");
            IMCMessage.addBlock("hammer", "exnihilo:lead_sand");
            IMCMessage.addBlock("hammer", "exnihilo:nether_copper_gravel");
            IMCMessage.addBlock("hammer", "exnihilo:nether_gold_gravel");
            IMCMessage.addBlock("hammer", "exnihilo:nether_iron_gravel");
            IMCMessage.addBlock("hammer", "exnihilo:nether_nickel_gravel");
            IMCMessage.addBlock("hammer", "exnihilo:nickel_dust");
            IMCMessage.addBlock("hammer", "exnihilo:nickel_gravel");
            IMCMessage.addBlock("hammer", "exnihilo:nickel_sand");
            IMCMessage.addBlock("hammer", "exnihilo:platinum_dust");
            IMCMessage.addBlock("hammer", "exnihilo:platinum_gravel");
            IMCMessage.addBlock("hammer", "exnihilo:platinum_sand");
            IMCMessage.addBlock("hammer", "exnihilo:silver_dust");
            IMCMessage.addBlock("hammer", "exnihilo:silver_gravel");
            IMCMessage.addBlock("hammer", "exnihilo:silver_sand");
            IMCMessage.addBlock("hammer", "exnihilo:tin_dust");
            IMCMessage.addBlock("hammer", "exnihilo:tin_gravel");
            IMCMessage.addBlock("hammer", "exnihilo:tin_sand");
            IMCMessage.addBlock("hammer", "minecraft:cobblestone");
            IMCMessage.addBlock("hammer", "minecraft:gravel");
            IMCMessage.addBlock("hammer", "minecraft:sand");
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
    public void makeToolsWork(VeinminerHarvestFailedCheck event) {
        ItemStack currentEquipped = event.player.getCurrentEquippedItem();

        if(currentEquipped == null) {
            return;
        }

        Item currentEquippedItem = event.player.getCurrentEquippedItem().getItem();
        if(Loader.isModLoaded("DartCraft")) {
            devLog("Dartcraft detected");
            if(currentEquippedItem instanceof IBreakable && event.allowContinue == Permission.DENY) {
                devLog("Allowed start");
                event.allowContinue = Permission.ALLOW;
            }
        }
        if(Loader.isModLoaded("TConstruct")) {
            devLog("Tinkers Construct detected");
            tinkersConstructToolEvent(event);
        }
        if(Loader.isModLoaded("exnihilo")) {
            devLog("Ex Nihilo detected");
            if (currentEquippedItem != null && currentEquippedItem.getClass().getCanonicalName().startsWith("exnihilo.items.hammers")
                    && event.allowContinue == Permission.DENY) {
                devLog("Allowed hammer start");
                event.allowContinue = Permission.ALLOW;
            }
            else if(currentEquippedItem != null) {
                devLog(currentEquippedItem.getClass().getCanonicalName());
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
        ItemStack currentItem = event.player.getCurrentEquippedItem();

        if(currentItem == null) {
            devLog("ERROR: Item is null");
            return;
        }

        if(!currentItem.hasTagCompound()) {
            devLog("ERROR: No NBT data");
            return;
        }
        NBTTagCompound toolTags = currentItem.getTagCompound().getCompoundTag("InfiTool");
        if(toolTags == null) {
            devLog("ERROR: Not Tinkers Construct Tool");
            return;
        }

        Block block = Block.getBlockFromName(event.blockName);
        if(block == null) {
            devLog("ERROR: Block id wrong.");
            return;
        }

        devLog("Allowing event");
        if(event.allowContinue == Permission.DENY) {
            event.allowContinue = Permission.ALLOW;
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    @SubscribeEvent
    public void applyForce(VeinminerPostUseTool event) {
        ItemStack currentEquippedItemStack = event.player.getCurrentEquippedItem();

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
