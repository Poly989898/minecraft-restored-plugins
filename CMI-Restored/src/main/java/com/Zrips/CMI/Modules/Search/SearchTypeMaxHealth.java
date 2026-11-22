package com.Zrips.CMI.Modules.Search;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import net.Zrips.CMILib.Container.CMIPlayer;
import org.bukkit.entity.Player;

public class SearchTypeMaxHealth extends SearchTypePlayerBase {
  double health = 20.0;

  public SearchTypeMaxHealth(int value) {
    this.health = value;
  }

  @Override
  public Set<CMIDataResultBase> checkPlayer(Player player) {
    double number = CMIPlayer.getMaxHealth(player);
    if (number < this.health) {
      return Collections.emptySet();
    }

    HashSet<CMIDataResultBase> hashSet = new HashSet<>();
    hashSet.add(
        new CMIDataResultValue(
            CMIPlayerSearchPlaceType.Custom, player.getUniqueId(), (int) number));
    return hashSet;
  }

  @Override
  public String getSearchTypeName() {
    return "MaxHealth " + this.health;
  }
}
