package portablejim.veinminer.core;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import portablejim.veinminer.VeinMiner;
import portablejim.veinminer.api.VeinminerHarvestFailedCheck;
import portablejim.veinminer.api.VeinminerInitalToolCheck;
import portablejim.veinminer.configuration.ConfigurationSettings;
import portablejim.veinminer.lib.MinerLogger;
import portablejim.veinminer.server.MinerServer;
import portablejim.veinminer.util.BlockID;

/**
 * Created with IntelliJ IDEA.
 * User: james
 * Date: 20/02/14
 * Time: 9:22 AM
 * To change this template use File | Settings | File Templates.
 */
@SuppressWarnings("UnusedDeclaration")
public class InjectedCalls {
    @SuppressWarnings("UnusedDeclaration")
    public static void blockMined(World world, EntityPlayerMP player, int x, int y, int z, boolean harvestBlockSuccess, BlockID blockName) {
        MinerLogger.debug("Block mined at %d,%d,%d, result %s, block id is %s/%d", x, y, z, harvestBlockSuccess, blockName.name, blockName.metadata);

        if(blockName.name.isEmpty() || Block.getBlockFromName(blockName.name) == null  || !player.canHarvestBlock(Block.getBlockFromName(blockName.name))) {
            return;
        }

        if(!harvestBlockSuccess) {
            VeinminerHarvestFailedCheck startEvent = new VeinminerHarvestFailedCheck(player, blockName.name, blockName.metadata);
            MinecraftForge.EVENT_BUS.post(startEvent);
            if(startEvent.allowContinue.isDenied()) {
                return;
            }
        }

        ConfigurationSettings configurationSettings = VeinMiner.instance.configurationSettings;
        int radiusLimit = configurationSettings.getRadiusLimit();
        int blockLimit = configurationSettings.getBlockLimit();

        VeinminerInitalToolCheck startConfig = new VeinminerInitalToolCheck(player, radiusLimit, blockLimit, configurationSettings.getRadiusLimit(), configurationSettings.getBlockLimit());
        MinecraftForge.EVENT_BUS.post(startConfig);
        if(startConfig.allowVeinminerStart.isAllowed()) {
            radiusLimit = Math.min(startConfig.radiusLimit, radiusLimit);
            blockLimit = Math.min(startConfig.blockLimit, blockLimit);

            MinerInstance ins = new MinerInstance(world, player, x, y, z, blockName, MinerServer.instance, radiusLimit, blockLimit);
            ins.mineVein(x, y, z);
        }
    }
}
