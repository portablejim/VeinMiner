package portablejim.veinminer.util;

import net.minecraftforge.event.world.BlockEvent;
import portablejim.veinminer.api.Point;

/**
 * Created by james on 5/06/16.
 */
public class Compatibility {
    public static Point getPoint(BlockEvent.BreakEvent event) {
        return new Point(event.x, event.y, event.z);
    }
}
