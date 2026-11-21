package litebans;

import org.jetbrains.annotations.NotNull;

public abstract class CommandTask implements Runnable {
   public static final CommandOutput COMMAND_OUTPUT = new CommandOutput(null);
   private final Platform platform;
   private static final ThreadLocal senderHolder = new ThreadLocal();

   public CommandTask(@NotNull Platform platform) {
      this.platform = platform;
   }

   public final Platform getPlatform() {
      return this.platform;
   }

   public final ConfigModule getConfigModule() {
      return (ConfigModule)this.getPlatform().getModule(ConfigModule.class);
   }

   public final PunishmentLookupModule getLookupModule() {
      return (PunishmentLookupModule)this.getPlatform().getModule(PunishmentLookupModule.class);
   }

   public final BroadcastModule getBroadcastModule() {
      return (BroadcastModule)this.getPlatform().getModule(BroadcastModule.class);
   }

   public final DatabaseModule getDatabaseModule() {
      return (DatabaseModule)this.getPlatform().getModule(DatabaseModule.class);
   }

   public final LiteSender getSender() {
      Object obj = senderHolder.get();
      Intrinsics.checkNotNull(obj);
      return (LiteSender)obj;
   }

   @Override
   public void run() {
      StandardKt.notImplemented();
      throw new CommandAbortException();
   }

   public void perform2() {
   }

   public void perform3() {
   }

   public static final void handleLiteSender(@NotNull LiteSender liteSender, @NotNull CharSequence message, @NotNull Platform platform) {
      COMMAND_OUTPUT.send(liteSender, message, platform);
   }

   public static final ThreadLocal getSenderHolder() {
      return senderHolder;
   }
}
