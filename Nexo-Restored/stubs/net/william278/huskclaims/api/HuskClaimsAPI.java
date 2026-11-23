package net.william278.huskclaims.api;

import java.util.UUID;
import net.william278.huskclaims.user.OnlineUser;

/** Compile-only stub. */
public abstract class HuskClaimsAPI {
    public static HuskClaimsAPI getInstance() { return null; }
    public abstract OnlineUser getOnlineUser(UUID uuid);
}