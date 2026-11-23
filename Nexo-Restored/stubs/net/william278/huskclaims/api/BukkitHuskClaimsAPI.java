package net.william278.huskclaims.api;

import java.util.Optional;
import net.william278.huskclaims.position.Position;
import net.william278.huskclaims.trust.TrustLevel;
import net.william278.huskclaims.trust.Trustable;
import org.bukkit.Location;

/** Compile-only stub. */
public abstract class BukkitHuskClaimsAPI {
    public static BukkitHuskClaimsAPI getInstance() { return null; }
    public abstract Position getPosition(Location location);
    public abstract Optional<TrustLevel> getTrustLevelAt(Position position, Trustable trustable);
}