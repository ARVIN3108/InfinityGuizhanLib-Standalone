package net.guizhanss.guizhanlib.slimefun.addon;

import com.google.common.base.Preconditions;
import id.arvin3108.standalone.SlimefunAddonInstance;
import net.guizhanss.guizhanlib.minecraft.utils.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.logging.Level;

/**
 * An abstract {@link JavaPlugin} class that contains
 * the updater and some utilities.
 * <p>
 * Extend this as your main class to use them.
 * <p>
 * Modified from InfinityLib
 *
 * @author Mooy1
 * @author ybw0014
 * @author ARVIN3108
 */
@ParametersAreNonnullByDefault
@SuppressWarnings("ConstantConditions")
public abstract class AbstractAddon extends JavaPlugin {

    private static final String NULL_LOG_LEVEL = "Log level cannot be null";
    private static final String NULL_LOG_MESSAGE = "Log message cannot be null";

    private static AbstractAddon instance;

    private final Environment environment;
    private final String autoUpdateKey;

    private AddonConfig config;
    private Scheduler scheduler;
    private boolean loading;
    private boolean enabling;
    private boolean disabling;
    private boolean autoUpdateEnabled;

    /**
     * Live addon constructor.
     *
     * @param autoUpdateKey Auto update key in the config
     */
    protected AbstractAddon(String autoUpdateKey) {
        this.environment = Environment.LIVE;
        this.autoUpdateKey = autoUpdateKey;
        validate();
    }

    /**
     * Testing addon constructor
     *
     * @param loader        the {@link JavaPluginLoader}
     * @param description   the {@link PluginDescriptionFile} of plugin
     * @param dataFolder    the {@link File} of plugin's data folder
     * @param file          the {@link File} of plugin
     * @param autoUpdateKey Auto update key in the config
     */
    protected AbstractAddon(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file,
                            String autoUpdateKey) {
        this(loader, description, dataFolder, file, autoUpdateKey, Environment.TESTING);
    }

    /**
     * Testing library constructor
     *
     * @param loader        the {@link JavaPluginLoader}
     * @param description   the {@link PluginDescriptionFile} of plugin
     * @param dataFolder    the {@link File} of plugin's data folder
     * @param file          the {@link File} of plugin
     * @param autoUpdateKey Auto update key in the config
     * @param environment   the {@link Environment} of file
     */
    AbstractAddon(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file,
                  String autoUpdateKey, Environment environment) {
        super(loader, description, dataFolder, file);
        this.environment = environment;
        this.autoUpdateKey = autoUpdateKey;
        validate();
    }

    /**
     * Get an instance of extended class of {@link AbstractAddon}
     *
     * @param <T> The class that extends {@link AbstractAddon}, which is the real addon main class
     * @return The instance of extended class of {@link AbstractAddon}
     */
    @Nonnull
    @SuppressWarnings("unchecked")
    public static <T extends AbstractAddon> T getInstance() {
        return (T) Objects.requireNonNull(instance, "Addon is not enabled!");
    }

    private static <T extends AbstractAddon> void setInstance(@Nullable T inst) {
        instance = inst;
    }

    /**
     * Get the {@link AddonConfig}
     *
     * @return the {@link AddonConfig}
     */
    @Nonnull
    public static AddonConfig getAddonConfig() {
        return getInstance().config;
    }

    /**
     * Get the {@link Scheduler}
     *
     * @return the {@link Scheduler}
     */
    @Nonnull
    public static Scheduler getScheduler() {
        return getInstance().scheduler;
    }

    /**
     * Get the {@link PluginCommand} of {@link AbstractAddon}.
     *
     * @return the {@link PluginCommand} of {@link AbstractAddon}.
     */
    @Nonnull
    public static PluginCommand getPluginCommand(@Nonnull String command) {
        Preconditions.checkArgument(command != null, "command should not be null");
        return Objects.requireNonNull(getInstance().getCommand(command));
    }

    /**
     * Creates a {@link NamespacedKey} from the given string
     *
     * @param key the {@link String} representation of the key
     * @return the {@link NamespacedKey} created from given string
     */
    @Nonnull
    public static NamespacedKey createKey(String key) {
        return new NamespacedKey(getInstance(), key);
    }

    /**
     * Call the logger to log a message with arguments.
     * ChatColor code will be translated automatically,
     * and message is dealt with MessageFormat#format().
     *
     * @param level   the log {@link Level}
     * @param message the message
     * @param args    the arguments with in
     * @see MessageFormat
     */
    public static void log(@Nonnull Level level, @Nonnull String message, @Nullable Object... args) {
        Preconditions.checkArgument(level != null, NULL_LOG_LEVEL);
        Preconditions.checkArgument(message != null, NULL_LOG_MESSAGE);

        getInstance().getLogger().log(level, ChatUtil.color(MessageFormat.format(message, args)));
    }

