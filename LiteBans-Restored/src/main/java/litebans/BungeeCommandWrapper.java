package litebans;

import java.util.List;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import org.jetbrains.annotations.NotNull;

@CommandMarker
@PlatformMarker(obtainByteValue = 1)
public class BungeeCommandWrapper extends Command implements LiteCommand, TabExecutor {
   private final LiteCommand liteCommand;
   private final Platform platform;
   private final String[] texts;

   public BungeeCommandWrapper(LiteCommand liteCommand, String text, Platform platform, String[] texts) {
      super(liteCommand.getName(), text, texts);
      this.liteCommand = liteCommand;
      this.platform = platform;
      this.texts = texts;
   }

   public void execute(CommandSender commandSender, String[] texts) {
      this.execute(this.platform.wrapSender(commandSender), texts);
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
   public @NotNull List suggest(@NotNull LiteSender liteSender, String[] texts) {
      return this.platform.getCommandManager().computeList(this, this.getName(), liteSender, texts);
   }

   public Iterable onTabComplete(CommandSender commandSender, String[] texts) {
      return this.suggest(this.platform.wrapSender(commandSender), texts);
   }

   @Override
   public String[] getAliases() {
      return this.texts;
   }
}
