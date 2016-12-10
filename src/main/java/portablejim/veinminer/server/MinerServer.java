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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import portablejim.veinminer.configuration.ConfigurationSettings;
import portablejim.veinminer.configuration.ConfigurationValues;
import portablejim.veinminer.core.MinerInstance;
import portablejim.veinminer.util.PlayerStatus;
import portablejim.veinminer.api.Point;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Singleton class that co-ordinates various actions. It allows the current
 * instances of MinerInstance to be contacted by classes responding to entity
 * drop events. It manages player states (cleared when the server is
 * restarted). It manages tool overrides.
 */

public class MinerServer {

    private final Set<MinerInstance> minerInstances;
    private ConcurrentHashMap<EntityPlayerMP, MinerInstance> playerMinerInstances;
    private HashSet<UUID> clientPlayers;
    private ConcurrentHashMap<UUID, PlayerStatus> players;
    private ConfigurationSettings settings;

    public MinerServer(ConfigurationValues configValues) {
        minerInstances = Collections.synchronizedSet(new HashSet<MinerInstance>());
        playerMinerInstances = new ConcurrentHashMap<EntityPlayerMP, MinerInstance>();
        clientPlayers = new HashSet<UUID>();
        players = new ConcurrentHashMap<UUID, PlayerStatus>();
        settings = new ConfigurationSettings(configValues);
    }

    public void setPlayerStatus(UUID player, PlayerStatus status) {
            players.put(player, status);
    }

    public PlayerStatus getPlayerStatus(UUID player) {
        if(players.containsKey(player)) {
            return players.get(player);
        }
        else {
            return PlayerStatus.INACTIVE;
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

        synchronized (minerInstances) {
            for (MinerInstance minerInstance : minerInstances) {
                if (minerInstance.isRegistered(p)) {
                    minerInstance.addDrop(entityItem);
                }
            }
        }
    }

    public boolean awaitingDrop(Point p) {
        synchronized (minerInstances) {
            for (MinerInstance minerInstance : minerInstances) {
                if (minerInstance.isRegistered(p)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean pointIsBlacklisted(Point point) {
        synchronized (minerInstances) {
            for (MinerInstance minerInstance : minerInstances) {
                if (minerInstance.pointIsBlacklisted(point)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void removeFromBlacklist(Point point) {
        synchronized (minerInstances) {
            for (MinerInstance minerInstance : minerInstances) {
                if (minerInstance.pointIsBlacklisted(point)) {
                    minerInstance.removeFromBlacklist(point);
                }
            }
        }
    }

    public void addInstance(MinerInstance ins) {
        synchronized (minerInstances) {
            minerInstances.add(ins);
        }
        playerMinerInstances.put(ins.getPlayer(), ins);
    }

    public MinerInstance getInstance(EntityPlayer playerMP) {
        if(playerMinerInstances.containsKey(playerMP)) {
            return playerMinerInstances.get(playerMP);
        }
        return null;
    }

    public void removeInstance(MinerInstance ins) {
        synchronized (minerInstances) {
            minerInstances.remove(ins);
        }
        if(playerMinerInstances.containsKey(ins.getPlayer())) {
            playerMinerInstances.remove(ins.getPlayer());
        }
    }

    public ConfigurationSettings getConfigurationSettings() {
        return settings;
    }

    public boolean playerHasClient(UUID playerName) {
        return clientPlayers.contains(playerName);
    }

    public void addClientPlayer(UUID playerName) {
        clientPlayers.add(playerName);
        setPlayerStatus(playerName, PlayerStatus.INACTIVE);
    }

    public void removeClientPlayer(UUID playerName) {
        clientPlayers.remove(playerName);
    }
}
