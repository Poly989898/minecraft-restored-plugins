package io.lumine.mythic.bukkit.utils.network.messaging.redis;

import javax.annotation.Nonnull;

public interface RedisProvider {
   @Nonnull
   Redis getRedis();

   @Nonnull
   Redis getRedis(@Nonnull RedisCredentials redisCredentials);

   @Nonnull
   RedisCredentials getGlobalCredentials();
}
