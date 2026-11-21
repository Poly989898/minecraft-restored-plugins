package litebans;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class DriverDefinition {
   private final String name;
   private final String className;
   private final String url;
   private final String version;
   private final String checksum;

   public DriverDefinition(@NotNull String text, @NotNull String text2, @Nullable String text3, @Nullable String text4, @Nullable String text5) {
      this.name = text;
      this.className = text2;
      this.url = text3;
      this.version = text4;
      this.checksum = text5;
   }

   public final String getName() {
      return this.name;
   }

   public final String getClassName() {
      return this.className;
   }

   public final String getUrl() {
      return this.url;
   }

   public final String getChecksum() {
      return this.checksum;
   }

   @Override
   public String toString() {
      return this.name + ' ' + this.version;
   }

   @Override
   public int hashCode() {
      int value = this.name.hashCode();
      int value2 = value * 31 + this.className.hashCode();
      int value3 = value2 * 31 + (this.url == null ? 0 : this.url.hashCode());
      int value4 = value3 * 31 + (this.version == null ? 0 : this.version.hashCode());
      return value4 * 31 + (this.checksum == null ? 0 : this.checksum.hashCode());
   }

   @Override
   public boolean equals(@Nullable Object obj) {
      if (this == obj) {
         return true;
      } else if (!(obj instanceof DriverDefinition)) {
         return false;
      } else {
         DriverDefinition namedRecord = (DriverDefinition)obj;
         if (!Intrinsics.areEqual(this.name, namedRecord.name)) {
            return false;
         } else if (!Intrinsics.areEqual(this.className, namedRecord.className)) {
            return false;
         } else if (!Intrinsics.areEqual(this.url, namedRecord.url)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.version, namedRecord.version) ? false : Intrinsics.areEqual(this.checksum, namedRecord.checksum);
         }
      }
   }
}
