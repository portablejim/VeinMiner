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
 * Simple class to make the config options for tools. Each tools options follow the same structure.
 */

public class ConfigToolValue {
    ConfigOptionBoolean autodetectToggle;
    ConfigOptionString autodetectList;
    ConfigOptionString blockIdList;
    ConfigOptionString toolIdList;

    public ConfigToolValue(String toolName, boolean autodetectDefault, String autodetectStringDefault, String blockListDefault, String toolListDefault) {
        autodetectToggle = new ConfigOptionBoolean(autodetectDefault, String.format("autodetect.blocks.%s.enable", toolName),
                String.format("Autodetect blocks with the below prefixes in the ore dictionary, adding the ids to the %s list. [default: %s]", toolName, autodetectDefault ? "true" : "false"));
        autodetectList = new ConfigOptionString(autodetectStringDefault, String.format("autodetect.blocks.%s.prefixes", toolName),
                String.format("List of prefixes to autodetect as blocks to be used with a %s.\n" +
                        "Separate with ',' [default: '%s'] ", toolName, autodetectStringDefault));
        blockIdList = new ConfigOptionString(blockListDefault, String.format("blockList.%s", toolName),
                String.format("Block ids to auto-mine when using a configured %s. [default: '%s']", toolName, blockListDefault));
        toolIdList = new ConfigOptionString(toolListDefault, String.format("itemList.%s", toolName),
                String.format("Item ids to use as a %s. [default '%s']", toolName, toolListDefault));
    }
}
