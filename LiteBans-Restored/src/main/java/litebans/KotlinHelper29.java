package litebans;

import java.util.Collection;
import litebans.api.Entry;
import litebans.api.Events;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class KotlinHelper29 {
   public static final KotlinHelper29 KOTLIN_HELPER = new KotlinHelper29();

   private KotlinHelper29() {
   }

   public final void handleDatabaseSession(@NotNull DatabaseSession databaseSession, @NotNull Platform platform, @NotNull CharSequence message, @Nullable String text) {
      DatabaseSession databaseSession2 = databaseSession;
      Collection collection = this.obtainCollection();
      if (!collection.isEmpty()) {
         platform.execute(new KotlinRunnableImpl5(collection, databaseSession2, message, text));
      }
   }

   public final Entry computeEntry(@NotNull Platform platform, @NotNull Punishment punishment) {
      Punishment punishment2 = punishment;
      return new KotlinEntrySub(
         platform,
         punishment2.getId(),
         punishment2.getType().obtainPunishmentType().toString(),
         punishment2.getUuid(),
         punishment2.getIp(),
         punishment2.getReason().toString(),
         punishment2.getBannedByUuid(),
         punishment2.getBannedByName(),
         punishment2.getRemovedByUuid(),
         punishment2.getRemovedByName(),
         String.valueOf(punishment2.getRemovedByReason()),
         punishment2.getTime(),
         punishment2.getUntil(),
         punishment2.getServerScope(),
         punishment2.getServerOrigin(),
         punishment2.getTemplate(),
         punishment2.isSilent(),
         punishment2.isIpBan(),
         punishment2.isActive()
      );
   }

   public final void handleDatabaseSession(@NotNull DatabaseSession databaseSession, @NotNull Platform platform, @NotNull Punishment punishment) {
      Entry entry = this.computeEntry(platform, punishment);
      DatabaseSession databaseSession2 = databaseSession;
      Collection collection = this.obtainCollection();
      if (!collection.isEmpty()) {
         platform.execute(new KotlinRunnableImpl4(collection, databaseSession2, entry));
      }
   }

   public final void handleDatabaseSession2(@NotNull DatabaseSession databaseSession, @NotNull Platform platform, @NotNull Punishment punishment) {
      Entry entry = this.computeEntry(platform, punishment);
      DatabaseSession databaseSession2 = databaseSession;
      Collection collection = this.obtainCollection();
      if (!collection.isEmpty()) {
         platform.execute(new KotlinRunnableImpl6(collection, databaseSession2, entry));
      }
   }

   public final void handleDatabaseSession(@NotNull DatabaseSession databaseSession, @NotNull Throwable error) {
      databaseSession.getPlatform().getLogger().severe("An uncaught exception was thrown by a LiteBans API event listener.");
      if (error instanceof Exception) {
         databaseSession.getPlatform().getLogger().severe("This is likely a bug in a plugin which is using the LiteBans API.");
      }

      error.printStackTrace();
   }

   public final Collection obtainCollection() {
      Events events = Events.get();
      Intrinsics.checkNotNullExpressionValue(events, "");
      return ((KotlinEventsSub)events).getCopyOnWriteArrayList();
   }
}
