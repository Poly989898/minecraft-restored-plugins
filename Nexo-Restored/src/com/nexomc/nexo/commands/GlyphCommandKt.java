package com.nexomc.nexo.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.configs.Message;
import com.nexomc.nexo.configs.Settings;
import com.nexomc.nexo.glyphs.Glyph;
import com.nexomc.nexo.glyphs.GlyphShadow;
import com.nexomc.nexo.utils.AdventureUtilsKt;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import kotlin.ranges.IntRange;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.event.HoverEventSource;
import net.kyori.adventure.text.event.HoverEvent.Action;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 2,
   xi = 48,
   d1 = {"\u0000\u0012\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\u001a\u0016\u0010\u0000\u001a\u00020\u00012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u0000¨\u0006\u0005"},
   d2 = {"emojiCommand", "", "root", "Lcom/mojang/brigadier/builder/LiteralArgumentBuilder;", "Lio/papermc/paper/command/brigadier/CommandSourceStack;", "com.nexomc:core"}
)
public final class GlyphCommandKt {
   public static final void emojiCommand(@NotNull LiteralArgumentBuilder<CommandSourceStack> root) {
      NexoCommand nexoCommand = NexoCommand.INSTANCE;
      NexoCommand nexoCommand2 = NexoCommand.INSTANCE;
      LiteralArgumentBuilder literalArgumentBuilder = Commands.literal("emojis");
      LiteralArgumentBuilder literalArgumentBuilder2 = nexoCommand2.withPermission$com_nexomc_core(literalArgumentBuilder, "nexo.command.emojis");
      root.then((ArgumentBuilder)nexoCommand.executesPlayer$com_nexomc_core(literalArgumentBuilder2, GlyphCommandKt::emojiCommand$lambda$0));
   }

   private static final TextComponent emojiCommand$lambda$0$3(List it) {
      Collection collection = (Collection)it;
      Component[] components = (Component[])collection.toArray(new Component[0]);
      return Component.textOfChildren((ComponentLike[])Arrays.copyOf(components, components.length));
   }

   private static final Unit emojiCommand$lambda$0(Player player) {
      boolean onlyShowPermissable = Settings.SHOW_PERMISSION_EMOJIS.toBool();
      Iterable iterable = (Iterable)NexoPlugin.Companion.instance().fontManager().emojis();
      Collection destination = (Collection)(new ArrayList());

      for(Object element : iterable) {
         Glyph it = (Glyph)element;
         int value = 0;
         if (!onlyShowPermissable || it.hasPermission(player)) {
            destination.add(element);
         }
      }

      List list2 = (List)destination;
      int value3 = 0;
      List list = !list2.isEmpty() ? list2 : null;
      if (list == null) {
         Message.NO_EMOJIS.send((CommandSender)player);
         return Unit.INSTANCE;
      } else {
         List emojis = list;
         Iterable iterable2 = (Iterable)emojis;
         destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable2, 10)));

         for(Object item : iterable2) {
            Glyph emoji = (Glyph)item;
            int value2 = 0;
            String str = CollectionsKt.joinToString((Iterable)emoji.getPlaceholders(), (CharSequence)"\n", (CharSequence)"", (CharSequence)"", -1, (CharSequence)"...", (Function1)null);
            Component hoverText = AdventureUtilsKt.deserialize(str + (onlyShowPermissable ? "" : (emoji.hasPermission(player) ? "\n<green>Permitted" : "\n<red>No Permission")));
            HoverEvent hoverEvent2 = HoverEvent.hoverEvent(Action.SHOW_TEXT, hoverText);
            HoverEvent hoverEvent = hoverEvent2;
            destination.add(emoji.glyphComponent(false, emoji.getDefaultShadow(), kotlin.ranges.IntRange.Companion.getEMPTY()).hoverEvent((HoverEventSource)hoverEvent));
         }

         List emojiComponents = (List)destination;
         List bookPages = CollectionsKt.take((Iterable)CollectionsKt.chunked((Iterable)emojiComponents, Settings.EMOJI_BOOK_SIZE.toInt(255), GlyphCommandKt::emojiCommand$lambda$0$3), 100);
         Book book2 = Book.book((Component)Component.text("Glyph Book"), (Component)Component.text("Nexo"), (Collection)bookPages);
         Book book = book2;
         player.openBook(book);
         return Unit.INSTANCE;
      }
   }
}
