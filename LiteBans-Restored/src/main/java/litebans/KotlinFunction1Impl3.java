package litebans;

final class KotlinFunction1Impl3 implements Function1 {
   public static final KotlinFunction1Impl3 KOTLIN_FUNCTION1_IMPL_3 = new KotlinFunction1Impl3();

   public final String computeText4(PunishmentTable punishmentTable) {
      return SqlText.addColumn(SqlText.constructorImpl("ALTER TABLE " + punishmentTable), "removed_by_uuid VARCHAR(36)");
   }

   @Override
   public Object computeObj(Object obj) {
      return SqlText.box(this.computeText4((PunishmentTable)obj));
   }
}
