package net.Indyuce.mmoitems.api.crafting;

import io.lumine.mythic.lib.api.MMOLineConfig;
import java.util.function.Function;

public class LoadedCraftingObject<C> {
   private final String id;
   private final Function<MMOLineConfig, C> function;
   private ConditionalDisplay display;

   public LoadedCraftingObject(String var1, Function<MMOLineConfig, C> var2, ConditionalDisplay var3) {
      this.id = var1;
      this.function = var2;
      this.display = var3;
   }

   public String getId() {
      return this.id;
   }

   public void setDisplay(ConditionalDisplay var1) {
      this.display = var1;
   }

   public boolean hasDisplay() {
      return this.display != null;
   }

   public ConditionalDisplay getDisplay() {
      return this.display;
   }

   public C load(MMOLineConfig var1) {
      return this.function.apply(var1);
   }
}
