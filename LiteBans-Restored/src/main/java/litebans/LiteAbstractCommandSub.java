package litebans;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@CommandMarker
public final class LiteAbstractCommandSub extends AbstractCommand {
   private final String commandName;
   private final BrigadierCommand brigadierCommand;
   private final String permission;
   private final LiteCommandManager liteCommandManager;

   public LiteAbstractCommandSub(@NotNull String text, @NotNull String text2, @Nullable BrigadierCommand brigadierCommand, @Nullable String text3, @NotNull LiteCommandManager liteCommandManager) {
      super(text, liteCommandManager.platform);
      this.commandName = text2;
      this.brigadierCommand = brigadierCommand;
      this.permission = text3;
      this.liteCommandManager = liteCommandManager;
   }

   @Override
   public String getPermission() {
      return this.permission;
   }

   @Override
   public void execute(@NotNull LiteSender liteSender, @NotNull String[] texts) {
      this.liteCommandManager.handleLiteSender(liteSender, this.commandName, texts);
   }

   @Override
   public void handleTexts(@NotNull String[] texts, @NotNull Function0 supplier) {
      super.handleTexts(texts, () -> Message.computeMessage2(this.commandName + "_USAGE"));
   }

}
