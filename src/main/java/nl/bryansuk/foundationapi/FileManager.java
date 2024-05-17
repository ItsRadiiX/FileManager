package nl.bryansuk.foundationapi;

import nl.bryansuk.foundationapi.exceptions.FileManagerException;
import nl.bryansuk.foundationapi.handlers.Handler;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused")
public final class FileManager {

    private static FileManager instance;

    public final JavaPlugin plugin;
    public final Logger logger;

    private static final List<Handler> autoReloadingHandlers = Collections.synchronizedList(new ArrayList<>());
    private static BukkitTask autoReloadTask;
    private static boolean startedAutoReloading = false;

    public FileManager(JavaPlugin plugin, Logger logger) throws FileManagerException {
        if(instance != null){
            throw new FileManagerException("You can only have one instance of the FileManager at a time.");
        }
        this.plugin = plugin;
        this.logger = logger;
    }

    private static FileManager getInstance() throws FileManagerException {
        if (instance == null) throw new FileManagerException("FileManager has not yet been initialized.");
        return instance;
    }

    public static Logger getLogger() {
        try {
            return getInstance().logger;
        } catch (FileManagerException e) {
            throw new RuntimeException(e);
        }
    }

    public static JavaPlugin getPlugin() {
        try {
            return getInstance().plugin;
        } catch (FileManagerException e) {
            throw new RuntimeException(e);
        }
    }

    /*
        A U T O     R E L O A D I N G    M E T H O D S
     */

    /**
     * Starts auto-reloading of files with the specified time interval.
     *
     * @param autoReloadManagerTime the time interval in ticks
     */
    public static boolean startAutoReloading(JavaPlugin plugin, int autoReloadManagerTime, boolean autoReload){
        if (!autoReload) {
            stopAutoReloading();
            return false;
        }

        if (!startedAutoReloading) {
            autoReloadTask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
                for (Handler handler : autoReloadingHandlers) {
                    if (handler.onReload()) {
                        getLogger().debug("Reloaded: {}", handler.getFile().getName());
                    }
                }
            }, (autoReloadManagerTime * 20L), (autoReloadManagerTime * 20L));
            startedAutoReloading = true;
        }
        return true;
    }

    /**
     * Stops auto-reloading of files.
     */
    public static void stopAutoReloading(){
        if (startedAutoReloading) {
            if (autoReloadTask!= null) {
                autoReloadTask.cancel();
                autoReloadTask = null;
            }
            startedAutoReloading = false;
        }
    }

    public static void addHandler(Handler handler) {
        if (!containsHandler(handler)) {
            autoReloadingHandlers.add(handler);
        }
    }

    public static void removeHandler(Handler handler) {
        autoReloadingHandlers.remove(handler);
    }

    public static boolean containsHandler(Handler handler) {
        return autoReloadingHandlers.contains(handler);
    }


}
