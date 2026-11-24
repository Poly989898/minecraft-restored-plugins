package io.lumine.mythic.lib.listener.option;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.event.IndicatorDisplayEvent;
import io.lumine.mythic.lib.util.CustomFont;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

public class RegenIndicators extends GameIndicators {
   @Nullable
   private final CustomFont font;
   private static final double HEAL_EPSILON = 0.001;

   public RegenIndicators(ConfigurationSection config) {
      super(config);
      this.font = config.getBoolean("custom-font.enabled") ? new CustomFont(config.getConfigurationSection("custom-font")) : null;
   }

   @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
   public void displayIndicators(EntityRegainHealthEvent event) {
      Entity entity = event.getEntity();
      if (entity instanceof LivingEntity
         && !(event.getAmount() <= 0.0)
         && !(((LivingEntity)entity).getHealth() + 0.001 > ((LivingEntity)entity).getAttribute(Attribute.MAX_HEALTH).getValue())) {
         if (!(entity instanceof Player) || !UtilityMethods.isVanished((Player)entity)) {
            String formattedNumber = this.formatNumber(event.getAmount());
            String formattedDamage = this.font == null ? formattedNumber : this.font.format(formattedNumber);
            String indicatorMessage = this.getRaw().replace("#", formattedDamage);
            this.displayIndicator(entity, indicatorMessage, this.getIndicatorDirection(entity), IndicatorDisplayEvent.IndicatorType.REGENERATION);
         }
      }
   }

   private Vector getIndicatorDirection(Entity entity) {
      if (entity instanceof Player) {
         double a = Math.toRadians(((Player)entity).getEyeLocation().getYaw()) + Math.PI * (1.0 + (RANDOM.nextDouble() - 0.5) / 2.0);
         return new Vector(Math.cos(a), 0.0, Math.sin(a));
      } else {
         double a = RANDOM.nextDouble() * Math.PI * 2.0;
         return new Vector(Math.cos(a), 0.0, Math.sin(a));
      }
   }
}
