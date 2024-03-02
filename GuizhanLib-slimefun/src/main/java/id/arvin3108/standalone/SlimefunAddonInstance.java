package id.arvin3108.standalone;

import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import lombok.Getter;
import net.guizhanss.guizhanlib.slimefun.addon.AbstractAddon;
import net.guizhanss.guizhanlib.slimefun.addon.Environment;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * A class for constructing {@link SlimefunAddon}
 * without having to come into direct contact
 * with the {@link AbstractAddon} class
 *
 * @author ARVIN3108
 */
public class SlimefunAddonInstance implements SlimefunAddon {

    private static SlimefunAddonInstance instance;

    /**
     * Returns the total number of Slimefun ticks that have occurred
     *
     * @return total number of Slimefun ticks
     */
    @Getter
    private static int slimefunTickCount = 0;
    private static final Pattern GITHUB_PATTERN = Pattern.compile("[\\w-]+");
    @Getter
    private final String githubUser;
    @Getter
    private final String githubRepo;
    @Getter
    private final String githubBranch;
    private final String bugTrackerURL;

    /**
     * {@link SlimefunAddon} constructor
     *
     * @param githubUser   GitHub username of this project
     * @param githubRepo   GitHub repository of this project
     * @param githubBranch GitHub branch of this project
     */
    public SlimefunAddonInstance(String githubUser, String githubRepo, String githubBranch) {
        this.githubUser = githubUser;
        this.githubRepo = githubRepo;
        this.githubBranch = githubBranch;
        this.bugTrackerURL = MessageFormat.format("https://github.com/{0}/{1}/issues", githubUser, githubRepo);
        validate();
    }

    /**
     * Validate the information given by constructor
     */
    private void validate() {
        if (instance != null) {
            throw new IllegalStateException("SlimefunAddonInstance is already initialized!");
        }
        if (!GITHUB_PATTERN.matcher(githubUser).matches()) {
            throw new IllegalArgumentException("Invalid githubUser");
        }
        if (!GITHUB_PATTERN.matcher(githubRepo).matches()) {
            throw new IllegalArgumentException("Invalid githubRepo");
        }
        if (!GITHUB_PATTERN.matcher(githubBranch).matches()) {
            throw new IllegalArgumentException("Invalid githubBranch");
        }
    }

    /**
     * Used for Slimefun to get instance of this {@link JavaPlugin}
     *
     * @return the instance of this {@link JavaPlugin}
     */
    @Nonnull
    @Override
    public JavaPlugin getJavaPlugin() {
        return AbstractAddon.getInstance();
    }

    /**
     * This returns the default bug tracker URL by
     * the given GitHub username and repository in constructor.
     * <p>
     * Override it if you don't use GitHub issues as bug tracker
     *
     * @return the default bug tracker url
     */
    @Nullable
    @Override
    public String getBugTrackerURL() {
        return bugTrackerURL;
    }

    public void setSlimefunTickCount(int tick) {
        slimefunTickCount = tick;
    }

    public void create() {
        // Create total tick count
        if (AbstractAddon.getInstance().getEnvironment() == Environment.LIVE) {
            AbstractAddon.getScheduler().repeat(Slimefun.getTickerTask().getTickRate(), () -> slimefunTickCount++);
        }

        instance = this;
    }

    /**
     * Get an instance of extended class of {@link SlimefunAddon}
     *
     * @return The instance of extended class of {@link SlimefunAddon}
     */
    public static SlimefunAddonInstance getSFAInstance() {
        return Objects.requireNonNull(instance, "SlimefunAddonInstance is not initialized!");
    }

    /**
     * Check if {@link SlimefunAddonInstance} is initialized or not
     *
     * @return true if {@link SlimefunAddonInstance} is initialized
     */
    public static boolean isInitialized() {
        return instance != null;
    }
}
