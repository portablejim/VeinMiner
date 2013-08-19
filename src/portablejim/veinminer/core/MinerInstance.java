package portablejim.veinminer.core;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemInWorldManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.FoodStats;
import net.minecraft.world.World;
import portablejim.veinminer.configuration.ConfigurationSettings;
import portablejim.veinminer.server.MinerServer;
import portablejim.veinminer.server.PlayerStatus;
import portablejim.veinminer.util.BlockID;
import portablejim.veinminer.util.Point;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created with IntelliJ IDEA.
 * User: james
 * Date: 13/08/13
 * Time: 8:48 AM
 * To change this template use File | Settings | File Templates.
 */
public class MinerInstance {
    public MinerServer serverInstance;
    private ConcurrentLinkedQueue<Point> destroyQueue;
    private HashSet<Point> awaitingEntityDrop;
    private LinkedHashMap<BlockID, Integer> drops;
    private World world;
    private EntityPlayerMP player;
    private BlockID targetBlock;
    private boolean finished;
    private ItemStack usedItem;

    private static final int MIN_HUNGER = 1;

    public MinerInstance(World world, EntityPlayerMP player, int x, int y, int z, BlockID blockID, MinerServer server) {
        destroyQueue = new ConcurrentLinkedQueue<Point>();
        awaitingEntityDrop = new HashSet<Point>();
        drops = new LinkedHashMap<BlockID, Integer>();
        this.world = world;
        this.player = player;
        targetBlock = blockID;
        finished = false;
        serverInstance = server;
        usedItem = player.getCurrentEquippedItem();
    }

    private boolean shouldContinue() {
        if(!player.getCurrentEquippedItem().isItemEqual(usedItem)) {
            this.finished = true;
        }
        FoodStats food = player.getFoodStats();
        if(food.getFoodLevel() < MIN_HUNGER) {
            this.finished = true;
        }
        String playerName = player.getEntityName();
        PlayerStatus playerStatus = serverInstance.getPlayerStatus(playerName);
        if(playerStatus == null) {
            this.finished = true;
        }
        else if(playerStatus == PlayerStatus.DISABLED || playerStatus == PlayerStatus.INACTIVE ||
                (playerStatus == PlayerStatus.SHIFT_ACTIVE && !player.isSneaking()) ||
                (playerStatus == PlayerStatus.SHIFT_INACTIVE && player.isSneaking())) {
            this.finished = true;
        }

        return !this.finished;
    }

    private boolean toolAllowedForBlock(ItemStack tool, BlockID block) {
        for(ConfigurationSettings.ToolType type : ConfigurationSettings.ToolType.values()) {
            if(serverInstance.getConfigurationSettings().toolIsOfType(tool, type)) {
                return serverInstance.getConfigurationSettings().whiteListHasBlockId(type, block);
            }
        }
        return false;
    }

    private void mineBlock(int x, int y, int z) {
        player.theItemInWorldManager.tryHarvestBlock(x, y, z);
    }

    public synchronized void mineVein(int x, int y, int z) {
        if(this.world == null || this.player == null || this.targetBlock == null) {
            finished = true;
        }
        if(finished || !shouldContinue()) {
            return;
        }

        byte d = 1;
        for (int dx = -d; dx <= d; dx++) {
            for (int dy = -d; dy <= d; dy++) {
                for (int dz = -d; dz <= d; dz++) {
                    if (dx == 0 && dy == 0 && dz == 0) {
                        continue;
                    }

                    Point newBlockPos = new Point(x + dx, y + dy, z + dz);
                    BlockID newBlock = new BlockID(world, x + dx, y + dy, z + dz);

                    if(Block.blocksList[newBlock.id] == null) {
                        continue;
                    }
                    // Block already scheduled.
                    if(awaitingEntityDrop.contains(newBlockPos)) {
                        continue;
                    }

                    if(toolAllowedForBlock(usedItem, newBlock)) {
                        mineBlock(x, y, z);
                    }
                }
            }
        }
    }
}
