package me.ulrich.clans.interfaces;

import java.util.Optional;
import java.util.UUID;
import me.ulrich.clans.data.ClanData;

public interface UClans {
    PlayerAPI getPlayerAPI();

    interface PlayerAPI {
        Optional<ClanData> getPlayerClan(UUID uuid);
    }
}
