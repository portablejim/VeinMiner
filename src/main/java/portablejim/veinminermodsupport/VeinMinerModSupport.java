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
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import portablejim.veinminer.api.IMCMessage;
import portablejim.veinminer.api.Permission;
import portablejim.veinminer.api.ToolType;
import portablejim.veinminer.api.VeinminerHarvestFailedCheck;
import portablejim.veinminer.api.VeinminerPostUseTool;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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

        addTools();
    }

    private void addTools() {
        if(Loader.isModLoaded("IC2")) {
            IMCMessage.addTool(ToolType.AXE, "IC2:itemToolBronzeAxe");
            IMCMessage.addTool(ToolType.AXE, "IC2:itemToolChainsaw");
            IMCMessage.addTool(ToolType.HOE, "IC2:itemToolBronzeHoe");
            IMCMessage.addTool(ToolType.PICKAXE, "IC2:itemToolBronzePickaxe");
            IMCMessage.addTool(ToolType.PICKAXE, "IC2:itemToolDrill");
            IMCMessage.addTool(ToolType.PICKAXE, "IC2:itemToolDDrill");
            IMCMessage.addTool(ToolType.PICKAXE, "IC2:itemToolIridiumDrill");
            IMCMessage.addTool(ToolType.SHEARS, "IC2:itemToolBronzeHoe");
            IMCMessage.addTool(ToolType.SHOVEL, "IC2:itemToolBronzeSpade");
        }
        if(Loader.isModLoaded("appliedenergistics2")) {
            IMCMessage.addTool(ToolType.AXE, "appliedenergistics2:item.ToolCertusQuartzAxe");
            IMCMessage.addTool(ToolType.HOE, "appliedenergistics2:item.ToolCertusQuartzHoe");
            IMCMessage.addTool(ToolType.PICKAXE, "appliedenergistics2:item.ToolCertusQuartzPickaxe");
            IMCMessage.addTool(ToolType.SHOVEL, "appliedenergistics2:item.ToolCertusQuartzSpade");
            IMCMessage.addTool(ToolType.AXE, "appliedenergistics2:item.ToolNetherQuartzAxe");
            IMCMessage.addTool(ToolType.HOE, "appliedenergistics2:item.ToolNetherQuartzHoe");
            IMCMessage.addTool(ToolType.PICKAXE, "appliedenergistics2:item.ToolNetherQuartzPickaxe");
            IMCMessage.addTool(ToolType.SHOVEL, "appliedenergistics2:item.ToolNetherQuartzSpade");
        }
        if(Loader.isModLoaded("BiomesOPlenty")) {
            IMCMessage.addTool(ToolType.AXE, "BiomesOPlenty:axeMud");
            IMCMessage.addTool(ToolType.HOE, "BiomesOPlenty:hoeMud");
            IMCMessage.addTool(ToolType.PICKAXE, "BiomesOPlenty:pickaxeMud");
            IMCMessage.addTool(ToolType.SHOVEL, "BiomesOPlenty:shovelMud");
            IMCMessage.addTool(ToolType.AXE, "BiomesOPlenty:axeAmethyst");
            IMCMessage.addTool(ToolType.HOE, "BiomesOPlenty:hoeAmethyst");
            IMCMessage.addTool(ToolType.PICKAXE, "BiomesOPlenty:pickaxeAmethyst");
            IMCMessage.addTool(ToolType.SHOVEL, "BiomesOPlenty:shovelAmethyst");
            IMCMessage.addTool(ToolType.SHEARS, "BiomesOPlenty:scytheWood");
            IMCMessage.addTool(ToolType.SHEARS, "BiomesOPlenty:scytheStone");
            IMCMessage.addTool(ToolType.SHEARS, "BiomesOPlenty:scytheIron");
            IMCMessage.addTool(ToolType.SHEARS, "BiomesOPlenty:scytheGold");
            IMCMessage.addTool(ToolType.SHEARS, "BiomesOPlenty:scytheDiamond");
            IMCMessage.addTool(ToolType.SHEARS, "BiomesOPlenty:scytheMud");
            IMCMessage.addTool(ToolType.SHEARS, "BiomesOPlenty:scytheAmethyst");
        }
        if(Loader.isModLoaded("TConstruct")) {
            devLog("Tinkers support loaded");
            IMCMessage.addTool(ToolType.AXE, "TConstruct:hatchet");
            IMCMessage.addTool(ToolType.HOE, "TConstruct:mattock");
            IMCMessage.addTool(ToolType.PICKAXE, "TConstruct:pickaxe");
            IMCMessage.addTool(ToolType.SHOVEL, "TConstruct:shovel");
            IMCMessage.addTool(ToolType.SHOVEL, "TConstruct:mattock");
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
