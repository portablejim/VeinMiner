package portablejim.veinminer.lib;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

/**
 * Created with IntelliJ IDEA.
 * User: james
 * Date: 24/02/14
 * Time: 7:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class IconRenderer {
    private final Minecraft minecraft;
    private final double zLevel;
    private final FontRenderer fontRenderer;
    private final TextureManager textureManager;
    private RenderItem itemRenderer;

    public IconRenderer(Minecraft minecraft, double zLevel, FontRenderer fontRenderer, TextureManager textureManager) {

        this.minecraft = minecraft;
        this.zLevel = zLevel;
        this.fontRenderer = fontRenderer;
        this.textureManager = textureManager;
        this.itemRenderer = new RenderItem();
    }

    public void renderItemStackIcon(int renderX, int renderY, ItemStack itemStack)
    {
        this.setupRender(renderX + 1, renderY + 1, 0, 0);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        if (itemStack != null)
        {
            RenderHelper.enableGUIStandardItemLighting();
            this.itemRenderer.renderItemIntoGUI(this.fontRenderer, textureManager, itemStack, renderX + 2, renderY + 2);
            RenderHelper.disableStandardItemLighting();
        }
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
    }

    private void setupRender(int xBase, int yBase, int uBase, int vBase)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        minecraft.getTextureManager().bindTexture(Gui.statIcons);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV((double)(xBase), (double)(yBase + 18), zLevel, (double)((float)(uBase) * 0.0078125F), (double)((float)(vBase + 18) * 0.0078125F));
        tessellator.addVertexWithUV((double) (xBase + 18), (double) (yBase + 18), zLevel, (double) ((float) (uBase + 18) * 0.0078125F), (double) ((float) (vBase + 18) * 0.0078125F));
        tessellator.addVertexWithUV((double)(xBase + 18), (double)(yBase), zLevel, (double)((float)(uBase + 18) * 0.0078125F), (double)((float)(vBase) * 0.0078125F));
        tessellator.addVertexWithUV((double)(xBase), (double)(yBase), zLevel, (double)((float)(uBase) * 0.0078125F), (double)((float)(vBase) * 0.0078125F));
        tessellator.draw();
    }
}
