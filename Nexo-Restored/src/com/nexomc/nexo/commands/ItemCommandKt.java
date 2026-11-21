package com.nexomc.nexo.commands;

import com.nexomc.nexo.utils.SneakyThrow;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.configs.Message;
import com.nexomc.nexo.items.ItemBuilder;
import com.nexomc.nexo.items.ItemUpdater;
import com.nexomc.nexo.nms.NMSHandlers;
import com.nexomc.nexo.utils.AdventureUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.functions.Function4;
import kotlin.jvm.internal.ArrayIteratorKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import kotlin.sequences.Sequence;
import kotlin.sequences.SequencesKt;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 2,
   xi = 48,
   d1 = {"\u0000\u0014\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\u001a\u0016\u0010\u0000\u001a\u00020\u00012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u0000\u001a\u0016\u0010\u0005\u001a\u00020\u00012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u0000\u001a\u0016\u0010\u0006\u001a\u00020\u00012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u0000\u001a\u0016\u0010\u0007\u001a\u00020\u00012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u0000\u001a\u0016\u0010\b\u001a\u00020\u00012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u0000\u001a\u0016\u0010\t\u001a\u00020\u00012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u0000¨\u0006\n"},
   d2 = {"inventoryCommand", "", "root", "Lcom/mojang/brigadier/builder/LiteralArgumentBuilder;", "Lio/papermc/paper/command/brigadier/CommandSourceStack;", "dropItemCommand", "giveItemCommand", "takeItemCommand", "itemInfoCommand", "dumpItemCommand", "com.nexomc:core"}
)
public final class ItemCommandKt {
   public static final void inventoryCommand(@NotNull LiteralArgumentBuilder<CommandSourceStack> root) {
      RequiredArgumentBuilder logic = (RequiredArgumentBuilder)Commands.argument("target", ArgumentTypes.player()).executes(ItemCommandKt::inventoryCommand$lambda$0);
      NexoCommand nexoCommand = NexoCommand.INSTANCE;
      NexoCommand nexoCommand2 = NexoCommand.INSTANCE;
      LiteralArgumentBuilder literalArgumentBuilder = Commands.literal("inventory");
      LiteralArgumentBuilder literalArgumentBuilder2 = nexoCommand2.withPermission$com_nexomc_core(literalArgumentBuilder, "nexo.command.inventory");
      root.then(nexoCommand.executesPlayer$com_nexomc_core(literalArgumentBuilder2, ItemCommandKt::inventoryCommand$lambda$1).then((ArgumentBuilder)logic));
      nexoCommand = NexoCommand.INSTANCE;
      NexoCommand nexoCommand3 = NexoCommand.INSTANCE;
      literalArgumentBuilder = Commands.literal("inv");
      LiteralArgumentBuilder literalArgumentBuilder3 = nexoCommand3.withPermission$com_nexomc_core(literalArgumentBuilder, "nexo.command.inventory");
      root.then(nexoCommand.executesPlayer$com_nexomc_core(literalArgumentBuilder3, ItemCommandKt::inventoryCommand$lambda$2).then((ArgumentBuilder)logic));
   }

   public static final void dropItemCommand(@NotNull LiteralArgumentBuilder<CommandSourceStack> root) {
      Function4 dropLogic = ((a0_, a1_, a2_, a3_) -> ItemCommandKt.dropItemCommand$lambda$0((CommandSender)a0_, (Player[])a1_, (String)a2_, (Integer)a3_));
      NexoCommand nexoCommand = NexoCommand.INSTANCE;
      LiteralArgumentBuilder literalArgumentBuilder = Commands.literal("drop");
      LiteralArgumentBuilder literalArgumentBuilder2 = nexoCommand.withPermission$com_nexomc_core(literalArgumentBuilder, "nexo.command.drop");
      NexoCommand nexoCommand2 = NexoCommand.INSTANCE;
      NexoCommand nexoCommand3 = NexoCommand.INSTANCE;
      RequiredArgumentBuilder requiredArgumentBuilder = Commands.argument("item", (ArgumentType)StringArgumentType.word());
      RequiredArgumentBuilder requiredArgumentBuilder2 = nexoCommand3.suggestAsync$com_nexomc_core(requiredArgumentBuilder, ItemCommandKt::dropItemCommand$lambda$1);
      RequiredArgumentBuilder requiredArgumentBuilder3 = nexoCommand2.executesSender$com_nexomc_core(requiredArgumentBuilder2, ((a0_, a1_) -> ItemCommandKt.dropItemCommand$lambda$2(dropLogic, a0_, a1_)));
      nexoCommand3 = NexoCommand.INSTANCE;
      requiredArgumentBuilder = Commands.argument("amount", (ArgumentType)IntegerArgumentType.integer(1));
      RequiredArgumentBuilder requiredArgumentBuilder4 = nexoCommand3.executesSender$com_nexomc_core(requiredArgumentBuilder, ((a0_, a1_) -> ItemCommandKt.dropItemCommand$lambda$3(dropLogic, a0_, a1_)));
      NexoCommand nexoCommand4 = NexoCommand.INSTANCE;
      RequiredArgumentBuilder requiredArgumentBuilder5 = Commands.argument("players", ArgumentTypes.players());
      root.then(literalArgumentBuilder2.then(requiredArgumentBuilder3.then(requiredArgumentBuilder4.then((ArgumentBuilder)nexoCommand4.executesSender$com_nexomc_core(requiredArgumentBuilder5, ((a0_, a1_) -> ItemCommandKt.dropItemCommand$lambda$4(dropLogic, a0_, a1_)))))));
   }

