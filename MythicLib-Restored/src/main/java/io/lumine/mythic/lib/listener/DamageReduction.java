package io.lumine.mythic.lib.listener;

import io.lumine.mythic.lib.api.event.AttackEvent;
import io.lumine.mythic.lib.api.player.MMOPlayerData;
import io.lumine.mythic.lib.api.stat.StatMap;
import io.lumine.mythic.lib.damage.DamageMetadata;
import io.lumine.mythic.lib.damage.DamageType;
import io.lumine.mythic.lib.element.Element;
import io.lumine.mythic.lib.util.DefenseFormula;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.jetbrains.annotations.NotNull;

public class DamageReduction implements Listener {
   private static final Set<DamageCause> FIRE_DAMAGE_CAUSES = new HashSet<>(
      Arrays.asList(DamageCause.FIRE, DamageCause.FIRE_TICK, DamageCause.LAVA, DamageCause.MELTING)
   );

   @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
   public void damageMitigation(AttackEvent event) {
      MMOPlayerData data = MMOPlayerData.getOrNull(event.getEntity());
      if (data != null) {
         for (DamageReduction.SpecificDamageReductionType type : DamageReduction.SpecificDamageReductionType.values()) {
            type.applyReduction(data.getStatMap(), event.getDamage(), event.toBukkit());
         }

         for (DamageType damageType : DamageType.values()) {
            event.getDamage().multiplicativeModifier(Math.max(0.0, 1.0 - data.getStatMap().getStat(damageType + "_DAMAGE_REDUCTION") / 100.0), damageType);
         }

         double defense = data.getStatMap().getStat("DEFENSE");
         double neutralDamage;
         if (defense > 0.0 && (neutralDamage = event.getDamage().getDamage((Element)null)) > 0.0) {
            double ratio = Math.max(0.0, DefenseFormula.calculateDamage(false, defense, neutralDamage)) / neutralDamage;
            event.getDamage().multiplicativeModifier(ratio, (Element)null);
         }
      }
   }

   private static LivingEntity getDamager(EntityDamageByEntityEvent event) {
      if (event.getDamager() instanceof LivingEntity) {
         return (LivingEntity)event.getDamager();
      }

      if (event.getDamager() instanceof Projectile) {
         Projectile proj = (Projectile)event.getDamager();
         if (proj.getShooter() instanceof LivingEntity) {
            return (LivingEntity)proj.getShooter();
         }
      }

      return null;
   }

   public enum SpecificDamageReductionType {
      ENVIRONMENTAL("DAMAGE_REDUCTION", event -> true),
      PVP(event -> event instanceof EntityDamageByEntityEvent && DamageReduction.getDamager((EntityDamageByEntityEvent)event) instanceof Player),
      PVE(event -> event instanceof EntityDamageByEntityEvent && !(DamageReduction.getDamager((EntityDamageByEntityEvent)event) instanceof Player)),
      FIRE(event -> DamageReduction.FIRE_DAMAGE_CAUSES.contains(event.getCause())),
      FALL(event -> event.getCause() == DamageCause.FALL);

      @NotNull
      private final String stat;
      @NotNull
      private final Predicate<EntityDamageEvent> apply;

      SpecificDamageReductionType(String stat, Predicate<EntityDamageEvent> apply) {
         this.stat = stat;
         this.apply = Objects.requireNonNull(apply);
      }

      SpecificDamageReductionType(Predicate<EntityDamageEvent> apply) {
         this.stat = this.name() + "_DAMAGE_REDUCTION";
         this.apply = Objects.requireNonNull(apply);
      }

      public void applyReduction(StatMap statMap, DamageMetadata damageMeta, EntityDamageEvent event) {
         if (this.apply.test(event)) {
            damageMeta.multiplicativeModifier(1.0 - Math.min(statMap.getStat(this.stat) / 100.0, 1.0));
         }
      }
   }
}
