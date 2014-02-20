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

import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import net.minecraft.block.Block;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.logging.log4j.Logger;
import portablejim.veinminer.api.ToolType;
import portablejim.veinminer.configuration.ConfigurationSettings;
import portablejim.veinminer.configuration.ConfigurationValues;
import portablejim.veinminer.lib.MinerLogger;
import portablejim.veinminer.lib.ModInfo;
import portablejim.veinminer.network.ChannelHandler;
import portablejim.veinminer.proxy.CommonProxy;
import portablejim.veinminer.server.MinerServer;
import portablejim.veinminer.util.BlockID;

import java.util.ArrayList;
import java.util.Set;

/**
 * This class is the main mod class for VeinMiner. It is loaded as a mod
 * through ForgeModLoader.
 */

@Mod(modid = ModInfo.MODID, acceptedMinecraftVersions = "[1.7,1.8)")
public class VeinMiner extends DummyModContainer{

    @Instance(ModInfo.MODID)
    public static VeinMiner instance;

    @SidedProxy(clientSide = ModInfo.PROXY_CLIENT_CLASS, serverSide = ModInfo.PROXY_SERVER_CLASS)
    public static CommonProxy proxy;

    public ChannelHandler channelHandler;

    ConfigurationValues configurationValues;
    public ConfigurationSettings configurationSettings;

    public Logger logger;

    @SuppressWarnings("UnusedDeclaration")
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();

        configurationValues = new ConfigurationValues(event.getSuggestedConfigurationFile());
        configurationValues.loadConfigFile();
        configurationSettings = new ConfigurationSettings(configurationValues);
        proxy.registerKeybind();
    }

    @SuppressWarnings("UnusedDeclaration")
    @EventHandler
    public void init(@SuppressWarnings("UnusedParameters") FMLInitializationEvent event) {
        //TODO: Enable when EntityDropHook added.
        //MinecraftForge.EVENT_BUS.register(new EntityDropHook());

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
        for(ToolType toolType : ToolType.values()) {
            Set<String> autodetectValues = configurationSettings.getAutodetectBlocksList(toolType);
            if(configurationSettings.getAutodetectBlocksToggle(toolType)) {
                for(String oreDictEntry : oreDictList) {
                    for(String autodetectValue : autodetectValues) {
                        if(!autodetectValue.isEmpty() && oreDictEntry.startsWith(autodetectValue)) {
                            ArrayList<ItemStack> itemStacks = OreDictionary.getOres(oreDictEntry);
                            for(ItemStack item : itemStacks) {
                                if(item.getItem() instanceof ItemBlock) {
                                    String blockName = Block.blockRegistry.getNameForObject(item.getItem());
                                    configurationSettings.addBlockToWhitelist(toolType, new BlockID(blockName, item.getItemDamage()));
                                    try {
                                        // Some mods raise an exception when calling getDisplayName on blocks.
                                        MinerLogger.debug("Adding %d:%d (%s) to block whitelist for %s (%s:%s)", blockName, item.getItemDamage(), item.getDisplayName(), toolType.toString(), autodetectValue, oreDictEntry);
                                    }
                                    catch (Exception e) {
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
        new MinerServer(configurationValues);

        ServerCommandManager serverCommandManger = (ServerCommandManager) MinecraftServer.getServer().getCommandManager();
        // TODO: Enable when MinerCommand is fixed.
        //serverCommandManger.registerCommand(new MinerCommand());
    }
}
