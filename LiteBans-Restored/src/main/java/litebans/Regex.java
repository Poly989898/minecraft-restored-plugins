package litebans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;

public final class Regex implements Serializable {
   public static final RegexCompanion REGEX_COMPANION = new RegexCompanion(null);
   private final Pattern pattern;

   public Regex(@NotNull Pattern pattern) {
      Intrinsics.checkParameterIsNotNull(pattern, "nativePattern");
      super();
      this.pattern = pattern;
   }

   public Regex(@NotNull String text) {
      Intrinsics.checkParameterIsNotNull(text, "pattern");
      this(Pattern.compile(text));
   }

   public final boolean matches(@NotNull CharSequence message) {
      Intrinsics.checkParameterIsNotNull(message, "input");
      return this.pattern.matcher(message).matches();
   }

   public final String replaceAll(@NotNull CharSequence message, @NotNull String text) {
      Intrinsics.checkParameterIsNotNull(message, "input");
      Intrinsics.checkParameterIsNotNull(text, "replacement");
      return this.pattern.matcher(message).replaceAll(text);
   }

   public final String replaceFirst(@NotNull CharSequence message, @NotNull String text) {
      Intrinsics.checkParameterIsNotNull(message, "input");
      Intrinsics.checkParameterIsNotNull(text, "replacement");
      return this.pattern.matcher(message).replaceFirst(text);
   }

   public final List split(@NotNull CharSequence message, int value) {
      Intrinsics.checkParameterIsNotNull(message, "input");
      StringsKt.requireNonNegativeLimit(value);
      Matcher matcher2 = this.pattern.matcher(message);
      if (value != 1 && matcher2.find()) {
         ArrayList arrayList = new ArrayList(value > 0 ? KotlinRangesKtSub.coerceAtMost(value, 10) : 10);
         int value2 = 0;
         int value3 = value - 1;

         do {
            arrayList.add(message.subSequence(value2, matcher2.start()).toString());
            value2 = matcher2.end();
         } while ((value3 < 0 || arrayList.size() != value3) && matcher2.find());

         arrayList.add(message.subSequence(value2, message.length()).toString());
         return arrayList;
      } else {
         return CollectionsKt.computeList(message.toString());
      }
   }

   @Override
   public String toString() {
      return this.pattern.toString();
   }

   public final Pattern getPattern() {
      return this.pattern;
   }

   private final Object writeReplace() {
      return new KotlinSerializableImpl3(this.pattern.pattern(), this.pattern.flags());
   }
}
