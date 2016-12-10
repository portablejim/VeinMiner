package portablejim.veinminer.core;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import portablejim.veinminer.VeinMiner;
import portablejim.veinminer.api.VeinminerInitalToolCheck;
import portablejim.veinminer.configuration.ConfigurationSettings;
import portablejim.veinminer.server.MinerServer;
import portablejim.veinminer.util.BlockID;
import portablejim.veinminer.util.Compatibility;
import portablejim.veinminer.api.Point;

/**
 * Created by james on 27/05/16.
 */
public class CoreEvents {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void blockBreakEvent(BlockEvent.BreakEvent event) {
        if(event.getWorld().isRemote) {
            // I am officially lost.
            // I am a server method but I find myself on the client.
            return;
        }

        Point breakPont = Compatibility.getPoint(event);
        MinerServer server = VeinMiner.instance.minerServer;
        if(server == null || server.pointIsBlacklisted(breakPont)) {
           return;
        }

        if(event.getPlayer() != null && server.getInstance(event.getPlayer()) != null) {
            // Ignore additional blocks for the player whilst still veinmining.
            return;
        }

        ConfigurationSettings configurationSettings = VeinMiner.instance.minerServer.getConfigurationSettings();
        int radiusLimit = configurationSettings.getRadiusLimit();
        int blockLimit = configurationSettings.getBlockLimit();

        VeinminerInitalToolCheck startConfig = new VeinminerInitalToolCheck(event.getPlayer(), breakPont, radiusLimit, blockLimit, configurationSettings.getRadiusLimit(), configurationSettings.getBlockLimit());
        MinecraftForge.EVENT_BUS.post(startConfig);
        if(startConfig.allowVeinminerStart.isAllowed()) {
            radiusLimit = Math.min(startConfig.radiusLimit, radiusLimit);
            blockLimit = Math.min(startConfig.blockLimit, blockLimit);
            //MinerInstance instance = new MinerInstance(event.world, (EntityPlayerMP) event.getPlayer(), Compatibility.getPoint(event), new BlockID(Block.blockRegistry.getNameForObject(event.block), event.blockMetadata), server, radiusLimit, blockLimit);
            MinerInstance instance = new MinerInstance(event.getWorld(), (EntityPlayerMP) event.getPlayer(), Compatibility.getPoint(event), new BlockID(event.getState()), server, radiusLimit, blockLimit);

            if (instance.mineBlock(breakPont) > 0) {
                event.setCanceled(true);
            }
        }
    }

    public void collectDrops(BlockEvent.HarvestDropsEvent event) {

    }
}
