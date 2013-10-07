package portablejim.veinminer.util;

/**
 * Created with IntelliJ IDEA.
 * User: james
 * Date: 6/10/13
 * Time: 8:55 AM
 * To change this template use File | Settings | File Templates.
 */
public class ItemStackID {
    private int itemId;
    private int damage;
    private int maxStackSize;

    public ItemStackID(int id, int dam, int stackSize) {
        itemId = id;
        damage = dam;
        maxStackSize = stackSize;
    }

    public int getItemId() {
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
        output += itemId << 16;
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
        return (itemId == rhs.itemId && damage == rhs.damage);
    }
}
