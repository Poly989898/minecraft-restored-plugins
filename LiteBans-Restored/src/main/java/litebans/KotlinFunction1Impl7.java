package litebans;

final class KotlinFunction1Impl7 implements Function1 {
   final SchemaBuilder schemaBuilder3;

   KotlinFunction1Impl7(SchemaBuilder schemaBuilder) {
      this.schemaBuilder3 = schemaBuilder;
   }

   public final String computeText2(PunishmentTable punishmentTable) {
      String constructor = SqlText.constructorImpl("ALTER TABLE " + punishmentTable);
      SchemaBuilder schemaBuilder = this.schemaBuilder3;
      String text = "template";
      String text2 = " DEFAULT 255";
      String text3 = schemaBuilder.getColumnDefinition().getText5();
      return SqlText.addColumn(
         constructor,
         !Intrinsics.areEqual(text3, "pgsql") && !Intrinsics.areEqual(text3, "h2") ? text + " TINYINT" + schemaBuilder.getColumnDefinition().getText8() + " NOT NULL" + text2 : text + " SMALLINT NOT NULL" + text2
      );
   }

   @Override
   public Object computeObj(Object obj) {
      return SqlText.box(this.computeText2((PunishmentTable)obj));
   }
}
