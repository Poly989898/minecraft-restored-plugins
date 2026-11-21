package litebans;

import org.jetbrains.annotations.NotNull;

public final class SqlTypes {
   private SqlTypes() {
   }

   public final ColumnDefinition computeColumnDefinition(@NotNull String text, @NotNull String text2) {
      return DatabaseModule.DATABASE_TYPE.checkText(text)
         ? new ColumnDefinition(text, StringsKt.isBlank(text2) ? "" : "ENGINE=" + text2, " CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci", " UNSIGNED")
         : new ColumnDefinition(text, "", "", " ");
   }

   public SqlTypes(DefaultConstructorMarker defaultConstructorMarker) {
      this();
   }
}
