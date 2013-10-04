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
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import portablejim.veinminer.configuration.ConfigurationValues;
import portablejim.veinminer.core.MinerInstance;
import portablejim.veinminer.event.EntityDropHook;
import portablejim.veinminer.lib.ModInfo;
import portablejim.veinminer.proxy.CommonProxy;
import portablejim.veinminer.server.MinerServer;
import portablejim.veinminer.server.PlayerStatus;
import portablejim.veinminer.util.BlockID;

@Mod(modid = ModInfo.MOD_ID, name = ModInfo.MOD_NAME, version = ModInfo.VERSION)
@NetworkMod(clientSideRequired = false, serverSideRequired = false, channels = { ModInfo.CHANNEL } )
public class VeinMiner {

    ConfigurationValues configurationValues;

    @Instance(ModInfo.MOD_ID)
    public static VeinMiner instance;

    @SidedProxy(clientSide = ModInfo.PROXY_CLIENT_CLASS, serverSide = ModInfo.PROXY_SERVER_CLASS)
    public static CommonProxy proxy;

    @PreInit
    public void preInit(FMLPreInitializationEvent event) {
        configurationValues = new ConfigurationValues(event.getSuggestedConfigurationFile());
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
    }

    public void blockMined(World world, EntityPlayerMP player, int x, int y, int z, boolean harvestBlockSuccess, BlockID blockId) {
        String output = String.format("Block mined at %d,%d,%d, result %b, block id is %d:%d", x, y, z, harvestBlockSuccess, blockId.id, blockId.metadata);
        MinerInstance ins = new MinerInstance(world, player, x, y, z, blockId, MinerServer.instance);
        MinerServer.instance.setPlayerStatus(player.username, PlayerStatus.SHIFT_ACTIVE);
        ins.mineVein(x, y, z);
        FMLLog.getLogger().info(output);
    }
}
