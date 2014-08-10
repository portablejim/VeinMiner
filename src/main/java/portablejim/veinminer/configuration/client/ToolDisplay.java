package portablejim.veinminer.configuration.client;

/**
 * Simple class to help organise data for the man GUI config screen.
 */
public class ToolDisplay {
    public String id;
    public String icon;
    public String name;

    public ToolDisplay(String id, String icon, String name) {
        this.id = id;
        this.icon = icon;
        this.name = name;
    }
}
