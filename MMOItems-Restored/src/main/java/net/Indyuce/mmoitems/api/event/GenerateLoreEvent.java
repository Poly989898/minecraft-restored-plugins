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

   public GenerateLoreEvent(MMOItem var1, LoreBuilder var2, List<String> var3, List<String> var4) {
      this.item = var1;
      this.lore = var3;
      this.parsed = var4;
      this.builder = var2;
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

   public void setFinalLore(List<String> var1) {
      this.parsed.clear();
      this.parsed.addAll(var1);
   }

   public HandlerList getHandlers() {
      return handlers;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }
}
