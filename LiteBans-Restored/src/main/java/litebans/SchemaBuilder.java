package litebans;

import java.util.ArrayList;
import java.util.Arrays;
import org.jetbrains.annotations.NotNull;

public final class SchemaBuilder {
   private final ColumnDefinition columnDefinition;

   public SchemaBuilder(@NotNull ColumnDefinition columnDefinition2) {
      this.columnDefinition = columnDefinition2;
   }

   public final ColumnDefinition getColumnDefinition() {
      return this.columnDefinition;
   }

   public final String computeText(@NotNull PunishmentTable punishmentTable, @NotNull String[] texts) {
      PunishmentTable punishmentTable2 = punishmentTable;
      String[] copyOf2 = Arrays.copyOf(texts, texts.length);
      ColumnDefinition columnDefinition2 = this.getColumnDefinition();
      StringBuilder stringBuilder = new StringBuilder();
      char c = '(';
      var arrayList = new ArrayList();
      int value = 0;

      for (int i = copyOf2.length; value < i; value++) {
         String text = copyOf2[value];
         if (text.length() > 0) {
            arrayList.add(text);
         }
      }

      String joinToString = CollectionsKt.joinToStringWithDefaults(arrayList, ",", null, null, 0, null, null, 62, null);
      CharSequence string = stringBuilder.append(c + joinToString).append(')').append(columnDefinition2.getText6()).append(columnDefinition2.getText7()).toString();
      return SqlText.constructorImpl("CREATE TABLE IF NOT EXISTS " + punishmentTable2 + string);
   }
}
