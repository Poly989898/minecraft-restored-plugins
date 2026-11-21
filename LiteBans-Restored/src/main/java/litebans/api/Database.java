package litebans.api;

import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.UUID;
import litebans.api.exception.MissingImplementationException;
import lombok.Generated;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class Database {
   public static final String ANY_SERVER_SCOPE = "__ALL__";
   private static Database instance;

   public static Database get() {
      if (instance == null) {
         throw new MissingImplementationException();
      } else {
         return instance;
      }
   }

   public abstract boolean isPlayerBanned(@Nullable UUID uuid, @Nullable String text);

   public abstract boolean isPlayerBanned(@Nullable UUID uuid, @Nullable String text, @Nullable String text2);

   public abstract boolean isPlayerMuted(@Nullable UUID uuid, @Nullable String text);

   public abstract boolean isPlayerMuted(@Nullable UUID uuid, @Nullable String text, @Nullable String text2);

   @Nullable
   public abstract Entry getBan(long value, @Nullable String text);

   @Nullable
   public abstract Entry getBan(@Nullable UUID uuid, @Nullable String text, @Nullable String text2);

   @Nullable
   public abstract Entry getMute(long value, @Nullable String text);

   @Nullable
   public abstract Entry getMute(@Nullable UUID uuid, @Nullable String text, @Nullable String text2);

   @Nullable
   public abstract Entry getWarning(long value, @Nullable String text);

   @Nullable
   public abstract Entry getWarning(@Nullable UUID uuid, @Nullable String text, @Nullable String text2);

   @Nullable
   public abstract Entry getKick(@Nullable UUID uuid, @Nullable String text, @Nullable String text2);

   @Nullable
   public abstract Entry getKick(long value, @Nullable String text);

   public abstract Collection getUsersByIP(String text);

   @Nullable
   public abstract String getPlayerName(@NonNull UUID uuid);

   @Nullable
   public abstract UUID getPlayerUUID(@NonNull String text);

   public abstract PreparedStatement prepareStatement(@NotNull String text);

   @Generated
   public static void setInstance(Database database) {
      instance = database;
   }
}
