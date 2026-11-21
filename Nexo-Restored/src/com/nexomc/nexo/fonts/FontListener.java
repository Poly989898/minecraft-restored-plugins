package com.nexomc.nexo.fonts;

import com.nexomc.nexo.configs.Settings;
import com.nexomc.nexo.glyphs.Glyph;
import com.nexomc.nexo.nms.GlyphHandlers;
import com.nexomc.nexo.nms.IPacketHandler;
import com.nexomc.nexo.nms.NMSHandlers;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.event.player.AsyncChatCommandDecorateEvent;
import io.papermc.paper.event.player.AsyncChatDecorateEvent;
import kotlin.Metadata;
import kotlin.TuplesKt;
import kotlin.collections.MapsKt;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u001c\u0010\u0006\u001a\u00020\u0007*\u00020\bH\u0007b\u000e\b\t\u0012\n\b\n\u0012\u0006\b\n0\u000b8\fJ\u0010\u0010\r\u001a\u00020\u0007*\u00020\u000eH\u0007b\u0002\b\tJ\u0010\u0010\u000f\u001a\u00020\u0007*\u00020\u0010H\u0007b\u0002\b\tJ\u0010\u0010\u0011\u001a\u00020\u0007*\u00020\u0012H\u0007b\u0002\b\tJ\u0010\u0010\u0013\u001a\u00020\u0007*\u00020\u0014H\u0007b\u0002\b\tR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0015"},
   d2 = {"Lcom/nexomc/nexo/fonts/FontListener;", "Lorg/bukkit/event/Listener;", "manager", "Lcom/nexomc/nexo/fonts/FontManager;", "<init>", "(Lcom/nexomc/nexo/fonts/FontManager;)V", "onJoin", "", "Lorg/bukkit/event/player/PlayerJoinEvent;", "Lorg/bukkit/event/EventHandler;", "priority", "Lorg/bukkit/event/EventPriority;", "LOWEST", "onQuit", "Lorg/bukkit/event/player/PlayerQuitEvent;", "onDecorate", "Lio/papermc/paper/event/player/AsyncChatDecorateEvent;", "onPlayerCommandChat", "Lio/papermc/paper/event/player/AsyncChatCommandDecorateEvent;", "onRename", "Lorg/bukkit/event/inventory/PrepareAnvilEvent;", "com.nexomc:core"}
)
public final class FontListener implements Listener {
   @NotNull
   private final FontManager manager;

   public FontListener(@NotNull FontManager manager) {
      super();
      this.manager = manager;
   }

   @EventHandler(
      priority = EventPriority.LOWEST
   )
   public final void onJoin(@NotNull PlayerJoinEvent playerJoinEvent) {
      if (!Settings.DISABLE_NETTY_INJECT.toBool()) {
         IPacketHandler packetHandler = NMSHandlers.handler().packetHandler();
         Player player = playerJoinEvent.getPlayer();
         packetHandler.inject(player);
      }

      FontManager fontManager = this.manager;
      Player player2 = playerJoinEvent.getPlayer();
      fontManager.sendGlyphTabCompletion(player2);
   }

   @EventHandler
   public final void onQuit(@NotNull PlayerQuitEvent playerQuitEvent) {
      IPacketHandler packetHandler = NMSHandlers.handler().packetHandler();
      Player player = playerQuitEvent.getPlayer();
      packetHandler.uninject(player);
   }

   @EventHandler
   public final void onDecorate(@NotNull AsyncChatDecorateEvent asyncChatDecorateEvent) {
      GlyphHandlers glyphHandlers = GlyphHandlers.INSTANCE;
      Component result = asyncChatDecorateEvent.result();
      asyncChatDecorateEvent.result(glyphHandlers.escapeGlyphs(result, asyncChatDecorateEvent.player()));
   }

   @EventHandler
   public final void onPlayerCommandChat(@NotNull AsyncChatCommandDecorateEvent asyncChatCommandDecorateEvent) {
      GlyphHandlers glyphHandlers = GlyphHandlers.INSTANCE;
      Component result = asyncChatCommandDecorateEvent.result();
      asyncChatCommandDecorateEvent.result(glyphHandlers.escapeGlyphs(result, asyncChatCommandDecorateEvent.player()));
   }

   @EventHandler
   public final void onRename(@NotNull PrepareAnvilEvent prepareAnvilEvent) {
      ItemStack result = prepareAnvilEvent.getResult();
      if (result != null) {
         ItemStack it = result;
         int value2 = 0;
         result = Settings.FORMAT_ANVIL.toBool() ? it : null;
         if (result != null) {
            ItemStack itemStack = result;
            String renameText = prepareAnvilEvent.getView().getRenameText();
            if (renameText != null) {
               String it_1 = renameText;
               value2 = 0;
               renameText = ((CharSequence)it_1).length() > 0 && itemStack.hasData((DataComponentType)DataComponentTypes.CUSTOM_NAME) ? it_1 : null;
            } else {
               renameText = null;
            }

            String value = renameText;
            NMSHandlers.handler().itemUtils().customDataComponent(itemStack, MapsKt.mapOf(TuplesKt.to(Glyph.Companion.getORIGINAL_ITEM_RENAME_STRING(), value)));
            return;
         }
      }

   }
}
