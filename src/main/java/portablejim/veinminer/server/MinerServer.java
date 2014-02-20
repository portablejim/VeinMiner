/* This file is part of VeinMiner.
 *
 *    VeinMiner is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU Lesser General Public License as
 *    published by the Free Software Foundation, either version 3 of
 *     the License, or (at your option) any later version.
 *
 *    VeinMiner is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with VeinMiner.
 *    If not, see <http://www.gnu.org/licenses/>.
 */

package portablejim.veinminer.server;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import portablejim.veinminer.configuration.ConfigurationSettings;
import portablejim.veinminer.configuration.ConfigurationValues;
import portablejim.veinminer.core.MinerInstance;
import portablejim.veinminer.util.PlayerStatus;
import portablejim.veinminer.util.Point;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

/**
 * Singleton class that co-ordinates various actions. It allows the current
 * instances of MinerInstance to be contacted by classes responding to entity
 * drop events. It manages player states (cleared when the server is
 * restarted). It manages tool overrides.
 */

public class MinerServer {

    public static MinerServer instance;
    private HashSet<MinerInstance> minerInstances;
    private HashSet<UUID> clientPlayers;
    private HashMap<UUID, PlayerStatus> players;
    private ConfigurationSettings settings;

    public MinerServer(ConfigurationValues configValues) {
        instance = this;
        minerInstances = new HashSet<MinerInstance>();
        clientPlayers = new HashSet<UUID>();
        players = new HashMap<UUID, PlayerStatus>();
        settings = new ConfigurationSettings(configValues);
    }

    public void setPlayerStatus(UUID player, PlayerStatus status) {
        if(status == PlayerStatus.DISABLED) {
            players.remove(player);
        }
        else {
            players.put(player, status);
        }
    }

    public void removePlayer(UUID player) {
        if(players.containsKey(player)) {
            players.remove(player);
        }
    }

    public PlayerStatus getPlayerStatus(UUID player) {
        if(players.containsKey(player)) {
            return players.get(player);
        }
        else {
            return PlayerStatus.DISABLED;
        }
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

        for (MinerInstance minerInstance : minerInstances) {
            if (minerInstance.isRegistered(p)) {
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

        for (MinerInstance minerInstance : minerInstances) {
            if (minerInstance.isRegistered(p)) {
                registered = true;
            }
        }
        return registered;
    }

    public ConfigurationSettings getConfigurationSettings() {
        return settings;
    }

    public HashSet<UUID> getClientPlayers() {
        return clientPlayers;
    }

    public boolean playerHasClient(UUID playerName) {
        return clientPlayers.contains(playerName);
    }

    public void addClientPlayer(UUID playerName) {
        clientPlayers.add(playerName);
        if(getPlayerStatus(playerName).equals(PlayerStatus.DISABLED)) {
            setPlayerStatus(playerName, PlayerStatus.INACTIVE);
        }
    }

    public void removeClientPlayer(UUID playerName) {
        clientPlayers.remove(playerName);
    }

    public void setClientPlayers(HashSet<UUID> clientPlayers) {
        this.clientPlayers = clientPlayers;
    }
}
