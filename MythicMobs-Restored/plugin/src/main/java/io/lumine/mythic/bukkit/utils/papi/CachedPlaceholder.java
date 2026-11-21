package io.lumine.mythic.bukkit.utils.papi;

import io.lumine.mythic.bukkit.utils.cooldown.Cooldown;
import io.lumine.mythic.bukkit.utils.plugin.LuminePlugin;
import io.lumine.mythic.bukkit.utils.promise.Promise;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.bukkit.entity.Player;

public abstract class CachedPlaceholder<T extends LuminePlugin> extends Placeholder<T> {
   private Cooldown cooldown;
   private String cachedValue = "";

   public CachedPlaceholder(T plugin) {
      super(plugin);
      Duration rr = this.refreshRate();
      this.cooldown = Cooldown.of(rr.toMillis(), TimeUnit.MILLISECONDS);
   }

   public CachedPlaceholder(Placeholder<T> parent) {
      this(parent.getPlugin());
   }

   @Override
   public String onPlaceholderRequest(Player player, String[] args) {
      if (this.cooldown.test()) {
         this.onPlaceholderRefresh(player, args).thenAcceptSync(value -> this.cachedValue = value);
      }

      return this.cachedValue;
   }

   public abstract Promise<String> onPlaceholderRefresh(Player player, String[] strs);

   public abstract Duration refreshRate();
}
