package litebans;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@CommandMarker
public final class VelocityAltsCommand extends AbstractCommand {
   private final String permission;
   private final LiteCommandManager liteCommandManager;

   public VelocityAltsCommand(@NotNull String text, @Nullable String text2, @NotNull LiteCommandManager liteCommandManager) {
      super(text, liteCommandManager.platform);
      this.permission = text2;
      this.liteCommandManager = liteCommandManager;
   }

   @Override
   public String getPermission() {
      return this.permission;
   }

   @Override
   public void execute(@NotNull LiteSender liteSender, @NotNull String[] texts) {
      this.liteCommandManager.handleLiteSender(liteSender, this.getName(), texts);
   }
}