    /**
     * Call the logger to log a message with arguments.
     * ChatColor code will be translated automatically,
     * and message is dealt with MessageFormat#format().
     *
     * @param level   the log {@link Level}
     * @param ex      the {@link Throwable} exception
     * @param message the message
     * @param args    the arguments with in
     * @see MessageFormat
     */
    public static void log(@Nonnull Level level, @Nonnull Throwable ex, @Nonnull String message, @Nullable Object... args) {
        Preconditions.checkArgument(level != null, NULL_LOG_LEVEL);
        Preconditions.checkArgument(message != null, NULL_LOG_MESSAGE);

        getInstance().getLogger().log(level, ex, () -> ChatUtil.color(MessageFormat.format(message, args)));
    }

    /**
     * Call the {@link org.bukkit.command.ConsoleCommandSender} to send a message with arguments.
     * ChatColor code will be translated automatically,
     * and message is dealt with MessageFormat#format().
     *
     * @param message the message
     * @param args    the arguments with in
     * @see MessageFormat
     */
    public static void sendConsole(@Nonnull String message, @Nullable Object... args) {
        Preconditions.checkArgument(message != null, NULL_LOG_MESSAGE);

        Bukkit.getConsoleSender().sendMessage("[" + getInstance().getName() + "] " + ChatUtil.color(MessageFormat.format(message, args)));
    }

    /**
     * Validate the information given by constructor
     */
    private void validate() {
        if (instance != null) {
            throw new IllegalStateException("Addon " + instance.getName() + " is already using this GuizhanLib, Shade an relocate your own!");
        }
    }

    /**
     * Use load() instead
     */
    @Override
    public final void onLoad() {
        if (loading) {
            throw new IllegalStateException(getName() + " is already loading! Do not call super.onLoad()!");
        }

        loading = true;

        // Load
        try {
            load();
        } catch (RuntimeException e) {
            handleException(e);
        } finally {
            loading = false;
        }
    }

    /**
     * Use enable() instead
     */
    @Override
    public final void onEnable() {
        if (enabling) {
            throw new IllegalStateException(getName() + " is already enabling! Do not call super.onEnable()!");
        }

        enabling = true;

        // Set instance
        setInstance(this);

        // This is used to check if the auto update config is broken
        boolean brokenConfig = false;

        // Check config.yml
        try {
            config = new AddonConfig("config.yml");
        } catch (RuntimeException e) {
            brokenConfig = true;
            e.printStackTrace();
        }

        // Validate autoUpdateKey
        if (autoUpdateKey == null || autoUpdateKey.isEmpty()) {
            brokenConfig = true;
            handleException(new IllegalStateException("Invalid autoUpdateKey"));
        }

        // Check updater
        if (environment == Environment.LIVE) {
            // Set up warning
            if (brokenConfig) {
                handleException(new IllegalArgumentException("Auto update is not configured correctly"));
            } else if (config.getBoolean(autoUpdateKey)) {
                autoUpdateEnabled = true;
                autoUpdate();
            }
        }

        // Create Scheduler
        scheduler = new Scheduler(this);

        // Call enable()
        try {
            enable();
        } catch (RuntimeException ex) {
            handleException(ex);
        } finally {
            enabling = false;
        }
    }

    /**
     * Use disable() instead.
     */
    @Override
    public final void onDisable() {
        if (disabling) {
            throw new IllegalStateException(getName() + " is already disabling! Do not call super.onDisable()!");
        }

        disabling = true;

        try {
            disable();
        } catch (RuntimeException e) {
            handleException(e);
        } finally {
            disabling = false;
            if (SlimefunAddonInstance.isInitialized())
                SlimefunAddonInstance.getSFAInstance().setSlimefunTickCount(0);
            setInstance(null);
            config = null;
        }
    }

    /**
     * Called when loading
     */
    protected void load() {
    }

    /**
     * Called when enabling
     */
    protected abstract void enable();

    /**
     * Called when disabling
     */
    protected abstract void disable();

    /**
     * Called when auto update is enabled
     */
    protected void autoUpdate() {
    }

    /**
     * Handle the {@link RuntimeException} in different environments.
     * Print the exception if in live environment, throw the exception if in testing environment
     *
     * @param ex the {@link RuntimeException}
     */
    private void handleException(RuntimeException ex) {
        switch (environment) {
            case LIVE -> ex.printStackTrace();
            case TESTING, LIB_TESTING -> throw ex;
        }
    }

    /**
     * Get the current {@link Environment}
     *
     * @return the current {@link Environment}
     */
    @Nonnull
    public final Environment getEnvironment() {
        return environment;
    }

    /**
     * If the auto update is enabled
     *
     * @return if the auto update is enabled
     */
    public final boolean isAutoUpdateEnabled() {
        return autoUpdateEnabled;
    }

    /**
     * Get the {@link AddonConfig}
     *
     * @return the {@link AddonConfig}
     */
    @Override
    @Nonnull
    public final FileConfiguration getConfig() {
        return config;
    }

    /**
     * Save default config.
     * Overridden and does nothing since it is handled in #onEnable()
     */
    @Override
    public final void saveDefaultConfig() {
    }
}
