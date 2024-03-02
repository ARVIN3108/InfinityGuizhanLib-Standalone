package net.guizhanss.guizhanlib.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.guizhanss.guizhanlib.slimefun.addon.AbstractAddon;
import org.bukkit.Bukkit;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Consumer;

/**
 * A class for registering listeners and event handlers
 *
 * @author Mooy1
 */
@ParametersAreNonnullByDefault
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Event implements Listener {

    private static final Listener LISTENER = new Event();

    /**
     * Calls the given event
     */
    public static <T extends org.bukkit.event.Event> T call(T event) {
        Bukkit.getPluginManager().callEvent(event);
        return event;
    }

    /**
     * Registers the given listener class
     */
    public static void registerListener(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, AbstractAddon.getInstance());
    }

    /**
     * Registers the given handler to the given event
     */
    @SuppressWarnings("unchecked")
    public static <T extends org.bukkit.event.Event> void addHandler(Class<T> eventClass, EventPriority priority,
                                                                     boolean ignoreCancelled, Consumer<T> handler) {
        Bukkit.getPluginManager().registerEvent(eventClass, LISTENER, priority,
            (listener, event) -> handler.accept((T) event), AbstractAddon.getInstance(), ignoreCancelled);
    }

}
