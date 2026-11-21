package com.nexomc.nexo.commands;

import com.nexomc.nexo.utils.SneakyThrow;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.pack.multipack.MultipackManager;
import com.nexomc.nexo.pack.multipack.PackTemplate;
import com.nexomc.nexo.pack.multipack.PlayerPackData;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 2,
   xi = 48,
   d1 = {"\u0000\u0012\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\u001a\u0016\u0010\u0000\u001a\u00020\u00012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u0000¨\u0006\u0005"},
   d2 = {"multipackCommand", "", "root", "Lcom/mojang/brigadier/builder/LiteralArgumentBuilder;", "Lio/papermc/paper/command/brigadier/CommandSourceStack;", "com.nexomc:core"}
)
public final class MultipackCommandKt {
   public static final void multipackCommand(@NotNull LiteralArgumentBuilder<CommandSourceStack> root) {
      NexoCommand nexoCommand = NexoCommand.INSTANCE;
      NexoCommand nexoCommand2 = NexoCommand.INSTANCE;
      LiteralArgumentBuilder literalArgumentBuilder = Commands.literal("multipack");
      LiteralArgumentBuilder literalArgumentBuilder2 = nexoCommand2.withPermission$com_nexomc_core(literalArgumentBuilder, "nexo.command.multipack");
      LiteralArgumentBuilder literalArgumentBuilder3 = nexoCommand.executesPlayer$com_nexomc_core(literalArgumentBuilder2, MultipackCommandKt::multipackCommand$lambda$0);
      NexoCommand nexoCommand3 = NexoCommand.INSTANCE;
      NexoCommand nexoCommand4 = NexoCommand.INSTANCE;
      LiteralArgumentBuilder literalArgumentBuilder4 = Commands.literal("menu");
      LiteralArgumentBuilder literalArgumentBuilder5 = nexoCommand4.withPermission$com_nexomc_core(literalArgumentBuilder4, "nexo.command.multipack.menu");
      LiteralArgumentBuilder literalArgumentBuilder6 = nexoCommand3.executesPlayer$com_nexomc_core(literalArgumentBuilder5, MultipackCommandKt::multipackCommand$lambda$1);
      NexoCommand nexoCommand5 = NexoCommand.INSTANCE;
      NexoCommand nexoCommand6 = NexoCommand.INSTANCE;
      RequiredArgumentBuilder requiredArgumentBuilder = Commands.argument("targets", ArgumentTypes.players());
      RequiredArgumentBuilder requiredArgumentBuilder2 = nexoCommand6.withPermission$com_nexomc_core(requiredArgumentBuilder, "nexo.command.multipack.menu.other");
      literalArgumentBuilder3 = (LiteralArgumentBuilder)literalArgumentBuilder3.then(literalArgumentBuilder6.then((ArgumentBuilder)nexoCommand5.executesSender$com_nexomc_core(requiredArgumentBuilder2, MultipackCommandKt::multipackCommand$lambda$2)));
      NexoCommand nexoCommand7 = NexoCommand.INSTANCE;
      LiteralArgumentBuilder literalArgumentBuilder7 = Commands.literal("list");
      literalArgumentBuilder3 = (LiteralArgumentBuilder)literalArgumentBuilder3.then((ArgumentBuilder)nexoCommand7.executesPlayer$com_nexomc_core(literalArgumentBuilder7, MultipackCommandKt::multipackCommand$lambda$3));
      nexoCommand7 = NexoCommand.INSTANCE;
      literalArgumentBuilder7 = Commands.literal("toggle");
      LiteralArgumentBuilder literalArgumentBuilder8 = nexoCommand7.withPermission$com_nexomc_core(literalArgumentBuilder7, "nexo.command.multipack.toggle");
      NexoCommand nexoCommand8 = NexoCommand.INSTANCE;
      requiredArgumentBuilder2 = Commands.argument("template", (ArgumentType)StringArgumentType.word()).suggests(MultipackCommandKt::multipackCommand$lambda$4);
      literalArgumentBuilder3 = (LiteralArgumentBuilder)literalArgumentBuilder3.then(literalArgumentBuilder8.then((ArgumentBuilder)nexoCommand8.withOthers$com_nexomc_core(requiredArgumentBuilder2, "nexo.command.multipack.toggle", MultipackCommandKt::multipackCommand$lambda$5)));
      NexoCommand nexoCommand9 = NexoCommand.INSTANCE;
      LiteralArgumentBuilder literalArgumentBuilder9 = Commands.literal("debug");
      LiteralArgumentBuilder literalArgumentBuilder10 = nexoCommand9.withPermission$com_nexomc_core(literalArgumentBuilder9, "nexo.command.multipack.debug");
      LiteralArgumentBuilder literalArgumentBuilder11 = literalArgumentBuilder3;
      int value = 0;
      NexoCommand nexoCommand10 = NexoCommand.INSTANCE;
      LiteralArgumentBuilder literalArgumentBuilder12 = nexoCommand10.executesPlayer$com_nexomc_core(literalArgumentBuilder10, MultipackCommandKt::multipackCommand$lambda$6$0);
      NexoCommand nexoCommand11 = NexoCommand.INSTANCE;
      nexoCommand9 = NexoCommand.INSTANCE;
      RequiredArgumentBuilder requiredArgumentBuilder3 = Commands.argument("targets", ArgumentTypes.players());
      RequiredArgumentBuilder requiredArgumentBuilder4 = nexoCommand9.withPermission$com_nexomc_core(requiredArgumentBuilder3, "nexo.command.multipack.debug.others");
      literalArgumentBuilder12.then((ArgumentBuilder)nexoCommand11.executesSender$com_nexomc_core(requiredArgumentBuilder4, MultipackCommandKt::multipackCommand$lambda$6$1));
      Unit unit = Unit.INSTANCE;
      root.then(literalArgumentBuilder11.then((ArgumentBuilder)literalArgumentBuilder10));
   }

