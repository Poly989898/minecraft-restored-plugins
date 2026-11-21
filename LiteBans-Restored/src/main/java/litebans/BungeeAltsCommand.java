package litebans;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@CommandMarker
public final class BungeeAltsCommand extends AbstractCommand {
   private final String permission;
   private final BungeeAltsCommands bungeeAltsCommands;

   public BungeeAltsCommand(@NotNull String text, @Nullable String text2, @NotNull BungeeAltsCommands bungeeAltsCommands) {
      super(text, bungeeAltsCommands.platform);
      this.permission = text2;
      this.bungeeAltsCommands = bungeeAltsCommands;
   }

   @Override
   public String getPermission() {
      return this.permission;
   }

   @Override
   public void execute(@NotNull LiteSender liteSender, @NotNull String[] texts) {
      this.bungeeAltsCommands.handleLiteSender(liteSender, this.getName(), texts);
   }
}
