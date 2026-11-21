package litebans;

final class NoopSuspendResumeLock extends SuspendResumeLock {
   NoopSuspendResumeLock(boolean flag) {
      super(flag, null);
   }

   @Override
   public void perform() {
   }

   @Override
   public void perform2() {
   }

   @Override
   public void perform3() {
   }

   @Override
   public void perform4() {
   }
}
