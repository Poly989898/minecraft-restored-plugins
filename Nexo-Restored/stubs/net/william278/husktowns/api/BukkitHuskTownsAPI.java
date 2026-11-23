package net.william278.husktowns.api;

import net.william278.husktowns.claim.Position;
import net.william278.husktowns.user.OnlineUser;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/** Compile-only stub. */
public abstract class BukkitHuskTownsAPI {
    public static BukkitHuskTownsAPI getInstance() { return null; }
    public abstract OnlineUser getOnlineUser(Player player);
    public abstract Position getPosition(Location location);
}