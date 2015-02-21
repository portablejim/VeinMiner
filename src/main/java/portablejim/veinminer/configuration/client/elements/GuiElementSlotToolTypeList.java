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

package portablejim.veinminer.configuration.client.elements;

import net.minecraftforge.fml.client.GuiScrollingList;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import portablejim.veinminer.configuration.client.ConfigGuiScreen;
import portablejim.veinminer.configuration.client.ToolDisplay;
import portablejim.veinminer.lib.IconRenderer;
import portablejim.veinminer.util.BlockID;

import java.util.ArrayList;

/**
 * List the items/blocks on the whitelist for the specificed tool and allow
 * selection of items on the list.
 */

public class GuiElementSlotToolTypeList extends GuiScrollingList {
    private final IconRenderer iconRenderer;
    ConfigGuiScreen parent;
    public ArrayList<ToolDisplay> items;
    public int selectedItem;

    public GuiElementSlotToolTypeList(ConfigGuiScreen parent) {
        super(Minecraft.getMinecraft(), 148, parent.height, 100, parent.height - 40, parent.width / 2 - 150, 22);
        iconRenderer = new IconRenderer(parent.mc, parent.getZLevel());
        this.parent = parent;
        updateItemIds();
    }

    public void updateItemIds() {
        items = parent.getList();
    }

    @Override
    protected int getSize() {
        return items.size();
    }

    @Override
    protected void elementClicked(int index, boolean doubleClick) {
        selectedItem = index;
    }

    @Override
    protected boolean isSelected(int index) {
        return selectedItem == index;
    }

    @Override
    protected void drawBackground() {
        parent.drawDefaultBackground();
    }

    @Override
    protected void drawSlot(int listIndex, int var2, int var3, int var4, Tessellator var5) {
        String name = items.get(listIndex).name;
        BlockID icon = new BlockID(items.get(listIndex).icon);
        String[] iconParts = icon.name.split(":", 2);
        String entry = items.get(listIndex).name;
        BlockID itemParts = new BlockID(entry);

        boolean renderItem = false;

        Item tryItem = GameRegistry.findItem(iconParts[0], iconParts[1]);
        if(tryItem != null) {
            ItemStack tryItemStack = new ItemStack(tryItem);
            tryItemStack.setItemDamage(itemParts.metadata == -1 ? 0 : itemParts.metadata);
            renderItem = true;
        }

        if(renderItem) {
            ItemStack itemStack = new ItemStack(tryItem);
            itemStack.setItemDamage(itemParts.metadata == -1 ? 0 : itemParts.metadata);

            iconRenderer.renderItemStackIcon(this.left + 2, var3 - 1 , itemStack);
        }

        this.parent.getFontRenderer().drawString(name, this.left + 26, var3 + 5, 0xFFFFFF);
        //this.parent.getFontRenderer().drawString(entry, this.listWidth / 2 - 120, var3 + 5, 0xFFFFFF);
    }
}