   private static final void multipackCommand$lambda$6$debugPlayer(Player player) {
      MultipackManager manager = NexoPlugin.Companion.instance().multipackManager();
      if (!manager.isEnabled()) {
         player.sendRichMessage("<red>Multipack system is not enabled.");
      } else {
         Set enabledIds = PlayerPackData.INSTANCE.enabledTemplates((OfflinePlayer)player);
         Map selections = PlayerPackData.INSTANCE.getSelections((OfflinePlayer)player);
         player.sendMessage((Component)Component.text("--- Multipack Debug ---", (TextColor)NamedTextColor.GOLD));
         player.sendRichMessage("<gray>Templates loaded: <white>" + manager.getTemplates().size());
         player.sendRichMessage("<gray>Player selections: <white>" + selections);
         player.sendRichMessage("<gray>Resolved enabled: <white>" + enabledIds);

         for(PackTemplate template : manager.getTemplates()) {
            boolean enabled = enabledIds.contains(template.getId());
            NamedTextColor color = template.getRequired() ? NamedTextColor.DARK_RED : (template.getDefault() && enabled ? NamedTextColor.YELLOW : (enabled ? NamedTextColor.GREEN : NamedTextColor.GRAY));
            String conflicts = !((Collection)template.getConflictsWith()).isEmpty() ? " conflicts=" + template.getConflictsWith() : "";
            TextComponent textComponent = Component.text("  " + template.getId(), (TextColor)color);
            boolean required = template.getRequired();
            player.sendMessage(textComponent.append((Component)Component.text(" req=" + required + " def=" + template.getDefault() + " on=" + enabled + conflicts, (TextColor)NamedTextColor.DARK_GRAY)));
         }

      }
   }

   private static final Unit multipackCommand$lambda$0(Player player) {
      NexoPlugin.Companion.instance().multipackManager().openGui(player);
      return Unit.INSTANCE;
   }

   private static final Unit multipackCommand$lambda$1(Player player) {
      NexoPlugin.Companion.instance().multipackManager().openGui(player);
      return Unit.INSTANCE;
   }

   private static final Unit multipackCommand$lambda$2(CommandSender commandSender, CommandContext ctx) {
      NexoCommand nexoCommand = NexoCommand.INSTANCE;
      String str = "targets";
      int value = 0;
      Object argument = ctx.getArgument(str, PlayerSelectorArgumentResolver.class);

      for(Object var10000_it : (List)SneakyThrow.resolve((PlayerSelectorArgumentResolver)argument, (CommandSourceStack)ctx.getSource())) {

         argument = var10000_it;
         Player target = (Player)argument;
         NexoPlugin.Companion.instance().multipackManager().openGui(target);
      }

      return Unit.INSTANCE;
   }

