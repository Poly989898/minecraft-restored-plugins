package net.minecrell.serverlistplus.core.player.ban;
import java.util.Date;
import net.minecrell.serverlistplus.core.player.PlayerIdentity;
public interface BanProvider {
    boolean isBanned(PlayerIdentity player);
    String getBanReason(PlayerIdentity player);
    String getBanOperator(PlayerIdentity player);
    Date getBanExpiration(PlayerIdentity player);
}
