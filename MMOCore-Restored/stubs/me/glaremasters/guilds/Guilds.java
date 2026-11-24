package me.glaremasters.guilds;

import me.glaremasters.guilds.guild.Guild;
import java.util.UUID;
import org.bukkit.entity.Player;

public class Guilds {
    public static Guilds.GuildsAPI getApi() { throw new UnsupportedOperationException("compile-only stub"); }

    public interface GuildsAPI {
        Guild getGuild(UUID uuid);
        Guild getGuild(Player player);
    }
}
