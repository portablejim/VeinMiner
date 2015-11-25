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

package portablejim.veinminer;

import net.minecraft.block.Block;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.logging.log4j.Logger;
import portablejim.veinminer.configuration.ConfigurationSettings;
import portablejim.veinminer.configuration.ConfigurationValues;
import portablejim.veinminer.configuration.ToolType;
import portablejim.veinminer.core.InjectedCalls;
import portablejim.veinminer.lib.MinerLogger;
import portablejim.veinminer.lib.ModInfo;
import portablejim.veinminer.network.PacketClientPresent;
import portablejim.veinminer.network.PacketMinerActivate;
import portablejim.veinminer.network.PacketPingClient;
import portablejim.veinminer.proxy.CommonProxy;
import portablejim.veinminer.server.MinerCommand;
import portablejim.veinminer.server.MinerServer;
import portablejim.veinminer.util.BlockID;
import portablejim.veinminer.util.Point;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class is the main mod class for VeinMiner. It is loaded as a mod
 * through ForgeModLoader.
 */

@Mod(modid = ModInfo.MODID, acceptedMinecraftVersions = "[1.8,1.8.8)",
        canBeDeactivated = true, guiFactory = "portablejim.veinminer.configuration.client.ConfigGuiFactory")
public class VeinMiner {

    @Mod.Instance(ModInfo.MODID)
    public static VeinMiner instance;

    @SidedProxy(clientSide = ModInfo.PROXY_CLIENT_CLASS, serverSide = ModInfo.PROXY_SERVER_CLASS)
    public static CommonProxy proxy;

    public SimpleNetworkWrapper networkWrapper;

    ConfigurationValues configurationValues;
    public ConfigurationSettings configurationSettings;

    public MinerServer minerServer = null;

    public Logger logger;

    @NetworkCheckHandler
    public boolean checkClientModVersion(Map<String, String> mods, Side side) {
        FMLLog.fine("Check Version");
        /*
         * Accept vanilla clients and servers.
         * Vanilla server: No check packet will come.
         * Vanilla client: Won't respond to ping packets. But it will work if forge works I think.
         */
        if(mods.size() == 0) {
            return true;
        }
        else {
            if(mods.containsKey(ModInfo.MODID)) {
                String clientVersion = mods.get(ModInfo.MODID);
                // Connect with matching versions or if one side is a dev build.
                String ourVersionString = Loader.instance().activeModContainer().getVersion();
                if(ourVersionString.equals(clientVersion) || ourVersionString.startsWith("${version}") || clientVersion.startsWith("${version}")) {
                    return true;
                }
                int clientMajor = 0, clientMinor = 0, major = 0, minor = 0;
                String[] splitVersion = clientVersion.split("\\.");
                if (splitVersion.length >= 2) {
                    clientMajor = Integer.parseInt(splitVersion[0]);
                    clientMinor = Integer.parseInt(splitVersion[1]);
                }
                String[] splitOurVersion = ourVersionString.split("\\.");
                if(splitOurVersion.length >= 2) {
                    major = Integer.parseInt(splitOurVersion[0]);
                    minor = Integer.parseInt(splitOurVersion[1]);
                }

                // After version 1, don't check minor versions.
                return !(major != clientMajor || (major == 0 && minor != clientMinor) || (major == 0 && minor == 0));
            }
        }
        if(side == Side.CLIENT) {
            // Client side checks.
            if(!mods.containsKey(ModInfo.MODID)) {
                return true;
            }
        }
        if(side == Side.SERVER) {
            // Client side checks.
            if(!mods.containsKey(ModInfo.MODID)) {
                return true;
            }
        }
        return true;
    }

    @SuppressWarnings("UnusedDeclaration")
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();

        setupNetworking();

        File modDir = new File(event.getModConfigurationDirectory(), "veinminer");
        if(!modDir.exists()) {
            //noinspection ResultOfMethodCallIgnored
            modDir.mkdir();
        }

        configurationValues = new ConfigurationValues(new File(modDir, "general.cfg"), new File(modDir, "tools-and-blocks.json"));
        configurationValues.loadConfigFile();
        configurationSettings = new ConfigurationSettings(configurationValues);
        proxy.registerClientEvents();
        proxy.registerCommonEvents();
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void setupNetworking() {
        networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(ModInfo.CHANNEL);
        networkWrapper.registerMessage(PacketPingClient.Handler.class, PacketPingClient.class, 0, Side.CLIENT);
        networkWrapper.registerMessage(PacketClientPresent.Handler.class, PacketClientPresent.class, 1, Side.SERVER);
        networkWrapper.registerMessage(PacketMinerActivate.Handler.class, PacketMinerActivate.class, 2, Side.SERVER);
    }

