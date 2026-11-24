package net.Indyuce.mmocore.api.player;

import java.util.Objects;
import javax.inject.Provider;
import net.Indyuce.mmocore.MMOCore;

/** @deprecated */
public enum PlayerActivity {
   USE_WAYPOINT(() -> 5000L),
   FRIEND_REQUEST(() -> 120000L),
   ACTION_BAR_MESSAGE(() -> MMOCore.plugin.actionBarManager.getTimeOut() * 50L),
   LOOT_CHEST_SPAWN(() -> MMOCore.plugin.configManager.lootChestPlayerCooldown),
   CAST_SKILL(() -> MMOCore.plugin.configManager.globalSkillCooldown);

   private final Provider<Long> timeout;

   PlayerActivity(Provider<Long> timeout) {
      this.timeout = timeout;
   }

   public long getTimeOut() {
      return (Long)Objects.requireNonNull(this.timeout, "Time out not supported").get();
   }
}
