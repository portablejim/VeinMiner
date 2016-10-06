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
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.FoodStats;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import portablejim.veinminer.api.Permission;
import portablejim.veinminer.api.VeinminerHarvestFailedCheck;
import portablejim.veinminer.api.VeinminerNoToolCheck;
import portablejim.veinminer.api.VeinminerPostUseTool;
import portablejim.veinminer.configuration.ConfigurationSettings;
import portablejim.veinminer.lib.BlockLib;
import portablejim.veinminer.lib.MinerLogger;
import portablejim.veinminer.server.MinerServer;
import portablejim.veinminer.util.BlockID;
import portablejim.veinminer.util.ExpCalculator;
import portablejim.veinminer.util.ItemStackID;
import portablejim.veinminer.util.PlayerStatus;
import portablejim.veinminer.api.Point;

import java.util.ArrayList;
import java.util.Collections;
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
    private HashSet<Point> startBlacklist;
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

    public MinerInstance(World world, EntityPlayerMP player, Point startPoint, BlockID blockID, MinerServer server, int radiusLimit, int blockLimit) {
        startBlacklist = new HashSet<Point>();
        destroyQueue = new ConcurrentLinkedQueue<Point>();
        awaitingEntityDrop = new HashSet<Point>();
        drops = new LinkedHashMap<ItemStackID, Integer>();
        this.world = world;
        this.player = player;
        targetBlock = blockID;
        finished = false;
        serverInstance = server;
        usedItem = player.getHeldItemMainhand();
        numBlocksMined = 1;
        initalBlock = startPoint;
        this.radiusLimit = radiusLimit;
        this.blockLimit = blockLimit;

        serverInstance.addInstance(this);

        FMLCommonHandler.instance().bus().register(this);
    }

    public Point getInitalBlock() {
        return initalBlock;
    }

    public EntityPlayerMP getPlayer() {
        return player;
    }

    public void cleanUp() {
        System.out.println("Cleaning up MinerInstance");
        FMLCommonHandler.instance().bus().unregister(this);
    }

    private boolean shouldContinue() {
        // Item equipped
        if(!serverInstance.getConfigurationSettings().getEnableAllTools() && player.getHeldItemMainhand() == null) {
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
                HarvestCheck event = new HarvestCheck(player, testBlock.getDefaultState(), false);
                MinecraftForge.EVENT_BUS.post(event);
                this.finished = !event.canHarvest();
            }
        }

        if(usedItem == null) {
            if(player.getHeldItemMainhand() != null) {
                this.finished = true;
            }
        }
        else if(player.getHeldItemMainhand() == null || !player.getHeldItemMainhand().isItemEqual(usedItem)) {
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
                player.addChatMessage(new TextComponentTranslation(problem));
            }
            else {
                String translatedProblem = I18n.translateToLocal(problem);
                player.addChatMessage(new TextComponentString(translatedProblem));
            }
        }

        // Experience
        int experienceMod = serverInstance.getConfigurationSettings().getExperienceMultiplier();
        if(experienceMod > 0 && ExpCalculator.getExp(player.experienceLevel, player.experience) < experienceMod) {
            this.finished = true;

            String problem = "mod.veinminer.finished.noExp";

            // Fix bugged xp
            if(player.experience < 0) player.experience = 0;
            if(player.experience > 1) player.experience = 1;
            if(player.experienceLevel < 0) player.experienceLevel = 0;
            player.addExperienceLevel(0);

            if(serverInstance.playerHasClient(player.getUniqueID())) {
                player.addChatMessage(new TextComponentTranslation(problem));
            }
            else {
                String translatedProblem = I18n.translateToLocal(problem);
                player.addChatMessage(new TextComponentString(translatedProblem));
            }
        }

        // Within mined block limits
        if (numBlocksMined >= blockLimit && blockLimit != -1) {
            MinerLogger.debug("Blocks mined: %d; Blocklimit: %d. Forcing finish.", numBlocksMined, blockLimit);
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

    private void takeHunger() {
        float hungerMod = ((float) serverInstance.getConfigurationSettings().getHungerMultiplier()) * 0.025F;
        FoodStats s = player.getFoodStats();
        NBTTagCompound nbt = new NBTTagCompound();
        s.writeNBT(nbt);
        int foodLevel = nbt.getInteger("foodLevel");
        int foodTimer = nbt.getInteger("foodTickTimer");
        float foodSaturationLevel = nbt.getFloat("foodSaturationLevel");
        float foodExhaustionLevel = nbt.getFloat("foodExhaustionLevel");

        float newExhaustion = (foodExhaustionLevel + hungerMod) % 4;
        float newSaturation = foodSaturationLevel - (float)((int)((foodExhaustionLevel + hungerMod) / 4));
        int newFoodLevel = foodLevel;
        if(newSaturation < 0) {
            newFoodLevel += newSaturation;
            newSaturation = 0;
        }
        nbt.setInteger("foodLevel", newFoodLevel);
        nbt.setInteger("foodTickTimer", foodTimer);
        nbt.setFloat("foodSaturationLevel", newSaturation);
        nbt.setFloat("foodExhaustionLevel", newExhaustion);

        s.readNBT(nbt);
    }

    private void takeExperience() {
        int targetLevel = player.experienceLevel;
        int expToTakeAway = serverInstance.getConfigurationSettings().getExperienceMultiplier();

        if(expToTakeAway == 0) {
            return;
        }

        if(expToTakeAway > player.experience * player.xpBarCap()) {
            int newExp = ExpCalculator.getExp(player.experienceLevel, player.experience) - expToTakeAway;
            while(ExpCalculator.getExp(targetLevel, 0) > newExp)
                targetLevel--;
            player.experienceLevel = targetLevel < 0 ? 0 : targetLevel;
            //expToTakeAway -= ExpCalculator.getExp(targetLevel, 0);
            int newExpTotal = newExp - ExpCalculator.getExp(targetLevel, 0);
            player.experience = Math.max(0, Math.min(1, (float)newExpTotal / player.xpBarCap()));
            player.experienceTotal = Math.max(0, newExpTotal);
            if(newExp <= 0) {
                player.experience = 0;
                player.experienceLevel = 0;
                player.experienceTotal = 0;
            }
        }
        else {
            player.addExperience(-expToTakeAway);
        }
        player.addExperienceLevel(0);
    }

    public  int mineBlock(Point point) {
        return mineBlock(point.getX(), point.getY(), point.getZ());
    }

    private int mineBlock(int x, int y, int z) {
        int mineSuccessful = 0;
        Point newPoint = new Point(x, y, z);
        BlockID newBlock = new BlockID(world, new BlockPos(x , y, z ));
        ConfigurationSettings configurationSettings = serverInstance.getConfigurationSettings();
        startBlacklist.add(newPoint);
        if(mineAllowed(newBlock, newPoint, configurationSettings)) {
            mineSuccessful = mineSuccessful | 1;
            awaitingEntityDrop.add(newPoint);
            boolean success = player.interactionManager.tryHarvestBlock(new BlockPos(x, y, z));
            numBlocksMined++;

            if(!player.capabilities.isCreativeMode) {
                takeHunger();
                takeExperience();
            }

            VeinminerPostUseTool toolUsedEvent = new VeinminerPostUseTool(player, newPoint);
            MinecraftForge.EVENT_BUS.post(toolUsedEvent);

            // Only go ahead if block was destroyed. Stops mining through protected areas.
            VeinminerHarvestFailedCheck continueCheck = new VeinminerHarvestFailedCheck(player, newPoint, targetBlock.name, targetBlock.metadata);
            MinecraftForge.EVENT_BUS.post(continueCheck);
            if (success || continueCheck.allowContinue.isAllowed()) {
                mineSuccessful = mineSuccessful | 2;
                postSuccessfulBreak(newPoint);
                awaitingEntityDrop.remove(newPoint);
                System.out.println("Mining Successful");
            } else {
                awaitingEntityDrop.remove(newPoint);
                System.out.println("Mining failed");
            }
        }

        return mineSuccessful;
    }


    public void postSuccessfulBreak(Point breakPoint) {
        ArrayList<Point> surrondingPoints = getPoints(breakPoint);
        destroyQueue.addAll(surrondingPoints);
    }

    private ArrayList<Point> getPoints(Point origin) {
        ArrayList<Point> points = new ArrayList<Point>(9);
        int dimRange[] = {-1, 0, 1};
        for(int dx : dimRange) {
            for(int dy : dimRange) {
                for(int dz : dimRange) {
                    if(dx == 0 && dy == 0 && dz == 0) {
                        // If 0, 0, 0
                        continue;
                    }
                    points.add(new Point(origin.getX() + dx, origin.getY() + dy, origin.getZ() + dz));
                }
            }
        }
        Collections.shuffle(points);
        return points;
    }

    private boolean mineAllowed(BlockID newBlock, Point newBlockPos, ConfigurationSettings configSettings) {
        if(finished || !shouldContinue()) return false;
        // Ensure valid block
        if (Block.getBlockFromName(newBlock.name) == null) {
            return false;
        }
        if (!newBlock.wildcardEquals(targetBlock) && !configSettings.areBlocksCongruent(newBlock, targetBlock)
                && !BlockLib.arePickBlockEqual(newBlock, targetBlock)) {
            return false;
        }
        if (!newBlockPos.isWithinRange(initalBlock, radiusLimit) && radiusLimit > 0) {
            MinerLogger.debug("Initial block: %d,%d,%d; New block: %d,%d,%d; Radius: %.2f; Raidus limit: %d.", initalBlock.getX(), initalBlock.getY(), initalBlock.getZ(), newBlockPos.getX(), newBlockPos.getY(), newBlockPos.getZ(), Math.sqrt(initalBlock.distanceFrom(newBlockPos)), radiusLimit);
            return false;
        }
        // Block already scheduled.
        if (awaitingEntityDrop.contains(newBlockPos))
            return false;
        //noinspection SimplifiableIfStatement
        if (numBlocksMined >= blockLimit && blockLimit != -1) {
            MinerLogger.debug("Block limit is: %d; Blocks mined: %d", blockLimit, numBlocksMined);
            return false;
        }
        // Seem to get wrong result if inlined. ??!??!
        //noinspection UnnecessaryLocalVariable
        boolean result =  (configSettings.getEnableAllBlocks() || toolAllowedForBlock(usedItem, newBlock));
        return result;
    }


    @SuppressWarnings({"UnusedParameters", "UnusedDeclaration"})
    @SubscribeEvent
    public void mineScheduled(ServerTickEvent event) {
        int quantity = serverInstance.getConfigurationSettings().getBlocksPerTick();
        int i = 0;
        while(i < quantity) {
            if (!destroyQueue.isEmpty()) {
                Point target = destroyQueue.remove();
                i += 1;
                int status = (mineBlock(target.getX(), target.getY(), target.getZ()) & 2);
                if (status != 2) {
                    System.out.println("Failed to remove block: " + status);
                }
                System.out.println("Emptying queue: " + i);
            } else {
            System.out.println("All mining tasks have been dequeued");
            // All blocks have been mined. This is done last.
            serverInstance.removeInstance(this);
            if(!drops.isEmpty()) {
                spawnDrops();
            }
            cleanUp();
            return;
            }
        }
    }

    private void spawnDrops() {
        for(Map.Entry<ItemStackID, Integer> schedDrop : drops.entrySet()) {
            ItemStackID itemStack = schedDrop.getKey();
            String itemName = itemStack.getItemId();

            Item foundItem = Item.getByNameOrId(itemName);
            if(foundItem == null) {
                continue;
            }

            int itemDamage = itemStack.getDamage();

            int numItems = schedDrop.getValue();
            while (numItems > itemStack.getMaxStackSize()) {
                ItemStack newItemStack = new ItemStack(foundItem, itemStack.getMaxStackSize(), itemDamage);
                EntityItem newEntityItem = new EntityItem(world, initalBlock.getX(), initalBlock.getY(), initalBlock.getZ(), newItemStack);
                world.spawnEntityInWorld(newEntityItem);
                numItems -= itemStack.getMaxStackSize();
            }
            ItemStack newItemStack = new ItemStack(foundItem, numItems, itemDamage);
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

    public boolean pointIsBlacklisted(Point point) {
        return startBlacklist.contains(point);
    }

    public void removeFromBlacklist(Point point) {
        if(startBlacklist.contains(point)) {
            startBlacklist.remove(point);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MinerInstance that = (MinerInstance) o;

        if (radiusLimit != that.radiusLimit) return false;
        if (blockLimit != that.blockLimit) return false;
        if (serverInstance != null ? !serverInstance.equals(that.serverInstance) : that.serverInstance != null)
            return false;
        if (world != null ? !world.equals(that.world) : that.world != null) return false;
        if (player != null ? !player.equals(that.player) : that.player != null) return false;
        if (usedItem != null ? !usedItem.equals(that.usedItem) : that.usedItem != null) return false;
        return initalBlock != null ? initalBlock.equals(that.initalBlock) : that.initalBlock == null;

    }

    @Override
    public int hashCode() {
        int result = serverInstance != null ? serverInstance.hashCode() : 0;
        result = 31 * result + (world != null ? world.hashCode() : 0);
        result = 31 * result + (player != null ? player.hashCode() : 0);
        result = 31 * result + (usedItem != null ? usedItem.hashCode() : 0);
        result = 31 * result + (initalBlock != null ? initalBlock.hashCode() : 0);
        result = 31 * result + radiusLimit;
        result = 31 * result + blockLimit;
        return result;
    }
}
