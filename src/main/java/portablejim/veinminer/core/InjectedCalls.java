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

package portablejim.veinminer.core;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.BlockPos;
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
 * Holds functions called by calls injected via ASM.
 */

@SuppressWarnings("UnusedDeclaration")
public class InjectedCalls {
    @SuppressWarnings("UnusedDeclaration")
    public static void blockMined(World world, EntityPlayerMP player, BlockPos blockPos, boolean harvestBlockSuccess, BlockID blockName) {
        MinerLogger.debug("Block mined at %d,%d,%d, result %s, block id is %s/%d", blockPos.getX(), blockPos.getY(), blockPos.getZ(), harvestBlockSuccess, blockName.name, blockName.metadata);

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

        ConfigurationSettings configurationSettings = VeinMiner.instance.minerServer.getConfigurationSettings();
        int radiusLimit = configurationSettings.getRadiusLimit();
        int blockLimit = configurationSettings.getBlockLimit();

        VeinminerInitalToolCheck startConfig = new VeinminerInitalToolCheck(player, radiusLimit, blockLimit, configurationSettings.getRadiusLimit(), configurationSettings.getBlockLimit());
        MinecraftForge.EVENT_BUS.post(startConfig);
        if(startConfig.allowVeinminerStart.isAllowed()) {
            radiusLimit = Math.min(startConfig.radiusLimit, radiusLimit);
            blockLimit = Math.min(startConfig.blockLimit, blockLimit);

            MinerInstance ins = new MinerInstance(world, player, blockPos.getX(), blockPos.getY(), blockPos.getZ(), blockName, VeinMiner.instance.minerServer, radiusLimit, blockLimit);
            ins.mineVein(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        }
    }
}
