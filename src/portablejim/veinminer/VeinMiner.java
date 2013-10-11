package portablejim.veinminer;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.ServerStarted;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.command.ICommandManager;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import portablejim.veinminer.configuration.ConfigurationValues;
import portablejim.veinminer.core.MinerInstance;
import portablejim.veinminer.event.EntityDropHook;
import portablejim.veinminer.lib.ModInfo;
import portablejim.veinminer.network.ConnectionHandler;
import portablejim.veinminer.network.PacketHandler;
import portablejim.veinminer.proxy.CommonProxy;
import portablejim.veinminer.server.MinerCommand;
import portablejim.veinminer.server.MinerServer;
import portablejim.veinminer.server.PlayerStatus;
import portablejim.veinminer.util.BlockID;

@Mod(modid = ModInfo.MOD_ID, name = ModInfo.MOD_NAME, version = ModInfo.VERSION)
@NetworkMod(clientSideRequired = false, serverSideRequired = false, channels = { ModInfo.CHANNEL },
        packetHandler = PacketHandler.class, connectionHandler = ConnectionHandler.class)
public class VeinMiner {

    ConfigurationValues configurationValues;

    @Instance(ModInfo.MOD_ID)
    public static VeinMiner instance;

    @SidedProxy(clientSide = ModInfo.PROXY_CLIENT_CLASS, serverSide = ModInfo.PROXY_SERVER_CLASS)
    public static CommonProxy proxy;

    @PreInit
    public void preInit(FMLPreInitializationEvent event) {
        configurationValues = new ConfigurationValues(event.getSuggestedConfigurationFile());
        proxy.setupConfig(configurationValues);
        proxy.registerKeybind();
    }

    @Init
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new EntityDropHook());

    }

    @PostInit
    public void postInit(FMLPostInitializationEvent event) {

    }

    @ServerStarted
    public void serverStarted(FMLServerStartedEvent event) {
        new MinerServer(configurationValues);

        LanguageRegistry.instance().addStringLocalization("command.veinminer", "/veinminer enable");
        LanguageRegistry.instance().addStringLocalization("command.veinminer.enable", "/veinminer enable disable/auto/sneak/no_sneak");
        LanguageRegistry.instance().addStringLocalization("command.veinminer.set.disable", "Veinminer activation: disabled");
        LanguageRegistry.instance().addStringLocalization("command.veinminer.set.auto", "Veinminer activation: Using client keybind (useless without mod on client)");
        LanguageRegistry.instance().addStringLocalization("command.veinminer.set.sneak", "Veinminer activation: When sneaking");
        LanguageRegistry.instance().addStringLocalization("command.veinminer.set.nosneak", "Veinminer activation: When not sneaking");

        ServerCommandManager serverCommandManger = (ServerCommandManager) MinecraftServer.getServer().getCommandManager();
        serverCommandManger.registerCommand(new MinerCommand());
    }

    public void blockMined(World world, EntityPlayerMP player, int x, int y, int z, boolean harvestBlockSuccess, BlockID blockId) {
        String output = String.format("Block mined at %d,%d,%d, result %b, block id is %d:%d", x, y, z, harvestBlockSuccess, blockId.id, blockId.metadata);
        MinerInstance ins = new MinerInstance(world, player, x, y, z, blockId, MinerServer.instance);
        ins.mineVein(x, y, z);
        FMLLog.getLogger().info(output);
    }
}
