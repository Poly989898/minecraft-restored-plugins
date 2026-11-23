package world.bentobox.bentobox.managers;

import java.util.Optional;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import world.bentobox.bentobox.database.objects.Island;

/** Compile-only stub. */
public interface IslandsManager {
    Optional<Island> getIslandAt(Location location);
    boolean locationIsOnIsland(Player player, Location location);
}