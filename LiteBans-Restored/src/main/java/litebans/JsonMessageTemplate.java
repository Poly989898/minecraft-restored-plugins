package litebans;

import org.jetbrains.annotations.NotNull;

public final class JsonMessageTemplate {
   private final String text5;
   private final String text6;
   private final String text7;
   private final String text8;
   private final boolean flag3;
   private final boolean flag4;

   public JsonMessageTemplate(@NotNull String text, @NotNull String text2, @NotNull String text3, @NotNull String text4, boolean flag, boolean flag2) {
      this.text5 = text;
      this.text6 = text2;
      this.text7 = text3;
      this.text8 = text4;
      this.flag3 = flag;
      this.flag4 = flag2;
   }

   public JsonMessageTemplate(String text, String text2, String text3, String text4, boolean flag, boolean flag2, int value, DefaultConstructorMarker defaultConstructorMarker) {
      if ((value & 8) != 0) {
         text4 = "";
      }

      if ((value & 16) != 0) {
         flag = false;
      }

      if ((value & 32) != 0) {
         flag2 = false;
      }

      this(text, text2, text3, text4, flag, flag2);
   }

   public final String getText5() {
      return this.text5;
   }

   public final String getText6() {
      return this.text6;
   }

   public final String getText7() {
      return this.text7;
   }

   public final String getText8() {
      return this.text8;
   }

   public final boolean isFlag3() {
      return this.flag3;
   }

   public final boolean isFlag4() {
      return this.flag4;
   }
}
