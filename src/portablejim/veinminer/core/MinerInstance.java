package portablejim.veinminer.core;

import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemInWorldManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.FoodStats;
import net.minecraft.world.World;
import portablejim.veinminer.configuration.ConfigurationSettings;
import portablejim.veinminer.event.InstanceTicker;
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
    private int numBlocksMined;
    private Point initalBlock;

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
        numBlocksMined = 0;
        initalBlock = new Point(x, y, z);
        TickRegistry.registerTickHandler(new InstanceTicker(this), Side.SERVER);
    }

    private boolean shouldContinue() {
        // Item equipped
        if(player.getCurrentEquippedItem() == null || !player.getCurrentEquippedItem().isItemEqual(usedItem)) {
            this.finished = true;
        }

        // Not hungry
        FoodStats food = player.getFoodStats();
        if(food.getFoodLevel() < MIN_HUNGER) {
            this.finished = true;
        }


        // Player exists and is in correct status (correct button held)
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

        // Within mined block limits
        if(numBlocksMined < serverInstance.getConfigurationSettings().getBlockLimit()) {
            numBlocksMined++;
        }
        else {
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
        boolean success = player.theItemInWorldManager.tryHarvestBlock(x, y, z);
        // Only go ahead if block was destroyed. Stops mining through protected areas.
        if(success) {
            Point newPoint = new Point(x, y, z);
            destroyQueue.add(newPoint);
        }
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

                    // Ensure valid block
                    if(Block.blocksList[newBlock.id] == null) {
                        continue;
                    }

                    ConfigurationSettings configSettings = serverInstance.getConfigurationSettings();

                    if(!newBlock.equals(targetBlock) && !configSettings.areBlocksCongruent(newBlock, targetBlock)) {
                        continue;
                    }

                    if(!newBlockPos.isWithinRange(initalBlock, configSettings.getRadiusLimit())) {
                        continue;
                    }

                    // Block already scheduled.
                    if(awaitingEntityDrop.contains(newBlockPos)) {
                        continue;
                    }

                    if(toolAllowedForBlock(usedItem, newBlock)) {
                        mineBlock(x + dx, y + dy, z + dz);
                    }
                }
            }
        }
    }

    public void mineScheduled() {
        int quantity = serverInstance.getConfigurationSettings().getBlocksPerTick();
        for(int i = 0; i < quantity; i++) {
            if(!destroyQueue.isEmpty()) {
                Point target = destroyQueue.remove();
                mineVein(target.getX(), target.getY(), target.getZ());
            }
        }
    }
}
