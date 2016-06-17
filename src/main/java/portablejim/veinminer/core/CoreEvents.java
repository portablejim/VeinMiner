package portablejim.veinminer.core;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.ItemInWorldManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import portablejim.veinminer.VeinMiner;
import portablejim.veinminer.api.VeinminerInitalToolCheck;
import portablejim.veinminer.configuration.ConfigurationSettings;
import portablejim.veinminer.server.MinerServer;
import portablejim.veinminer.util.BlockID;
import portablejim.veinminer.util.Compatibility;
import portablejim.veinminer.util.Point;

import java.util.logging.Logger;

/**
 * Created by james on 27/05/16.
 */
public class CoreEvents {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void blockBreakEvent(BlockEvent.BreakEvent event) {
        Point breakPont = Compatibility.getPoint(event);
        MinerServer server = VeinMiner.instance.minerServer;
        if(server.pointIsBlacklisted(breakPont)) {
           return;
        }

        ConfigurationSettings configurationSettings = VeinMiner.instance.minerServer.getConfigurationSettings();
        int radiusLimit = configurationSettings.getRadiusLimit();
        int blockLimit = configurationSettings.getBlockLimit();

        VeinminerInitalToolCheck startConfig = new VeinminerInitalToolCheck(event.getPlayer(), radiusLimit, blockLimit, configurationSettings.getRadiusLimit(), configurationSettings.getBlockLimit());
        MinecraftForge.EVENT_BUS.post(startConfig);
        if(startConfig.allowVeinminerStart.isAllowed()) {
            radiusLimit = Math.min(startConfig.radiusLimit, radiusLimit);
            blockLimit = Math.min(startConfig.blockLimit, blockLimit);
            MinerInstance instance = new MinerInstance(event.world, (EntityPlayerMP) event.getPlayer(), Compatibility.getPoint(event), new BlockID(Block.blockRegistry.getNameForObject(event.block), event.blockMetadata), server, radiusLimit, blockLimit);

            if (instance.mineBlock(breakPont) > 0) {
                event.setCanceled(true);
            }
        }
    }

    public void collectDrops(BlockEvent.HarvestDropsEvent event) {

    }
}
