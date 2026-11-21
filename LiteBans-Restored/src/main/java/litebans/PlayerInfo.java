package litebans;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class PlayerInfo {
   private final String name;
   private final String uuid;
   private final String ip;

   public PlayerInfo(@NotNull String text, @NotNull String text2, @Nullable String text3) {
      this.name = text;
      this.uuid = text2;
      this.ip = text3;
   }

   public final String getName() {
      return this.name;
   }

   public final String getUuid() {
      return this.uuid;
   }

   public final String getIp() {
      return this.ip;
   }

   public final String component1() {
      return this.name;
   }

   public final String component2() {
      return this.uuid;
   }

   public final String component3() {
      return this.ip;
   }

   @Override
   public String toString() {
      return "(a=" + this.name + ", uuid=" + this.uuid + ", ip=" + this.ip + ')';
   }

   @Override
   public int hashCode() {
      int value = this.name.hashCode();
      int value2 = value * 31 + this.uuid.hashCode();
      return value2 * 31 + (this.ip == null ? 0 : this.ip.hashCode());
   }

   @Override
   public boolean equals(@Nullable Object obj) {
      if (this == obj) {
         return true;
      } else if (!(obj instanceof PlayerInfo)) {
         return false;
      } else {
         PlayerInfo playerInfo = (PlayerInfo)obj;
         if (!Intrinsics.areEqual(this.name, playerInfo.name)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.uuid, playerInfo.uuid) ? false : Intrinsics.areEqual(this.ip, playerInfo.ip);
         }
      }
   }
}
