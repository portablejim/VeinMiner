package portablejim.veinminer.api;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.Event;

/**
 * Created with IntelliJ IDEA.
 * User: james
 * Date: 3/11/13
 * Time: 9:36 AM
 * To change this template use File | Settings | File Templates.
 */
public class VeinminerToolCheck extends Event {
    public boolean allowTool;
    public final EntityPlayerMP player;

    public VeinminerToolCheck(EntityPlayerMP player) {
        allowTool = false;
        this.player = player;
    }
}
