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
package portablejim.veinminer.event;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import portablejim.veinminer.core.MinerInstance;

import java.util.EnumSet;

/**
 * Ticker that calls the MinerInstance to perform it's actions, allowing
 * the blocks to be mined asynchronously, improving performance.
 */

public class InstanceTicker implements ITickHandler {
    MinerInstance minerInstance;
    public InstanceTicker(MinerInstance caller) {
        minerInstance = caller;
    }

    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void tickEnd(EnumSet<TickType> type, Object... tickData) {
        if(type.contains(TickType.SERVER)) {
            minerInstance.mineScheduled();
        }
    }

    @Override
    public EnumSet<TickType> ticks() {
        return EnumSet.of(TickType.SERVER);
    }

    @Override
    public String getLabel() {
        return "VeinMinerInstanceTicker";
    }
}
