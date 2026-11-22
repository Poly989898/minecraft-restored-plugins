package com.Zrips.CMI.Modules.Search;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.entity.Player;

public class SearchTypeFlyMode extends SearchTypePlayerBase {
  boolean allowFlight = false;

  public SearchTypeFlyMode(boolean flag) {
    this.allowFlight = flag;
  }

  @Override
  public Set<CMIDataResultBase> checkPlayer(Player player) {
    if (player != null && this.allowFlight == player.getAllowFlight()) {
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
    return "Fly state " + this.allowFlight;
  }
}
