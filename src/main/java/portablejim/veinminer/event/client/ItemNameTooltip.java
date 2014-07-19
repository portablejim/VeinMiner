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

package portablejim.veinminer.event.client;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;

/**
 * Adds the item registry name to the item tooltip when ids are shown
 * (i.e. after F3 + H )
 */
public class ItemNameTooltip {
    public ItemNameTooltip() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SuppressWarnings("UnusedDeclaration")
    @SubscribeEvent
    public void addTooltip(ItemTooltipEvent event) {
        GameRegistry.UniqueIdentifier uniqueIdentifierFor = GameRegistry.findUniqueIdentifierFor(event.itemStack.getItem());
        if(event.showAdvancedItemTooltips)
            event.toolTip.add(uniqueIdentifierFor.toString());
    }
}
