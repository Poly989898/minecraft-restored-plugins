package com.Zrips.CMI.Modules.Search;

import java.util.Collections;
import java.util.Set;
import javax.annotation.Nonnull;
import org.bukkit.entity.Player;

public class SearchTypePlayerBase extends SearchTypeBase {
  @Nonnull
  public Set<CMIDataResultBase> checkPlayer(@Nonnull Player player) {
    return Collections.emptySet();
  }
}
