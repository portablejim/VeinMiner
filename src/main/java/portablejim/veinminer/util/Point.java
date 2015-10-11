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
 * Point class to store a 3D point, including distance calculations.
 *
 * Has been mostly been copied from Bspkrs' bspkrsCore mod.
 */
public class Point {
    private int x;
    private int y;
    private int z;
    public Point(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getZ() {
        return this.z;
    }

    @Override
    public  int hashCode() {
        int hash = 5;
        hash += (13 * this.x);
        hash += (19 * this.y);
        hash += (31 * this.z);

        return hash;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other == this) {
            return true;
        }
        if(!(other instanceof Point)) {
            return false;
        }
        Point otherPoint = (Point)other;
        return this.x == otherPoint.x && this.y == otherPoint.y && this.z == otherPoint.z;
    }

    public int distanceFrom(Point target) {
        return distanceFrom(target.x, target.y, target.z);
    }

    /**
     *
     * @param x X coordinate of target.
     * @param y Y coordinate of target.
     * @param z Z coordinate of target.
     * @return square of distance
     */
    public int distanceFrom(int x, int y, int z) {
        int distanceX = this.x - x;
        int distanceY = this.y - y;
        int distanceZ = this.z - z;
        return distanceX * distanceX + distanceY * distanceY + distanceZ * distanceZ;
    }

    public boolean isWithinRange(Point target, int range) {
        return isWithinRange(target.x, target.y, target.z, range);
    }

    public boolean isWithinRange(int x, int y, int z, int range) {
        return distanceFrom(x, y, z) <= (range * range);
    }

    @Override
    public String toString() {
        return String.format("(%d, %d, %d)", x, y, z);
    }
}
