package litebans;

import java.util.UUID;
import org.jetbrains.annotations.Nullable;

public final class Profile {
   private final String text2;
   private final UUID uUID;

   public Profile(@Nullable String text, @Nullable UUID uuid) {
      this.text2 = text;
      this.uUID = uuid;
   }

   public final String getText2() {
      return this.text2;
   }

   public final UUID getUUID() {
      return this.uUID;
   }

   public final boolean obtainFlag() {
      return Intrinsics.areEqual(this, KotlinUtil11.obtainProfile());
   }

   @Override
   public String toString() {
      return "Profile(name=" + this.text2 + ", uuid=" + this.uUID + ')';
   }

   @Override
   public int hashCode() {
      int value = this.text2 == null ? 0 : this.text2.hashCode();
      return value * 31 + (this.uUID == null ? 0 : this.uUID.hashCode());
   }

   @Override
   public boolean equals(@Nullable Object obj) {
      if (this == obj) {
         return true;
      }

      if (!(obj instanceof Profile)) {
         return false;
      }

      Profile profile = (Profile)obj;
      return !Intrinsics.areEqual(this.text2, profile.text2) ? false : Intrinsics.areEqual(this.uUID, profile.uUID);
   }
}
