package com.nexomc.nexo.commands;

import com.nexomc.nexo.utils.SneakyThrow;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.pack.server.NexoPackServer;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 2,
   xi = 48,
   d1 = {"\u0000\u0012\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\u001a\u0016\u0010\u0000\u001a\u00020\u00012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u0000¨\u0006\u0005"},
   d2 = {"packCommand", "", "root", "Lcom/mojang/brigadier/builder/LiteralArgumentBuilder;", "Lio/papermc/paper/command/brigadier/CommandSourceStack;", "com.nexomc:core"}
)
public final class PackCommandKt {
   public static final void packCommand(@NotNull LiteralArgumentBuilder<CommandSourceStack> root) {
      NexoCommand nexoCommand = NexoCommand.INSTANCE;
      NexoCommand nexoCommand2 = NexoCommand.INSTANCE;
      LiteralArgumentBuilder literalArgumentBuilder = Commands.literal("pack");
      LiteralArgumentBuilder literalArgumentBuilder2 = nexoCommand2.withPermission$com_nexomc_core(literalArgumentBuilder, "nexo.command.pack");
      root.then(nexoCommand.executesSender$com_nexomc_core(literalArgumentBuilder2, PackCommandKt::packCommand$lambda$0).then(Commands.argument("players", ArgumentTypes.players()).executes(PackCommandKt::packCommand$lambda$1)));
   }

   private static final Unit packCommand$lambda$0(CommandSender sender) {
      if (sender instanceof Player) {
         NexoPlugin.Companion.instance().packServer().sendPack((Player)sender);
      } else {
         sender.sendRichMessage("<red>Only players can use this command.");
      }

      return Unit.INSTANCE;
   }

   private static final int packCommand$lambda$1(CommandContext ctx) {
      NexoPackServer packServer = NexoPlugin.Companion.instance().packServer();
      NexoCommand nexoCommand = NexoCommand.INSTANCE;
      String str = "players";
      int value2 = 0;
      Object argument = ctx.getArgument(str, PlayerSelectorArgumentResolver.class);
      argument = SneakyThrow.resolve((PlayerSelectorArgumentResolver)argument, (CommandSourceStack)ctx.getSource());
      List players = (List)argument;
      Iterable iterable = (Iterable)players;

      for(Object element : iterable) {
         Player p0 = (Player)element;
         int value = 0;
         packServer.sendPack(p0);
      }

      return 1;
   }
}
