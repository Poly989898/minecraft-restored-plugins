package io.lumine.mythic.bukkit.utils.profiles;

import com.google.common.collect.Lists;
import com.google.gson.stream.JsonReader;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.bukkit.utils.promise.Promise;
import io.lumine.mythic.bukkit.utils.uuid.UndashedUUIDs;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Deprecated
public final class MojangApi {
   public static final String PROFILES_URL = "https://api.mojang.com/users/profiles/minecraft/%s";
   public static final String NAME_HISTORY_URL = "https://api.mojang.com/user/profiles/%s/names";

   public static Promise<UUID> usernameToUuid(String username) {
      Objects.requireNonNull(username, "username");
      if (username.isEmpty()) {
         throw new IllegalArgumentException("empty");
      } else {
         return Schedulers.async()
            .supply(
               () -> {
                  try {
                     JsonReader reader = new JsonReader(
                        new InputStreamReader(
                           new URL(String.format("https://api.mojang.com/users/profiles/minecraft/%s", username)).openConnection().getInputStream()
                        )
                     );

                     UUID uuid;
                     try {
                        reader.beginObject();
                        reader.skipValue();
                        uuid = UndashedUUIDs.fromString(reader.nextString());
                     } catch (Throwable e) {
                        try {
                           reader.close();
                        } catch (Throwable ex) {
                           e.addSuppressed(ex);
                        }

                        throw e;
                     }

                     reader.close();
                     return uuid;
                  } catch (IOException ex2) {
                     ex2.printStackTrace();
                     return null;
                  }
               }
            );
      }
   }

   public static Promise<String> uuidToUsername(UUID uuid) {
      return getUsernameHistory(uuid).thenApplyAsync(names -> names.isEmpty() ? null : names.get(names.size() - 1));
   }

   public static Promise<List<String>> getUsernameHistory(UUID uuid) {
      Objects.requireNonNull(uuid, "uuid");
      return Schedulers.async()
         .supply(
            () -> {
               List<String> names = Lists.newArrayList();

               try {
                  JsonReader reader = new JsonReader(
                     new InputStreamReader(
                        new URL(String.format("https://api.mojang.com/user/profiles/%s/names", UndashedUUIDs.toString(uuid))).openConnection().getInputStream()
                     )
                  );

                  try {
                     reader.beginArray();

                     for (int i = 0; reader.hasNext(); i++) {
                        reader.beginObject();
                        reader.skipValue();
                        names.add(reader.nextString());
                        if (i != 0) {
                           reader.skipValue();
                           reader.skipValue();
                        }

                        reader.endObject();
                     }
                  } catch (Throwable e) {
                     try {
                        reader.close();
                     } catch (Throwable ex) {
                        e.addSuppressed(ex);
                     }

                     throw e;
                  }

                  reader.close();
               } catch (IOException ex2) {
                  ex2.printStackTrace();
               }

               return names;
            }
         );
   }

   private MojangApi() {
      throw new UnsupportedOperationException("This class cannot be instantiated");
   }
}
