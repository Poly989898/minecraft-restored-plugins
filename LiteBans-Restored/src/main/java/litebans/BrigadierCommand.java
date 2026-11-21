package litebans;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BrigadierCommand implements LiteCommand, Command, SuggestionProvider {
   private final LiteCommand liteCommand;
   private final String permission;
   private final Platform platform;
   private final String[] texts;

   public BrigadierCommand(LiteCommand liteCommand2, String text, Platform platform, String[] texts) {
      this.liteCommand = liteCommand2;
      this.permission = text;
      this.platform = platform;
      this.texts = texts;
   }

   public int run(CommandContext commandContext) {
      LiteSender wrapSender2 = this.platform.wrapSender(commandContext.getSource());
      String[] split2 = commandContext.getInput().split(" ");
      if (split2.length >= 2) {
         String[] copyOfRange2 = Arrays.copyOfRange(split2, 1, split2.length);
         this.execute(wrapSender2, copyOfRange2);
      } else {
         this.execute(wrapSender2, new String[0]);
      }

      return 1;
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

   public CompletableFuture getSuggestions(CommandContext commandContext, SuggestionsBuilder suggestionsBuilder) {
      LiteSender wrapSender2 = this.platform.wrapSender(commandContext.getSource());
      String[] split2 = commandContext.getInput().split(" ");
      List suggest2 = this.suggest(wrapSender2, split2);
      suggest2.forEach(obj -> suggestionsBuilder.suggest((String) obj));
      return suggestionsBuilder.buildFuture();
   }

   public LiteCommand getLiteCommand() {
      return this.liteCommand;
   }

   @Override
   public String[] getAliases() {
      return this.texts;
   }
}
