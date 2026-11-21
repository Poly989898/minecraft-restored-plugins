package io.lumine.mythic.core.glow;

import io.lumine.mythic.api.adapters.AbstractPlayer;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

public record GlowData(Map<UUID, GlowColor> data) {
   public Map<UUID, GlowColor> data() {
      return Collections.unmodifiableMap(this.data);
   }

   public GlowData set(AbstractPlayer player, GlowColor color) {
      this.data.put(player.getUniqueId(), color);
      return this;
   }

   public GlowData remove(AbstractPlayer player) {
      this.data.remove(player.getUniqueId());
      return this;
   }

   public GlowColor getColor(AbstractPlayer player) {
      return this.data.get(player.getUniqueId());
   }
}
