package litebans;

final class KotlinFunction1Impl12 implements Function1 {
   public static final KotlinFunction1Impl12 KOTLIN_FUNCTION1_IMPL_4 = new KotlinFunction1Impl12();

   public final String computeText5(PunishmentTable punishmentTable) {
      return SqlText.addColumn(SqlText.constructorImpl("ALTER TABLE " + punishmentTable), "removed_by_date TIMESTAMP");
   }

   @Override
   public Object computeObj(Object obj) {
      return SqlText.box(this.computeText5((PunishmentTable)obj));
   }
}
