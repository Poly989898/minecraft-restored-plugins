package net.Indyuce.mmoitems.stat.type;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.item.ItemTag;
import io.lumine.mythic.lib.api.item.SupportedNBTTagValues;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.edition.StatEdition;
import net.Indyuce.mmoitems.api.item.build.ItemStackBuilder;
import net.Indyuce.mmoitems.api.item.mmoitem.ReadMMOItem;
import net.Indyuce.mmoitems.gui.edition.EditionInventory;
import net.Indyuce.mmoitems.stat.data.BooleanData;
import net.Indyuce.mmoitems.stat.data.random.RandomBooleanData;
import net.Indyuce.mmoitems.util.MMOUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BooleanStat extends ItemStat<RandomBooleanData, BooleanData> {
   public BooleanStat(String var1, Material var2, String var3, String[] var4, String[] var5, Material... var6) {
      super(var1, var2, var3, var4, var5, var6);
   }

   public RandomBooleanData whenInitialized(Object var1) {
      if (var1 instanceof Boolean) {
         return new RandomBooleanData((Boolean)var1);
      } else if (var1 instanceof Number) {
         return new RandomBooleanData(Double.parseDouble(var1.toString()));
      } else {
         throw new IllegalArgumentException("Must specify a number (chance) or true/false");
      }
   }

   public void whenApplied(@NotNull ItemStackBuilder var1, @NotNull BooleanData var2) {
      if (var2.isEnabled()) {
         var1.addItemTag(this.getAppliedNBT(var2));
         var1.getLore().insert(this.getPath(), this.getGeneralStatFormat());
      }
   }

   @NotNull
   public ArrayList<ItemTag> getAppliedNBT(@NotNull BooleanData var1) {
      ArrayList var2 = new ArrayList();
      if (var1.isEnabled()) {
         var2.add(new ItemTag(this.getNBTPath(), true));
      }

      return var2;
   }

   @Override
   public void whenClicked(@NotNull EditionInventory var1, @NotNull InventoryClickEvent var2) {
      if (var2.getAction() == InventoryAction.PICKUP_ALL) {
         var1.getEditedSection().set(this.getPath(), var1.getEditedSection().getBoolean(this.getPath()) ? null : true);
         var1.registerTemplateEdition();
      } else if (var2.getAction() == InventoryAction.PICKUP_HALF) {
         new StatEdition(var1, this).enable("Write in the chat the probability you want (a percentage)");
      }
   }

   @Override
   public void whenInput(@NotNull EditionInventory var1, @NotNull String var2, Object... var3) {
      double var4 = MMOUtils.parseDouble(var2);
      Validate.isTrue(var4 >= 0.0 && var4 <= 100.0, "Chance must be between 0 and 100");
      var1.getEditedSection().set(this.getPath(), var4 / 100.0);
      var1.registerTemplateEdition();
      var1.getPlayer()
         .sendMessage(
            MMOItems.plugin.getPrefix()
               + this.getName()
               + " successfully changed to "
               + ChatColor.GREEN
               + MythicLib.plugin.getMMOConfig().decimal.format(var4)
               + "% Chance"
               + ChatColor.GRAY
               + "."
         );
   }

   @Override
   public void whenLoaded(@NotNull ReadMMOItem var1) {
      ArrayList var2 = new ArrayList();
      if (var1.getNBT().hasTag(this.getNBTPath())) {
         var2.add(ItemTag.getTagAtPath(this.getNBTPath(), var1.getNBT(), SupportedNBTTagValues.BOOLEAN));
      }

      BooleanData var3 = this.getLoadedNBT(var2);
      if (var3 != null) {
         var1.setData(this, var3);
      }
   }

   @Nullable
   public BooleanData getLoadedNBT(@NotNull ArrayList<ItemTag> var1) {
      ItemTag var2 = ItemTag.getTagAtPath(this.getNBTPath(), var1);
      return var2 != null ? new BooleanData((Boolean)var2.getValue()) : null;
   }

   @Override
   public void whenDisplayed(List<String> var1, Optional<RandomBooleanData> var2) {
      if (var2.isPresent()) {
         double var3 = ((RandomBooleanData)var2.get()).getChance();
         var1.add(
            ChatColor.GRAY
               + "Current Value: "
               + (
                  var3 >= 1.0
                     ? ChatColor.GREEN + "True"
                     : (var3 <= 0.0 ? ChatColor.RED + "False" : ChatColor.GREEN + MythicLib.plugin.getMMOConfig().decimal.format(var3 * 100.0) + "% Chance")
               )
         );
      } else {
         var1.add(ChatColor.GRAY + "Current Value: " + ChatColor.RED + "False");
      }

      var1.add("");
      var1.add(ChatColor.YELLOW + "►" + " Left click to switch this value.");
      var1.add(ChatColor.YELLOW + "►" + " Right click to choose a probability to have this option.");
   }

   @NotNull
   public BooleanData getClearStatData() {
      return new BooleanData(false);
   }
}
