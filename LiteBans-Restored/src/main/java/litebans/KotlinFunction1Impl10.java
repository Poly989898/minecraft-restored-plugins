package litebans;

final class KotlinFunction1Impl10 implements Function1 {
   public static final KotlinFunction1Impl10 KOTLIN_FUNCTION1_IMPL_5 = new KotlinFunction1Impl10();

   public final String computeText6(PunishmentTable punishmentTable) {
      return SqlText.addColumn(SqlText.constructorImpl("ALTER TABLE " + punishmentTable), "removed_by_name VARCHAR(128)");
   }

   @Override
   public Object computeObj(Object obj) {
      return SqlText.box(this.computeText6((PunishmentTable)obj));
   }
}
