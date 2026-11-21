package io.lumine.mythic.bukkit.utils.cooldown;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;

class CooldownMapImpl<T> implements CooldownMap<T> {
   private final Cooldown base;
   private final LoadingCache<T, Cooldown> cache;

   CooldownMapImpl(final Cooldown base) {
      this.base = base;
      this.cache = CacheBuilder.newBuilder().expireAfterAccess(base.getTimeout() + 10000L, TimeUnit.MILLISECONDS).build(new CacheLoader<T, Cooldown>() {
         public Cooldown load(@Nonnull T key) {
            return base.copy();
         }
      });
   }

   @Nonnull
   @Override
   public Cooldown getBase() {
      return this.base;
   }

   @Nonnull
   @Override
   public Cooldown get(@Nonnull T key) {
      Objects.requireNonNull(key, "key");
      return this.cache.getUnchecked(key);
   }

   @Override
   public void put(@Nonnull T key, @Nonnull Cooldown cooldown) {
      Objects.requireNonNull(key, "key");
      this.cache.put(key, cooldown);
   }

   @Nonnull
   @Override
   public Map<T, Cooldown> getAll() {
      return this.cache.asMap();
   }
}
