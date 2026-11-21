package litebans;

import java.util.Date;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class LoginRecord {
   private final String name;
   private final String uuid;
   private final String ip;
   private final Date date;

   public LoginRecord(@Nullable String text, @Nullable String text2, @Nullable String text3, @NotNull Date date2) {
      this.name = text;
      this.uuid = text2;
      this.ip = text3;
      this.date = date2;
   }

   public LoginRecord(String text, String text2, String text3, Date date2, int value, DefaultConstructorMarker defaultConstructorMarker) {
      if ((value & 4) != 0) {
         text3 = "#";
      }

      if ((value & 8) != 0) {
         date2 = new Date();
      }

      this(text, text2, text3, date2);
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

   public final Date getDate() {
      return this.date;
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

   public final Date component4() {
      return this.date;
   }

   @Override
   public String toString() {
      return "(a=" + this.name + ", uuid=" + this.uuid + ", ip=" + this.ip + ", date=" + this.date + ')';
   }

   @Override
   public int hashCode() {
      int value = this.name == null ? 0 : this.name.hashCode();
      int value2 = value * 31 + (this.uuid == null ? 0 : this.uuid.hashCode());
      int value3 = value2 * 31 + (this.ip == null ? 0 : this.ip.hashCode());
      return value3 * 31 + this.date.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object obj) {
      if (this == obj) {
         return true;
      } else if (!(obj instanceof LoginRecord)) {
         return false;
      } else {
         LoginRecord loginRecord = (LoginRecord)obj;
         if (!Intrinsics.areEqual(this.name, loginRecord.name)) {
            return false;
         } else if (!Intrinsics.areEqual(this.uuid, loginRecord.uuid)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.ip, loginRecord.ip) ? false : Intrinsics.areEqual(this.date, loginRecord.date);
         }
      }
   }
}