   public static final void giveItemCommand(@NotNull LiteralArgumentBuilder<CommandSourceStack> root) {
      Function4 giveLogic = ((a0_, a1_, a2_, a3_) -> ItemCommandKt.giveItemCommand$lambda$0((CommandSender)a0_, (Player[])a1_, (String)a2_, (Integer)a3_));
      NexoCommand nexoCommand = NexoCommand.INSTANCE;
      LiteralArgumentBuilder literalArgumentBuilder = Commands.literal("give");
      LiteralArgumentBuilder literalArgumentBuilder2 = nexoCommand.withPermission$com_nexomc_core(literalArgumentBuilder, "nexo.command.give");
      NexoCommand nexoCommand2 = NexoCommand.INSTANCE;
      NexoCommand nexoCommand3 = NexoCommand.INSTANCE;
      RequiredArgumentBuilder requiredArgumentBuilder = Commands.argument("item", (ArgumentType)StringArgumentType.word());
      RequiredArgumentBuilder requiredArgumentBuilder2 = nexoCommand3.suggestAsync$com_nexomc_core(requiredArgumentBuilder, ItemCommandKt::giveItemCommand$lambda$1);
      RequiredArgumentBuilder requiredArgumentBuilder3 = nexoCommand2.executesPlayer$com_nexomc_core(requiredArgumentBuilder2, ((a0_, a1_) -> ItemCommandKt.giveItemCommand$lambda$2(giveLogic, a0_, a1_)));
      nexoCommand3 = NexoCommand.INSTANCE;
      requiredArgumentBuilder = Commands.argument("amount", (ArgumentType)IntegerArgumentType.integer(1));
      RequiredArgumentBuilder requiredArgumentBuilder4 = nexoCommand3.executesPlayer$com_nexomc_core(requiredArgumentBuilder, ((a0_, a1_) -> ItemCommandKt.giveItemCommand$lambda$3(giveLogic, a0_, a1_)));
      NexoCommand nexoCommand4 = NexoCommand.INSTANCE;
      RequiredArgumentBuilder requiredArgumentBuilder5 = Commands.argument("players", ArgumentTypes.players());
      root.then(literalArgumentBuilder2.then(requiredArgumentBuilder3.then(requiredArgumentBuilder4.then((ArgumentBuilder)nexoCommand4.executesSender$com_nexomc_core(requiredArgumentBuilder5, ((a0_, a1_) -> ItemCommandKt.giveItemCommand$lambda$4(giveLogic, a0_, a1_)))))));
   }

