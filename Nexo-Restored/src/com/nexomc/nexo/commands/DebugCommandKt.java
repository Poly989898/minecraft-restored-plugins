package com.nexomc.nexo.commands;

import com.nexomc.nexo.decomp.ResultShim;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.nexomc.nexo.configs.Message;
import com.nexomc.nexo.configs.Settings;
import com.nexomc.nexo.utils.AdventureUtils;
import com.nexomc.nexo.utils.UtilsKt;
import com.nexomc.protectionlib.ProtectionLib;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 2,
   xi = 48,
   d1 = {"\u0000\u0012\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\u001a\u0016\u0010\u0000\u001a\u00020\u00012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u0000¨\u0006\u0005"},
   d2 = {"debugCommand", "", "root", "Lcom/mojang/brigadier/builder/LiteralArgumentBuilder;", "Lio/papermc/paper/command/brigadier/CommandSourceStack;", "com.nexomc:core"}
)
public final class DebugCommandKt {
   public static final void debugCommand(@NotNull LiteralArgumentBuilder<CommandSourceStack> root) {
      NexoCommand nexoCommand = NexoCommand.INSTANCE;
      NexoCommand nexoCommand2 = NexoCommand.INSTANCE;
      LiteralArgumentBuilder literalArgumentBuilder = Commands.literal("debug");
      LiteralArgumentBuilder literalArgumentBuilder2 = nexoCommand2.withPermission$com_nexomc_core(literalArgumentBuilder, "nexo.command.debug");
      root.then((ArgumentBuilder)nexoCommand.executesSender$com_nexomc_core(literalArgumentBuilder2, DebugCommandKt::debugCommand$lambda$0));
   }

   private static final Unit debugCommand$lambda$0(CommandSender sender) {
      boolean debugState = !Settings.DEBUG.toBool();
      Settings.Companion.settings().set("debug", debugState);

      Object obj;
      try {
         int value = 0;
         Settings.Companion.save();
         ProtectionLib.debug = debugState;
         Message message = Message.DEBUG_TOGGLE;
         TagResolver[] tagResolvers = new TagResolver[]{AdventureUtils.tagResolver("state", debugState ? "enabled" : "disabled")};
         message.send(sender, tagResolvers);
         obj = ResultShim.ok(Unit.INSTANCE);
      } catch (Throwable e) {
         obj = ResultShim.ok(ResultKt.createFailure(e));
      }

      UtilsKt.printOnFailure(obj, false);
      return Unit.INSTANCE;
   }
}
