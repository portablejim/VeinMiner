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
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import portablejim.veinminer.configuration.ConfigurationSettings;
import portablejim.veinminer.configuration.ConfigurationValues;
import portablejim.veinminer.lib.MinerLogger;
import portablejim.veinminer.lib.ModInfo;
import portablejim.veinminer.network.ChannelHandler;
import portablejim.veinminer.proxy.CommonProxy;

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

    @SuppressWarnings("UnusedDeclaration")
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
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
}
