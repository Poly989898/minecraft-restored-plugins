package com.nexomc.nexo.commands;

import com.nexomc.nexo.decomp.ResultShim;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.glyphs.Glyph;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.event.HoverEventSource;
import net.kyori.adventure.text.event.ClickEvent.Action;
import net.kyori.adventure.text.event.ClickEvent.Payload;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 2,
   xi = 48,
   d1 = {"\u0000\u0012\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\u001a\u0016\u0010\u0000\u001a\u00020\u00012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u0000¨\u0006\u0005"},
   d2 = {"glyphInfoCommand", "", "root", "Lcom/mojang/brigadier/builder/LiteralArgumentBuilder;", "Lio/papermc/paper/command/brigadier/CommandSourceStack;", "com.nexomc:core"}
)
public final class GlyphInfoCommandKt {
   public static final void glyphInfoCommand(@NotNull LiteralArgumentBuilder<CommandSourceStack> root) {
      NexoCommand nexoCommand = NexoCommand.INSTANCE;
      LiteralArgumentBuilder literalArgumentBuilder = Commands.literal("glyphinfo");
      LiteralArgumentBuilder literalArgumentBuilder2 = nexoCommand.withPermission$com_nexomc_core(literalArgumentBuilder, "nexo.command.glyphinfo");
      NexoCommand nexoCommand2 = NexoCommand.INSTANCE;
      NexoCommand nexoCommand3 = NexoCommand.INSTANCE;
      RequiredArgumentBuilder requiredArgumentBuilder = Commands.argument("glyphid", (ArgumentType)StringArgumentType.word());
      RequiredArgumentBuilder requiredArgumentBuilder2 = nexoCommand3.suggestAsync$com_nexomc_core(requiredArgumentBuilder, GlyphInfoCommandKt::glyphInfoCommand$lambda$0);
      root.then(literalArgumentBuilder2.then((ArgumentBuilder)nexoCommand2.executesSender$com_nexomc_core(requiredArgumentBuilder2, GlyphInfoCommandKt::glyphInfoCommand$lambda$1)));
   }

   private static final String[] glyphInfoCommand$lambda$0() {
      Iterable glyphs = (Iterable)NexoPlugin.Companion.instance().fontManager().glyphs();
      Object[] result = new String[CollectionsKt.count(glyphs)];
      int index = 0;

      for(Object item : glyphs) {
         int value = index++;
         Glyph it = (Glyph)item;
         int value2 = value;
         int value3 = 0;
         String id = it.getId();
         result[value2] = id;
      }

      return (String[])(result);
   }

   private static final Unit glyphInfoCommand$lambda$1(CommandSender sender, CommandContext ctx) {
      NexoCommand nexoCommand = NexoCommand.INSTANCE;
      String str = "glyphid";
      int value3 = 0;
      Object argument = ctx.getArgument(str, String.class);
      String glyphId = (String)argument;
      Glyph glyph2 = NexoPlugin.Companion.instance().fontManager().glyphFromID(glyphId);
      if (glyph2 == null) {
         sender.sendRichMessage("<red>No glyph found with glyph-id <i><dark_red>" + glyphId);
         return Unit.INSTANCE;
      } else {
         Glyph glyph = glyph2;
         sender.sendRichMessage("<dark_aqua>GlyphID: <aqua>" + glyphId);
         sender.sendRichMessage("<dark_aqua>Texture: <aqua>" + glyph.getTexture().asString());
         sender.sendRichMessage("<dark_aqua>Font: <aqua>" + glyph.getFont().asString());
         String unicode = CollectionsKt.joinToString((Iterable)glyph.getUnicodes(), (CharSequence)"", (CharSequence)"", (CharSequence)"", -1, (CharSequence)"...", (Function1)null);

         Object obj;
         try {
            int value = 0;
            obj = ResultShim.ok(ClickEvent.copyToClipboard(unicode));
         } catch (Throwable e) {
            obj = ResultShim.ok(ResultKt.createFailure(e));
         }

         Object obj2;
         if (ResultShim.exceptionOrNull(obj) == null) {
            obj2 = obj;
         } else {
            int value2 = 0;
            obj2 = ClickEvent.copyToClipboard(unicode);
         }

         ClickEvent clickEvent = (ClickEvent)obj2;
         sender.sendMessage(Component.text("Unicode:", (TextColor)NamedTextColor.DARK_AQUA).append(glyph.getComponent().color((TextColor)NamedTextColor.WHITE).hoverEvent((HoverEventSource)HoverEvent.showText((Component)Component.text("Click to copy to clipboard!", (TextColor)NamedTextColor.GOLD))).clickEvent(clickEvent)));
         return Unit.INSTANCE;
      }
   }
}
