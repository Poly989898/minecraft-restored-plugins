package io.lumine.mythic.lib.listener.option;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.event.AttackUnregisteredEvent;
import io.lumine.mythic.lib.api.event.IndicatorDisplayEvent;
import io.lumine.mythic.lib.damage.DamageMetadata;
import io.lumine.mythic.lib.damage.DamagePacket;
import io.lumine.mythic.lib.damage.DamageType;
import io.lumine.mythic.lib.element.Element;
import io.lumine.mythic.lib.util.CustomFont;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DamageIndicators extends GameIndicators {
   private final String skillIcon;
   private final String weaponIcon;
   private final String skillIconCrit;
   private final String weaponIconCrit;
   private final boolean splitHolograms;
   @Nullable
   private final CustomFont font;
   @Nullable
   private final CustomFont fontCrit;

   public DamageIndicators(ConfigurationSection config) {
      super(config);
      this.skillIcon = config.getString("icon.skill.normal");
      this.weaponIcon = config.getString("icon.weapon.normal");
      this.skillIconCrit = config.getString("icon.skill.crit");
      this.weaponIconCrit = config.getString("icon.weapon.crit");
      this.splitHolograms = config.getBoolean("split-holograms");
      if (config.getBoolean("custom-font.enabled")) {
         this.font = new CustomFont(config.getConfigurationSection("custom-font.normal"));
         this.fontCrit = new CustomFont(config.getConfigurationSection("custom-font.crit"));
      } else {
         this.font = null;
         this.fontCrit = null;
      }
   }

   @EventHandler
   public void displayIndicators(AttackUnregisteredEvent event) {
      Entity entity = event.getEntity();
      if (!(event.getDamage().getDamage() < 0.02)) {
         if (!(entity instanceof Player) || !UtilityMethods.isVanished((Player)entity)) {
            List<String> holos = new ArrayList<>();
            Map<DamageIndicators.IndicatorType, Double> mappedDamage = this.mapDamage(event.getDamage());
            double modifierDue = (event.toBukkit().getFinalDamage() - event.getDamage().getDamage()) / Math.max(1, mappedDamage.size());
            mappedDamage.forEach((type, val) -> holos.add(type.computeFormat(val + modifierDue)));
            if (this.splitHolograms) {
               for (String holo : holos) {
                  this.displayIndicator(entity, holo, this.getDirection(event.toBukkit()), IndicatorDisplayEvent.IndicatorType.DAMAGE);
               }
            } else {
               String joined = String.join(" ", holos);
               this.displayIndicator(entity, joined, this.getDirection(event.toBukkit()), IndicatorDisplayEvent.IndicatorType.DAMAGE);
            }
         }
      }
   }

   @NotNull
   private Vector getDirection(EntityDamageEvent event) {
      if (event instanceof EntityDamageByEntityEvent) {
         Vector dir = event.getEntity().getLocation().toVector().subtract(((EntityDamageByEntityEvent)event).getDamager().getLocation().toVector()).setY(0);
         if (dir.lengthSquared() > 0.0) {
            double a = Math.atan2(dir.getZ(), dir.getX());
            a += (Math.PI / 2) * (RANDOM.nextDouble() - 0.5);
            return new Vector(Math.cos(a), 0.0, Math.sin(a));
         }
      }

      double a = RANDOM.nextDouble() * Math.PI * 2.0;
      return new Vector(Math.cos(a), 0.0, Math.sin(a));
   }

   @NotNull
   private Map<DamageIndicators.IndicatorType, Double> mapDamage(DamageMetadata damageMetadata) {
      Map<DamageIndicators.IndicatorType, Double> mapped = new HashMap<>();

      for (DamagePacket packet : damageMetadata.getPackets()) {
         DamageIndicators.IndicatorType type = new DamageIndicators.IndicatorType(damageMetadata, packet);
         mapped.put(type, mapped.getOrDefault(type, 0.0) + packet.getFinalValue());
      }

      return mapped;
   }

   private class IndicatorType {
      final boolean physical;
      @Nullable
      final Element element;
      final boolean crit;

      IndicatorType(DamageMetadata damageMetadata, DamagePacket packet) {
         this.physical = packet.hasType(DamageType.PHYSICAL);
         this.element = packet.getElement();
         this.crit = (this.physical ? damageMetadata.isWeaponCriticalStrike() : damageMetadata.isSkillCriticalStrike())
            || this.element != null && damageMetadata.isElementalCriticalStrike(this.element);
      }

      @NotNull
      private String computeIcon() {
         StringBuilder build = new StringBuilder();
         if (this.physical) {
            build.append(this.crit ? DamageIndicators.this.weaponIconCrit : DamageIndicators.this.weaponIcon);
         } else {
            build.append(this.crit ? DamageIndicators.this.skillIconCrit : DamageIndicators.this.skillIcon);
         }

         if (this.element != null) {
            build.append(this.element.getColor() + this.element.getLoreIcon());
         }

         return build.toString();
      }

      @NotNull
      private String computeFormat(double damage) {
         CustomFont indicatorFont = this.crit && DamageIndicators.this.fontCrit != null ? DamageIndicators.this.fontCrit : DamageIndicators.this.font;
         String formattedDamage = indicatorFont == null
            ? DamageIndicators.this.formatNumber(damage)
            : indicatorFont.format(DamageIndicators.this.formatNumber(damage));
         return MythicLib.plugin
            .getPlaceholderParser()
            .parse(null, DamageIndicators.this.getRaw().replace("{icon}", this.computeIcon()).replace("{value}", formattedDamage));
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (o != null && this.getClass() == o.getClass()) {
            DamageIndicators.IndicatorType that = (DamageIndicators.IndicatorType)o;
            return this.physical == that.physical && Objects.equals(this.element, that.element);
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         return Objects.hash(this.physical, this.element);
      }
   }
}
