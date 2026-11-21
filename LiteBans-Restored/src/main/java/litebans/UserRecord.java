package litebans;

import java.util.Objects;
import org.jetbrains.annotations.Nullable;

public final class UserRecord {
   private final int id;
   private String name;
   private String uuid;

   private UserRecord(int value, String text, String text2) {
      this.id = value;
      this.name = text;
      this.uuid = text2;
   }

   public final int getId() {
      return this.id;
   }

   public final String getName() {
      return this.name;
   }

   public final void setName(@Nullable String text) {
      this.name = text;
   }

   public final String getUuid() {
      return this.uuid;
   }

   public final void setUuid(@Nullable String text) {
      this.uuid = text;
   }

   @Override
   public String toString() {
      return Objects.toString(this.name);
   }

   public final int getId2() {
      return this.id;
   }

   public final boolean obtainFlag() {
      return this.name != null && this.uuid != null && Integer.compareUnsigned(this.id, 65535) < 0;
   }

   public final boolean obtainFlag2() {
      return !this.obtainFlag();
   }

   @Override
   public int hashCode() {
      int hashCode2 = UInt.hashCodeImpl(this.id);
      int value = hashCode2 * 31 + (this.name == null ? 0 : this.name.hashCode());
      return value * 31 + (this.uuid == null ? 0 : this.uuid.hashCode());
   }

   @Override
   public boolean equals(@Nullable Object obj) {
      if (this == obj) {
         return true;
      } else if (!(obj instanceof UserRecord)) {
         return false;
      } else {
         UserRecord userRecord = (UserRecord)obj;
         if (this.id != userRecord.id) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.name, userRecord.name) ? false : Intrinsics.areEqual(this.uuid, userRecord.uuid);
         }
      }
   }

   public UserRecord(int value, String text, String text2, DefaultConstructorMarker defaultConstructorMarker) {
      this(value, text, text2);
   }
}
