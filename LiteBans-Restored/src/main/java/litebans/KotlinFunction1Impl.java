package litebans;

final class KotlinFunction1Impl implements Function1 {
   final SchemaBuilder schemaBuilder4;

   KotlinFunction1Impl(SchemaBuilder schemaBuilder) {
      this.schemaBuilder4 = schemaBuilder;
   }

   public final String computeText7(PunishmentTable punishmentTable) {
      String constructor = SqlText.constructorImpl("ALTER TABLE " + punishmentTable);
      SchemaBuilder schemaBuilder = this.schemaBuilder4;
      String text = "server_origin";
      byte b = 32;
      String text2 = "";
      String text3 = schemaBuilder.getColumnDefinition().getText7();
      return SqlText.addColumn(constructor, DatabaseModule.DATABASE_TYPE.checkText(schemaBuilder.getColumnDefinition().getText5()) ? text + " VARCHAR(" + b + ')' + text3 + text2 : text + " VARCHAR(" + b + ')' + text2);
   }

   @Override
   public Object computeObj(Object obj) {
      return SqlText.box(this.computeText7((PunishmentTable)obj));
   }
}
