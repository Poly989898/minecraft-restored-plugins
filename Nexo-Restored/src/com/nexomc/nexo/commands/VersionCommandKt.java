package com.nexomc.nexo.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.configs.Message;
import com.nexomc.nexo.utils.AdventureUtils;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import kotlin.Metadata;
import kotlin.Unit;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 2,
   xi = 48,
   d1 = {"\u0000\u0012\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\u001a\u0016\u0010\u0000\u001a\u00020\u00012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u0000¨\u0006\u0005"},
   d2 = {"versionCommand", "", "root", "Lcom/mojang/brigadier/builder/LiteralArgumentBuilder;", "Lio/papermc/paper/command/brigadier/CommandSourceStack;", "com.nexomc:core"}
)
public final class VersionCommandKt {
   public static final void versionCommand(@NotNull LiteralArgumentBuilder<CommandSourceStack> root) {
      NexoCommand nexoCommand = NexoCommand.INSTANCE;
      NexoCommand nexoCommand2 = NexoCommand.INSTANCE;
      LiteralArgumentBuilder literalArgumentBuilder = Commands.literal("version");
      LiteralArgumentBuilder literalArgumentBuilder2 = nexoCommand2.withPermission$com_nexomc_core(literalArgumentBuilder, "nexo.command.version");
      root.then((ArgumentBuilder)nexoCommand.executesSender$com_nexomc_core(literalArgumentBuilder2, VersionCommandKt::versionCommand$lambda$0));
   }

   private static final Unit versionCommand$lambda$0(CommandSender sender) {
      Message message = Message.VERSION;
      TagResolver[] tagResolvers = new TagResolver[1];
      String version = NexoPlugin.Companion.instance().getPluginMeta().getVersion();
      tagResolvers[0] = AdventureUtils.tagResolver("version", version);
      message.send(sender, tagResolvers);
      return Unit.INSTANCE;
   }
}
