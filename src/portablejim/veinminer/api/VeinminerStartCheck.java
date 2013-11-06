package portablejim.veinminer.api;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.Event;

/**
 * Created with IntelliJ IDEA.
 * User: james
 * Date: 3/11/13
 * Time: 9:35 AM
 * To change this template use File | Settings | File Templates.
 */
public class VeinminerStartCheck extends Event {
    public boolean allowVeinminerStart;
    public final EntityPlayerMP player;
    public final int blockId;
    public final int blockMetadata;

    public VeinminerStartCheck(EntityPlayerMP player, int id, int metadata) {
        allowVeinminerStart = false;
        this.player = player;
        this.blockId = id;
        this.blockMetadata = metadata;
    }
}
