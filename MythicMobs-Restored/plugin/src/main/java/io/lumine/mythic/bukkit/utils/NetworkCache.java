package io.lumine.mythic.bukkit.utils;

import io.lumine.mythic.bukkit.utils.network.messaging.redis.Redis;
import io.lumine.mythic.bukkit.utils.promise.Promise;
import io.lumine.mythic.bukkit.utils.redis.jedis.Jedis;
import io.lumine.mythic.bukkit.utils.redis.jedis.JedisPool;
import io.lumine.mythic.bukkit.utils.redis.jedis.Pipeline;
import io.lumine.mythic.bukkit.utils.redis.jedis.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import org.bukkit.Bukkit;

public final class NetworkCache {
   public static Redis get() {
      return (Redis)Bukkit.getServer().getServicesManager().load(Redis.class);
   }

   public static JedisPool getPool() {
      return get().getPool();
   }

   public static Promise<String> getValue(@Nonnull String key) {
      return Promise.supplyingAsync(() -> {
         String str;
         try (Jedis j = getPool().getResource()) {
            str = j.get(key);
         }

         return str;
      });
   }

   public static String getValueSync(@Nonnull String key) {
      String str;
      try (Jedis j = getPool().getResource()) {
         str = j.get(key);
      }

      return str;
   }

   public static Promise<Boolean> setMapValue(@Nonnull String key, Map<String, String> value) {
      return Promise.supplyingAsync(() -> {
         Boolean flag;
         try (Jedis j = getPool().getResource()) {
            j.hset(key, value);
            flag = true;
         }

         return flag;
      });
   }

   public static void setMapValueSync(@Nonnull String key, Map<String, String> value) {
      try (Jedis j = getPool().getResource()) {
         j.hset(key, value);
      }
   }

   public static Promise<Boolean> deleteMapValue(@Nonnull String key, @Nonnull String field) {
      return Promise.supplyingAsync(() -> {
         Boolean flag;
         try (Jedis j = getPool().getResource()) {
            j.hdel(key, field);
            flag = true;
         }

         return flag;
      });
   }

   public static void deleteMapValueSync(@Nonnull String key, @Nonnull String field) {
      try (Jedis j = getPool().getResource()) {
         j.hdel(key, field);
      }
   }

   public static Promise<Map<String, String>> getMap(@Nonnull String key) {
      return Promise.supplyingAsync(() -> {
         Map map;
         try (Jedis j = getPool().getResource()) {
            map = j.hgetAll(key);
         }

         return map;
      });
   }

   public static Map<String, String> getMapSync(@Nonnull String key) {
      Map map;
      try (Jedis j = getPool().getResource()) {
         map = j.hgetAll(key);
      }

      return map;
   }

   public static Promise<String> getMapValue(@Nonnull String key, @Nonnull String field) {
      return Promise.supplyingAsync(() -> {
         String str;
         try (Jedis j = getPool().getResource()) {
            str = j.hget(key, field);
         }

         return str;
      });
   }

   public static Promise<List<String>> getMultipleMapValues(@Nonnull List<String> keys, @Nonnull String field) {
      return Promise.supplyingAsync(() -> {
         List<Response<String>> responseList = new ArrayList<>(keys.size());

         try (Jedis j = getPool().getResource()) {
            Pipeline pipelined = j.pipelined();

            for (String key : keys) {
               Response<String> response = pipelined.hget(key, field);
               responseList.add(response);
            }

            pipelined.sync();
         }

         return responseList.stream().map(Response::get).toList();
      });
   }

   public static String getMapValueSync(@Nonnull String key, @Nonnull String field) {
      String str;
      try (Jedis j = getPool().getResource()) {
         str = j.hget(key, field);
      }

      return str;
   }

   public static Promise<Boolean> addListValue(@Nonnull String key, @Nonnull String value) {
      return Promise.supplyingAsync(() -> {
         try (Jedis j = getPool().getResource()) {
            j.lpush(key, value);
         }

         return true;
      });
   }

   public static void addListValueSync(@Nonnull String key, @Nonnull String value) {
      try (Jedis j = getPool().getResource()) {
         j.lpush(key, value);
      }
   }

   public static Promise<Boolean> removeListValue(@Nonnull String key, @Nonnull String value) {
      return Promise.supplyingAsync(() -> {
         try {
            Boolean flag;
            try (Jedis j = getPool().getResource()) {
               j.lrem(key, 0L, value);
               flag = true;
            }

            return flag;
         } catch (Exception e) {
            e.printStackTrace();
            return false;
         }
      });
   }

   public static void removeListValueSync(@Nonnull String key, @Nonnull String value) {
      try (Jedis j = getPool().getResource()) {
         j.lrem(key, 0L, value);
      }
   }

   public static Promise<List<String>> getListValues(@Nonnull String key) {
      return Promise.supplyingAsync(() -> {
         List list;
         try (Jedis j = getPool().getResource()) {
            list = j.lrange(key, 0L, -1L);
         }

         return list;
      });
   }

   public static List<String> getListValuesSync(@Nonnull String key) {
      List list;
      try (Jedis j = getPool().getResource()) {
         list = j.lrange(key, 0L, -1L);
      }

      return list;
   }

   public static Promise<Boolean> setValue(@Nonnull String key, @Nonnull String value) {
      return Promise.supplyingAsync(() -> {
         try (Jedis j = getPool().getResource()) {
            j.set(key, value);
         }

         return true;
      });
   }

   public static void setValueSync(@Nonnull String key, @Nonnull String value) {
      try (Jedis j = getPool().getResource()) {
         j.set(key, value);
      }
   }

   public static Promise<Boolean> deleteKey(@Nonnull String key) {
      return Promise.supplyingAsync(() -> {
         try {
            Boolean flag;
            try (Jedis j = getPool().getResource()) {
               j.del(key);
               flag = true;
            }

            return flag;
         } catch (Exception e) {
            e.printStackTrace();
            return false;
         }
      });
   }

   public static void deleteKeySync(@Nonnull String key) {
      try (Jedis j = getPool().getResource()) {
         j.del(key);
      }
   }

   private NetworkCache() {
      throw new UnsupportedOperationException("This class cannot be instantiated");
   }
}