    @SuppressWarnings("UnusedDeclaration")
    @EventHandler
    public void init(@SuppressWarnings("UnusedParameters") FMLInitializationEvent event) {
        ModContainer thisMod = Loader.instance().getIndexedModList().get(ModInfo.MODID);
        if(thisMod != null) {
            String fileName = thisMod.getSource().getName();
            if(fileName.contains("-dev") || !fileName.contains(".jar")) {
                ModInfo.DEBUG_MODE = true;
                MinerLogger.debug("Enabling debug mode");
            }
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        String[] oreDictList = OreDictionary.getOreNames();
        for(ToolType toolTypeEnum : ToolType.values()) {
            Set<String> autodetectValues = configurationSettings.getAutodetectBlocksList(toolTypeEnum);
            if(configurationSettings.getAutodetectBlocksToggle(toolTypeEnum)) {
                String[] toolTypeLookup = new String[] { "axe", "hoe", "pickaxe", "shears", "shovel" };
                String toolType = toolTypeLookup[toolTypeEnum.ordinal()];
                for(String oreDictEntry : oreDictList) {
                    for(String autodetectValue : autodetectValues) {
                        if(!autodetectValue.isEmpty() && oreDictEntry.startsWith(autodetectValue)) {
                            List<ItemStack> itemStacks = OreDictionary.getOres(oreDictEntry);
                            for(ItemStack item : itemStacks) {
                                if(item.getItem() instanceof ItemBlock) {
                                    String blockName = Item.itemRegistry.getNameForObject(item.getItem()).toString();
                                    if(blockName != null) {
                                        configurationSettings.addBlockToWhitelist(toolType, new BlockID(blockName, item.getItemDamage()));
                                    }
                                    try {
                                        // Some mods raise an exception when calling getDisplayName on blocks.
                                        MinerLogger.debug("Adding %s/%d (%s) to block whitelist for %s (%s:%s)", blockName, item.getItemDamage(), item.getDisplayName(), toolType, autodetectValue, oreDictEntry);
                                    }
                                    catch (Exception e) {
                                        // Left over from 1.5/1.6 where some mods were throwing exceptions on item.getDisplayName()
                                        // I just changed id => mod in 1.7
                                        logger.error("ERROR while looking at block with name %d. This is a bug with the respective mod.", blockName);
                                        logger.catching(e);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        configurationSettings.saveConfigs();
    }

    @SuppressWarnings("UnusedDeclaration")
    @EventHandler
    public void serverStarted(FMLServerStartedEvent event) {
        minerServer = new MinerServer(configurationValues);

        proxy.setMinerServer(minerServer);

        ServerCommandManager serverCommandManger = (ServerCommandManager) MinecraftServer.getServer().getCommandManager();
        serverCommandManger.registerCommand(new MinerCommand(minerServer));
    }

    @SuppressWarnings("UnusedDeclaration")
    @Mod.EventHandler
    public void imcCallback(FMLInterModComms.IMCEvent event) {
        for(final FMLInterModComms.IMCMessage message : event.getMessages()) {
            if("whitelist".equalsIgnoreCase(message.key) && message.isNBTMessage()) {
                NBTTagCompound nbtMessage = message.getNBTValue();

                String whitelistName = nbtMessage.getString("whitelistType");
                String toolType = nbtMessage.getString("toolType");
                String toolName = nbtMessage.getString("blockName");

                if(!configurationSettings.getToolTypeNames().contains(toolType)) {
                    MinerLogger.debug("Tool %s does not exist. Cannot add %s", toolType, toolName);
                    continue;
                }

                if("block".equalsIgnoreCase(whitelistName)) {
                    BlockID blockName = new BlockID(toolName);
                    MinerLogger.debug("Adding block %s %s to whitelist because of IMC", toolType, blockName.toString());
                    configurationSettings.addBlockToWhitelist(toolType, blockName);
                }
                else if("item".equalsIgnoreCase(whitelistName)) {
                    MinerLogger.debug("Adding item/tool %s %s to whitelist because of IMC", toolType, toolName);
                        configurationSettings.addTool(toolType, toolName);
                }
                configurationSettings.saveConfigs();
            }
            else if("addTool".equalsIgnoreCase(message.key) && message.isNBTMessage()) {
                NBTTagCompound nbtMessage = message.getNBTValue();
                String newToolType = nbtMessage.getString("toolType");
                String newToolName = nbtMessage.getString("toolName");
                String newToolIcon = nbtMessage.getString("toolIcon");

                MinerLogger.debug("Adding '%s' (Name: '%s' Icon: '%s')as a tool", newToolType, newToolName, newToolIcon);

                configurationSettings.addToolType(newToolType, newToolName, newToolIcon);
                configurationSettings.saveConfigs();
            }
            else if("addEqualBlocks".equalsIgnoreCase(message.key) && message.isNBTMessage()) {
                NBTTagCompound nbtMessage = message.getNBTValue();
                String block1 = nbtMessage.getString("existingBlock");
                String block2 = nbtMessage.getString("newBlock");

                configurationSettings.addCongruentBlocks(block1, block2);
            }
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void blockBreakEventFalse(BlockEvent.BreakEvent event) {
        Point eventPoint = new Point(event.pos.getX(), event.pos.getY(), event.pos.getZ());
        if(!event.world.isRemote && !event.isCanceled() && event.getPlayer() instanceof EntityPlayerMP && !minerServer.pointIsBlacklisted(eventPoint)) {
            MinerLogger.debug(String.format("Block Break (False) at %s | %s | %s || Cancel: %s / %s", event.pos.getX(), event.pos.getY(), event.pos.getZ(), !event.isCancelable(), event.isCanceled()));
            InjectedCalls.blockMined(event.world, (EntityPlayerMP) event.getPlayer(), event.pos, false, new BlockID(event.state));
            minerServer.removeFromBlacklist(eventPoint);
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void blockBreakEventTrue(BlockEvent.HarvestDropsEvent event) {
        Point eventPoint = new Point(event.pos.getX(), event.pos.getY(), event.pos.getZ());
        if(!event.world.isRemote && !event.isCanceled() && event.harvester instanceof EntityPlayerMP && !minerServer.pointIsBlacklisted(eventPoint)) {
            MinerLogger.debug(String.format("Block Break (True) at %s | %s | %s || Cancel: %s / %s", event.pos.getX(), event.pos.getY(), event.pos.getZ(), !event.isCancelable(), event.isCanceled()));
            InjectedCalls.blockMined(event.world, (EntityPlayerMP) event.harvester, event.pos, true, new BlockID(event.state));
            minerServer.removeFromBlacklist(eventPoint);
        }
    }
}
