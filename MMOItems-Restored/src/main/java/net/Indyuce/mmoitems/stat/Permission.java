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
   public StringListData whenInitialized(Object obj) {
      Validate.isTrue(obj instanceof List, "Must specify a string list");
      return new StringListData((List<String>)obj);
   }

   @Override
   public void whenClicked(@NotNull EditionInventory editionInventory, @NotNull InventoryClickEvent event) {
      if (event.getAction() == InventoryAction.PICKUP_ALL) {
         new StatEdition(editionInventory, ItemStats.PERMISSION).enable("Write in the chat the permission you want your item to require.");
      }

      if (event.getAction() == InventoryAction.PICKUP_HALF && editionInventory.getEditedSection().contains("permission")) {
         List permission = editionInventory.getEditedSection().getStringList("permission");
         if (permission.size() < 1) {
            return;
         }

         String text = (String)permission.get(permission.size() - 1);
         permission.remove(text);
         editionInventory.getEditedSection().set("permission", permission.size() == 0 ? null : permission);
         editionInventory.registerTemplateEdition();
         editionInventory.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Successfully removed " + text + ".");
      }
   }

   @Override
   public void whenInput(@NotNull EditionInventory editionInventory, @NotNull String text, Object... objs) {
      Validate.isTrue(!text.contains("|"), "Your perm node must not contain any | symbol.");
      List list = editionInventory.getEditedSection().contains("permission") ? editionInventory.getEditedSection().getStringList("permission") : new ArrayList();
      list.add(text);
      editionInventory.getEditedSection().set("permission", list);
      editionInventory.registerTemplateEdition();
      editionInventory.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Permission successfully added.");
   }

   @Override
   public void whenDisplayed(List<String> texts, Optional<StringListData> optional) {
      if (optional.isPresent()) {
         texts.add(ChatColor.GRAY + "Current Value:");
         StringListData stringListData = (StringListData)optional.get();
         stringListData.getList().forEach(line -> texts.add(ChatColor.GRAY + "* " + ChatColor.GREEN + line));
      } else {
         texts.add(ChatColor.GRAY + "Current Value: " + ChatColor.RED + "None");
      }

      texts.add("");
      texts.add(ChatColor.YELLOW + "►" + " Click to add a compatible permission.");
      texts.add(ChatColor.YELLOW + "►" + " Right click to remove the last permission.");
   }

   @NotNull
   @Override
   public ArrayList<ItemTag> getAppliedNBT(@NotNull StringListData stringListData) {
      ArrayList list = new ArrayList();
      list.add(new ItemTag(this.getNBTPath(), String.join("|", stringListData.getList())));
      return list;
   }

   @Override
   public void whenLoaded(@NotNull ReadMMOItem readMMOItem) {
      ArrayList list = new ArrayList();
      if (readMMOItem.getNBT().hasTag(this.getNBTPath())) {
         list.add(ItemTag.getTagAtPath(this.getNBTPath(), readMMOItem.getNBT(), SupportedNBTTagValues.STRING));
      }

      StringListData loadedNBT = this.getLoadedNBT(list);
      if (loadedNBT != null) {
         readMMOItem.setData(this, loadedNBT);
      }
   }

   @Nullable
   @Override
   public StringListData getLoadedNBT(@NotNull ArrayList<ItemTag> itemTags) {
      ItemTag tagAtPath = ItemTag.getTagAtPath(this.getNBTPath(), itemTags);
      if (tagAtPath != null) {
         ArrayList list = new ArrayList<>(Arrays.asList(((String)tagAtPath.getValue()).split("\\|")));
         return new StringListData(list);
      } else {
         return null;
      }
   }

   @Override
   public boolean canUse(RPGPlayer rpgPlayer, NBTItem nbtItem, boolean flag) {
      String mMOITEMSPERMISSION = nbtItem.getString("MMOITEMS_PERMISSION");
      if (!mMOITEMSPERMISSION.equals("") && !rpgPlayer.getPlayer().hasPermission("mmoitems.bypass.item") && MMOItems.plugin.getConfig().getBoolean("permissions.items")) {
         String[] texts = mMOITEMSPERMISSION.split("\\|");

         for (String text : texts) {
            if (!rpgPlayer.getPlayer().hasPermission(text)) {
               if (flag) {
                  Message.NOT_ENOUGH_PERMS.format(ChatColor.RED).send(rpgPlayer.getPlayer());
                  rpgPlayer.getPlayer().playSound(rpgPlayer.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.5F);
               }

               return false;
            }
         }
      }

      return true;
   }
}
