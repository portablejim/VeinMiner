package portablejim.veinminer.api;

import net.minecraft.entity.player.EntityPlayerMP;

/**
 * Created with IntelliJ IDEA.
 * User: james
 * Date: 11/10/13
 * Time: 6:49 PM
 * To change this template use File | Settings | File Templates.
 */
public interface IToolOverride {
    public void updateToolAllowed(Boolean toolAllowed, EntityPlayerMP player);
}
