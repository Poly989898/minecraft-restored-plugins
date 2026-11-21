package com.nexomc.nexo.commands;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.nexomc.nexo.api.NexoBlocks;
import com.nexomc.nexo.api.NexoItems;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import kotlin.Metadata;
import kotlin.Unit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 2,
   xi = 48,
   d1 = {"\u0000\u0012\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\u001a\u0016\u0010\u0000\u001a\u00020\u00012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u0000¨\u0006\u0005"},
   d2 = {"blockInfoCommand", "", "root", "Lcom/mojang/brigadier/builder/LiteralArgumentBuilder;", "Lio/papermc/paper/command/brigadier/CommandSourceStack;", "com.nexomc:core"}
)
public final class BlockInfoCommandKt {
   public static final void blockInfoCommand(@NotNull LiteralArgumentBuilder<CommandSourceStack> root) {
      NexoCommand nexoCommand = NexoCommand.INSTANCE;
      NexoCommand nexoCommand2 = NexoCommand.INSTANCE;
      LiteralArgumentBuilder literalArgumentBuilder = Commands.literal("blockinfo");
      LiteralArgumentBuilder literalArgumentBuilder2 = nexoCommand2.withPermission$com_nexomc_core(literalArgumentBuilder, "nexo.command.blockinfo");
      LiteralArgumentBuilder literalArgumentBuilder3 = nexoCommand.executesSender$com_nexomc_core(literalArgumentBuilder2, BlockInfoCommandKt::blockInfoCommand$lambda$0);
      NexoCommand nexoCommand3 = NexoCommand.INSTANCE;
      NexoCommand nexoCommand4 = NexoCommand.INSTANCE;
      RequiredArgumentBuilder requiredArgumentBuilder = Commands.argument("itemid", (ArgumentType)StringArgumentType.word());
      RequiredArgumentBuilder requiredArgumentBuilder2 = nexoCommand4.suggestAsync$com_nexomc_core(requiredArgumentBuilder, BlockInfoCommandKt::blockInfoCommand$lambda$1);
      root.then(literalArgumentBuilder3.then((ArgumentBuilder)nexoCommand3.executesSender$com_nexomc_core(requiredArgumentBuilder2, BlockInfoCommandKt::blockInfoCommand$lambda$2)));
   }

   private static final Unit blockInfoCommand$lambda$0(CommandSender sender) {
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
         BlockInfoCommand.INSTANCE.sendBlockInfo$com_nexomc_core(sender, itemId);
         return Unit.INSTANCE;
      }
   }

   private static final String[] blockInfoCommand$lambda$1() {
      return NexoBlocks.blockIDs();
   }

   private static final Unit blockInfoCommand$lambda$2(CommandSender sender, CommandContext ctx) {
      NexoCommand nexoCommand = NexoCommand.INSTANCE;
      String str = "itemid";
      int value = 0;
      Object argument = ctx.getArgument(str, String.class);
      String itemId = (String)argument;
      if (!NexoItems.exists(itemId)) {
         sender.sendRichMessage("<red>No block found with ID</red> <dark_red>" + itemId);
      } else {
         BlockInfoCommand.INSTANCE.sendBlockInfo$com_nexomc_core(sender, itemId);
      }

      return Unit.INSTANCE;
   }
}