   public static final void takeItemCommand(@NotNull LiteralArgumentBuilder<CommandSourceStack> root) {
      Function4 takeLogic = ((a0_, a1_, a2_, a3_) -> ItemCommandKt.takeItemCommand$lambda$0((CommandSender)a0_, (Player[])a1_, (String)a2_, (Integer)a3_));
      NexoCommand nexoCommand = NexoCommand.INSTANCE;
      LiteralArgumentBuilder literalArgumentBuilder = Commands.literal("take");
      LiteralArgumentBuilder literalArgumentBuilder2 = nexoCommand.withPermission$com_nexomc_core(literalArgumentBuilder, "nexo.command.take");
      NexoCommand nexoCommand2 = NexoCommand.INSTANCE;
      NexoCommand nexoCommand3 = NexoCommand.INSTANCE;
      RequiredArgumentBuilder requiredArgumentBuilder = Commands.argument("item", (ArgumentType)StringArgumentType.word());
      RequiredArgumentBuilder requiredArgumentBuilder2 = nexoCommand3.suggestAsync$com_nexomc_core(requiredArgumentBuilder, ItemCommandKt::takeItemCommand$lambda$1);
      RequiredArgumentBuilder requiredArgumentBuilder3 = nexoCommand2.executesSender$com_nexomc_core(requiredArgumentBuilder2, ((a0_, a1_) -> ItemCommandKt.takeItemCommand$lambda$2(takeLogic, a0_, a1_)));
      nexoCommand3 = NexoCommand.INSTANCE;
      requiredArgumentBuilder = Commands.argument("amount", (ArgumentType)IntegerArgumentType.integer(1));
      RequiredArgumentBuilder requiredArgumentBuilder4 = nexoCommand3.executesSender$com_nexomc_core(requiredArgumentBuilder, ((a0_, a1_) -> ItemCommandKt.takeItemCommand$lambda$3(takeLogic, a0_, a1_)));
      NexoCommand nexoCommand4 = NexoCommand.INSTANCE;
      RequiredArgumentBuilder requiredArgumentBuilder5 = Commands.argument("players", ArgumentTypes.players());
      requiredArgumentBuilder3 = (RequiredArgumentBuilder)requiredArgumentBuilder3.then(requiredArgumentBuilder4.then((ArgumentBuilder)nexoCommand4.executesSender$com_nexomc_core(requiredArgumentBuilder5, ((a0_, a1_) -> ItemCommandKt.takeItemCommand$lambda$4(takeLogic, a0_, a1_)))));
      NexoCommand nexoCommand5 = NexoCommand.INSTANCE;
      RequiredArgumentBuilder requiredArgumentBuilder6 = Commands.argument("players", ArgumentTypes.players());
      root.then(literalArgumentBuilder2.then(requiredArgumentBuilder3.then((ArgumentBuilder)nexoCommand5.executesSender$com_nexomc_core(requiredArgumentBuilder6, ((a0_, a1_) -> ItemCommandKt.takeItemCommand$lambda$5(takeLogic, a0_, a1_))))));
   }

   public static final void itemInfoCommand(@NotNull LiteralArgumentBuilder<CommandSourceStack> root) {
      NexoCommand nexoCommand = NexoCommand.INSTANCE;
      NexoCommand nexoCommand2 = NexoCommand.INSTANCE;
      LiteralArgumentBuilder literalArgumentBuilder = Commands.literal("iteminfo");
      LiteralArgumentBuilder literalArgumentBuilder2 = nexoCommand2.withPermission$com_nexomc_core(literalArgumentBuilder, "nexo.command.iteminfo");
      LiteralArgumentBuilder literalArgumentBuilder3 = nexoCommand.executesSender$com_nexomc_core(literalArgumentBuilder2, ItemCommandKt::itemInfoCommand$lambda$0);
      NexoCommand nexoCommand3 = NexoCommand.INSTANCE;
      NexoCommand nexoCommand4 = NexoCommand.INSTANCE;
      RequiredArgumentBuilder requiredArgumentBuilder = Commands.argument("itemid", (ArgumentType)StringArgumentType.word());
      RequiredArgumentBuilder requiredArgumentBuilder2 = nexoCommand4.suggestAsync$com_nexomc_core(requiredArgumentBuilder, ItemCommandKt::itemInfoCommand$lambda$1);
      root.then(literalArgumentBuilder3.then((ArgumentBuilder)nexoCommand3.executesSender$com_nexomc_core(requiredArgumentBuilder2, ItemCommandKt::itemInfoCommand$lambda$2)));
   }

   public static final void dumpItemCommand(@NotNull LiteralArgumentBuilder<CommandSourceStack> root) {
      Function3 dumpLogic = ((a0_, a1_, a2_) -> ItemCommandKt.dumpItemCommand$lambda$0((Player)a0_, (String)a1_, (CommandSender)a2_));
      NexoCommand nexoCommand = NexoCommand.INSTANCE;
      NexoCommand nexoCommand2 = NexoCommand.INSTANCE;
      LiteralArgumentBuilder literalArgumentBuilder = Commands.literal("dumpitem");
      LiteralArgumentBuilder literalArgumentBuilder2 = nexoCommand2.withPermission$com_nexomc_core(literalArgumentBuilder, "nexo.command.dumpitem");
      LiteralArgumentBuilder literalArgumentBuilder3 = nexoCommand.executesPlayer$com_nexomc_core(literalArgumentBuilder2, (a0_ -> ItemCommandKt.dumpItemCommand$lambda$1(dumpLogic, a0_)));
      NexoCommand nexoCommand3 = NexoCommand.INSTANCE;
      RequiredArgumentBuilder requiredArgumentBuilder = Commands.argument("itemId", (ArgumentType)StringArgumentType.word());
      literalArgumentBuilder3 = (LiteralArgumentBuilder)literalArgumentBuilder3.then(nexoCommand3.suggestAsync$com_nexomc_core(requiredArgumentBuilder, ItemCommandKt::dumpItemCommand$lambda$2).executes((a0_ -> ItemCommandKt.dumpItemCommand$lambda$3(dumpLogic, a0_))));
      nexoCommand3 = NexoCommand.INSTANCE;
      requiredArgumentBuilder = Commands.argument("players", ArgumentTypes.players());
      root.then(literalArgumentBuilder3.then((ArgumentBuilder)nexoCommand3.executesPlayer$com_nexomc_core(requiredArgumentBuilder, ((a0_, a1_) -> ItemCommandKt.dumpItemCommand$lambda$4(dumpLogic, a0_, a1_)))));
   }

