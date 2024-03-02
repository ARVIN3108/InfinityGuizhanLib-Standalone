package net.guizhanss.guizhanlib.slimefun.addon;

import id.arvin3108.standalone.SlimefunAddonInstance;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;

/**
 * A mock addon for testing purpose.
 * Modified from InfinityLib
 *
 * @author Mooy1
 * @author ybw0014
 */
public class MockAddon extends AbstractAddon {

    public MockAddon(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
        this(loader, description, dataFolder, file, Environment.LIB_TESTING);
    }

    public MockAddon(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file,
                     Environment environment) {
        super(loader, description, dataFolder, file, "auto-update", environment);
    }

    @Override
    protected void enable() {
        new SlimefunAddonInstance("ybw0014", "GuizhanLib", "master").initialize();
    }

    @Override
    protected void disable() {

    }
}
