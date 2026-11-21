package com.nexomc.nexo.commands;
import com.nexomc.nexo.utils.SneakyThrow;
import com.mojang.brigadier.arguments.ArgumentType;
import com.nexomc.nexo.commands.BlockInfoCommandKt;
import com.nexomc.nexo.commands.DebugCommandKt;
import com.nexomc.nexo.commands.DyeCommandKt;
import com.nexomc.nexo.commands.GlyphCommandKt;
import com.nexomc.nexo.commands.GlyphInfoCommandKt;
import com.nexomc.nexo.commands.ItemCommandKt;
import com.nexomc.nexo.commands.LogDumpCommandKt;
import com.nexomc.nexo.commands.MultipackCommandKt;
import com.nexomc.nexo.commands.PackCommandKt;
import com.nexomc.nexo.commands.RecipesCommandKt;
import com.nexomc.nexo.commands.ReloadCommandKt;
import com.nexomc.nexo.commands.UpdateCommandKt;
import com.nexomc.nexo.commands.VersionCommandKt;
import java.util.Comparator;
import java.util.Iterator;
import kotlin.comparisons.ComparisonsKt;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.configs.Message;
import com.nexomc.nexo.utils.NexoYamlKt;
import com.nexomc.nexo.utils.StringUtilsKt;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.text.StringsKt;
import net.kyori.adventure.util.TriState;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000`\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010 \n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010\u0011\n\u0002\b\u0006\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0014\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u00052\u0006\u0010\u0007\u001a\u00020\bJO\u0010\t\u001a&\u0012\f\u0012\n \u000b*\u0004\u0018\u00010\u00060\u0006 \u000b*\u0012\u0012\f\u0012\n \u000b*\u0004\u0018\u00010\u00060\u0006\u0018\u00010\n0\n*\b\u0012\u0004\u0012\u00020\u00060\n2\u0012\u0010\f\u001a\u000e\u0012\u0004\u0012\u00020\u000e\u0012\u0004\u0012\u00020\u000f0\rH\u0000¢\u0006\u0002\b\u0010JO\u0010\u0011\u001a&\u0012\f\u0012\n \u000b*\u0004\u0018\u00010\u00060\u0006 \u000b*\u0012\u0012\f\u0012\n \u000b*\u0004\u0018\u00010\u00060\u0006\u0018\u00010\n0\n*\b\u0012\u0004\u0012\u00020\u00060\n2\u0012\u0010\f\u001a\u000e\u0012\u0004\u0012\u00020\u0012\u0012\u0004\u0012\u00020\u000f0\rH\u0000¢\u0006\u0002\b\u0013J\u0083\u0001\u0010\t\u001aB\u0012\f\u0012\n \u000b*\u0004\u0018\u00010\u00060\u0006\u0012\f\u0012\n \u000b*\u0004\u0018\u0001H\u0015H\u0015 \u000b* \u0012\f\u0012\n \u000b*\u0004\u0018\u00010\u00060\u0006\u0012\f\u0012\n \u000b*\u0004\u0018\u0001H\u0015H\u0015\u0018\u00010\u00140\u0014\"\u0004\b\u0000\u0010\u0015*\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u0002H\u00150\u00142\u001e\u0010\f\u001a\u001a\u0012\u0004\u0012\u00020\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00060\u0017\u0012\u0004\u0012\u00020\u000f0\u0016H\u0000¢\u0006\u0002\b\u0010J\u0083\u0001\u0010\u0011\u001aB\u0012\f\u0012\n \u000b*\u0004\u0018\u00010\u00060\u0006\u0012\f\u0012\n \u000b*\u0004\u0018\u0001H\u0015H\u0015 \u000b* \u0012\f\u0012\n \u000b*\u0004\u0018\u00010\u00060\u0006\u0012\f\u0012\n \u000b*\u0004\u0018\u0001H\u0015H\u0015\u0018\u00010\u00140\u0014\"\u0004\b\u0000\u0010\u0015*\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u0002H\u00150\u00142\u001e\u0010\f\u001a\u001a\u0012\u0004\u0012\u00020\u0012\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00060\u0017\u0012\u0004\u0012\u00020\u000f0\u0016H\u0000¢\u0006\u0002\b\u0013JC\u0010\u0018\u001a&\u0012\f\u0012\n \u000b*\u0004\u0018\u00010\u00060\u0006 \u000b*\u0012\u0012\f\u0012\n \u000b*\u0004\u0018\u00010\u00060\u0006\u0018\u00010\n0\n*\b\u0012\u0004\u0012\u00020\u00060\n2\u0006\u0010\u0019\u001a\u00020\bH\u0000¢\u0006\u0002\b\u001aJk\u0010\u0018\u001aB\u0012\f\u0012\n \u000b*\u0004\u0018\u00010\u00060\u0006\u0012\f\u0012\n \u000b*\u0004\u0018\u0001H\u0015H\u0015 \u000b* \u0012\f\u0012\n \u000b*\u0004\u0018\u00010\u00060\u0006\u0012\f\u0012\n \u000b*\u0004\u0018\u0001H\u0015H\u0015\u0018\u00010\u00140\u0014\"\u0004\b\u0000\u0010\u0015*\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u0002H\u00150\u00142\u0006\u0010\u0019\u001a\u00020\bH\u0000¢\u0006\u0002\b\u001aJ\u0091\u0001\u0010\u001b\u001aB\u0012\f\u0012\n \u000b*\u0004\u0018\u00010\u00060\u0006\u0012\f\u0012\n \u000b*\u0004\u0018\u0001H\u0015H\u0015 \u000b* \u0012\f\u0012\n \u000b*\u0004\u0018\u00010\u00060\u0006\u0012\f\u0012\n \u000b*\u0004\u0018\u0001H\u0015H\u0015\u0018\u00010\u00140\u0014\"\u0004\b\u0000\u0010\u0015*\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u0002H\u00150\u00142\u0006\u0010\u001c\u001a\u00020\b2$\u0010\f\u001a \u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00120\u001d\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00060\u0017\u0012\u0004\u0012\u00020\u000f0\u0016H\u0000¢\u0006\u0002\b\u001eJw\u0010\u001f\u001aB\u0012\f\u0012\n \u000b*\u0004\u0018\u00010\u00060\u0006\u0012\f\u0012\n \u000b*\u0004\u0018\u0001H\u0015H\u0015 \u000b* \u0012\f\u0012\n \u000b*\u0004\u0018\u00010\u00060\u0006\u0012\f\u0012\n \u000b*\u0004\u0018\u0001H\u0015H\u0015\u0018\u00010\u00140\u0014\"\u0004\b\u0000\u0010\u0015*\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u0002H\u00150\u00142\u0012\u0010 \u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\"0!H\u0000¢\u0006\u0002\b#J.\u0010$\u001a\u0002H\u0015\"\n\b\u0000\u0010\u0015\u0018\u0001*\u00020\u0001*\b\u0012\u0004\u0012\u00020\u00060\u00172\u0006\u0010%\u001a\u00020\bH\u0080\b¢\u0006\u0004\b&\u0010'¨\u0006("},
   d2 = {"Lcom/nexomc/nexo/commands/NexoCommand;", "", "<init>", "()V", "createCommand", "Lcom/mojang/brigadier/tree/LiteralCommandNode;", "Lio/papermc/paper/command/brigadier/CommandSourceStack;", "literal", "", "executesPlayer", "Lcom/mojang/brigadier/builder/LiteralArgumentBuilder;", "kotlin.jvm.PlatformType", "block", "Lkotlin/Function1;", "Lorg/bukkit/entity/Player;", "", "executesPlayer$com_nexomc_core", "executesSender", "Lorg/bukkit/command/CommandSender;", "executesSender$com_nexomc_core", "Lcom/mojang/brigadier/builder/RequiredArgumentBuilder;", "T", "Lkotlin/Function2;", "Lcom/mojang/brigadier/context/CommandContext;", "withPermission", "permission", "withPermission$com_nexomc_core", "withOthers", "basePermission", "", "withOthers$com_nexomc_core", "suggestAsync", "supplier", "Lkotlin/Function0;", "", "suggestAsync$com_nexomc_core", "arg", "name", "arg$com_nexomc_core", "(Lcom/mojang/brigadier/context/CommandContext;Ljava/lang/String;)Ljava/lang/Object;", "com.nexomc:core"}
)
public final class NexoCommand {
   @NotNull
   public static final NexoCommand INSTANCE = new NexoCommand();

   private NexoCommand() {
   }

   @NotNull
   public final LiteralCommandNode<CommandSourceStack> createCommand(@NotNull String literal) {
      LiteralArgumentBuilder root = (LiteralArgumentBuilder)Commands.literal(literal).requires(NexoCommand::createCommand$lambda$0);
      ReloadCommandKt.reloadCommand(root);
      PackCommandKt.packCommand(root);
      RecipesCommandKt.recipeCommand(root);
      DebugCommandKt.debugCommand(root);
      UpdateCommandKt.updateCommand(root);
      ItemCommandKt.dumpItemCommand(root);
      ItemCommandKt.itemInfoCommand(root);
      GlyphInfoCommandKt.glyphInfoCommand(root);
      BlockInfoCommandKt.blockInfoCommand(root);
      ItemCommandKt.takeItemCommand(root);
      ItemCommandKt.giveItemCommand(root);
      ItemCommandKt.dropItemCommand(root);
      ItemCommandKt.inventoryCommand(root);
      DyeCommandKt.dyeCommand(root);
      LogDumpCommandKt.dumpLogCommand(root);
      VersionCommandKt.versionCommand(root);
      GlyphCommandKt.emojiCommand(root);
      MultipackCommandKt.multipackCommand(root);
      LiteralArgumentBuilder literalArgumentBuilder = Commands.literal("reset_custom_model_data");
      root.then(this.withPermission$com_nexomc_core(literalArgumentBuilder, "nexo.command.resetcmd").executes(NexoCommand::createCommand$lambda$1));
      this.executesSender$com_nexomc_core(root, (Function1<CommandSender, Unit>)(p0 -> { Message.COMMAND_HELP.send(p0); return Unit.INSTANCE; }));
      LiteralCommandNode literalCommandNode = root.build();
      return literalCommandNode;
   }

   public final LiteralArgumentBuilder<CommandSourceStack> executesPlayer$com_nexomc_core(@NotNull LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder, @NotNull Function1<? super Player, Unit> block) {
      return (LiteralArgumentBuilder)literalArgumentBuilder.executes((a0_ -> NexoCommand.executesPlayer$lambda$0(block, (CommandContext)a0_)));
   }

   public final LiteralArgumentBuilder<CommandSourceStack> executesSender$com_nexomc_core(@NotNull LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder, @NotNull Function1<? super CommandSender, Unit> block) {
      return (LiteralArgumentBuilder)literalArgumentBuilder.executes((a0_ -> NexoCommand.executesSender$lambda$0(block, (CommandContext)a0_)));
   }

   public final <T> RequiredArgumentBuilder<CommandSourceStack, T> executesPlayer$com_nexomc_core(@NotNull RequiredArgumentBuilder<CommandSourceStack, T> requiredArgumentBuilder, @NotNull Function2<? super Player, ? super CommandContext<CommandSourceStack>, Unit> block) {
      return (RequiredArgumentBuilder)requiredArgumentBuilder.executes((a0_ -> NexoCommand.executesPlayer$lambda$1(block, (CommandContext)a0_)));
   }

   public final <T> RequiredArgumentBuilder<CommandSourceStack, T> executesSender$com_nexomc_core(@NotNull RequiredArgumentBuilder<CommandSourceStack, T> requiredArgumentBuilder, @NotNull Function2<? super CommandSender, ? super CommandContext<CommandSourceStack>, Unit> block) {
      return (RequiredArgumentBuilder)requiredArgumentBuilder.executes((a0_ -> NexoCommand.executesSender$lambda$1(block, (CommandContext)a0_)));
   }

   public final LiteralArgumentBuilder<CommandSourceStack> withPermission$com_nexomc_core(@NotNull LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder, @NotNull String permission) {
      return (LiteralArgumentBuilder)literalArgumentBuilder.requires((a0_ -> NexoCommand.withPermission$lambda$0(permission, a0_)));
   }

   public final <T> RequiredArgumentBuilder<CommandSourceStack, T> withPermission$com_nexomc_core(@NotNull RequiredArgumentBuilder<CommandSourceStack, T> requiredArgumentBuilder, @NotNull String permission) {
      return (RequiredArgumentBuilder)requiredArgumentBuilder.requires((a0_ -> NexoCommand.withPermission$lambda$1(permission, a0_)));
   }

   public final <T> RequiredArgumentBuilder<CommandSourceStack, T> withOthers$com_nexomc_core(@NotNull RequiredArgumentBuilder<CommandSourceStack, T> requiredArgumentBuilder4, @NotNull String basePermission, @NotNull Function2<? super List<? extends CommandSender>, ? super CommandContext<CommandSourceStack>, Unit> block) {
      RequiredArgumentBuilder requiredArgumentBuilder = this.executesSender$com_nexomc_core(requiredArgumentBuilder4, ((a0_, a1_) -> NexoCommand.withOthers$lambda$0(block, a0_, a1_)));
      RequiredArgumentBuilder requiredArgumentBuilder2 = Commands.argument("targets", ArgumentTypes.players());
      RequiredArgumentBuilder requiredArgumentBuilder3 = this.withPermission$com_nexomc_core(requiredArgumentBuilder2, basePermission + ".others");
      return (RequiredArgumentBuilder)requiredArgumentBuilder.then((ArgumentBuilder)this.executesSender$com_nexomc_core(requiredArgumentBuilder3, ((a0_, a1_) -> NexoCommand.withOthers$lambda$1(block, a0_, a1_))));
   }

   public final <T> RequiredArgumentBuilder<CommandSourceStack, T> suggestAsync$com_nexomc_core(@NotNull RequiredArgumentBuilder<CommandSourceStack, T> requiredArgumentBuilder, @NotNull Function0<String[]> supplier) {
      return requiredArgumentBuilder.suggests(((a0_, a1_) -> NexoCommand.suggestAsync$lambda$0(supplier, a0_, a1_)));
   }

   private static final boolean createCommand$lambda$0(CommandSourceStack it) {
      return it.getSender().permissionValue("nexo.command") != TriState.FALSE;
   }

   private static final int createCommand$lambda$1(CommandContext it) {
      Map itemConfigs$com_nexomc_core = NexoPlugin.Companion.instance().configsManager().getItemConfigs$com_nexomc_core();

      for(Map.Entry element : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)itemConfigs$com_nexomc_core.entrySet()) {
         int value2 = 0;
         File key = (File)element.getKey();
         YamlConfiguration value = (YamlConfiguration)element.getValue();
         Map map = NexoYamlKt.childSections((ConfigurationSection)value);

         for(Map.Entry entry : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)map.entrySet()) {
            int value3 = 0;
            ConfigurationSection configurationSection2 = NexoYamlKt.remove((ConfigurationSection)entry.getValue(), "Pack.custom_model_data");
            String str = "Pack";
            Object obj = configurationSection2.get(str);
            if (!(obj instanceof ConfigurationSection)) {
               obj = null;
            }

            ConfigurationSection configurationSection = (ConfigurationSection)obj;
            if (configurationSection != null) {
               Object obj2 = configurationSection;
               int value4 = 0;
               int value5 = 0;
               if (((ConfigurationSection)obj2).getKeys(false).isEmpty()) {
                  configurationSection2.set(str, (Object)null);
               }
            }
         }

         try {
            value.save(key);
         } catch (java.io.IOException e) {
            throw new java.io.UncheckedIOException(e);
         }
      }

      return 1;
   }

   private static final int executesPlayer$lambda$0(Function1 block, CommandContext ctx) {
      CommandSender sender = ((CommandSourceStack)ctx.getSource()).getSender();
      Player player = sender instanceof Player ? (Player)sender : null;
      if ((sender instanceof Player ? (Player)sender : null) != null) {
         Player player2 = player;
         int value = 0;
         block.invoke(player2);
      }

      return 1;
   }

   private static final int executesSender$lambda$0(Function1 block, CommandContext ctx) {
      CommandSender sender = ((CommandSourceStack)ctx.getSource()).getSender();
      block.invoke(sender);
      return 1;
   }

   private static final int executesPlayer$lambda$1(Function2 block, CommandContext ctx) {
      CommandSender sender = ((CommandSourceStack)ctx.getSource()).getSender();
      Player player = sender instanceof Player ? (Player)sender : null;
      if ((sender instanceof Player ? (Player)sender : null) != null) {
         Player player2 = player;
         int value = 0;
         block.invoke(player2, ctx);
      }

      return 1;
   }

   private static final int executesSender$lambda$1(Function2 block, CommandContext ctx) {
      CommandSender sender = ((CommandSourceStack)ctx.getSource()).getSender();
      block.invoke(sender, ctx);
      return 1;
   }

   private static final boolean withPermission$lambda$0(String permission, CommandSourceStack it) {
      return it.getSender().hasPermission(permission);
   }

   private static final boolean withPermission$lambda$1(String permission, CommandSourceStack it) {
      return it.getSender().hasPermission(permission);
   }

   private static final Unit withOthers$lambda$0(Function2 block, CommandSender sender, CommandContext ctx) {
      block.invoke(CollectionsKt.listOf(sender), ctx);
      return Unit.INSTANCE;
   }

   private static final Unit withOthers$lambda$1(Function2 block, CommandSender sender, CommandContext ctx) {
      NexoCommand nexoCommand = INSTANCE;
      String str = "targets";
      int value = 0;
      Object argument = ctx.getArgument(str, PlayerSelectorArgumentResolver.class);
      argument = SneakyThrow.resolve((PlayerSelectorArgumentResolver)argument, (CommandSourceStack)ctx.getSource());
      List targets = (List)argument;
      block.invoke(targets, ctx);
      return Unit.INSTANCE;
   }

   private static final Suggestions suggestAsync$lambda$0$0(Function0 supplier, SuggestionsBuilder builder, CommandContext ctx) {
      String[] items = (String[])supplier.invoke();
      String input = ctx.getInput();
      String lastWord = StringUtilsKt.remove(StringsKt.substringAfterLast(input, " ", input), "_");
      List<String> matches = new ArrayList<>();

      for(String it : items) {
         if (StringsKt.contains((CharSequence)StringUtilsKt.remove(it, "_"), (CharSequence)lastWord, false)) {
            matches.add(it);
         }
      }

      matches.sort((x, y) -> Boolean.compare(StringsKt.startsWith(x, input, false), StringsKt.startsWith(y, input, false)));

      for(String suggestion : matches) {
         builder.suggest(suggestion);
      }

      return builder.build();
   }

   private static final CompletableFuture suggestAsync$lambda$0(Function0 supplier, CommandContext ctx, SuggestionsBuilder builder) {
        return CompletableFuture.supplyAsync(() -> NexoCommand.suggestAsync$lambda$0$0(supplier, builder, ctx));
    }
}