   private static final int inventoryCommand$lambda$0(CommandContext ctx) {
      NexoCommand nexoCommand = NexoCommand.INSTANCE;
      String str = "target";
      int value = 0;
      Object argument = ctx.getArgument(str, PlayerSelectorArgumentResolver.class);
      argument = SneakyThrow.resolve((PlayerSelectorArgumentResolver)argument, (CommandSourceStack)ctx.getSource());
      Player player2 = (Player)CollectionsKt.firstOrNull((List)argument);
      if (player2 == null) {
         return 1;
      } else {
         Player player = player2;
         NexoPlugin.Companion.instance().invManager().itemsView(player).getGui().open((HumanEntity)player);
         return 1;
      }
   }

   private static final Unit inventoryCommand$lambda$1(Player it) {
      NexoPlugin.Companion.instance().invManager().itemsView(it).getGui().open((HumanEntity)it);
      return Unit.INSTANCE;
   }

   private static final Unit inventoryCommand$lambda$2(Player it) {
      NexoPlugin.Companion.instance().invManager().itemsView(it).getGui().open((HumanEntity)it);
      return Unit.INSTANCE;
   }

   private static final int dropItemCommand$lambda$0(CommandSender sender, Player[] targets, String itemId, int amount) {
      int value = 0;
      ItemBuilder itemBuilder2 = NexoItems.itemFromId(itemId);
      if (itemBuilder2 == null) {
         Message message = Message.ITEM_NOT_FOUND;
         TagResolver[] tagResolvers = new TagResolver[]{AdventureUtils.tagResolver("item", itemId)};
         message.send(sender, tagResolvers);
      } else {
         ItemBuilder itemBuilder = itemBuilder2;
         List items = ArraysKt.filterNotNull(itemBuilder.buildArray(amount));
         Object[] objs = targets;
         int value2 = 0;

         for(int i = targets.length; value2 < i; ++value2) {
            Object element = objs[value2];
            Player target = (Player)element;
            int value3 = 0;
            Iterable iterable = (Iterable)items;

            for(Object obj : iterable) {
               ItemStack item = (ItemStack)obj;
               int value4 = 0;
               target.getWorld().dropItemNaturally(target.getLocation(), item);
            }
         }
      }

      return 1;
   }

   private static final String[] dropItemCommand$lambda$1() {
      return NexoItems.unexcludedItemNames();
   }

   private static final Unit dropItemCommand$lambda$2(Function4 dropLogic, CommandSender sender, CommandContext ctx) {
      if (sender instanceof Player) {
         Player[] players = (new Player[]{(Player)sender});
         Player[] players2 = players;
         NexoCommand nexoCommand = NexoCommand.INSTANCE;
         String str = "item";
         boolean flag = false;
         Object argument = ctx.getArgument(str, String.class);
         dropLogic.invoke(sender, players2, argument, 1);
         return Unit.INSTANCE;
      } else {
         Message.NOT_PLAYER.send(sender);
         return Unit.INSTANCE;
      }
   }

   private static final Unit dropItemCommand$lambda$3(Function4 dropLogic, CommandSender sender, CommandContext ctx) {
      if (sender instanceof Player) {
         Player[] players2 = (new Player[]{(Player)sender});
         Player[] players = players2;
         NexoCommand nexoCommand = NexoCommand.INSTANCE;
         String str2 = "item";
         boolean flag = false;
         Object argument = ctx.getArgument(str2, String.class);
         String str = (String)argument;
         NexoCommand nexoCommand2 = NexoCommand.INSTANCE;
         String str3 = "amount";
         boolean flag2 = false;
         argument = ctx.getArgument(str3, Integer.class);
         int amount = ((Number)argument).intValue();
         dropLogic.invoke(sender, players, str, amount);
         return Unit.INSTANCE;
      } else {
         Message.NOT_PLAYER.send(sender);
         return Unit.INSTANCE;
      }
   }

