package net.Indyuce.mmoitems.api.event;

import java.util.List;
import net.Indyuce.mmoitems.api.item.build.LoreBuilder;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Deprecated
public class GenerateLoreEvent extends Event {
   private static final HandlerList handlers = new HandlerList();
   private final MMOItem item;
   private final List<String> lore;
   private final List<String> parsed;
   private final LoreBuilder builder;

   public GenerateLoreEvent(MMOItem mmoItem, LoreBuilder loreBuilder, List<String> texts, List<String> parsedLore) {
      this.item = mmoItem;
      this.lore = texts;
      this.parsed = parsedLore;
      this.builder = loreBuilder;
   }

   public MMOItem getItem() {
      return this.item;
   }

   public LoreBuilder getBuilder() {
      return this.builder;
   }

   public List<String> getLore() {
      return this.lore;
   }

   public List<String> getParsedLore() {
      return this.parsed;
   }

   public void setFinalLore(List<String> texts) {
      this.parsed.clear();
      this.parsed.addAll(texts);
   }

   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}
