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

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import portablejim.veinminer.server.MinerServer;
import portablejim.veinminer.api.Point;

/**
 * Hooks into the entity that are dropped into the world to stop entities that
 * are dropped from the VeinMiner process from being dropped normally, instead
 * adding it to VeinMiner's entity drops.
 */

public class EntityDropHook {
    private MinerServer minerServer = null;

    public EntityDropHook() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void setServer(MinerServer server) {
        minerServer = server;
    }

    @SuppressWarnings("UnusedDeclaration")
    @SubscribeEvent
    public void tryAddEntity(EntityJoinWorldEvent event) {
        Entity entity = event.getEntity();

        if(event.getWorld().isRemote) {
            return;
        }

        if(event.isCanceled()) {
            return;
        }

        if(minerServer == null) {
            return;
        }

        int entityX = (int)Math.floor(entity.posX);
        int entityY = (int)Math.floor(entity.posY);
        int entityZ = (int)Math.floor(entity.posZ);

        // Only return for what we are waiting for.
        if(!minerServer.awaitingDrop(new Point(entityX, entityY, entityZ))) {
            return;
        }

        if(!EntityItem.class.isInstance(entity)) {
            return;
        }

        EntityItem entityItem = (EntityItem) entity;

        /* Possibly obsolete
        if(entityItem.getDataWatcher().getWatchableObjectItemStack(10) == null) {
            return;
        } */

        if(entityItem.getEntityItem().hasTagCompound()) {
            return;
        }

        boolean isBlock;
        boolean isItem;

        ResourceLocation uniqueId = Item.REGISTRY.getNameForObject(entityItem.getEntityItem().getItem());

        isBlock = GameRegistry.findBlock(uniqueId.getResourceDomain(), uniqueId.getResourcePath()) != null;
        isItem = GameRegistry.findItem(uniqueId.getResourceDomain(), uniqueId.getResourcePath()) != null;

        StackTraceElement[] stackTrace = (new Throwable()).getStackTrace();
        boolean veinminerMethod = false;
        for(StackTraceElement element : stackTrace) {
            if(MinerInstance.class.getCanonicalName().equals(element.getClassName()) && "mineBlock".equals(element.getMethodName())) {
                veinminerMethod = true;
                break;
            }
        }

        //new MinerServer();
        if((isBlock || isItem) && veinminerMethod) {
            minerServer.addEntity(entity);
            event.setCanceled(true);
        }
    }
}
