package litebans;

public abstract class LiteHelper4 {
   @Override
   public String toString() {
      return "Content()";
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      }

      if (!(obj instanceof LiteHelper4)) {
         return false;
      }

      LiteHelper4 liteHelper4 = (LiteHelper4)obj;
      return liteHelper4.checkObj(this);
   }

   protected boolean checkObj(Object obj) {
      return obj instanceof LiteHelper4;
   }

   @Override
   public int hashCode() {
      return 1;
   }
}
