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

   public MythicItemIngredient(MMOLineConfig mmoLineConfig) {
      super("mythicitem", mmoLineConfig);
      mmoLineConfig.validate(new String[]{"item"});
      Optional item = MythicBukkit.inst().getItemManager().getItem(mmoLineConfig.getString("item"));
      Validate.isTrue(item.isPresent(), "Could not find MM Item with ID '" + mmoLineConfig.getString("item") + "'");
      this.display = mmoLineConfig.contains("display") ? mmoLineConfig.getString("display") : ((MythicItem)item.get()).getDisplayName();
      this.mythicitem = (MythicItem)item.get();
   }

   @Override
   public String getKey() {
      return "mythicitem:" + this.mythicitem.getInternalName().toLowerCase();
   }

   @Override
   public String formatDisplay(String text) {
      return text.replace("#item#", this.display).replace("#amount#", "" + this.getAmount());
   }

   public boolean matches(MythicItemPlayerIngredient mythicItemPlayerIngredient) {
      return false;
   }

   @NotNull
   @Override
   public ItemStack generateItemStack(@NotNull RPGPlayer rpgPlayer, boolean flag) {
      return BukkitAdapter.adapt(this.mythicitem.generateItemStack(this.getAmount()));
   }

   @Override
   public String toString() {
      return this.getKey();
   }
}
