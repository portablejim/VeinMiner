package portablejim.veinminer.core;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import portablejim.veinminer.VeinMiner;
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

        MinerInstance instance = new MinerInstance(event.getWorld(), (EntityPlayerMP) event.getPlayer(), event.getPos().getX(), event.getPos().getY(), event.getPos().getZ(), new BlockID(event.getState()), server, radiusLimit, blockLimit);

        if(instance.mineBlock(breakPont) > 0) {
            event.setCanceled(true);
        }
    }

    public void collectDrops(BlockEvent.HarvestDropsEvent event) {

    }
}
