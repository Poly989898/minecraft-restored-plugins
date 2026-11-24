package io.lumine.mythic.lib.api.stat.handler;

import io.lumine.mythic.lib.api.stat.StatInstance;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class DelegateStatHandler extends StatHandler {
   private final StatHandler delegate;

   public DelegateStatHandler(@NotNull ConfigurationSection config, @NotNull String stat, @NotNull StatHandler delegate) {
      super(config, stat);
      this.delegate = delegate;
   }

   @Override
   public void runUpdate(@NotNull StatInstance instance) {
      this.delegate.runUpdate(instance.getMap().getInstance(this.delegate.getStat()));
   }
}