   private static final Unit multipackCommand$lambda$3(Player player) {
      MultipackManager manager = NexoPlugin.Companion.instance().multipackManager();
      if (!manager.isEnabled()) {
         player.sendRichMessage("<red>Multipack system is not enabled.");
         return Unit.INSTANCE;
      } else {
         Map selections = PlayerPackData.INSTANCE.getSelections((OfflinePlayer)player);
         TextColor textColor = (TextColor)NamedTextColor.GOLD;
         TextDecoration[] textDecorations = new TextDecoration[]{TextDecoration.BOLD};
         player.sendMessage((Component)Component.text("--- Multipack Templates ---", textColor, textDecorations));

         for(PackTemplate template : manager.getTemplates()) {
            boolean enabled = template.getRequired() || (Boolean)selections.getOrDefault(template.getId(), template.getDefault());
            NamedTextColor statusColor = enabled ? NamedTextColor.GREEN : NamedTextColor.RED;
            String statusText = enabled ? "enabled" : "disabled";
            TextComponent typeTag = template.getRequired() ? Component.text(" [required]", (TextColor)NamedTextColor.DARK_RED) : (template.getDefault() ? Component.text(" [default]", (TextColor)NamedTextColor.YELLOW) : Component.empty());
            player.sendMessage((((Component.text(" - ", (TextColor)NamedTextColor.GRAY).append((Component)Component.text(template.getId(), (TextColor)NamedTextColor.AQUA))).append((Component)Component.text(": ", (TextColor)NamedTextColor.GRAY))).append((Component)Component.text(statusText, (TextColor)statusColor))).append((Component)typeTag));
         }

         return Unit.INSTANCE;
      }
   }

   private static final CompletableFuture multipackCommand$lambda$4(CommandContext ctx, SuggestionsBuilder builder) {
      MultipackManager manager = NexoPlugin.Companion.instance().multipackManager();
      Iterable templates = (Iterable)manager.getTemplates();
      Collection destination = (Collection)(new ArrayList());

      for(Object element : templates) {
         PackTemplate it = (PackTemplate)element;
         int value = 0;
         if (!it.getRequired()) {
            destination.add(element);
         }
      }

      templates = (Iterable)((List)destination);

      for(Object obj : templates) {
         PackTemplate it = (PackTemplate)obj;
         int value2 = 0;
         builder.suggest(it.getId());
      }

      return builder.buildFuture();
   }

   private static final Unit multipackCommand$lambda$5(List players, CommandContext ctx) {
      NexoCommand nexoCommand = NexoCommand.INSTANCE;
      String str = "template";
      int value2 = 0;
      Object argument = ctx.getArgument(str, String.class);
      String templateId = (String)argument;
      Iterable iterable = (Iterable)players;

      for(Object element : iterable) {
         CommandSender player = (CommandSender)element;
         int value = 0;
         if (!(player instanceof Player)) {
            player.sendRichMessage("<red>Only players can use this command.");
         } else {
            Boolean result = PlayerPackData.INSTANCE.toggleTemplate((Player)player, templateId);
            if (result == null) {
               player.sendRichMessage("<red>Template <aqua>" + templateId + "</aqua> does not exist or is required.");
            } else if (Intrinsics.areEqual(result, true)) {
               player.sendRichMessage("<green>Enabled template <aqua>" + templateId + "</aqua>. Applying packs...");
               NexoPlugin.Companion.instance().packServer().sendPack((Player)player);
            } else {
               if (!Intrinsics.areEqual(result, false)) {
                  throw new NoWhenBranchMatchedException();
               }

               player.sendRichMessage("<red>Disabled template <aqua>" + templateId + "</aqua>. Applying packs...");
               NexoPlugin.Companion.instance().packServer().sendPack((Player)player);
            }
         }
      }

      return Unit.INSTANCE;
   }

   private static final Unit multipackCommand$lambda$6$0(Player player) {
      multipackCommand$lambda$6$debugPlayer(player);
      return Unit.INSTANCE;
   }

   private static final Unit multipackCommand$lambda$6$1(CommandSender commandSender, CommandContext ctx) {
      NexoCommand nexoCommand = NexoCommand.INSTANCE;
      String str = "targets";
      int value = 0;
      Object argument = ctx.getArgument(str, PlayerSelectorArgumentResolver.class);

      for(Object var10000_it : (List)SneakyThrow.resolve((PlayerSelectorArgumentResolver)argument, (CommandSourceStack)ctx.getSource())) {

         argument = var10000_it;
         Player player = (Player)argument;
         multipackCommand$lambda$6$debugPlayer(player);
      }

      return Unit.INSTANCE;
   }
}
