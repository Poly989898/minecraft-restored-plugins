package io.lumine.mythic.lib.api.event.fake;

import io.lumine.mythic.lib.comp.interaction.InteractionType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.jetbrains.annotations.NotNull;

public class DamageCheckEvent extends FakeEntityDamageByEntityEvent {
   private final InteractionType interactionType;

   public DamageCheckEvent(@NotNull Player damager, @NotNull Entity target, @NotNull InteractionType interactionType) {
      super(damager, target, DamageCause.ENTITY_ATTACK, 0.0);
      this.interactionType = interactionType;
   }

   @NotNull
   public InteractionType getInteractionType() {
      return this.interactionType;
   }
}
