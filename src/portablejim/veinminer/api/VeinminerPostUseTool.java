package portablejim.veinminer.api;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.Event;

/**
 * Created with IntelliJ IDEA.
 * User: james
 * Date: 3/11/13
 * Time: 9:38 AM
 * To change this template use File | Settings | File Templates.
 */
public class VeinminerPostUseTool extends Event {
    public final EntityPlayerMP player;

    public VeinminerPostUseTool(EntityPlayerMP player) {
        this.player = player;
    }
}
