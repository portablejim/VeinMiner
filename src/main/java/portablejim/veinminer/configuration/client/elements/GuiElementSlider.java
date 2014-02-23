package portablejim.veinminer.configuration.client.elements;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import org.lwjgl.opengl.GL11;

/**
 * Created with IntelliJ IDEA.
 * User: james
 * Date: 23/02/14
 * Time: 9:35 AM
 * To change this template use File | Settings | File Templates.
 */
public class GuiElementSlider extends GuiButton {
    Minecraft minecraft;
    IGuiElementValuePersist callback;
    float storedValue;
    float min, max;

    boolean mouseDragging;

    public GuiElementSlider(int id, int xPos, int yPos, IGuiElementValuePersist callback) {
        this(id, xPos, yPos, 150, 20, callback);
    }
    public GuiElementSlider(int id, int xPos, int yPos, IGuiElementValuePersist callback, float min, float max) {
        this(id, xPos, yPos, 150, 20, callback, min, max);
    }

    public GuiElementSlider(int id, int xPos, int yPos, int width, int height, IGuiElementValuePersist callback) {
        this(id, xPos, yPos, 150, 20, callback, 0F, 1F);
    }

    public GuiElementSlider(int id, int xPos, int yPos, int width, int height, IGuiElementValuePersist callback, float min, float max) {
        super(id, xPos, yPos, width, height, "");
        this.callback = callback;
        this.min = min;
        this.max = max;
        this.storedValue = normalise((Float)callback.getGuiElementValue(this));
        this.displayString = callback.getGuiElementDisplayString(this, deNormalise(storedValue));
    }

    @Override
    protected int getHoverState(boolean val) {
        return 0;
    }

    @Override
    public void mouseDragged(Minecraft minecraft, int par1, int par2) {
        if(this.visible) {
            if(mouseDragging) {
                float rawValue = (float)(par1 - (this.xPosition + 4)) / (float)(this.width - 8);

                if(rawValue < 0F) {
                    rawValue = 0F;
                }
                else if(rawValue > 1F) {
                    rawValue = 1F;
                }
                this.storedValue = rawValue;

                float scaledValue = deNormalise(rawValue);
                callback.saveGuiElementValue(this, scaledValue);
                displayString = callback.getGuiElementDisplayString(this, scaledValue);
            }

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.drawTexturedModalRect(this.xPosition + (int)(this.storedValue * (float)(this.width - 8)), this.yPosition, 0, 66, 4, 20);
            this.drawTexturedModalRect(this.xPosition + (int)(this.storedValue * (float)(this.width - 8)) + 4, this.yPosition, 196, 66, 4, 20);
        }
    }

    @Override
    public boolean mousePressed(Minecraft minecraft, int par1, int par2) {
        if(super.mousePressed(minecraft, par1, par2)) {
            float rawValue = (float)(par1 - (this.xPosition + 4)) / (float)(this.width - 8);

            if(rawValue < 0F) {
                rawValue = 0F;
            }
            else if(rawValue > 1F) {
                rawValue = 1F;
            }
            this.storedValue = rawValue;

            float scaledValue = deNormalise(rawValue);
            callback.saveGuiElementValue(this, scaledValue);
            displayString = callback.getGuiElementDisplayString(this, scaledValue);

            mouseDragging = true;

            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public void mouseReleased(int par1, int par2) {
        mouseDragging = false;
    }

    protected float deNormalise(float input) {
        float range = max - min;
        float scaled = input * range;
        return min + scaled;
    }

    protected float normalise(float input) {
        float range = max - min;
        float reduced = input - min;
        return reduced / range;
    }
}
