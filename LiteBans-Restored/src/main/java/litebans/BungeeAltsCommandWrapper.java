package litebans;

import java.util.Arrays;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class BungeeAltsCommandWrapper extends BungeeCommandWrapper {
   private final Platform platform;

   public BungeeAltsCommandWrapper(@NotNull LiteCommand liteCommand, @NotNull Platform platform, @Nullable String text, @NotNull String[] texts) {
      super(liteCommand, text, platform, Arrays.copyOf(texts, texts.length));
      this.platform = platform;
      PlatformBridge bridge = this.platform.createBridge();
      Intrinsics.checkNotNullExpressionValue(bridge, "");
      if (((BungeeBridge)bridge).isFlag4()) {
         this.setPermissionMessage(JsonMessageModule.KOTLIN_DATA.computeText(Message.PERMISSION_ERROR.toString()));
      }
   }

   @Override
   public List suggest(@NotNull LiteSender liteSender, @NotNull String[] texts) {
      LiteCommandManager commandManager = this.platform.getCommandManager();
      Intrinsics.checkNotNullExpressionValue(commandManager, "");
      return ((BungeeAltsCommands)commandManager).computeList(this, this.getName(), liteSender, texts);
   }
}
