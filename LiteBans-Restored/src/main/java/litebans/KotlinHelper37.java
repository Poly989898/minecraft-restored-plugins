package litebans;

import java.io.Serializable;
import kotlin.jvm.internal.Reflection;

public abstract class KotlinHelper37 implements KotlinAInterface, Serializable {
   private transient KotlinAInterface kotlinAInterface2;
   protected final Object obj3;
   private final Class class3;
   private final String text4;
   private final String text5;
   private final boolean flag2;
   public static final Object OBJ = KotlinSerializableImpl2.obtainKotlinSerializableImpl();

   public KotlinHelper37() {
      this(OBJ);
   }

   protected KotlinHelper37(Object obj) {
      this(obj, null, null, null, false);
   }

   protected KotlinHelper37(Object obj, Class class2, String text, String text2, boolean flag) {
      this.obj3 = obj;
      this.class3 = class2;
      this.text4 = text;
      this.text5 = text2;
      this.flag2 = flag;
   }

   protected abstract KotlinAInterface obtainKotlinAInterface();

   public Object obtainObj() {
      return this.obj3;
   }

   public KotlinAInterface obtainKotlinAInterface2() {
      KotlinAInterface kotlinAInterface = this.kotlinAInterface2;
      if (kotlinAInterface == null) {
         kotlinAInterface = this.obtainKotlinAInterface();
         this.kotlinAInterface2 = kotlinAInterface;
      }

      return kotlinAInterface;
   }

   public KotlinMarkerInterface obtainKotlinMarkerInterface() {
      return (KotlinMarkerInterface)(this.class3 == null ? null : (this.flag2 ? Reflection.getOrCreateKotlinPackage(this.class3) : Reflection.getOrCreateKotlinClass(this.class3)));
   }

   @Override
   public String obtainText() {
      return this.text4;
   }

   public String obtainText2() {
      return this.text5;
   }
}
