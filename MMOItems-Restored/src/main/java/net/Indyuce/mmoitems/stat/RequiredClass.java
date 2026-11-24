package net.Indyuce.mmoitems.stat;

import io.lumine.mythic.lib.api.item.ItemTag;
import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.api.item.SupportedNBTTagValues;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.edition.StatEdition;
import net.Indyuce.mmoitems.api.item.mmoitem.ReadMMOItem;
import net.Indyuce.mmoitems.api.player.RPGPlayer;
import net.Indyuce.mmoitems.api.util.message.Message;
import net.Indyuce.mmoitems.gui.edition.EditionInventory;
import net.Indyuce.mmoitems.stat.data.StringListData;
import net.Indyuce.mmoitems.stat.type.GemStoneStat;
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

public class RequiredClass extends StringListStat implements ItemRestriction, GemStoneStat {
   public RequiredClass() {
      super(
         "REQUIRED_CLASS",
         Material.WRITABLE_BOOK,
         "Required Class",
         new String[]{"The class you need to", "profess to use your item."},
         new String[]{"!block", "all"}
      );
   }

   @Override
   public StringListData whenInitialized(Object obj) {
      Validate.isTrue(obj instanceof List, "Must specify a string list");
      return new StringListData((List<String>)obj);
   }

   @Override
   public void whenClicked(@NotNull EditionInventory editionInventory, @NotNull InventoryClickEvent event) {
      if (event.getAction() == InventoryAction.PICKUP_ALL) {
         new StatEdition(editionInventory, this).enable("Write in the chat the class you want your item to support.");
      }

      if (event.getAction() == InventoryAction.PICKUP_HALF && editionInventory.getEditedSection().getKeys(false).contains("required-class")) {
         List requiredClass = editionInventory.getEditedSection().getStringList("required-class");
         if (requiredClass.size() < 1) {
            return;
         }

         String text = (String)requiredClass.remove(requiredClass.size() - 1);
         editionInventory.getEditedSection().set(this.getPath(), requiredClass.size() == 0 ? null : requiredClass);
         editionInventory.registerTemplateEdition();
         editionInventory.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Successfully removed " + text + ".");
      }
   }

   @Override
   public void whenInput(@NotNull EditionInventory editionInventory, @NotNull String text, Object... objs) {
      List list = editionInventory.getEditedSection().getKeys(false).contains("required-class") ? editionInventory.getEditedSection().getStringList("required-class") : new ArrayList();
      list.add(text);
      editionInventory.getEditedSection().set(this.getPath(), list);
      editionInventory.registerTemplateEdition();
      editionInventory.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Required Class successfully added.");
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
      return tagAtPath != null ? new StringListData(((String)tagAtPath.getValue()).split(Pattern.quote(", "))) : null;
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
      texts.add(ChatColor.YELLOW + "►" + " Click to add a class.");
      texts.add(ChatColor.YELLOW + "►" + " Right click to remove the last class.");
   }

   @NotNull
   @Override
   public ArrayList<ItemTag> getAppliedNBT(@NotNull StringListData stringListData) {
      ArrayList list = new ArrayList();
      list.add(new ItemTag(this.getNBTPath(), String.join(", ", stringListData.getList())));
      return list;
   }

   @Override
   public boolean canUse(RPGPlayer rpgPlayer, NBTItem nbtItem, boolean flag) {
      String text = nbtItem.getString(ItemStats.REQUIRED_CLASS.getNBTPath());
      if (!text.equals("") && !this.hasRightClass(rpgPlayer, text) && !rpgPlayer.getPlayer().hasPermission("mmoitems.bypass.class")) {
         if (flag) {
            Message.WRONG_CLASS.format(ChatColor.RED).send(rpgPlayer.getPlayer());
            rpgPlayer.getPlayer().playSound(rpgPlayer.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.5F);
         }

         return false;
      } else {
         return true;
      }
   }

   private boolean hasRightClass(RPGPlayer rpgPlayer, String text) {
      String className = ChatColor.stripColor(rpgPlayer.getClassName());

      for (String allowedClass : text.split(Pattern.quote(", "))) {
         if (allowedClass.equalsIgnoreCase(className)) {
            return true;
         }
      }

      return false;
   }
}
