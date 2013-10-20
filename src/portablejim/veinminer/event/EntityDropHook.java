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

package portablejim.veinminer.event;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import portablejim.veinminer.server.MinerServer;

/**
 * Hooks into the entity that are dropped into the world to stop entities that
 * are dropped from the VeinMiner process from being dropped normally, instead
 * adding it to VeinMiner's entity drops.
 */

public class EntityDropHook {

    @ForgeSubscribe
    public void tryAddEntity(EntityJoinWorldEvent event) {
        Entity entity = event.entity;

        if(event.isCanceled()) {
            return;
        }

        if(MinerServer.instance == null) {
            return;
        }

        int entityX = (int)Math.floor(entity.posX);
        int entityY = (int)Math.floor(entity.posY);
        int entityZ = (int)Math.floor(entity.posZ);

        if(!EntityItem.class.isInstance(entity)) {
            return;
        }

        EntityItem entityItem = (EntityItem) entity;

        if(entityItem.getDataWatcher().getWatchableObjectItemStack(10) == null) {
            return;
        }

        if(entityItem.getEntityItem().hasTagCompound()) {
            return;
        }

        boolean isBlock = false;
        boolean isItem = false;

        if(entityItem.getEntityItem().itemID < Block.blocksList.length) {
            isBlock = Block.blocksList[entityItem.getEntityItem().itemID] != null;
        }
        if(entityItem.getEntityItem().itemID < Item.itemsList.length) {
            isItem = Item.itemsList[entityItem.getEntityItem().itemID] != null;
        }

        //new MinerServer();
        if((isBlock || isItem) && MinerServer.instance != null && MinerServer.instance.isRegistered(entityX, entityY, entityZ)) {
            MinerServer.instance.addEntity(entity);
            event.setCanceled(true);
        }
    }
}
