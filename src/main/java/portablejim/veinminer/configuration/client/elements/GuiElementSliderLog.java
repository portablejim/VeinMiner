package portablejim.veinminer.configuration.client.elements;

/**
 * Created with IntelliJ IDEA.
 * User: james
 * Date: 23/02/14
 * Time: 11:57 AM
 * To change this template use File | Settings | File Templates.
 */
public class GuiElementSliderLog extends GuiElementSlider {
    int exponent;

    public GuiElementSliderLog(int id, int xPos, int yPos, IGuiElementValuePersist callback, int exponent) {
        super(id, xPos, yPos, callback);
        this.exponent = exponent;
        this.storedValue = normalise((Float)callback.getGuiElementValue(this));
        this.displayString = callback.getGuiElementDisplayString(this, deNormalise(storedValue));
    }

    public GuiElementSliderLog(int id, int xPos, int yPos, IGuiElementValuePersist callback, float min, float max, int exponent) {
        super(id, xPos, yPos, callback, min, max);
        this.exponent = exponent;
        this.storedValue = normalise((Float)callback.getGuiElementValue(this));
        this.displayString = callback.getGuiElementDisplayString(this, deNormalise(storedValue));
    }

    public GuiElementSliderLog(int id, int xPos, int yPos, int width, int height, IGuiElementValuePersist callback, int exponent) {
        super(id, xPos, yPos, width, height, callback);
        this.exponent = exponent;
        this.storedValue = normalise((Float)callback.getGuiElementValue(this));
        this.displayString = callback.getGuiElementDisplayString(this, deNormalise(storedValue));
    }

    public GuiElementSliderLog(int id, int xPos, int yPos, int width, int height, IGuiElementValuePersist callback, float min, float max, int exponent) {
        super(id, xPos, yPos, width, height, callback, min, max);
        this.exponent = exponent;
        this.storedValue = normalise((Float)callback.getGuiElementValue(this));
        this.displayString = callback.getGuiElementDisplayString(this, deNormalise(storedValue));
    }


    @Override
    protected float deNormalise(float input) {
        float range = max - min;
        float scaled = (float) (Math.pow(input, exponent) * range);
        return min + scaled;
    }

    @Override
    protected float normalise(float input) {
        float range = max - min;
        float reduced = input - min;
        return (float) Math.pow(reduced / range, 1D/((double)exponent));
    }
}
