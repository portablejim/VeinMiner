package portablejim.veinminer.configuration.client.elements;

import net.minecraft.client.gui.GuiButton;

/**
 * Created with IntelliJ IDEA.
 * User: james
 * Date: 23/02/14
 * Time: 9:20 AM
 * To change this template use File | Settings | File Templates.
 */
public interface IGuiElementValuePersist {
    public void saveGuiElementValue(GuiButton element, Object value);
    public Object getGuiElementValue(GuiButton element);
    public String getGuiElementDisplayString(GuiButton element, Object value);
}