   private static final Unit dropItemCommand$lambda$4(Function4 dropLogic, CommandSender sender, CommandContext ctx) {
      NexoCommand nexoCommand = NexoCommand.INSTANCE;
      String str = "players";
      int value = 0;
      Object argument = ctx.getArgument(str, PlayerSelectorArgumentResolver.class);
      argument = SneakyThrow.resolve((PlayerSelectorArgumentResolver)argument, (CommandSourceStack)ctx.getSource());
      Collection collection = (Collection)argument;
      Player[] players = (Player[])collection.toArray(new Player[0]);
      NexoCommand nexoCommand2 = NexoCommand.INSTANCE;
      str = "item";
      value = 0;
      argument = ctx.getArgument(str, String.class);
      String itemId = (String)argument;
      NexoCommand nexoCommand3 = NexoCommand.INSTANCE;
      String str2 = "amount";
      int value2 = 0;
      argument = ctx.getArgument(str2, Integer.class);
      int amount = ((Number)argument).intValue();
      dropLogic.invoke(sender, players, itemId, amount);
      return Unit.INSTANCE;
   }

   private static final int giveItemCommand$lambda$0(CommandSender sender, Player[] targets, String itemId, int amount) {
      int value = 0;
      ItemBuilder itemBuilder2 = NexoItems.itemFromId(itemId);
      if (itemBuilder2 == null) {
         Message message = Message.ITEM_NOT_FOUND;
         TagResolver[] tagResolvers = new TagResolver[]{AdventureUtils.tagResolver("item", itemId)};
         message.send(sender, tagResolvers);
      } else {
         ItemBuilder itemBuilder = itemBuilder2;
         int amount_16 = amount;
         Integer maxStackSize = itemBuilder.getMaxStackSize();
         int maxAmount = maxStackSize != null ? maxStackSize : itemBuilder.getType().getMaxStackSize();
         int slots = amount_16 / maxAmount + (maxAmount % amount_16 > 0 ? 1 : 0);
         ItemBuilder itemBuilder3 = itemBuilder;
         int value2;
         if (slots > 36) {
            int value3 = maxAmount * 36;
            int value4 = 0;
            amount_16 = value3;
            itemBuilder3 = itemBuilder;
            value2 = value3;
         } else {
            value2 = amount_16;
         }

         List items = ArraysKt.filterNotNull(itemBuilder3.buildArray(value2));
         Object[] objs = targets;
         int value5 = 0;

         for(int i = targets.length; value5 < i; ++value5) {
            Object element = objs[value5];
            Player it = (Player)element;
            int value6 = 0;
            PlayerInventory inventory = ((Player)element).getInventory();
            Collection collection2 = (Collection)items;
            ItemStack[] itemStacks = (ItemStack[])collection2.toArray(new ItemStack[0]);
            HashMap hashMap = inventory.addItem(Arrays.copyOf(itemStacks, itemStacks.length));
            HashMap output = hashMap;
            Collection collection = output.values();
            Iterable iterable = (Iterable)collection;

            for(Object obj : iterable) {
               ItemStack stack = (ItemStack)obj;
               int value7 = 0;
               it.getWorld().dropItem(it.getLocation(), stack);
            }
         }

         if (targets.length == 1) {
            Message message2 = Message.GIVE_PLAYER;
            TagResolver[] tagResolvers3 = new TagResolver[3];
            String name = (ArrayIteratorKt.iterator(targets).next()).getName();
            tagResolvers3[0] = AdventureUtils.tagResolver("player", name);
            tagResolvers3[1] = AdventureUtils.tagResolver("amount", String.valueOf(amount_16));
            tagResolvers3[2] = AdventureUtils.tagResolver("item", itemId);
            message2.send(sender, tagResolvers3);
         } else {
            Message message3 = Message.GIVE_PLAYERS;
            TagResolver[] tagResolvers2 = new TagResolver[]{AdventureUtils.tagResolver("count", String.valueOf(targets.length)), AdventureUtils.tagResolver("amount", String.valueOf(amount_16)), AdventureUtils.tagResolver("item", itemId)};
            message3.send(sender, tagResolvers2);
         }
      }

      return 1;
   }

   private static final String[] giveItemCommand$lambda$1() {
      return NexoItems.unexcludedItemNames();
   }

