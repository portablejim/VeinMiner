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

package portablejim.veinminer;

import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Instance;
import org.apache.logging.log4j.Logger;
import portablejim.veinminer.lib.ModInfo;
import portablejim.veinminer.network.ChannelHandler;

/**
 * This class is the main mod class for VeinMiner. It is loaded as a mod
 * through ForgeModLoader.
 */

@Mod(modid = ModInfo.MODID, acceptedMinecraftVersions = "[1.7,1.8)")
public class VeinMiner extends DummyModContainer{
    @Instance(ModInfo.MODID)
    public static VeinMiner instance;

    public ChannelHandler channelHandler;
}
