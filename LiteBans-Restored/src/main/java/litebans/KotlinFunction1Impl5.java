package litebans;

final class KotlinFunction1Impl5 implements Function1 {
   public static final KotlinFunction1Impl5 KOTLIN_FUNCTION1_IMPL_2 = new KotlinFunction1Impl5();

   public final String computeText3(PunishmentTable punishmentTable) {
      return SqlText.addColumn(SqlText.constructorImpl("ALTER TABLE " + punishmentTable), "removed_by_reason VARCHAR(2048)");
   }

   @Override
   public Object computeObj(Object obj) {
      return SqlText.box(this.computeText3((PunishmentTable)obj));
   }
}
