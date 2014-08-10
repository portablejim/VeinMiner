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

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.FoodStats;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import portablejim.veinminer.api.Permission;
import portablejim.veinminer.api.VeinminerHarvestFailedCheck;
import portablejim.veinminer.api.VeinminerNoToolCheck;
import portablejim.veinminer.api.VeinminerPostUseTool;
import portablejim.veinminer.configuration.ConfigurationSettings;
import portablejim.veinminer.lib.BlockLib;
import portablejim.veinminer.server.MinerServer;
import portablejim.veinminer.util.BlockID;
import portablejim.veinminer.util.ItemStackID;
import portablejim.veinminer.util.PlayerStatus;
import portablejim.veinminer.util.Point;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import static net.minecraftforge.event.entity.player.PlayerEvent.HarvestCheck;

/**
 * Main class that performs the work of VeinMiner. It is initialised when a
 * block is mined and then is finished after the vein is mined.
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
    private int radiusLimit;
    private int blockLimit;

    private static final int MIN_HUNGER = 1;

    public MinerInstance(World world, EntityPlayerMP player, int x, int y, int z, BlockID blockID, MinerServer server, int radiusLimit, int blockLimit) {
        destroyQueue = new ConcurrentLinkedQueue<Point>();
        awaitingEntityDrop = new HashSet<Point>();
        drops = new LinkedHashMap<ItemStackID, Integer>();
        this.world = world;
        this.player = player;
        targetBlock = blockID;
        finished = false;
        serverInstance = server;
        usedItem = player.getCurrentEquippedItem();
        numBlocksMined = 1;
        initalBlock = new Point(x, y, z);
        this.radiusLimit = radiusLimit;
        this.blockLimit = blockLimit;

        serverInstance.addInstance(this);

        FMLCommonHandler.instance().bus().register(this);
    }

    private boolean shouldContinue() {
        // Item equipped
        if(!serverInstance.getConfigurationSettings().getEnableAllTools() && player.getCurrentEquippedItem() == null) {
            VeinminerNoToolCheck toolCheck = new VeinminerNoToolCheck(player);
            MinecraftForge.EVENT_BUS.post(toolCheck);

            if(toolCheck.allowTool.isAllowed()) {
                this.finished = false;
            }
            else if(toolCheck.allowTool == Permission.FORCE_DENY) {
                this.finished = true;
            }
            else {
                // Test to see if the player can mine stone.
                // If they can, they have other assistance and so should be
                // considered a tool.
                Block testBlock = Blocks.stone;
                HarvestCheck event = new HarvestCheck(player, testBlock, false);
                MinecraftForge.EVENT_BUS.post(event);
                this.finished = !event.success;
            }
        }

        if(usedItem == null) {
            if(player.getCurrentEquippedItem() != null) {
                this.finished = true;
            }
        }
        else if(player.getCurrentEquippedItem() == null || !player.getCurrentEquippedItem().isItemEqual(usedItem)) {
            this.finished = true;
        }

        // Player exists and is in correct status (correct button held)
        UUID playerName = player.getUniqueID();
        PlayerStatus playerStatus = serverInstance.getPlayerStatus(playerName);
        if(playerStatus == null) {
            this.finished = true;
        }
        else if(playerStatus == PlayerStatus.INACTIVE ||
                (playerStatus == PlayerStatus.SNEAK_ACTIVE && !player.isSneaking()) ||
                (playerStatus == PlayerStatus.SNEAK_INACTIVE && player.isSneaking())) {
            this.finished = true;
        }

        if(finished) {
            return false;
        }

        // Not hungry
        FoodStats food = player.getFoodStats();
        if(food.getFoodLevel() < MIN_HUNGER) {
            this.finished = true;

            String problem = "mod.veinminer.finished.tooHungry";
            if(serverInstance.playerHasClient(player.getUniqueID())) {
                player.addChatMessage(new ChatComponentTranslation(problem));
            }
            else {
                String translatedProblem = StatCollector.translateToLocal(problem);
                player.addChatMessage(new ChatComponentText(translatedProblem));
            }
        }

        // Within mined block limits
        if (numBlocksMined >= blockLimit && blockLimit != -1) {
            this.finished = true;
        }

        return !this.finished;
    }

    private boolean toolAllowedForBlock(ItemStack tool, BlockID block) {
        boolean toolAllowed = false;
        ConfigurationSettings settings = serverInstance.getConfigurationSettings();
        for(String type : settings.getToolTypeNames()) {
            if(settings.toolIsOfType(tool, type)) {
                if(serverInstance.getConfigurationSettings().whiteListHasBlockId(type, block)) {
                    toolAllowed = true;
                }
            }
        }
        return toolAllowed;
    }

    private void mineBlock(int x, int y, int z) {
        Point newPoint = new Point(x, y, z);
        awaitingEntityDrop.add(newPoint);
        boolean success = player.theItemInWorldManager.tryHarvestBlock(x, y, z);
        numBlocksMined++;

        VeinminerPostUseTool toolUsedEvent = new VeinminerPostUseTool(player);
        MinecraftForge.EVENT_BUS.post(toolUsedEvent);

        // Only go ahead if block was destroyed. Stops mining through protected areas.
        VeinminerHarvestFailedCheck continueCheck = new VeinminerHarvestFailedCheck(player, targetBlock.name, targetBlock.metadata);
        MinecraftForge.EVENT_BUS.post(continueCheck);
        if(success || continueCheck.allowContinue.isAllowed()) {
            destroyQueue.add(newPoint);
        }
        else {
            awaitingEntityDrop.remove(newPoint);
        }
    }

    @SuppressWarnings("ConstantConditions")
    public synchronized void mineVein(int x, int y, int z) {
        if(this.world == null || this.player == null || this.targetBlock == null) {
            finished = true;
        }
        if(finished || !shouldContinue()) {
            return;
        }

        player.addExhaustion(0.03F);

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
                    if(Block.getBlockFromName(newBlock.name) == null) {
                        continue;
                    }

                    ConfigurationSettings configSettings = serverInstance.getConfigurationSettings();

                    if(!newBlock.wildcardEquals(targetBlock) && !configSettings.areBlocksCongruent(newBlock, targetBlock)
                            && !BlockLib.arePickBlockEqual(newBlock, targetBlock)) {
                        continue;
                    }

                    if(!newBlockPos.isWithinRange(initalBlock, radiusLimit) && radiusLimit > 0) {
                        continue;
                    }

                    // Block already scheduled.
                    if(awaitingEntityDrop.contains(newBlockPos)) {
                        continue;
                    }

                    int blockLimit = serverInstance.getConfigurationSettings().getBlockLimit();
                    if (numBlocksMined >= blockLimit && blockLimit != -1) {
                        continue;
                    }

                    if(configSettings.getEnableAllBlocks() || toolAllowedForBlock(usedItem, newBlock)) {
                        mineBlock(x + dx, y + dy, z + dz);
                        //numBlocksMined++;
                    }
                }
            }
        }
    }

    @SuppressWarnings({"UnusedParameters", "UnusedDeclaration"})
    @SubscribeEvent
    public void mineScheduled(ServerTickEvent event) {
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
                return;
            }
        }
    }

    private void spawnDrops() {
        for(Map.Entry<ItemStackID, Integer> schedDrop : drops.entrySet()) {
            ItemStackID itemStack = schedDrop.getKey();
            String[] itemNames = itemStack.getItemId().split(":", 2);

            if(itemNames.length < 2 || GameRegistry.findItemStack(itemNames[0], itemNames[1], 1) == null) {
                continue;
            }

            int numItems = schedDrop.getValue();
            while (numItems > itemStack.getMaxStackSize()) {
                ItemStack newItemStack = GameRegistry.findItemStack(itemNames[0], itemNames[1], itemStack.getMaxStackSize());
                newItemStack.setItemDamage(itemStack.getDamage());
                EntityItem newEntityItem = new EntityItem(world, initalBlock.getX(), initalBlock.getY(), initalBlock.getZ(), newItemStack);
                world.spawnEntityInWorld(newEntityItem);
                numItems -= itemStack.getMaxStackSize();
            }
            ItemStack newItemStack = GameRegistry.findItemStack(itemNames[0], itemNames[1], numItems);
            newItemStack.setItemDamage(itemStack.getDamage());
            EntityItem newEntityItem = new EntityItem(world, initalBlock.getX(), initalBlock.getY(), initalBlock.getZ(), newItemStack);
            world.spawnEntityInWorld(newEntityItem);
        }
        drops.clear();
    }

    public boolean isRegistered(Point p) {
        return awaitingEntityDrop.contains(p);
    }

    public void addDrop(EntityItem entity) {
        ItemStack item = entity.getEntityItem();
        ItemStackID itemInfo = new ItemStackID(item.getItem(), item.getItemDamage(), item.getMaxStackSize());

        if(drops.containsKey(itemInfo)) {
            int oldDropNumber = drops.get(itemInfo);
            int newDropNumber = oldDropNumber + item.stackSize;
            drops.put(itemInfo, newDropNumber);
        }
        else {
            drops.put(itemInfo, item.stackSize);
        }
    }
}
