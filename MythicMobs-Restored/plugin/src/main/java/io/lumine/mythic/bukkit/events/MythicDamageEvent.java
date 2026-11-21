package io.lumine.mythic.bukkit.events;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.damage.DamageMetadata;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public class MythicDamageEvent extends Event implements Cancellable {
   private final SkillCaster caster;
   private final AbstractEntity target;
   private final DamageMetadata damageMetadata;
   boolean cancelled = false;
   private static final HandlerList handlers = new HandlerList();

   public MythicDamageEvent(@NotNull SkillCaster caster, @NotNull AbstractEntity target, DamageMetadata metadata) {
      this.caster = caster;
      this.target = target;
      this.damageMetadata = metadata;
   }

   public double getDamage() {
      return this.damageMetadata.getAmount();
   }

   public void setDamage(double amount) {
      this.damageMetadata.setAmount(amount);
   }

   @NotNull
   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}
