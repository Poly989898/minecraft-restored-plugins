package litebans;

import java.util.Iterator;
import java.util.OptionalInt;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.group.GroupManager;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import org.jetbrains.annotations.NotNull;

public final class LiteHelper {
   public static final LiteHelper LITE_HELPER = new LiteHelper();

   private LiteHelper() {
   }

   public final Object getLuckPerms() {
      return LuckPermsProvider.get();
   }

   public final LuckPerms asLuckPerms(@NotNull Object obj) {
      Intrinsics.checkNotNullExpressionValue(obj, "");
      return (LuckPerms)obj;
   }

   public final User asUser(@NotNull Object obj) {
      Intrinsics.checkNotNullExpressionValue(obj, "");
      return (User)obj;
   }

   public final Object loadUser(@NotNull Object obj, @NotNull String text) {
      if (!TargetParser.isUuid(text)) {
         return null;
      }

      UserManager userManager = this.asLuckPerms(obj).getUserManager();
      CompletableFuture user = userManager.loadUser(UUID.fromString(text));
      return user.join();
   }

   public final Set getLoadedGroups(@NotNull Object obj) {
      GroupManager groupManager = this.asLuckPerms(obj).getGroupManager();
      return groupManager.getLoadedGroups();
   }

   public final String getPrefix(@NotNull Object obj) {
      return this.asUser(obj).getCachedData().getMetaData().getPrefix();
   }

   public final String getSuffix(@NotNull Object obj) {
      return this.asUser(obj).getCachedData().getMetaData().getSuffix();
   }

   public final int getGroupWeight(@NotNull Object obj, @NotNull Object obj2) {
      java.util.OptionalInt optionalInt = null;
      Set set = this.getLoadedGroups(obj);
      String primaryGroup = this.asUser(obj2).getCachedData().getMetaData().getPrimaryGroup();
      Iterable iterable = set;
      Iterator iterator2 = iterable.iterator();

      Group group;
      while (true) {
         if (iterator2.hasNext()) {
            Object obj3 = iterator2.next();
            Group group2 = (Group)obj3;
            if (!Intrinsics.areEqual(group2.getName(), primaryGroup)) {
               continue;
            }

            group = (Group)obj3;
            break;
         }

         group = null;
         break;
      }

      label16: {
         group = group;
         if (group != null) {
            optionalInt = group.getWeight();
            if (optionalInt != null) {
               break label16;
            }
         }

         optionalInt = OptionalInt.empty();
      }

      OptionalInt optionalInt2 = optionalInt;
      return optionalInt2.orElse(0);
   }

   public final boolean hasPermission(@NotNull Object obj, @NotNull String text) {
      return this.asUser(obj).getCachedData().getPermissionData().checkPermission(text).asBoolean();
   }
}
