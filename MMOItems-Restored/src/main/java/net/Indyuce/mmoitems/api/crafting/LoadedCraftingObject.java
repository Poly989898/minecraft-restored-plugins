package net.Indyuce.mmoitems.api.crafting;

import io.lumine.mythic.lib.api.MMOLineConfig;
import java.util.function.Function;

public class LoadedCraftingObject<C> {
   private final String id;
   private final Function<MMOLineConfig, C> function;
   private ConditionalDisplay display;

   public LoadedCraftingObject(String text, Function<MMOLineConfig, C> function, ConditionalDisplay conditionalDisplay) {
      this.id = text;
      this.function = function;
      this.display = conditionalDisplay;
   }

   public String getId() {
      return this.id;
   }

   public void setDisplay(ConditionalDisplay conditionalDisplay) {
      this.display = conditionalDisplay;
   }

   public boolean hasDisplay() {
      return this.display != null;
   }

   public ConditionalDisplay getDisplay() {
      return this.display;
   }

   public C load(MMOLineConfig mmoLineConfig) {
      return this.function.apply(mmoLineConfig);
   }
}
