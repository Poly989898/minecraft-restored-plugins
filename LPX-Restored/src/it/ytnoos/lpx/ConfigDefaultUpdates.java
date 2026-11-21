package it.ytnoos.lpx;

import java.util.List;

public final class ConfigDefaultUpdates {
   public static final List<ConfigVersionUpdates> VERSIONS = List.of(
      new ConfigVersionUpdates(
         1,
         List.of(
            update("checks.creative.c.options.max", "3000", new String[]{"64", "128", "256", "1024", "1500"}),
            update("checks.creative.d.options.max", "16384", new String[]{"2048"}),
            update("checks.flood.a.options.max", "1100", new String[]{"900", "1000"}),
            update("checks.flood.b.options.limits.USE_ITEM.max", "80", new String[]{"60"}),
            update("checks.flood.c.options.big-threshold", "6000", new String[]{"2048", "4096"}),
            update("checks.flood.c.options.max", "20", new String[]{"70", "80", "50", "10", "15"}),
            update("checks.flood.c.options.reset-interval", "100", new String[]{"500"}),
            update("checks.flood.d.options.big-threshold", "4000", new String[]{"256", "2048"}),
            update("checks.flood.d.options.max", "13", new String[]{"10"}),
            update("checks.flood.d.options.max-big", "6", new String[]{"4"}),
            update("checks.flood.g.options.interval", "250", new String[]{"500"}),
            update("checks.netty.a.options.max", "98304", new String[]{"65536", "131328"}),
            update("checks.netty.b.options.max-item-depth", "4", new String[]{"3"}),
            update("checks.place.a.options.max", "100", new String[]{"20", "40"}),
            update("checks.place.c.options.max", "50", new String[]{"40"}),
            update("checks.position.b.options.delay", "2000", new String[]{"5000"}),
            update("checks.position.c.options.max", "300000", new String[]{"2048", "15000", "30000"}),
            update("checks.position.d.options.max", "1000000", new String[]{"100000"}),
            update("checks.tab.b.options.max-length", "256", new String[]{"2048", "64"}),
            update("options.mechanics.interact-container-delay", "100", new String[]{"80"}),
            update("checks.window.e.options.max-hashed-slot-changes", "90", new String[]{"32"}),
            update("checks.window.e.options.max-hashed-components-per-stack", "128", new String[]{"32"})
         )
      ),
      new ConfigVersionUpdates(2, List.of(update("checks.netty.b.options.max-nbt-elements-per-packet", "2500", new String[]{"1800"})))
   );

   private ConfigDefaultUpdates() {
   }

   private static ConfigDefaultUpdate update(String string, String string2, String[] strings) {
      return new ConfigDefaultUpdate(string, string2, List.of(strings));
   }
}
