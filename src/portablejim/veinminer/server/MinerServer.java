package portablejim.veinminer.server;

import net.minecraft.entity.Entity;

/**
 * Created with IntelliJ IDEA.
 * User: james
 * Date: 8/06/13
 * Time: 5:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class MinerServer {

    public static MinerServer instance;

    public MinerServer() {
        instance = this;
    }

    public void addEntity(Entity entity) {

    }

    public boolean isRegistered(int x, int y, int z) {
        return false;
    }
}
