package com.Zrips.CMI.Modules.Search;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class SearchTypeGameMode extends SearchTypePlayerBase {
  GameMode mode = null;

  public SearchTypeGameMode(GameMode gameMode) {
    this.mode = gameMode;
  }

  @Override
  public Set<CMIDataResultBase> checkPlayer(Player player) {
    if (player != null && player.getGameMode() == this.mode) {
      HashSet<CMIDataResultBase> hashSet = new HashSet<>();
      hashSet.add(
          new CMIDataResultPlayerBase(CMIPlayerSearchPlaceType.Custom, player.getUniqueId()));
      return hashSet;
    } else {
      return Collections.emptySet();
    }
  }

  @Override
  public String getSearchTypeName() {
    return "GameMode " + this.mode;
  }
}
