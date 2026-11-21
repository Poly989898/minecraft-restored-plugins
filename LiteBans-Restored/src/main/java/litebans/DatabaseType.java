package litebans;

import java.util.Locale;
import org.jetbrains.annotations.NotNull;

public final class DatabaseType {
   private DatabaseType() {
   }

   public final String obtainText() {
      return DatabaseModule.obtainText();
   }

   public final String obtainText2() {
      return DatabaseModule.obtainText2();
   }

   public final String obtainText3() {
      return DatabaseModule.obtainText3();
   }

   public final boolean checkText(@NotNull String text) {
      String text2 = text;
      return Intrinsics.areEqual(text2, "mysql") || Intrinsics.areEqual(text2, "mariadb");
   }

   public final boolean checkText2(@NotNull String text) {
      String text2 = text;
      return !Intrinsics.areEqual(text2, "sqlite") && !Intrinsics.areEqual(text2, "h2");
   }

   public final String computeText(@NotNull String text) {
      String text2 = text;
      switch (text2.toLowerCase(Locale.ENGLISH)) {
         case "sqlite":
            return "SQLite";
         case "h2":
            return "H2";
         case "mysql":
            return "MySQL";
         case "pgsql":
            return "PostgreSQL";
         case "mariadb":
            return "MariaDB";
      }

      return text;
   }

   public DatabaseType(DefaultConstructorMarker defaultConstructorMarker) {
      this();
   }
}
