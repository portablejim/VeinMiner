package portablejim.veinminer.core;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import portablejim.veinminer.util.BlockID;
import portablejim.veinminer.util.Point;

import java.util.HashSet;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created with IntelliJ IDEA.
 * User: james
 * Date: 13/08/13
 * Time: 8:48 AM
 * To change this template use File | Settings | File Templates.
 */
public class MinerInstance {
    private ConcurrentLinkedQueue<Point> destroyQueue;
    private HashSet<Point> awaitingEntityDrop;
    private BlockID targetBlock;
    private boolean finished;

    public MinerInstance(World world, EntityPlayerMP player, int x, int y, int z) {
        destroyQueue = new ConcurrentLinkedQueue<Point>();
        awaitingEntityDrop = new HashSet<Point>();
        //targetBlock = new BlockID()
    }
}
