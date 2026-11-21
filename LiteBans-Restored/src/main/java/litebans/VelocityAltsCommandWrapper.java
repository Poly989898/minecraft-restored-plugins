package litebans;

import java.util.Arrays;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class VelocityAltsCommandWrapper extends VelocityCommandWrapper {
   private final Platform platform;

   public VelocityAltsCommandWrapper(@NotNull LiteCommand liteCommand, @NotNull Platform platform, @Nullable String text, @NotNull String[] texts) {
      super(liteCommand, text, platform, Arrays.copyOf(texts, texts.length));
      this.platform = platform;
   }

   public VelocityAltsCommandWrapper(LiteCommand liteCommand, Platform platform, String text, String[] texts, int value, DefaultConstructorMarker defaultConstructorMarker) {
      if ((value & 4) != 0) {
         text = liteCommand.getPermission();
      }

      this(liteCommand, platform, text, texts);
   }

   @Override
   public List suggest(@NotNull LiteSender liteSender, @NotNull String[] texts) {
      LiteCommandManager commandManager = this.platform.getCommandManager();
      Intrinsics.checkNotNullExpressionValue(commandManager, "");
      return ((VelocityAltsCommands)commandManager).computeList(this, this.getName(), liteSender, texts);
   }
}
