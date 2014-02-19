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

package portablejim.veinminer.util;

/**
 * Class to store the basic information in an ItemStack (well, all that
 * matters in this situation) and allow comparison using only the itemID
 * and damage value. Is both sortable and hashable.
 */

public class ItemStackID {
    private String itemId;
    private int damage;
    private int maxStackSize;

    public ItemStackID(String id, int dam, int stackSize) {
        itemId = id;
        damage = dam;
        maxStackSize = stackSize;
    }

    public String getItemId() {
        return itemId;
    }

    public int getDamage() {
        return damage;
    }

    public int getMaxStackSize() {
        return maxStackSize;
    }

    @Override
    public int hashCode() {
        int output = 0;
        output += itemId.hashCode() << 16;
        output += damage;

        return output;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (!(obj instanceof ItemStackID))
            return false;

        ItemStackID rhs = (ItemStackID) obj;
        return (itemId.equals(rhs.itemId) && damage == rhs.damage);
    }
}
