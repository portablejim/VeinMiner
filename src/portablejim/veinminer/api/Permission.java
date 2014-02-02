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

package portablejim.veinminer.api;

/**
 * Permission levels used with events. Deny requests have priority over allow requests. The 4 events are:
 * FORCE_ALLOW: Indicates a strong desire for the action to be allowed. Value only to be changed if changing to FORCE_DENY.
 * ALLOW: Indicates a weak desire to allow the action (e.g. this tool should work). Value is free to be changed.
 * DENY: Indicates a weak desire to deny the action (e.g. Don't think this tool should work). Value is free to be changed.
 * FORCE_DENY: Indicates a strong desire for the action to be denied. Value should not be changed.
 */

public enum Permission {
    FORCE_ALLOW,
    ALLOW,
    DENY,
    FORCE_DENY;

    public boolean isAllowed() {
        return this == ALLOW || this == FORCE_ALLOW;
    }

    public boolean isDenied() {
        return this == DENY || this == FORCE_DENY;
    }
}
