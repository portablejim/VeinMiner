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

package portablejim.veinminer.lib;

/**
 * Class to store various constants relating to VeinMiner.
 */

public class ModInfo {
    public static final boolean DEBUG_MODE = false;

    public static final String MOD_ID = "VeinMiner";
    public static final String MOD_NAME = "Vein Miner";
    public static final String DESCRIPTION = "When a configured block is harvested a chain reaction is initiated to break connected blocks of the same type. Takes durability and health for each block mined.";
    public static final String VERSION = "@@@DEV@@@";
    public static final String URL = "http://minecraft.curseforge.com/mc-mods/veinminer/";
    public static final String UPDATE_URL = "";
    public static final String AUTHOR = "portablejim";
    public static final String CREDITS = "Inspired by ConnectedDestruction by DaftPVF (currently maintained by bspkrs).";

    public static final String COREMOD_ID = "VeinMinerCore";
    public static final String COREMOD_NAME = "Vein Miner CoreMod";
    public static final String COREMOD_DESCRIPTION = "CoreMod for VeinMiner doing the needed ASM things.";
    public static final String COREMOD_CREDITS = "Made using code from bspkrs Blockbreaker mod.";

    public static final String VALID_MC_VERSIONS = "[1.6,1.7)";
    public static final String PROXY_SERVER_CLASS = "portablejim.veinminer.proxy.ServerProxy";
    public static final String PROXY_CLIENT_CLASS = "portablejim.veinminer.proxy.ClientProxy";
    public static final String CHANNEL = "portablejim_vm";
}
