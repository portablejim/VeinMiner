package portablejim.veinminer.proxy;

import portablejim.veinminer.configuration.ConfigurationSettings;
import portablejim.veinminer.configuration.ConfigurationValues;

/**
 * Created with IntelliJ IDEA.
 * User: james
 * Date: 8/06/13
 * Time: 9:14 AM
 * To change this template use File | Settings | File Templates.
 */
public interface CommonProxy {
    public void registerKeybind();
    public void setupConfig(ConfigurationValues config);
    public ConfigurationSettings getConfigSettings();
}
