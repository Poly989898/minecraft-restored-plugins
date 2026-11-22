package com.Zrips.CMI.Modules.ChatHandling;

import com.Zrips.CMI.Components.CMIComponentBase;
import com.Zrips.CMI.events.CMIPlayerEvent;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public class CMIAsyncChatEvent extends CMIPlayerEvent implements Cancellable {
  private boolean cancel = false;
  Set<Player> viewers = new HashSet<>();
  CMIComponentBase componentBase = null;

  public CMIAsyncChatEvent(Player player, CMIComponentBase cmiComponentBase, Set<Player> set) {
    super(player, true);
    this.componentBase = cmiComponentBase;
    this.viewers = set;
  }

  public CMIComponentBase getComponent() {
    return this.componentBase;
  }

  public Set<Player> getViewers() {
    return this.viewers;
  }

  public final void setCancelled(boolean flag) {
    this.cancel = flag;
  }

  public final boolean isCancelled() {
    return this.cancel;
  }
}
