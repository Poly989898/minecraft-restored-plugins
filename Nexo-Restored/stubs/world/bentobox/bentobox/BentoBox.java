package world.bentobox.bentobox;

import org.bukkit.plugin.java.JavaPlugin;
import world.bentobox.bentobox.managers.IslandWorldManager;
import world.bentobox.bentobox.managers.IslandsManager;

/** Compile-only stub of the BentoBox API (signatures only, never packaged). */
public abstract class BentoBox extends JavaPlugin {
    public abstract IslandWorldManager getIWM();
    public abstract IslandsManager getIslands();
}