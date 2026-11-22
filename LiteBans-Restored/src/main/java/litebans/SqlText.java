package litebans;

import org.jetbrains.annotations.NotNull;

public final class SqlText implements CharSequence {
   private final String sql;

   public static int lengthImpl(String text) {
      return text.length();
   }

   public int obtainIntValue() {
      return lengthImpl(this.sql);
   }

   public static char charAtImpl(String text, int value) {
      return text.charAt(value);
   }

   public char computeCharValue(int value) {
      return charAtImpl(this.sql, value);
   }

   public static CharSequence subSequenceImpl(String text, int value, int value2) {
      return text.subSequence(value, value2);
   }

   @Override
   public CharSequence subSequence(int value, int value2) {
      return subSequenceImpl(this.sql, value, value2);
   }

   public static String expandImpl(String text) {
      String text2 = text;
      if (StringsKt.containsWithDefaults(text2, '{', false, 2, null)) {
         ConfigModule module = (ConfigModule)PlatformHolder.PLATFORM_STATE.obtainPlatform().getModule(ConfigModule.class);
         BroadcastModule module2 = (BroadcastModule)PlatformHolder.PLATFORM_STATE.obtainPlatform().getModule(BroadcastModule.class);
         String sqlDriver = module.getSettings().getSqlDriver();
         String text3 = "1";
         String text4 = "0";
         if (Intrinsics.areEqual(sqlDriver, "pgsql")) {
            text3 = "B'1'";
            text4 = "B'0'";
         }

         String text5 = CacheModule.CACHE_MODULE_COMPANION.computeText(text2, "{true}", text3);
         String text6 = CacheModule.CACHE_MODULE_COMPANION.computeText(text5, "{false}", text4);
         String text7 = CacheModule.CACHE_MODULE_COMPANION.computeText(text6, "{column.id}", !Intrinsics.areEqual(sqlDriver, "sqlite") ? "id" : "rowid as id");
         text2 = CacheModule.CACHE_MODULE_COMPANION.computeText(text7, "{=?}", "" + module2.getSqlLikeOperator() + '?' + module2.getCollation());
      }

      return text2;
   }

   @Override
   public String toString() {
      return expandImpl(this.sql);
   }

   public static final String and(String text, @NotNull CharSequence message) {
      CharSequence[] messages = new CharSequence[]{box(text), message};
      int value = 0;

      for (int i = messages.length; value < i; value++) {
         CharSequence message2 = messages[value];
         if (message2.length() <= 0) {
            String text2 = "Failed requirement.";
            throw new IllegalArgumentException(text2.toString());
         }
      }

      return constructorImpl(expandImpl(text) + " AND " + message);
   }

   public static final String computeText(String text, @NotNull CharSequence message) {
      CharSequence[] messages = new CharSequence[]{box(text), message};
      int value = 0;

      for (int i = messages.length; value < i; value++) {
         CharSequence message2 = messages[value];
         if (message2.length() <= 0) {
            String text2 = "Failed requirement.";
            throw new IllegalArgumentException(text2.toString());
         }
      }

      return constructorImpl(expandImpl(text) + " OR " + message);
   }

   public static final String whereIsNull(String text, @NotNull CharSequence message) {
      CharSequence[] messages = new CharSequence[]{box(text), message};
      int value = 0;

      for (int i = messages.length; value < i; value++) {
         CharSequence message2 = messages[value];
         if (message2.length() <= 0) {
            String text2 = "Failed requirement.";
            throw new IllegalArgumentException(text2.toString());
         }
      }

      SqlValidation.handleMessage(message);
      return constructorImpl(expandImpl(text) + " WHERE " + message + " IS NULL");
   }

   public static final String set(String text, @NotNull CharSequence message) {
      CharSequence[] messages = new CharSequence[]{box(text), message};
      int value = 0;

      for (int i = messages.length; value < i; value++) {
         CharSequence message2 = messages[value];
         if (message2.length() <= 0) {
            String text2 = "Failed requirement.";
            throw new IllegalArgumentException(text2.toString());
         }
      }

      SqlValidation.handleMessage3(message);
      return constructorImpl(expandImpl(text) + " SET " + message);
   }

   public static final String convertTo(String text, @NotNull CharSequence message) {
      CharSequence[] messages = new CharSequence[]{box(text), message};
      int value = 0;

      for (int i = messages.length; value < i; value++) {
         CharSequence message2 = messages[value];
         if (message2.length() <= 0) {
            String text2 = "Failed requirement.";
            throw new IllegalArgumentException(text2.toString());
         }
      }

      return constructorImpl(expandImpl(text) + " CONVERT TO " + message);
   }

   public static final String addColumn(String text, @NotNull CharSequence message) {
      CharSequence[] messages = new CharSequence[]{box(text), message};
      int value = 0;

      for (int i = messages.length; value < i; value++) {
         CharSequence message2 = messages[value];
         if (message2.length() <= 0) {
            String text2 = "Failed requirement.";
            throw new IllegalArgumentException(text2.toString());
         }
      }

      SqlValidation.handleMessage2(message);
      return constructorImpl(expandImpl(text) + " ADD COLUMN " + message);
   }

   public static final String dropColumn(String text, @NotNull CharSequence message) {
      CharSequence[] messages = new CharSequence[]{box(text), message};
      int value = 0;

      for (int i = messages.length; value < i; value++) {
         CharSequence message2 = messages[value];
         if (message2.length() <= 0) {
            String text2 = "Failed requirement.";
            throw new IllegalArgumentException(text2.toString());
         }
      }

      SqlValidation.handleMessage(message);
      return constructorImpl(expandImpl(text) + " DROP COLUMN " + message);
   }

   public static final String orderByDesc(String text, @NotNull CharSequence message) {
      CharSequence[] messages = new CharSequence[]{box(text), message};
      int value = 0;

      for (int i = messages.length; value < i; value++) {
         CharSequence message2 = messages[value];
         if (message2.length() <= 0) {
            String text2 = "Failed requirement.";
            throw new IllegalArgumentException(text2.toString());
         }
      }

      return constructorImpl(expandImpl(text) + " ORDER BY " + message + " DESC");
   }

   public static final String limit(String text, @NotNull Number number) {
      CharSequence[] messages = new CharSequence[]{box(text)};
      int value = 0;

      for (int i = messages.length; value < i; value++) {
         CharSequence message = messages[value];
         if (message.length() <= 0) {
            String text2 = "Failed requirement.";
            throw new IllegalArgumentException(text2.toString());
         }
      }

      return constructorImpl(expandImpl(text) + " LIMIT " + number);
   }

   public static int hashCodeImpl(String text) {
      return text.hashCode();
   }

   @Override
   public int hashCode() {
      return hashCodeImpl(this.sql);
   }

   public static boolean equalsImpl(String text, Object obj) {
      return !(obj instanceof SqlText) ? false : Intrinsics.areEqual(text, ((SqlText)obj).unbox());
   }

   @Override
   public boolean equals(Object obj) {
      return equalsImpl(this.sql, obj);
   }

   private SqlText(String text) {
      this.sql = text;
   }

   public static String constructorImpl(@NotNull String text) {
      return text;
   }

   public static final SqlText box(String text) {
      return new SqlText(text);
   }

   public final String unbox() {
      return this.sql;
   }

   @Override
   public int length() {
      return this.obtainIntValue();
   }

   @Override
   public final char charAt(int value) {
      return this.computeCharValue(value);
   }
}
