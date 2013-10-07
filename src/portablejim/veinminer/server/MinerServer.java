package portablejim.veinminer.server;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import portablejim.veinminer.configuration.ConfigurationSettings;
import portablejim.veinminer.configuration.ConfigurationValues;
import portablejim.veinminer.core.MinerInstance;
import portablejim.veinminer.util.Point;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: james
 * Date: 8/06/13
 * Time: 5:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class MinerServer {

    public static MinerServer instance;
    private HashSet<MinerInstance> minerInstances;
    private HashMap<String, PlayerStatus> players;
    private ConfigurationSettings settings;

    public MinerServer(ConfigurationValues configValues) {
        instance = this;
        minerInstances = new HashSet<MinerInstance>();
        players = new HashMap<String, PlayerStatus>();
        settings = new ConfigurationSettings(configValues);
    }

    public void setPlayerStatus(String player, PlayerStatus status) {
        players.put(player, status);
    }

    public void removePlayer(String player) {
        if(players.containsKey(player)) {
            players.remove(player);
        }
    }

    public PlayerStatus getPlayerStatus(String player) {
        return players.get(player);
    }

    public void addEntity(Entity entity) {
        int eX = (int) Math.floor(entity.posX);
        int eY = (int) Math.floor(entity.posY);
        int eZ = (int) Math.floor(entity.posZ);
        Point p = new Point(eX, eY, eZ);

        if(!EntityItem.class.isInstance(entity)) {
            return;
        }
        EntityItem entityItem = (EntityItem) entity;

        Iterator<MinerInstance> iterator = minerInstances.iterator();
        while(iterator.hasNext()) {
            MinerInstance minerInstance = iterator.next();
            if(minerInstance.isRegistered(p)) {
                minerInstance.addDrop(entityItem, p);
            }
        }
    }

    public void addInstance(MinerInstance ins) {
        minerInstances.add(ins);
    }

    public void removeInstance(MinerInstance ins) {
        minerInstances.remove(ins);
    }

    public boolean isRegistered(int x, int y, int z) {
        Point p = new Point(x, y, z);
        boolean registered = false;

        Iterator<MinerInstance> iterator = minerInstances.iterator();
        while(iterator.hasNext()) {
            if(iterator.next().isRegistered(p)) {
                registered = true;
            }
        }
        return registered;
    }

    public ConfigurationSettings getConfigurationSettings() {
        return settings;
    }
}
