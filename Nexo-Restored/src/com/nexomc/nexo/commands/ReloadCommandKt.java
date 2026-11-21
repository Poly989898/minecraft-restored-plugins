package com.nexomc.nexo.commands;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.nexomc.nexo.NexoPlugin;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import java.util.Collection;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import kotlin.text.StringsKt;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 2,
   xi = 48,
   d1 = {"\u0000\u0012\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\u001a\u0016\u0010\u0000\u001a\u00020\u00012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u0000¨\u0006\u0005"},
   d2 = {"reloadCommand", "", "root", "Lcom/mojang/brigadier/builder/LiteralArgumentBuilder;", "Lio/papermc/paper/command/brigadier/CommandSourceStack;", "com.nexomc:core"}
)
public final class ReloadCommandKt {
   public static final void reloadCommand(@NotNull LiteralArgumentBuilder<CommandSourceStack> root) {
      Function2 reloadLogic = ((a0_, a1_) -> ReloadCommandKt.reloadCommand$lambda$0((String)a0_, (CommandSender)a1_));
      String[] completions = new String[]{"items", "pack", "recipes", "configs", "dialogs"};
      NexoCommand nexoCommand = NexoCommand.INSTANCE;
      RequiredArgumentBuilder requiredArgumentBuilder = Commands.argument("type", (ArgumentType)StringArgumentType.word()).suggests(((a0_, a1_) -> ReloadCommandKt.reloadCommand$lambda$1(completions, a0_, a1_)));
      RequiredArgumentBuilder logic = nexoCommand.executesSender$com_nexomc_core(requiredArgumentBuilder, ((a0_, a1_) -> ReloadCommandKt.reloadCommand$lambda$2(reloadLogic, a0_, a1_)));
      NexoCommand nexoCommand2 = NexoCommand.INSTANCE;
      NexoCommand nexoCommand3 = NexoCommand.INSTANCE;
      LiteralArgumentBuilder literalArgumentBuilder = Commands.literal("reload");
      LiteralArgumentBuilder literalArgumentBuilder2 = nexoCommand3.withPermission$com_nexomc_core(literalArgumentBuilder, "nexo.command.reload");
      root.then(nexoCommand2.executesSender$com_nexomc_core(literalArgumentBuilder2, (a0_ -> ReloadCommandKt.reloadCommand$lambda$3(reloadLogic, a0_))).then((ArgumentBuilder)logic));
      nexoCommand2 = NexoCommand.INSTANCE;
      NexoCommand nexoCommand4 = NexoCommand.INSTANCE;
      literalArgumentBuilder = Commands.literal("rl");
      LiteralArgumentBuilder literalArgumentBuilder3 = nexoCommand4.withPermission$com_nexomc_core(literalArgumentBuilder, "nexo.command.reload");
      root.then(nexoCommand2.executesSender$com_nexomc_core(literalArgumentBuilder3, (a0_ -> ReloadCommandKt.reloadCommand$lambda$4(reloadLogic, a0_))).then((ArgumentBuilder)logic));
   }

   private static final int reloadCommand$lambda$0(String type, CommandSender sender) {
      block: {
         if (type != null) {
            switch (type) {
               case "pack":
                  ReloadCommand.reloadPack(sender);
                  break block;
               case "items":
                  ReloadCommand.reloadItems(sender);
                  break block;
               case "configs":
                  ReloadCommand.reloadConfigs(sender);
                  break block;
               case "recipes":
                  ReloadCommand.reloadRecipes(sender);
                  break block;
               case "dialogs":
                  ReloadCommand.reloadDialogs(sender);
                  break block;
            }
         }

         ReloadCommand.reloadAll(sender);
      }

      Collection onlinePlayers = Bukkit.getOnlinePlayers();
      Iterable iterable = (Iterable)onlinePlayers;

      for(Object element : iterable) {
         Player player = (Player)element;
         int value = 0;
         NexoPlugin.Companion.instance().fontManager().sendGlyphTabCompletion(player);
      }

      return 1;
   }

   private static final CompletableFuture reloadCommand$lambda$1(String[] completions, CommandContext commandContext, SuggestionsBuilder builder) {
      Object[] objs = completions;
      int value = 0;

      for(int i = completions.length; value < i; ++value) {
         Object element = objs[value];
         int value2 = 0;
         String remaining = builder.getRemaining();
         remaining = remaining.toLowerCase(Locale.ROOT);
         if (StringsKt.startsWith((String)element, remaining, false)) {
            builder.suggest((String)element);
         }
      }

      return builder.buildFuture();
   }

   private static final Unit reloadCommand$lambda$2(Function2 reloadLogic, CommandSender sender, CommandContext ctx) {
      NexoCommand nexoCommand = NexoCommand.INSTANCE;
      String str2 = "type";
      int value = 0;
      Object argument = ctx.getArgument(str2, String.class);
      String str = ((String)argument).toLowerCase(Locale.ROOT);
      reloadLogic.invoke(str, sender);
      return Unit.INSTANCE;
   }

   private static final Unit reloadCommand$lambda$3(Function2 reloadLogic, CommandSender it) {
      reloadLogic.invoke((Object)null, it);
      return Unit.INSTANCE;
   }

   private static final Unit reloadCommand$lambda$4(Function2 reloadLogic, CommandSender it) {
      reloadLogic.invoke((Object)null, it);
      return Unit.INSTANCE;
   }
}
