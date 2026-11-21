package litebans;

import org.jetbrains.annotations.NotNull;

@PluginMarker
public abstract class AbstractCommand extends CommandTask implements LiteCommand {
   public static final MutedCommandFilter MUTED_COMMAND_FILTER = new MutedCommandFilter(null);
   private final String name;
   private final boolean flag45;
   private final boolean flag46;

   public AbstractCommand(@NotNull String text, @NotNull Platform platform) {
      super(platform);
      this.name = text;
      this.flag45 = true;
      this.flag46 = true;
   }

   public boolean obtainFlag7() {
      return this.flag45;
   }

   public boolean obtainFlag() {
      return this.flag46;
   }

   @Override
   public String getName() {
      return this.name;
   }

   @Override
   public String[] getAliases() {
      return new String[0];
   }

   @Override
   public String getPermission() {
      return "litebans." + this.getName();
   }

   public final void handleLiteSender(@NotNull LiteSender liteSender, @NotNull String[] texts, @NotNull String text) {
      CommandOutput commandOutput = CommandTask.COMMAND_OUTPUT;
      LiteSender liteSender2 = liteSender;

      try {
         commandOutput.getSenderHolder().set(liteSender2);
         ThrottleModule module = (ThrottleModule)this.getPlatform().getModule(ThrottleModule.class);
         boolean flag = false;

         try {
            if (this.obtainFlag()) {
               handleAbstractCommand(this, texts, null, 2, null);
            }

            if (liteSender.isPlayer() && module.checkByteValue((byte)3, liteSender.getUuidString())) {
               LiteSender liteSender3 = liteSender;
               String text2 = "litebans.cooldown.bypass";
               if (!liteSender3.hasPermission(text2)) {
                  flag = true;
               }
            }

            if (!flag) {
               this.execute(liteSender, texts);
            }
         } catch (ResultException e) {
            String replacePlaceholderLazy2 = Placeholders.replacePlaceholderLazy(Placeholders.replacePlaceholderLazy(String.valueOf(e.obtainObj()), "commandArgs", () -> ArraysKt.joinToStringWithDefaults(texts, " ", null, null, 0, null, null, 62, null)), "command", () -> '/' + text);
            AltAccountPunisher.sendMessage((CommandTask) this, (CharSequence) replacePlaceholderLazy2);
         } catch (Exception e2) {
            AltAccountPunisher.sendMessage(this, Message.INTERNAL_ERROR);
            CommandTask commandTask = this;
            ((DatabaseModule)commandTask.getPlatform().getModule(DatabaseModule.class)).logConnectionError(e2);
         } finally {
            if (liteSender.isPlayer() && !flag) {
               module.handleByteValue((byte)3, liteSender.getUuidString());
            }
         }
      } finally {
         commandOutput.getSenderHolder().remove();
      }
   }

   public void handleTexts(@NotNull String[] texts, @NotNull Function0 supplier) {
      CommandOutput commandOutput = CommandTask.COMMAND_OUTPUT;
      boolean flag = texts.length == 0;
      if (flag) {
         commandOutput.returnResult(supplier.obtainObj2());
         throw new CommandAbortException();
      }
   }

   public static void handleAbstractCommand(AbstractCommand abstractCommand, String[] texts, Function0 supplier, int value, Object obj) {
      if (obj != null) {
         throw new UnsupportedOperationException("");
      }

      if ((value & 2) != 0) {
         supplier = () -> Message.computeMessage2(abstractCommand.name + "_USAGE");
      }

      abstractCommand.handleTexts(texts, supplier);
   }

}
