package portablejim.veinminer.event;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import portablejim.veinminer.server.MinerServer;

/**
 * Created with IntelliJ IDEA.
 * User: james
 * Date: 12/08/13
 * Time: 1:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class EntityDropHook {

    @ForgeSubscribe
    public void tryAddEntity(EntityJoinWorldEvent event) {
        Entity entity = event.entity;

        if(event.isCanceled()) {
            return;
        }

        int entityX = (int)Math.floor(entity.posX);
        int entityY = (int)Math.floor(entity.posY);
        int entityZ = (int)Math.floor(entity.posZ);

        boolean isBlock = Block.blocksList[entity.entityId] != null;
        boolean isItem = Item.itemsList[entity.entityId] != null;

        new MinerServer();
        if((isBlock || isItem) && MinerServer.instance.isRegistered(entityX, entityY, entityZ)) {
            MinerServer.instance.addEntity(entity);
            event.setCanceled(true);
        }
    }
}
