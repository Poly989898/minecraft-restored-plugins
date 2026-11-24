package io.lumine.mythic.lib.listener.option;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.util.formula.NumericalExpression;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.jetbrains.annotations.NotNull;

public class VanillaDamageModifiers implements Listener {
   private final Map<DamageCause, String> formulas = new HashMap<>();

   public VanillaDamageModifiers(@NotNull ConfigurationSection config) {
      config = config.getConfigurationSection("source");
      if (config != null) {
         for (String key : config.getKeys(false)) {
            try {
               DamageCause cause = DamageCause.valueOf(UtilityMethods.enumName(key));
               this.formulas.put(cause, config.getString(key));
            } catch (RuntimeException exception) {
               MythicLib.plugin.getLogger().log(Level.WARNING, "Could not load vanilla damage modifier '" + key + "': " + exception.getMessage());
            }
         }
      }
   }

   @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
   public void a(EntityDamageEvent event) {
      if (event.getEntity() instanceof Player && !UtilityMethods.isFake(event)) {
         Player player = (Player)event.getEntity();
         String formula = this.formulas.get(event.getCause());
         if (formula != null) {
            try {
               formula = this.applyPlaceholders(formula, event, player);
               double result = NumericalExpression.eval(formula);
               event.setDamage(result);
            } catch (Exception exception) {
               MythicLib.plugin
                  .getLogger()
                  .log(Level.WARNING, "Could not evaluate formula '" + formula + "' for player '" + player.getUniqueId() + "': " + exception.getMessage());
            }
         }
      }
   }

   @NotNull
   private String applyPlaceholders(@NotNull String str, @NotNull EntityDamageEvent event, @NotNull Player player) {
      str = str.replace("{event_damage}", String.valueOf(event.getDamage()));
      str = str.replace("{final_damage}", String.valueOf(event.getFinalDamage()));
      return MythicLib.plugin.getPlaceholderParser().parse(player, str);
   }
}
