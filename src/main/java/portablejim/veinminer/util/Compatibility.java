package portablejim.veinminer.util;

import net.minecraftforge.event.world.BlockEvent;

/**
 * Created by james on 5/06/16.
 */
public class Compatibility {
    public static Point getPoint(BlockEvent.BreakEvent event) {
        return new Point(event.getPos().getX(), event.getPos().getY(), event.getPos().getZ());
    }
}
