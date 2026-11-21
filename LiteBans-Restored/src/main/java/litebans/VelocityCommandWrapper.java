package litebans;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.command.SimpleCommand.Invocation;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@CommandMarker
@PlatformMarker(obtainByteValue = 2)
public class VelocityCommandWrapper implements LiteCommand, SimpleCommand {
   private final LiteCommand liteCommand;
   private final String permission;
   private final Platform platform;
   private final String[] texts;

   public VelocityCommandWrapper(LiteCommand liteCommand, String text, Platform platform, String[] texts) {
      this.liteCommand = liteCommand;
      this.permission = text;
      this.platform = platform;
      this.texts = texts;
   }

   @Override
   public List suggest(Invocation invocation) {
      return this.suggest(this.platform.wrapSender(invocation.source()), (String[])invocation.arguments());
   }

   @Override
   public void execute(Invocation invocation) {
      this.execute(this.platform.wrapSender(invocation.source()), (String[])invocation.arguments());
   }

   @Override
   public void execute(@NotNull LiteSender liteSender, String[] texts) {
      try {
         this.platform.getCommandManager().handleLiteCommand(this.liteCommand, liteSender, texts);
      } catch (Throwable e) {
         e.printStackTrace();
      }
   }

   @Override
   public String getName() {
      return this.liteCommand.getName();
   }

   @Override
   public @Nullable String getPermission() {
      return this.permission;
   }

   @Override
   public @NotNull List suggest(@NotNull LiteSender liteSender, String[] texts) {
      return this.platform.getCommandManager().computeList(this, this.getName(), liteSender, texts);
   }

   @Override
   public boolean hasPermission(Invocation invocation) {
      String permission = this.getPermission();
      return permission == null ? true : invocation.source().hasPermission(permission);
   }

   @Override
   public String[] getAliases() {
      return this.texts;
   }
}
