package com.Zrips.CMI.Modules.Search;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.entity.Player;

public class SearchTypeGodMode extends SearchTypePlayerBase {
  boolean godMode = false;

  public SearchTypeGodMode(boolean flag) {
    this.godMode = flag;
  }

  @Override
  public Set<CMIDataResultBase> checkPlayer(Player player) {
    if (player != null && this.godMode == player.isInvulnerable()) {
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
    return "God state " + this.godMode;
  }
}
