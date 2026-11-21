package litebans;

import java.util.Map.Entry;

public final class KotlinFunction1Impl2 implements Function1 {
   final String text6;
   final String text7;

   public KotlinFunction1Impl2(String text, String text2) {
      this.text6 = text;
      this.text7 = text2;
   }

   public final Boolean computeBoolean(Entry entry) {
      return this.text6 != null && Intrinsics.areEqual(this.text6, ((Punishment)entry.getValue()).getUuid())
         || this.text7 != null && this.text7.length() > 0 && Intrinsics.areEqual(this.text7, ((Punishment)entry.getValue()).getIp());
   }

   @Override
   public Object computeObj(Object obj) {
      return this.computeBoolean((Entry)obj);
   }
}
