package io.lumine.mythic.lib.api.event.fake;

import com.google.common.base.Function;
import java.util.Map;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;
import org.jetbrains.annotations.NotNull;

public abstract class FakeEntityDamageByEntityEvent extends EntityDamageByEntityEvent {
   public FakeEntityDamageByEntityEvent(@NotNull Entity damager, @NotNull Entity damagee, @NotNull DamageCause cause, double damage) {
      super(damager, damagee, cause, damage);
   }

   public FakeEntityDamageByEntityEvent(
      @NotNull Entity damager,
      @NotNull Entity damagee,
      @NotNull DamageCause cause,
      @NotNull Map<DamageModifier, Double> modifiers,
      @NotNull Map<DamageModifier, ? extends Function<? super Double, Double>> modifierFunctions
   ) {
      super(damager, damagee, cause, modifiers, modifierFunctions);
   }
}
