package net.william278.husktowns.api;

import net.william278.husktowns.claim.Position;
import net.william278.husktowns.libraries.cloplib.operation.OperationType;
import net.william278.husktowns.user.OnlineUser;

/** Compile-only stub. */
public abstract class HuskTownsAPI {
    public static HuskTownsAPI getInstance() { return null; }
    public abstract boolean isOperationAllowed(OnlineUser user, OperationType type, Position position);
}