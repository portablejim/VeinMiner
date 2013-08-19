package portablejim.veinminer.server;

import net.minecraft.entity.Entity;
import portablejim.veinminer.configuration.ConfigurationSettings;
import portablejim.veinminer.core.MinerInstance;

import java.util.HashMap;
import java.util.HashSet;

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

    public MinerServer() {
        instance = this;
        players = new HashMap<String, PlayerStatus>();
        settings = new ConfigurationSettings();
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

    }

    public boolean isRegistered(int x, int y, int z) {
        return false;
    }

    public ConfigurationSettings getConfigurationSettings() {
        return settings;
    }
}