   private static final Unit giveItemCommand$lambda$2(Function4 giveLogic, Player player, CommandContext ctx) {
      Player[] players = new Player[]{player};
      Player[] players2 = players;
      NexoCommand nexoCommand = NexoCommand.INSTANCE;
      String str = "item";
      int value = 0;
      Object argument = ctx.getArgument(str, String.class);
      giveLogic.invoke(player, players2, argument, 1);
      return Unit.INSTANCE;
   }

   private static final Unit giveItemCommand$lambda$3(Function4 giveLogic, Player player, CommandContext ctx) {
      NexoCommand nexoCommand = NexoCommand.INSTANCE;
      String str = "item";
      int value = 0;
      Object argument = ctx.getArgument(str, String.class);
      String itemId = (String)argument;
      NexoCommand nexoCommand2 = NexoCommand.INSTANCE;
      String str2 = "amount";
      int value2 = 0;
      argument = ctx.getArgument(str2, Integer.class);
      int amount = ((Number)argument).intValue();
      Player[] players = new Player[]{player};
      giveLogic.invoke(player, players, itemId, amount);
      return Unit.INSTANCE;
   }

   private static final Unit giveItemCommand$lambda$4(Function4 giveLogic, CommandSender sender, CommandContext ctx) {
      NexoCommand nexoCommand = NexoCommand.INSTANCE;
      String str = "players";
      int value = 0;
      Object argument = ctx.getArgument(str, PlayerSelectorArgumentResolver.class);
      argument = SneakyThrow.resolve((PlayerSelectorArgumentResolver)argument, (CommandSourceStack)ctx.getSource());
      List players = (List)argument;
      nexoCommand = NexoCommand.INSTANCE;
      str = "item";
      value = 0;
      argument = ctx.getArgument(str, String.class);
      String itemId = (String)argument;
      NexoCommand nexoCommand2 = NexoCommand.INSTANCE;
      String str2 = "amount";
      int value2 = 0;
      argument = ctx.getArgument(str2, Integer.class);
      int amount = ((Number)argument).intValue();
      Collection collection = (Collection)players;
      giveLogic.invoke(sender, collection.toArray(new Player[0]), itemId, amount);
      return Unit.INSTANCE;
   }

   private static final boolean takeItemCommand$lambda$0$0$0$2(String itemId, ItemStack it) {
      return !it.isEmpty() && Intrinsics.areEqual(itemId, NexoItems.idFromItem(it));
   }

   private static final int takeItemCommand$lambda$0(CommandSender sender, Player[] targets, String itemId, Integer amount) {
      int value = 0;
      if (!NexoItems.exists(itemId)) {
         Message message = Message.ITEM_NOT_FOUND;
         TagResolver[] tagResolvers = new TagResolver[]{AdventureUtils.tagResolver("item", itemId)};
         message.send(sender, tagResolvers);
      } else {
         Object[] objs = targets;
         int value2 = 0;
         int value3 = targets.length;

         while(true) {
            if (value2 >= value3) {
               break;
            }

            Object element = objs[value2];
            Player target = (Player)element;
            int value4 = 0;
            if (amount == null) {
               ItemStack[] contents = ((Player)element).getInventory().getContents();
               Iterable iterable = (Iterable)ArraysKt.filterNotNull(contents);
               int value8 = 0;
               Collection destination = (Collection)(new ArrayList());

               for(Object obj : iterable) {
                  ItemStack it = (ItemStack)obj;
                  int value5 = 0;
                  if (!it.isEmpty() && Intrinsics.areEqual(itemId, NexoItems.idFromItem(it))) {
                     destination.add(obj);
                  }
               }

               iterable = (Iterable)((List)destination);
               value8 = 0;

               for(Object obj2 : iterable) {
                  ItemStack it = (ItemStack)obj2;
                  int value6 = 0;
                  target.getInventory().remove(it);
               }
            } else {
               int toRemove = 0;
               toRemove = RangesKt.coerceAtLeast((int)(amount), 0);
               if (toRemove == 0) {
                  break;
               }

               while(toRemove > 0) {
                  ItemStack[] itemStacks = target.getInventory().getContents();
                  Sequence sequence = SequencesKt.filter(SequencesKt.filterNotNull(ArraysKt.asSequence(itemStacks)), (a0_ -> ItemCommandKt.takeItemCommand$lambda$0$0$0$2(itemId, a0_)));

                  for(Object obj3 : kotlin.sequences.SequencesKt.asIterable(sequence)) {
                     ItemStack item = (ItemStack)obj3;
                     int value9 = 0;
                     if (toRemove != 0) {
                        int it = item.getAmount();
                        int value7 = 0;
                        item.subtract(toRemove);
                        Unit unit = Unit.INSTANCE;
                        toRemove -= it;
                        if (toRemove < 0) {
                           toRemove = 0;
                        }
                     }
                  }

                  if (toRemove > 0) {
                     break;
                  }
               }
            }

            ++value2;
         }
      }

      return 1;
   }

