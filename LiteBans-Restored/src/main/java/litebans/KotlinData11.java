package litebans;

import kotlin.jvm.internal.Reflection;

public class KotlinData11 extends KotlinHelper37 implements KotlinKotlinFunctionSub2, KotlinMarkerInterface2 {
   private final int intValue;
   private final int intValue2;

   public KotlinData11(int value, Object obj, Class class2, String text, String text2, int value2) {
      super(obj, class2, text, text2, (value2 & 1) == 1);
      this.intValue = value;
      this.intValue2 = value2 >> 1;
   }

   @Override
   protected KotlinAInterface obtainKotlinAInterface() {
      return null;   // Kotlin reflection metadata (unused)
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      }

      if (!(obj instanceof KotlinData11)) {
         return obj instanceof KotlinMarkerInterface2 ? obj.equals(this.obtainKotlinAInterface2()) : false;
      }

      KotlinData11 kotlinData11 = (KotlinData11)obj;
      return this.obtainText().equals(kotlinData11.obtainText())
         && this.obtainText2().equals(kotlinData11.obtainText2())
         && this.intValue2 == kotlinData11.intValue2
         && this.intValue == kotlinData11.intValue
         && Intrinsics.areEqual(this.obtainObj(), kotlinData11.obtainObj())
         && Intrinsics.areEqual(this.obtainKotlinMarkerInterface(), kotlinData11.obtainKotlinMarkerInterface());
   }

   @Override
   public int hashCode() {
      return ((this.obtainKotlinMarkerInterface() == null ? 0 : this.obtainKotlinMarkerInterface().hashCode() * 31) + this.obtainText().hashCode()) * 31 + this.obtainText2().hashCode();
   }

   @Override
   public String toString() {
      KotlinAInterface kotlinAInterface = this.obtainKotlinAInterface2();
      if (kotlinAInterface != this) {
         return kotlinAInterface.toString();
      } else {
         return "<init>".equals(this.obtainText())
            ? "constructor (Kotlin reflection is not available)"
            : "function " + this.obtainText() + " (Kotlin reflection is not available)";
      }
   }
}
