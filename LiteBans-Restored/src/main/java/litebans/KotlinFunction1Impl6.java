package litebans;

import java.util.Locale;

public final class KotlinFunction1Impl6 implements Function1 {
   public static final KotlinFunction1Impl6 KOTLIN_FUNCTION1_IMPL = new KotlinFunction1Impl6();

   public final CharSequence computeMessage(Locale locale) {
      return locale.getLanguage();
   }

   @Override
   public Object computeObj(Object obj) {
      return this.computeMessage((Locale)obj);
   }
}
