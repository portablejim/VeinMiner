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

import com.google.common.base.Throwables;
import portablejim.veinminer.VeinMiner;

import java.io.InputStream;
import java.util.Properties;

/**
 * Class to store various constants relating to VeinMiner.
 */

public class ModInfo {
    public static boolean DEBUG_MODE = true;

    public static final String MODID = "veinminer";
    public static final String VERSION;
    static {
        Properties prop = new Properties();

        try {
            InputStream stream = VeinMiner.class.getClassLoader().getResourceAsStream("version.properties");
            prop.load(stream);
            stream.close();
        }
        catch (Exception e) {
            Throwables.propagate(e);
        }

        VERSION = String.format("%s_build-%s", prop.getProperty("version"), prop.getProperty("build_number"));
    }
    public static final String COREMOD_ID = "veinminer_coremod";
    public static final String COREMOD_NAME = "Core mod";
    public static final String PROXY_SERVER_CLASS = "portablejim.veinminer.proxy.ServerProxy";
    public static final String PROXY_CLIENT_CLASS = "portablejim.veinminer.proxy.ClientProxy";
    public static final String CHANNEL = "VeinMiner";
}
