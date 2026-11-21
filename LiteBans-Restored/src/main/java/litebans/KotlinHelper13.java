package litebans;

import org.jetbrains.annotations.Nullable;

public final class KotlinHelper13 {
   private String text7;
   private String target;
   private String executor;
   private String text8;
   private String text9;
   private final LiteSender liteSender;
   private final String text10;

   public KotlinHelper13(
      @Nullable String text,
   @Nullable String text2,
   @Nullable String text3,
   @Nullable String text4,
   @Nullable String text5,
   @Nullable LiteSender liteSender2,
   @Nullable String text6
   ) {
      this.text7 = text;
      this.target = text2;
      this.executor = text3;
      this.text8 = text4;
      this.text9 = text5;
      this.liteSender = liteSender2;
      this.text10 = text6;
      this.text9 = this.text9 != null ? StringsKt.computeText(this.text9, '*', '%', false, 4, null) : null;
   }

   public KotlinHelper13(String text, String text2, String text3, String text4, String text5, LiteSender liteSender2, String text6, int value, DefaultConstructorMarker defaultConstructorMarker) {
      if ((value & 2) != 0) {
         text2 = text;
      }

      if ((value & 4) != 0) {
         text3 = text;
      }

      if ((value & 32) != 0) {
         liteSender2 = null;
      }

      if ((value & 64) != 0) {
         text6 = text;
      }

      this(text, text2, text3, text4, text5, liteSender2, text6);
   }

   public final String getTarget() {
      return this.target;
   }

   public final void setTarget(@Nullable String text) {
      this.target = text;
   }

   public final String getExecutor() {
      return this.executor;
   }

   public final void setExecutor(@Nullable String text) {
      this.executor = text;
   }

   public final String getText8() {
      return this.text8;
   }

   public final String getText9() {
      return this.text9;
   }

   public final LiteSender getLiteSender() {
      return this.liteSender;
   }

   public final String getText10() {
      return this.text10;
   }

   public final boolean obtainFlag() {
      boolean flag;
      if (this.text10 != null) {
         String text = this.text10;
         flag = TargetParser.TARGET_PARSER.checkText(text);
      } else {
         flag = false;
      }

      return flag;
   }
}