   private static final String[] takeItemCommand$lambda$1() {
      return NexoItems.unexcludedItemNames();
   }

   private static final Unit takeItemCommand$lambda$2(Function4 takeLogic, CommandSender sender, CommandContext ctx) {
      if (!(sender instanceof Player)) {
         return Unit.INSTANCE;
      } else {
         NexoCommand nexoCommand = NexoCommand.INSTANCE;
         String str = "item";
         int value = 0;
         Object argument = ctx.getArgument(str, String.class);
         String itemId = (String)argument;
         Player[] players = (new Player[]{(Player)sender});
         takeLogic.invoke(sender, players, itemId, (Object)null);
         return Unit.INSTANCE;
      }
   }

   private static final Unit takeItemCommand$lambda$3(Function4 takeLogic, CommandSender sender, CommandContext ctx) {
      if (!(sender instanceof Player)) {
         return Unit.INSTANCE;
      } else {
         NexoCommand nexoCommand = NexoCommand.INSTANCE;
         String str = "item";
         int value = 0;
         Object argument = ctx.getArgument(str, String.class);
         String itemId = (String)argument;
         NexoCommand nexoCommand2 = NexoCommand.INSTANCE;
         String str2 = "amount";
         int value2 = 0;
         argument = ctx.getArgument(str2, Integer.class);
         int amount = ((Number)argument).intValue();
         Player[] players = new Player[]{(Player)sender};
         takeLogic.invoke(sender, players, itemId, amount);
         return Unit.INSTANCE;
      }
   }

   private static final Unit takeItemCommand$lambda$4(Function4 takeLogic, CommandSender sender, CommandContext ctx) {
      NexoCommand nexoCommand = NexoCommand.INSTANCE;
      String str = "players";
      int value = 0;
      Object argument = ctx.getArgument(str, PlayerSelectorArgumentResolver.class);
      argument = SneakyThrow.resolve((PlayerSelectorArgumentResolver)argument, (CommandSourceStack)ctx.getSource());
      List players = (List)argument;
      nexoCommand = NexoCommand.INSTANCE;
      str = "item";
      value = 0;
      argument = ctx.getArgument(str, String.class);
      String itemId = (String)argument;
      NexoCommand nexoCommand2 = NexoCommand.INSTANCE;
      String str2 = "amount";
      int value2 = 0;
      argument = ctx.getArgument(str2, Integer.class);
      int amount = ((Number)argument).intValue();
      Collection collection = (Collection)players;
      takeLogic.invoke(sender, collection.toArray(new Player[0]), itemId, amount);
      return Unit.INSTANCE;
   }

   private static final Unit takeItemCommand$lambda$5(Function4 takeLogic, CommandSender sender, CommandContext ctx) {
      NexoCommand nexoCommand = NexoCommand.INSTANCE;
      String str = "players";
      int value = 0;
      Object argument = ctx.getArgument(str, PlayerSelectorArgumentResolver.class);
      argument = SneakyThrow.resolve((PlayerSelectorArgumentResolver)argument, (CommandSourceStack)ctx.getSource());
      List players = (List)argument;
      nexoCommand = NexoCommand.INSTANCE;
      str = "item";
      value = 0;
      argument = ctx.getArgument(str, String.class);
      String itemId = (String)argument;
      Collection collection = (Collection)players;
      takeLogic.invoke(sender, collection.toArray(new Player[0]), itemId, (Object)null);
      return Unit.INSTANCE;
   }

   private static final Unit itemInfoCommand$lambda$0(CommandSender sender) {
      ItemStack itemStack;
      block: {
         Player player = sender instanceof Player ? (Player)sender : null;
         if ((sender instanceof Player ? (Player)sender : null) != null) {
            PlayerInventory inventory = player.getInventory();
            if (inventory != null) {
               itemStack = inventory.getItemInMainHand();
               break block;
            }
         }

         itemStack = null;
      }

      ItemStack heldItem = itemStack;
      String str = NexoItems.idFromItem(heldItem);
      if (str == null) {
         return Unit.INSTANCE;
      } else {
         String itemId = str;
         ItemBuilder itemBuilder = NexoItems.builderFromItem(heldItem);
         if (itemBuilder == null) {
            return Unit.INSTANCE;
         } else {
            ItemBuilder builder = itemBuilder;
            ItemCommand.INSTANCE.sendItemInfo$com_nexomc_core((Audience)sender, builder, itemId);
            return Unit.INSTANCE;
         }
      }
   }

