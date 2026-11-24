package net.Indyuce.mmoitems.stat;

import io.lumine.mythic.lib.api.item.ItemTag;
import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.api.item.SupportedNBTTagValues;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.edition.StatEdition;
import net.Indyuce.mmoitems.api.item.mmoitem.ReadMMOItem;
import net.Indyuce.mmoitems.api.player.RPGPlayer;
import net.Indyuce.mmoitems.api.util.message.Message;
import net.Indyuce.mmoitems.gui.edition.EditionInventory;
import net.Indyuce.mmoitems.stat.data.StringListData;
import net.Indyuce.mmoitems.stat.type.ItemRestriction;
import net.Indyuce.mmoitems.stat.type.StringListStat;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Permission extends StringListStat implements ItemRestriction {
   public Permission() {
      super("PERMISSION", Material.OAK_SIGN, "Permission", new String[]{"The permission needed to use this item."}, new String[]{"!block", "all"});
   }

   @Override
   public StringListData whenInitialized(Object var1) {
      Validate.isTrue(var1 instanceof List, "Must specify a string list");
      return new StringListData((List<String>)var1);
   }

   @Override
   public void whenClicked(@NotNull EditionInventory var1, @NotNull InventoryClickEvent var2) {
      if (var2.getAction() == InventoryAction.PICKUP_ALL) {
         new StatEdition(var1, ItemStats.PERMISSION).enable("Write in the chat the permission you want your item to require.");
      }

      if (var2.getAction() == InventoryAction.PICKUP_HALF && var1.getEditedSection().contains("permission")) {
         List var3 = var1.getEditedSection().getStringList("permission");
         if (var3.size() < 1) {
            return;
         }

         String var4 = (String)var3.get(var3.size() - 1);
         var3.remove(var4);
         var1.getEditedSection().set("permission", var3.size() == 0 ? null : var3);
         var1.registerTemplateEdition();
         var1.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Successfully removed " + var4 + ".");
      }
   }

   @Override
   public void whenInput(@NotNull EditionInventory var1, @NotNull String var2, Object... var3) {
      Validate.isTrue(!var2.contains("|"), "Your perm node must not contain any | symbol.");
      List var4 = var1.getEditedSection().contains("permission") ? var1.getEditedSection().getStringList("permission") : new ArrayList();
      var4.add(var2);
      var1.getEditedSection().set("permission", var4);
      var1.registerTemplateEdition();
      var1.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Permission successfully added.");
   }

   @Override
   public void whenDisplayed(List<String> var1, Optional<StringListData> var2) {
      if (var2.isPresent()) {
         var1.add(ChatColor.GRAY + "Current Value:");
         StringListData var3 = (StringListData)var2.get();
         var3.getList().forEach(var1x -> var1.add(ChatColor.GRAY + "* " + ChatColor.GREEN + var1x));
      } else {
         var1.add(ChatColor.GRAY + "Current Value: " + ChatColor.RED + "None");
      }

      var1.add("");
      var1.add(ChatColor.YELLOW + "►" + " Click to add a compatible permission.");
      var1.add(ChatColor.YELLOW + "►" + " Right click to remove the last permission.");
   }

   @NotNull
   @Override
   public ArrayList<ItemTag> getAppliedNBT(@NotNull StringListData var1) {
      ArrayList var2 = new ArrayList();
      var2.add(new ItemTag(this.getNBTPath(), String.join("|", var1.getList())));
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
   @Override
   public StringListData getLoadedNBT(@NotNull ArrayList<ItemTag> var1) {
      ItemTag var2 = ItemTag.getTagAtPath(this.getNBTPath(), var1);
      if (var2 != null) {
         ArrayList var3 = new ArrayList<>(Arrays.asList(((String)var2.getValue()).split("\\|")));
         return new StringListData(var3);
      } else {
         return null;
      }
   }

   @Override
   public boolean canUse(RPGPlayer var1, NBTItem var2, boolean var3) {
      String var4 = var2.getString("MMOITEMS_PERMISSION");
      if (!var4.equals("") && !var1.getPlayer().hasPermission("mmoitems.bypass.item") && MMOItems.plugin.getConfig().getBoolean("permissions.items")) {
         String[] var5 = var4.split("\\|");

         for (String var9 : var5) {
            if (!var1.getPlayer().hasPermission(var9)) {
               if (var3) {
                  Message.NOT_ENOUGH_PERMS.format(ChatColor.RED).send(var1.getPlayer());
                  var1.getPlayer().playSound(var1.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.5F);
               }

               return false;
            }
         }
      }

      return true;
   }
}
