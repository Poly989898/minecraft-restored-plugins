package io.lumine.mythic.bukkit.utils.network.messaging.redis;

import io.lumine.mythic.bukkit.utils.network.messaging.Messenger;
import io.lumine.mythic.bukkit.utils.redis.jedis.JedisPool;
import io.lumine.mythic.bukkit.utils.terminable.Terminable;

public interface Redis extends Terminable, Messenger {
   JedisPool getPool();
}
