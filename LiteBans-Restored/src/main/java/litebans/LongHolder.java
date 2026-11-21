package litebans;

import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class LongHolder extends TemplateInfo {
   private final Long long2;

   public LongHolder(
      @Nullable Long boxed, @Nullable String text, @Nullable String text2, @Nullable String text3, @Nullable String text4, @NotNull List list, @NotNull List list2
   ) {
      super(text, text2, text3, text4, list, list2);
      this.long2 = boxed;
   }

   public final Long getLong2() {
      return this.long2;
   }
}
