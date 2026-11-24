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
import net.Indyuce.mmoitems.stat.data.StringData;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StringStat extends ItemStat<StringData, StringData> {
   public StringStat(String var1, Material var2, String var3, String[] var4, String[] var5, Material... var6) {
      super(var1, var2, var3, var4, var5, var6);
   }

   public StringData whenInitialized(Object var1) {
      return new StringData(var1.toString());
   }

   public void whenApplied(@NotNull ItemStackBuilder var1, @NotNull StringData var2) {
      var1.addItemTag(this.getAppliedNBT(var2));
      var1.getLore().insert(this.getPath(), this.getGeneralStatFormat().replace("{value}", var2.toString()));
   }

   @NotNull
   public ArrayList<ItemTag> getAppliedNBT(@NotNull StringData var1) {
      ArrayList var2 = new ArrayList();
      var2.add(new ItemTag(this.getNBTPath(), var1.toString()));
      return var2;
   }

   @Override
   public void whenClicked(@NotNull EditionInventory var1, @NotNull InventoryClickEvent var2) {
      if (var2.getAction() == InventoryAction.PICKUP_HALF) {
         var1.getEditedSection().set(this.getPath(), null);
         var1.registerTemplateEdition();
         var1.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Successfully removed " + this.getName() + ".");
      } else {
         new StatEdition(var1, this).enable("Write in the chat the text you want.");
      }
   }

   @Override
   public void whenInput(@NotNull EditionInventory var1, @NotNull String var2, Object... var3) {
      var1.getEditedSection().set(this.getPath(), var2);
      var1.registerTemplateEdition();
      var1.getPlayer()
         .sendMessage(MMOItems.plugin.getPrefix() + this.getName() + " successfully changed to '" + MythicLib.plugin.parseColors(var2) + ChatColor.GRAY + "'.");
   }

   @Override
   public void whenLoaded(@NotNull ReadMMOItem var1) {
      ArrayList var2 = new ArrayList();
      if (var1.getNBT().hasTag(this.getNBTPath())) {
         var2.add(ItemTag.getTagAtPath(this.getNBTPath(), var1.getNBT(), SupportedNBTTagValues.STRING));
      }

      StringData var3 = this.getLoadedNBT(var2);
      if (var3 != null) {
         var1.setData(this, var3);
      }
   }

   @Nullable
   public StringData getLoadedNBT(@NotNull ArrayList<ItemTag> var1) {
      ItemTag var2 = ItemTag.getTagAtPath(this.getNBTPath(), var1);
      if (var2 != null) {
         String var3 = (String)var2.getValue();
         return new StringData(var3);
      } else {
         return null;
      }
   }

   @Override
   public void whenDisplayed(List<String> var1, Optional<StringData> var2) {
      if (var2.isPresent()) {
         String var3 = MythicLib.plugin.parseColors(((StringData)var2.get()).toString());
         var3 = var3.length() > 40 ? var3.substring(0, 40) + "..." : var3;
         var1.add(ChatColor.GRAY + "Current Value: " + ChatColor.GREEN + var3);
      } else {
         var1.add(ChatColor.GRAY + "Current Value: " + ChatColor.RED + "None");
      }

      var1.add("");
      var1.add(ChatColor.YELLOW + "►" + " Left click to change this value.");
      var1.add(ChatColor.YELLOW + "►" + " Right click to remove this value.");
   }

   @NotNull
   public StringData getClearStatData() {
      return new StringData("");
   }
}
