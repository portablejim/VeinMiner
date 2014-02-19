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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Stores the block ID and metadata of a block. Allows having no metadata value
 * to match blocks with any metadata value.
 *
 * This has been mostly copied from Bskprs Block ID he has in his bspkrsCore
 * mod, but some parts have been re-written.
 */

public class BlockID
{
    public String name;
    public int metadata;
    
    public BlockID(String fullDescription) {
        int preMeta = -1;
        Pattern p = Pattern.compile("(.+?)(?:/([-]?\\d{1,2})?)");
        Matcher m = p.matcher(fullDescription);

        if(m.matches()) {
            name = m.group(0);
            if(m.group(1) != null) {
                try {
                    preMeta = Integer.parseInt(m.group(1));
                }
                catch (NumberFormatException e) {
                    preMeta = -1;
                }
            }
        }
        else {
            name = "";
        }
        metadata = preMeta >= -1 ? preMeta : -1;
    }

    public BlockID(String name, int meta) {
        this.name = name;
        this.metadata =  meta < -1 ? -1 : meta;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        
        if (!(obj instanceof BlockID))
            return false;
        
        BlockID o = (BlockID) obj;
        if (o.metadata == -1 || metadata == -1)
            return name.equals(o.name);
        else
            return name.equals(o.name) && metadata == o.metadata;
    }
    
    @Override
    public int hashCode()
    {
        return (this.name.hashCode() << 6) + this.metadata;
    }
    
    @Override
    public String toString()
    {
        return (metadata == -1 ? name + "" : name + "/" + metadata);
    }
}
