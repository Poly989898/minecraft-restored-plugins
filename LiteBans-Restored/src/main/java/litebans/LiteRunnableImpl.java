package litebans;

public final class LiteRunnableImpl implements Runnable {
   final CommandTask commandTask;
   final LiteAInterface liteAInterface;

   public LiteRunnableImpl(CommandTask commandTask, LiteAInterface liteAInterface) {
      this.commandTask = commandTask;
      this.liteAInterface = liteAInterface;
   }

   @Override
   public final void run() {
      SyncModule module = (SyncModule)this.commandTask.getPlatform().getModule(SyncModule.class);
      module.removeListener(this.liteAInterface);
   }
}
