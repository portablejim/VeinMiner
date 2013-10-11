package portablejim.veinminer.core;

import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.FoodStats;
import net.minecraft.world.World;
import portablejim.veinminer.configuration.ConfigurationSettings;
import portablejim.veinminer.event.InstanceTicker;
import portablejim.veinminer.server.MinerServer;
import portablejim.veinminer.server.PlayerStatus;
import portablejim.veinminer.util.BlockID;
import portablejim.veinminer.util.ItemStackID;
import portablejim.veinminer.util.Point;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
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
    private LinkedHashMap<ItemStackID, Integer> drops;
    private World world;
    private EntityPlayerMP player;
    private BlockID targetBlock;
    private boolean finished;
    private ItemStack usedItem;
    private int numBlocksMined;
    private Point initalBlock;
    private boolean cleanedUp;

    private static final int MIN_HUNGER = 1;

    public MinerInstance(World world, EntityPlayerMP player, int x, int y, int z, BlockID blockID, MinerServer server) {
        destroyQueue = new ConcurrentLinkedQueue<Point>();
        awaitingEntityDrop = new HashSet<Point>();
        drops = new LinkedHashMap<ItemStackID, Integer>();
        this.world = world;
        this.player = player;
        targetBlock = blockID;
        finished = false;
        serverInstance = server;
        usedItem = player.getCurrentEquippedItem();
        numBlocksMined = 0;
        initalBlock = new Point(x, y, z);
        cleanedUp = false;

        serverInstance.addInstance(this);

        TickRegistry.registerTickHandler(new InstanceTicker(this), Side.SERVER);
    }

    private boolean shouldContinue() {
        // Item equipped
        if(!serverInstance.getConfigurationSettings().getEnableAllTools() &&
                (player.getCurrentEquippedItem() == null || !player.getCurrentEquippedItem().isItemEqual(usedItem))) {
            this.finished = true;
        }

        this.finished = serverInstance.getUpdateToolAllowed(this.finished, player);

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
                (playerStatus == PlayerStatus.SNEAK_ACTIVE && !player.isSneaking()) ||
                (playerStatus == PlayerStatus.SNEAK_INACTIVE && player.isSneaking())) {
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
        Point newPoint = new Point(x, y, z);
        awaitingEntityDrop.add(newPoint);
        boolean success = player.theItemInWorldManager.tryHarvestBlock(x, y, z);
        // Only go ahead if block was destroyed. Stops mining through protected areas.
        if(success) {
            destroyQueue.add(newPoint);
        }
        else {
            awaitingEntityDrop.remove(newPoint);
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

                    if(configSettings.getEnableAllBlocks() || toolAllowedForBlock(usedItem, newBlock)) {
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
            else if(!drops.isEmpty()){
                // All blocks have been mined. This is done last.
                serverInstance.removeInstance(this);
                spawnDrops();
                cleanedUp = true;
                return;
            }
        }
    }

    private void spawnDrops() {
        for(Map.Entry<ItemStackID, Integer> schedDrop : drops.entrySet()) {
            ItemStackID itemStack = schedDrop.getKey();
            int numItems = schedDrop.getValue();
            while (numItems > itemStack.getMaxStackSize()) {
                ItemStack newItemStack = new ItemStack(itemStack.getItemId(), itemStack.getMaxStackSize(), itemStack.getDamage());
                EntityItem newEntityItem = new EntityItem(world, initalBlock.getX(), initalBlock.getY(), initalBlock.getZ(), newItemStack);
                world.spawnEntityInWorld(newEntityItem);
                numItems -= itemStack.getMaxStackSize();
            }
            ItemStack newItemStack = new ItemStack(itemStack.getItemId(), numItems, itemStack.getDamage());
            EntityItem newEntityItem = new EntityItem(world, initalBlock.getX(), initalBlock.getY(), initalBlock.getZ(), newItemStack);
            world.spawnEntityInWorld(newEntityItem);
        }
        drops.clear();
    }

    public boolean isRegistered(Point p) {
        return awaitingEntityDrop.contains(p);
    }

    public void addDrop(EntityItem entity, Point point) {
        ItemStack item = entity.getEntityItem();
        ItemStackID itemInfo = new ItemStackID(item.itemID, item.getItemDamage(), item.getMaxStackSize());

        if(drops.containsKey(itemInfo)) {
            int oldDropNumber = drops.get(itemInfo);
            int newDropNumber = oldDropNumber + item.stackSize;
            drops.put(itemInfo, newDropNumber);
        }
        else {
            drops.put(itemInfo, item.stackSize);
        }

        awaitingEntityDrop.remove(point);
    }
}
