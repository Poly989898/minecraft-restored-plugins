package io.lumine.mythic.bukkit.utils.cooldown;

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import javax.annotation.Nonnull;

class ComposedCooldownMapImpl<I, O> implements ComposedCooldownMap<I, O> {
   private final Cooldown base;
   private final LoadingCache<O, Cooldown> cache;
   private final Function<I, O> composeFunction;

   ComposedCooldownMapImpl(final Cooldown base, Function<I, O> composeFunction) {
      this.base = base;
      this.composeFunction = composeFunction;
      this.cache = CacheBuilder.newBuilder().expireAfterAccess(base.getTimeout() + 10000L, TimeUnit.MILLISECONDS).build(new CacheLoader<O, Cooldown>() {
         public Cooldown load(@Nonnull O key) {
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
   public Cooldown get(@Nonnull I key) {
      Objects.requireNonNull(key, "key");
      return this.cache.getUnchecked(this.composeFunction.apply(key));
   }

   @Override
   public void put(@Nonnull O key, @Nonnull Cooldown cooldown) {
      Objects.requireNonNull(key, "key");
      Preconditions.checkArgument(cooldown.getTimeout() == this.base.getTimeout(), "different timeout");
      this.cache.put(key, cooldown);
   }

   @Nonnull
   @Override
   public Map<O, Cooldown> getAll() {
      return this.cache.asMap();
   }
}
