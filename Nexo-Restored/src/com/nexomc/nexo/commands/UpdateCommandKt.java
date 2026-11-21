package com.nexomc.nexo.commands;

import com.nexomc.nexo.utils.SneakyThrow;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.nexomc.nexo.api.NexoFurniture;
import com.nexomc.nexo.configs.Message;
import com.nexomc.nexo.items.ItemUpdater;
import com.nexomc.nexo.utils.AdventureUtils;
import com.nexomc.nexo.utils.AdventureUtilsKt;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.IntIterator;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.ranges.IntRange;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 2,
   xi = 48,
   d1 = {"\u0000\u0012\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\u001a\u0016\u0010\u0000\u001a\u00020\u00012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u0000¨\u0006\u0005"},
   d2 = {"updateCommand", "", "root", "Lcom/mojang/brigadier/builder/LiteralArgumentBuilder;", "Lio/papermc/paper/command/brigadier/CommandSourceStack;", "com.nexomc:core"}
)
public final class UpdateCommandKt {
   public static final void updateCommand(@NotNull LiteralArgumentBuilder<CommandSourceStack> root) {
      Function1 itemUpdateLogic = (a0_ -> UpdateCommandKt.updateCommand$lambda$0((Player)a0_));
      Function2 furnitureLogic = ((a0_, a1_) -> UpdateCommandKt.updateCommand$lambda$1((Player)a0_, (Double)a1_));
      NexoCommand nexoCommand = NexoCommand.INSTANCE;
      LiteralArgumentBuilder literalArgumentBuilder = Commands.literal("update");
      LiteralArgumentBuilder literalArgumentBuilder2 = nexoCommand.withPermission$com_nexomc_core(literalArgumentBuilder, "nexo.command.update");
      NexoCommand nexoCommand2 = NexoCommand.INSTANCE;
      LiteralArgumentBuilder literalArgumentBuilder3 = Commands.literal("item");
      literalArgumentBuilder2 = (LiteralArgumentBuilder)literalArgumentBuilder2.then(nexoCommand2.executesSender$com_nexomc_core(literalArgumentBuilder3, (a0_ -> UpdateCommandKt.updateCommand$lambda$2(itemUpdateLogic, a0_))).then(Commands.argument("players", ArgumentTypes.players()).executes((a0_ -> UpdateCommandKt.updateCommand$lambda$3(itemUpdateLogic, (CommandContext)a0_)))));
      nexoCommand2 = NexoCommand.INSTANCE;
      literalArgumentBuilder3 = Commands.literal("furniture");
      root.then(literalArgumentBuilder2.then(nexoCommand2.executesSender$com_nexomc_core(literalArgumentBuilder3, (a0_ -> UpdateCommandKt.updateCommand$lambda$4(furnitureLogic, a0_))).then(Commands.argument("radius", (ArgumentType)DoubleArgumentType.doubleArg((double)0.0F)).executes((a0_ -> UpdateCommandKt.updateCommand$lambda$5(furnitureLogic, a0_))))));
   }

   private static final Unit updateCommand$lambda$0(Player player) {
      int updated = 0;
      Iterable iterable = (Iterable)(new IntRange(0, player.getInventory().getSize()));
      Iterator iterator = iterable.iterator();

      while(iterator.hasNext()) {
         int element = ((IntIterator)iterator).nextInt();
         int value = 0;
         ItemStack item = player.getInventory().getItem(element);
         if (item != null) {
            ItemStack oldItem = item;
            ItemStack p0 = ItemUpdater.Companion.updateItem(oldItem);
            int value2 = 0;
            item = !oldItem.equals(p0) ? p0 : null;
            if (item != null) {
               ItemStack newItem = item;
               player.getInventory().setItem(element, newItem);
               ++updated;
            }
         }
      }

      player.updateInventory();
      Message message = Message.UPDATED_ITEMS;
      CommandSender commandSender = (CommandSender)player;
      TagResolver[] tagResolvers = new TagResolver[]{AdventureUtils.tagResolver("amount", String.valueOf(updated)), null};
      Component displayName = player.displayName();
      tagResolvers[1] = AdventureUtils.tagResolver("player", AdventureUtilsKt.serialize(displayName));
      message.send(commandSender, tagResolvers);
      return Unit.INSTANCE;
   }

   private static final Unit updateCommand$lambda$1(Player sender, double radius) {
      List nearbyEntities = sender.getNearbyEntities(radius, radius, radius);
      Iterable iterable = (Iterable)nearbyEntities;
      Collection destination = (Collection)(new ArrayList());

      for(Object element : iterable) {
         if (element instanceof ItemDisplay) {
            destination.add(element);
         }
      }

      iterable = (Iterable)((List)destination);
      NexoFurniture nexoFurniture = NexoFurniture.INSTANCE;

      for(Object obj : iterable) {
         ItemDisplay p0 = (ItemDisplay)obj;
         int value = 0;
         NexoFurniture.updateFurniture(p0);
      }

      return Unit.INSTANCE;
   }

   private static final Unit updateCommand$lambda$2(Function1 itemUpdateLogic, CommandSender sender) {
      if (sender instanceof Player) {
         itemUpdateLogic.invoke(sender);
      } else {
         Message.NOT_PLAYER.send(sender);
      }

      return Unit.INSTANCE;
   }

   private static final int updateCommand$lambda$3(Function1 itemUpdateLogic, CommandContext it) {
      Object obj = SneakyThrow.resolve((PlayerSelectorArgumentResolver)it.getArgument("players", PlayerSelectorArgumentResolver.class), (CommandSourceStack)it.getSource());
      List players = (List)obj;
      Iterable iterable = (Iterable)players;

      for(Object element : iterable) {
         itemUpdateLogic.invoke(element);
      }

      return 1;
   }

   private static final Unit updateCommand$lambda$4(Function2 furnitureLogic, CommandSender it) {
      if (it instanceof Player) {
         furnitureLogic.invoke(it, (double)10.0F);
      }

      return Unit.INSTANCE;
   }

   private static final int updateCommand$lambda$5(Function2 furnitureLogic, CommandContext it) {
      CommandSender sender = ((CommandSourceStack)it.getSource()).getSender();
      Player player2 = sender instanceof Player ? (Player)sender : null;
      if ((sender instanceof Player ? (Player)sender : null) == null) {
         return 1;
      } else {
         Player player = player2;
         Double radius = (Double)it.getArgument("radius", Double.TYPE);
         furnitureLogic.invoke(player, radius);
         return 1;
      }
   }
}
