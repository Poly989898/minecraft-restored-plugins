package litebans;

final class KotlinFunction1Impl8 implements Function1 {
   final SchemaBuilder schemaBuilder2;

   KotlinFunction1Impl8(SchemaBuilder schemaBuilder) {
      this.schemaBuilder2 = schemaBuilder;
   }

   public final String computeText(PunishmentTable punishmentTable) {
      String constructor = SqlText.constructorImpl("ALTER TABLE " + punishmentTable);
      SchemaBuilder schemaBuilder = this.schemaBuilder2;
      String text = "accept";
      String text2 = " DEFAULT 0";
      String text3 = schemaBuilder.getColumnDefinition().getText5();
      return SqlText.addColumn(
         constructor,
         !Intrinsics.areEqual(text3, "pgsql") && !Intrinsics.areEqual(text3, "h2") ? text + " TINYINT" + schemaBuilder.getColumnDefinition().getText8() + " NOT NULL" + text2 : text + " SMALLINT NOT NULL" + text2
      );
   }

   @Override
   public Object computeObj(Object obj) {
      return SqlText.box(this.computeText((PunishmentTable)obj));
   }
}
