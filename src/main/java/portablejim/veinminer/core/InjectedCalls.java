package portablejim.veinminer.core;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import portablejim.veinminer.lib.MinerLogger;
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
    public static void blockMined(World world, EntityPlayerMP player, int x, int y, int z, boolean harvestBlockSuccess, BlockID blockId) {
        MinerLogger.debug("Block mined at %d,%d,%d, result %s, block id is %s/%d", x, y, z, harvestBlockSuccess, blockId.name, blockId.metadata);

        if(blockId.name.isEmpty() || Block.getBlockFromName(blockId.name) == null  || !player.canHarvestBlock(Block.getBlockFromName(blockId.name))) {
            return;
        }

        /*if(!harvestBlockSuccess) {
            VeinminerHarvestFailedCheck startEvent = new VeinminerHarvestFailedCheck(player, blockName.name, blockName.metadata);
            MinecraftForge.EVENT_BUS.post(startEvent);
            if(startEvent.allowContinue.isDenied()) {
                return;
            }
        }

        int radiusLimit = configurationSettings.getRadiusLimit();
        int blockLimit = configurationSettings.getBlockLimit();

        VeinminerInitalToolCheck startConfig = new VeinminerInitalToolCheck(player, radiusLimit, blockLimit, configurationSettings.getRadiusLimit(), configurationSettings.getBlockLimit());
        MinecraftForge.EVENT_BUS.post(startConfig);
        if(startConfig.allowVeinminerStart.isAllowed()) {
            radiusLimit = Math.min(startConfig.radiusLimit, radiusLimit);
            blockLimit = Math.min(startConfig.blockLimit, blockLimit);

            MinerInstance ins = new MinerInstance(world, player, x, y, z, blockName, MinerServer.instance, radiusLimit, blockLimit);
            ins.mineVein(x, y, z);
        }*/
    }
}
