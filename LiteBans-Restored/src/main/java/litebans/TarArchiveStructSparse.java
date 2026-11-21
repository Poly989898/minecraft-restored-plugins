package litebans;

import java.util.Objects;

public final class TarArchiveStructSparse {
   private final long longValue;
   private final long longValue2;

   public TarArchiveStructSparse(long value, long value2) {
      this.longValue = value;
      this.longValue2 = value2;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj != null && this.getClass() == obj.getClass()) {
         TarArchiveStructSparse tarArchiveStructSparse = (TarArchiveStructSparse)obj;
         return this.longValue == tarArchiveStructSparse.longValue && this.longValue2 == tarArchiveStructSparse.longValue2;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.longValue, this.longValue2);
   }

   @Override
   public String toString() {
      return "TarArchiveStructSparse{offset=" + this.longValue + ", numbytes=" + this.longValue2 + '}';
   }

   public long getLongValue() {
      return this.longValue;
   }

   public long getLongValue2() {
      return this.longValue2;
   }
}
