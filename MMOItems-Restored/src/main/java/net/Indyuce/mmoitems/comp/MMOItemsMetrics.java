package net.Indyuce.mmoitems.comp;

import io.lumine.mythic.lib.metrics.bukkit.Metrics;
import io.lumine.mythic.lib.metrics.bukkit.Metrics.SingleLineChart;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;

public class MMOItemsMetrics extends Metrics {
   public MMOItemsMetrics() {
      super(MMOItems.plugin);
      this.addCustomChart(new SingleLineChart("items", () -> {
         int count = 0;

         for (Type type : MMOItems.plugin.getTypes().getAll()) {
            count += type.getConfigFile().getConfig().getKeys(false).size();
         }

         return count;
      }));
   }
}
