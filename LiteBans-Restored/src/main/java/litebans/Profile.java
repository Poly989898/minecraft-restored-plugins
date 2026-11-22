package litebans;

import java.util.UUID;
import org.jetbrains.annotations.Nullable;

public final class Profile {
   private final String name;
   private final UUID uUID;

   public Profile(@Nullable String text, @Nullable UUID uuid) {
      this.name = text;
      this.uUID = uuid;
   }

   public final String getName() {
      return this.name;
   }

   public final UUID getUUID() {
      return this.uUID;
   }

   public final boolean obtainFlag() {
      return Intrinsics.areEqual(this, KotlinUtil11.obtainProfile());
   }

   @Override
   public String toString() {
      return "Profile(name=" + this.name + ", uuid=" + this.uUID + ')';
   }

   @Override
   public int hashCode() {
      int value = this.name == null ? 0 : this.name.hashCode();
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
      return !Intrinsics.areEqual(this.name, profile.name) ? false : Intrinsics.areEqual(this.uUID, profile.uUID);
   }
}
