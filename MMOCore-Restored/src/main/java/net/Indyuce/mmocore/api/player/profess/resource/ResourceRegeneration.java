package net.Indyuce.mmocore.api.player.profess.resource;

import java.util.function.BiFunction;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.api.util.math.formula.LinearValue;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;

public class ResourceRegeneration {
   private final boolean offCombatOnly;
   private final LinearValue scalar;
   private final ResourceRegeneration.HandlerType type;
   private final PlayerResource resource;

   public ResourceRegeneration(PlayerResource resource) {
      this(resource, null, null, false);
   }

   public ResourceRegeneration(PlayerResource resource, ConfigurationSection config) {
      this.resource = resource;
      this.offCombatOnly = config.getBoolean("off-combat");
      Validate.isTrue(config.contains("type"), "Could not find resource regen scaling type");
      this.type = ResourceRegeneration.HandlerType.valueOf(config.getString("type").toUpperCase());
      Validate.notNull(config.getConfigurationSection("value"), "Could not find resource regen value config section");
      this.scalar = new LinearValue(config.getConfigurationSection("value"));
   }

   public ResourceRegeneration(PlayerResource resource, ResourceRegeneration.HandlerType type, LinearValue scalar, boolean offCombatOnly) {
      this.resource = resource;
      this.type = type;
      this.scalar = scalar;
      this.offCombatOnly = offCombatOnly;
   }

   public double getRegen(PlayerData player) {
      double d = 0.0;
      if (!player.isInCombat() || !player.getProfess().hasOption(this.resource.getOffCombatRegen())) {
         d += player.getStats().getStat(this.resource.getRegenStat());
         d += player.getStats().getStat(this.resource.getMaxRegenStat()) / 100.0 * this.resource.getMax(player);
      }

      if (this.type != null && (!player.isInCombat() || !this.offCombatOnly)) {
         d += this.scalar.calculate(player.getLevel()) / 100.0 * this.type.getScaling(player, this.resource);
      }

      return d;
   }

   public enum HandlerType {
      MAX((player, resource) -> resource.getMax(player)),
      MISSING((player, resource) -> resource.getMax(player) - resource.getCurrent(player));

      private final BiFunction<PlayerData, PlayerResource, Double> calculation;

      HandlerType(BiFunction<PlayerData, PlayerResource, Double> calculation) {
         this.calculation = calculation;
      }

      public double getScaling(PlayerData player, PlayerResource resource) {
         return this.calculation.apply(player, resource);
      }
   }
}
