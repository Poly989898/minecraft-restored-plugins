package io.lumine.mythic.lib.listener.event;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.event.AttackEvent;
import io.lumine.mythic.lib.api.event.PlayerAttackEvent;
import io.lumine.mythic.lib.api.event.PlayerKillEntityEvent;
import io.lumine.mythic.lib.api.stat.provider.PlayerStatProvider;
import io.lumine.mythic.lib.damage.AttackMetadata;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;
import org.jetbrains.annotations.NotNull;

public class AttackEventListener implements Listener {
   private static final double MINIMUM_BASE_DAMAGE = 1.000001;
   private static final DamageModifier MODIFIER_USED = DamageModifier.ARMOR;

   @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
   public void registerEvents(EntityDamageEvent event) {
      if (event.getEntity() instanceof LivingEntity && !UtilityMethods.isFake(event)) {
         AttackMetadata attack = MythicLib.plugin.getDamage().findAttack(event);
         if (!attack.isPlayer() || ((PlayerStatProvider)attack.getAttacker()).getPlayer().getGameMode() != GameMode.SPECTATOR) {
            AttackEvent attackEvent = attack.isPlayer() ? new PlayerAttackEvent(event, attack) : new AttackEvent(event, attack);
            Bukkit.getPluginManager().callEvent(attackEvent);
            if (!attackEvent.isCancelled()) {
               event.setDamage(attackEvent.getDamage().getDamage());
               this.fixDamage(event);
               if (attack.isPlayer() && event.getFinalDamage() >= ((Damageable)event.getEntity()).getHealth()) {
                  Bukkit.getPluginManager().callEvent(new PlayerKillEntityEvent(attack, (LivingEntity)event.getEntity()));
               }
            }
         }
      }
   }

   private void fixDamage(@NotNull EntityDamageEvent event) {
      if (event.isApplicable(MODIFIER_USED)) {
         double baseDamage = event.getDamage(DamageModifier.BASE);
         double compensate = Math.max(0.0, 1.000001 - baseDamage);
         if (!(compensate <= 0.0)) {
            event.setDamage(DamageModifier.BASE, 1.000001);
            event.setDamage(MODIFIER_USED, event.getDamage(MODIFIER_USED) - compensate);
         }
      }
   }
}
