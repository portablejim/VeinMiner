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
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import portablejim.veinminer.configuration.client.ItemlistConfigGuiScreen;
import portablejim.veinminer.lib.IconRenderer;
import portablejim.veinminer.util.BlockID;

import java.util.ArrayList;

/**
 * List the items/blocks on the whitelist for the specificed tool and allow
 * selection of items on the list.
 */

public class GuiElementSlotItemlist extends GuiScrollingList {
    private final IconRenderer iconRenderer;
    ItemlistConfigGuiScreen parent;
    public ArrayList<String> items;
    public int selectedItem;
    String toolType;

    public GuiElementSlotItemlist(ItemlistConfigGuiScreen parent, String toolType) {
        super(Minecraft.getMinecraft(), parent.width - 10, parent.height, 60, parent.height - 40, 5, 22);
        iconRenderer = new IconRenderer(parent.mc, parent.getZLevel());
        this.parent = parent;
        this.toolType = toolType;
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
        String entry = items.get(listIndex);
        BlockID itemParts = new BlockID(entry);
        String[] nameParts = itemParts.name.split(":", 2);

        String displayName = "";
        boolean renderItem = false;

        Block tryBlock = Block.getBlockFromName(entry);
        Item tryItem = null;
        if(tryBlock != null) {
            displayName = tryBlock.getUnlocalizedName();

            tryItem = Item.getItemFromBlock(tryBlock);
            renderItem = tryItem != null;
        }
        else if(nameParts.length == 2) {
            tryItem = GameRegistry.findItem(nameParts[0], nameParts[1]);
            if(tryItem != null) {
                ItemStack tryItemStack = new ItemStack(tryItem);
                tryItemStack.setItemDamage(itemParts.metadata == -1 ? 0 : itemParts.metadata);
                displayName = tryItemStack.getUnlocalizedName();
                renderItem = true;
            }
        }

        if(renderItem) {
            ItemStack itemStack = new ItemStack(tryItem);
            itemStack.setItemDamage(itemParts.metadata == -1 ? 0 : itemParts.metadata);

            // Get new name based on damage value.
            displayName = itemStack.getUnlocalizedName();
            iconRenderer.renderItemStackIcon(this.listWidth / 2 - 148, var3 - 1 , itemStack);
        }


        String displayNameString;
        if(displayName.isEmpty()) {
            displayNameString = "gui.veinminer.unknown";
        }
        else {
            displayNameString = displayName + ".name";
        }

        this.parent.getFontRenderer().drawString(I18n.format(displayNameString), this.listWidth / 2 + 60, var3 + 5, 0xFFFFFF);
        this.parent.getFontRenderer().drawString(entry, this.listWidth / 2 - 120, var3 + 5, 0xFFFFFF);
    }
}
