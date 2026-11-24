package net.Indyuce.mmoitems.stat;

import io.lumine.mythic.lib.api.item.ItemTag;
import io.lumine.mythic.lib.api.item.SupportedNBTTagValues;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.edition.StatEdition;
import net.Indyuce.mmoitems.api.item.build.ItemStackBuilder;
import net.Indyuce.mmoitems.api.item.mmoitem.ReadMMOItem;
import net.Indyuce.mmoitems.gui.edition.EditionInventory;
import net.Indyuce.mmoitems.stat.data.StringListData;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemTypeRestriction extends ItemStat<StringListData, StringListData> {
   public ItemTypeRestriction() {
      super(
         "ITEM_TYPE_RESTRICTION",
         Material.EMERALD,
         "Item Type Restriction",
         new String[]{"This option defines the item types", "on which your gem can be applied."},
         new String[]{"gem_stone"}
      );
   }

   public StringListData whenInitialized(Object var1) {
      Validate.isTrue(var1 instanceof List, "Must specify a string list");
      return new StringListData((List<String>)var1);
   }

   @Override
   public void whenClicked(@NotNull EditionInventory var1, @NotNull InventoryClickEvent var2) {
      if (var2.getAction() == InventoryAction.PICKUP_ALL) {
         new StatEdition(var1, ItemStats.ITEM_TYPE_RESTRICTION)
            .enable("Write in the chat the item type you want your gem to support.", "Supported formats: WEAPON or BLUNT, PIERCING, SLASHING, OFFHAND, EXTRA.");
      }

      if (var2.getAction() == InventoryAction.PICKUP_HALF && var1.getEditedSection().contains(this.getPath())) {
         List var3 = var1.getEditedSection().getStringList("" + this.getPath());
         if (var3.size() < 1) {
            return;
         }

         String var4 = (String)var3.get(var3.size() - 1);
         var3.remove(var4);
         var1.getEditedSection().set("" + this.getPath(), var3.size() == 0 ? null : var3);
         var1.registerTemplateEdition();
         var1.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Successfully removed " + var4 + ".");
      }
   }

   @Override
   public void whenInput(@NotNull EditionInventory var1, @NotNull String var2, Object... var3) {
      String var4 = var2.toUpperCase().replace(" ", "_").replace("-", "_");
      Validate.isTrue(
         this.isValid(var4),
         var4
            + " is not a valid item type/set. You can enter WEAPON, BLUNT, PIERCING, SLASHING, OFFHAND, EXTRA, as well as other item types here: /mi list type."
      );
      List var5 = var1.getEditedSection().contains(this.getPath()) ? var1.getEditedSection().getStringList("" + this.getPath()) : new ArrayList();
      var5.add(var4);
      var1.getEditedSection().set(this.getPath(), var5);
      var1.registerTemplateEdition();
      var1.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Your gem now supports " + var4 + ".");
   }

   @Override
   public void whenDisplayed(List<String> var1, Optional<StringListData> var2) {
      if (var2.isPresent()) {
         var1.add(ChatColor.GRAY + "Current Value:");
         StringListData var3 = (StringListData)var2.get();
         var3.getList().forEach(var1x -> var1.add(ChatColor.GRAY + "* " + ChatColor.GREEN + var1x));
      } else {
         var1.add(ChatColor.GRAY + "Current Value: " + ChatColor.RED + "Compatible with any type.");
      }

      var1.add("");
      var1.add(ChatColor.YELLOW + "►" + " Click to add a supported item type/set.");
      var1.add(ChatColor.YELLOW + "►" + " Right click to remove the last element.");
   }

   public void whenApplied(@NotNull ItemStackBuilder var1, @NotNull StringListData var2) {
      var1.addItemTag(this.getAppliedNBT(var2));
   }

   @NotNull
   public ArrayList<ItemTag> getAppliedNBT(@NotNull StringListData var1) {
      ArrayList var2 = new ArrayList();
      String var3 = String.join(",", var1.getList());
      var2.add(new ItemTag(this.getNBTPath(), var3));
      return var2;
   }

   @Override
   public void whenLoaded(@NotNull ReadMMOItem var1) {
      ArrayList var2 = new ArrayList();
      if (var1.getNBT().hasTag(this.getNBTPath())) {
         var2.add(ItemTag.getTagAtPath(this.getNBTPath(), var1.getNBT(), SupportedNBTTagValues.STRING));
      }

      StringListData var3 = this.getLoadedNBT(var2);
      if (var3 != null) {
         var1.setData(this, var3);
      }
   }

   @Nullable
   public StringListData getLoadedNBT(@NotNull ArrayList<ItemTag> var1) {
      ItemTag var2 = ItemTag.getTagAtPath(this.getNBTPath(), var1);
      if (var2 == null) {
         return null;
      }

      String var3 = (String)var2.getValue();
      ArrayList var4 = new ArrayList();
      if (var3.contains(",")) {
         for (String var8 : var3.split(",")) {
            if (!var8.isEmpty()) {
               var4.add(var8);
            }
         }
      } else {
         var4.add(var3);
      }

      return new StringListData(var4);
   }

   private boolean isValid(String var1) {
      if (var1.equals("WEAPON")) {
         return true;
      }

      for (Type var3 : MMOItems.plugin.getTypes().getAll()) {
         if (var3.getId().equals(var1)) {
            return true;
         }
      }

      return false;
   }

   @NotNull
   public StringListData getClearStatData() {
      return new StringListData();
   }
}