   private static final String[] itemInfoCommand$lambda$1() {
      return NexoItems.unexcludedItemNames();
   }

   private static final Unit itemInfoCommand$lambda$2(CommandSender sender, CommandContext ctx) {
      NexoCommand nexoCommand = NexoCommand.INSTANCE;
      String str = "itemid";
      int value = 0;
      Object argument = ctx.getArgument(str, String.class);
      String itemId = (String)argument;
      ItemBuilder itemBuilder = NexoItems.itemFromId(itemId);
      if (itemBuilder == null) {
         sender.sendRichMessage("<red>No item found with ID</red> <dark_red>" + itemId);
         return Unit.INSTANCE;
      } else {
         ItemBuilder item = itemBuilder;
         ItemCommand.INSTANCE.sendItemInfo$com_nexomc_core((Audience)sender, item, itemId);
         return Unit.INSTANCE;
      }
   }

   private static final int dumpItemCommand$lambda$0(Player player, String itemId, CommandSender sender) {
      ItemStack itemStack;
      block: {
         if (player != null) {
            PlayerInventory inventory = player.getInventory();
            if (inventory != null) {
               ItemStack itemInMainHand = inventory.getItemInMainHand();
               if (itemInMainHand != null) {
                  ItemStack p0 = itemInMainHand;
                  int value = 0;
                  itemStack = !p0.isEmpty() ? p0 : null;
                  if (itemStack != null) {
                     break block;
                  }
               }
            }
         }

         block2: {
            if (player != null) {
               PlayerInventory playerInventory = player.getInventory();
               if (playerInventory != null) {
                  ItemStack itemInOffHand = playerInventory.getItemInOffHand();
                  if (itemInOffHand != null) {
                     ItemStack p0 = itemInOffHand;
                     int value2 = 0;
                     itemStack = !p0.isEmpty() ? p0 : null;
                     break block2;
                  }
               }
            }

            itemStack = null;
         }

         if (itemStack == null) {
            block2: {
               ItemBuilder itemBuilder = NexoItems.itemFromId(itemId);
               if (itemBuilder != null) {
                  ItemStack itemStack2 = itemBuilder.build();
                  if (itemStack2 != null) {
                     ItemStack p0 = itemStack2;
                     ItemUpdater.Companion companion = ItemUpdater.Companion;
                     int value3 = 0;
                     itemStack = companion.updateItem(p0);
                     break block2;
                  }
               }

               itemStack = null;
            }

            if (itemStack == null) {
               return 1;
            }
         }
      }

      ItemStack item = itemStack;
      NMSHandlers.handler().itemUtils().dumpItem(sender, item, false);
      return 1;
   }

   private static final Unit dumpItemCommand$lambda$1(Function3 dumpLogic, Player it) {
      dumpLogic.invoke(it, (Object)null, it);
      return Unit.INSTANCE;
   }

   private static final String[] dumpItemCommand$lambda$2() {
      return NexoItems.unexcludedItemNames();
   }

   private static final int dumpItemCommand$lambda$3(Function3 dumpLogic, CommandContext ctx) {
      NexoCommand nexoCommand = NexoCommand.INSTANCE;
      String str = "itemId";
      int value = 0;
      Object argument = ctx.getArgument(str, String.class);
      CommandSender sender = ((CommandSourceStack)ctx.getSource()).getSender();
      return ((Number)dumpLogic.invoke((Object)null, argument, sender)).intValue();
   }

   private static final Unit dumpItemCommand$lambda$4(Function3 dumpLogic, Player sender, CommandContext ctx) {
      NexoCommand nexoCommand = NexoCommand.INSTANCE;
      String str = "players";
      int value = 0;
      Object argument = ctx.getArgument(str, PlayerSelectorArgumentResolver.class);
      argument = SneakyThrow.resolve((PlayerSelectorArgumentResolver)argument, (CommandSourceStack)ctx.getSource());

      for(Object var10000_it : (List)argument) {

         argument = var10000_it;
         Player player = (Player)argument;
         CommandSender commandSender = ((CommandSourceStack)ctx.getSource()).getSender();
         dumpLogic.invoke(player, (Object)null, commandSender);
      }

      return Unit.INSTANCE;
   }
}
