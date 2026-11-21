package litebans;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class FabricPreLoginEvent {
   private final PlayerInfo playerInfo;
   private final Object obj2;
   private String text2;

   public FabricPreLoginEvent(@NotNull PlayerInfo playerInfo2, @NotNull Object obj, @Nullable String text) {
      this.playerInfo = playerInfo2;
      this.obj2 = obj;
      this.text2 = text;
   }

   public FabricPreLoginEvent(PlayerInfo playerInfo2, Object obj, String text, int value, DefaultConstructorMarker defaultConstructorMarker) {
      if ((value & 4) != 0) {
         text = null;
      }

      this(playerInfo2, obj, text);
   }

   public final PlayerInfo getPlayerInfo() {
      return this.playerInfo;
   }

   public final String getText2() {
      return this.text2;
   }

   public final void setText2(@Nullable String text) {
      this.text2 = text;
   }

   @Override
   public String toString() {
      return "FabricPreLoginEvent(data=" + this.playerInfo + ", login=" + this.obj2 + ", cancelReason=" + this.text2 + ')';
   }

   @Override
   public int hashCode() {
      int value = this.playerInfo.hashCode();
      int value2 = value * 31 + this.obj2.hashCode();
      return value2 * 31 + (this.text2 == null ? 0 : this.text2.hashCode());
   }

   @Override
   public boolean equals(@Nullable Object obj) {
      if (this == obj) {
         return true;
      } else if (!(obj instanceof FabricPreLoginEvent)) {
         return false;
      } else {
         FabricPreLoginEvent fabricPreLoginEvent = (FabricPreLoginEvent)obj;
         if (!Intrinsics.areEqual(this.playerInfo, fabricPreLoginEvent.playerInfo)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.obj2, fabricPreLoginEvent.obj2) ? false : Intrinsics.areEqual(this.text2, fabricPreLoginEvent.text2);
         }
      }
   }
}
