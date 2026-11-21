package litebans;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ColumnDefinition {
   public static final SqlTypes SQL_TYPES = new SqlTypes(null);
   private final String text5;
   private final String engine;
   private final String charset;
   private final String unsigned;
   public static final String TEXT = "utf8mb4_unicode_ci";
   public static final String TEXT_2 = " CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci";
   public static final String TEXT_3 = " CHARACTER SET ascii COLLATE ascii_general_ci";

   public ColumnDefinition(@NotNull String text, @NotNull String text2, @NotNull String text3, @NotNull String text4) {
      this.text5 = text;
      this.engine = text2;
      this.charset = text3;
      this.unsigned = text4;
   }

   public final String getText5() {
      return this.text5;
   }

   public final String getText6() {
      return this.engine;
   }

   public final String getText7() {
      return this.charset;
   }

   public final String getText8() {
      return this.unsigned;
   }

   @Override
   public String toString() {
      return "(a=" + this.text5 + ", engine=" + this.engine + ", charset=" + this.charset + ", unsigned=" + this.unsigned + ')';
   }

   @Override
   public int hashCode() {
      int value = this.text5.hashCode();
      int value2 = value * 31 + this.engine.hashCode();
      int value3 = value2 * 31 + this.charset.hashCode();
      return value3 * 31 + this.unsigned.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object obj) {
      if (this == obj) {
         return true;
      } else if (!(obj instanceof ColumnDefinition)) {
         return false;
      } else {
         ColumnDefinition columnDefinition = (ColumnDefinition)obj;
         if (!Intrinsics.areEqual(this.text5, columnDefinition.text5)) {
            return false;
         } else if (!Intrinsics.areEqual(this.engine, columnDefinition.engine)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.charset, columnDefinition.charset) ? false : Intrinsics.areEqual(this.unsigned, columnDefinition.unsigned);
         }
      }
   }
}
