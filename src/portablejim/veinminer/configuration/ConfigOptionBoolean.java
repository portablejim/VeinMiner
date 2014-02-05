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

package portablejim.veinminer.configuration;

/**
 * Simple class to gather information on a config option into one class.
 */

public class ConfigOptionBoolean {
    public boolean value;
    public final boolean valueDefault;
    public final String configName;
    public final String description;

    public ConfigOptionBoolean(boolean valueDefault, String configName, String description) {
        this.valueDefault = valueDefault;
        this.configName = configName;
        this.description = description;
    }
}
