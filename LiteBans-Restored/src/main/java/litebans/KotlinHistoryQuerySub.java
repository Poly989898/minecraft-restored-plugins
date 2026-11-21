package litebans;

public final class KotlinHistoryQuerySub extends HistoryQuery {
   final KotlinSerializableImpl kotlinSerializable;

   KotlinHistoryQuerySub(DatabaseSessionImpl databaseSession, boolean flag, int value, KotlinSerializableImpl kotlinSerializable, String text, ExecutorSource executorSource, ServerScopeClause serverScopeClause) {
      super(databaseSession, text, executorSource, flag, serverScopeClause, "time", false, value, null, 320, null);
      this.kotlinSerializable = kotlinSerializable;
   }

   @Override
   public void perform() {
      this.handleFunction(DatabaseSessionImpl.DATABASE_SESSION_IMPL_COMPANION.computeFunction2(this.obtainFunction(), (Function1)this.kotlinSerializable.obj));
   }
}
