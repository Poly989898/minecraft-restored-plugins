package net.Indyuce.mmoitems.comp.mythicmobs.crafting;

import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.items.MythicItem;
import io.lumine.mythic.lib.api.MMOLineConfig;
import java.util.Optional;
import net.Indyuce.mmoitems.api.crafting.ingredient.Ingredient;
import net.Indyuce.mmoitems.api.player.RPGPlayer;
import org.apache.commons.lang.Validate;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Deprecated
public class MythicItemIngredient extends Ingredient<MythicItemPlayerIngredient> {
   private final MythicItem mythicitem;
   private final String display;

   public MythicItemIngredient(MMOLineConfig var1) {
      super("mythicitem", var1);
      var1.validate(new String[]{"item"});
      Optional var2 = MythicBukkit.inst().getItemManager().getItem(var1.getString("item"));
      Validate.isTrue(var2.isPresent(), "Could not find MM Item with ID '" + var1.getString("item") + "'");
      this.display = var1.contains("display") ? var1.getString("display") : ((MythicItem)var2.get()).getDisplayName();
      this.mythicitem = (MythicItem)var2.get();
   }

   @Override
   public String getKey() {
      return "mythicitem:" + this.mythicitem.getInternalName().toLowerCase();
   }

   @Override
   public String formatDisplay(String var1) {
      return var1.replace("#item#", this.display).replace("#amount#", "" + this.getAmount());
   }

   public boolean matches(MythicItemPlayerIngredient var1) {
      return false;
   }

   @NotNull
   @Override
   public ItemStack generateItemStack(@NotNull RPGPlayer var1, boolean var2) {
      return BukkitAdapter.adapt(this.mythicitem.generateItemStack(this.getAmount()));
   }

   @Override
   public String toString() {
      return this.getKey();
   }
}
