package litebans;

import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TemplateInfo {
   private final String message;
   private final String reason;
   private final String broadcast;
   private final String permission;
   private final List actions;
   private final List flags;

   public TemplateInfo(@Nullable String text, @Nullable String text2, @Nullable String text3, @Nullable String text4, @NotNull List list, @NotNull List list2) {
      this.message = text;
      this.reason = text2;
      this.broadcast = text3;
      this.permission = text4;
      this.actions = list;
      this.flags = list2;
   }

   public final String getMessage() {
      return this.message;
   }

   public final String getReason() {
      return this.reason;
   }

   public final String getBroadcast() {
      return this.broadcast;
   }

   public final String getPermission() {
      return this.permission;
   }

   public final List getActions() {
      return this.actions;
   }

   public final List getFlags() {
      return this.flags;
   }
}
