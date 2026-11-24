package net.Indyuce.mmoitems.stat;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.item.ItemTag;
import io.lumine.mythic.lib.api.item.SupportedNBTTagValues;
import io.lumine.mythic.lib.gson.JsonArray;
import io.lumine.mythic.lib.gson.JsonParser;
import io.lumine.mythic.lib.gson.JsonSyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.Indyuce.mmoitems.MMOItems;
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

public class CompatibleMaterials extends ItemStat<StringListData, StringListData> {
   public CompatibleMaterials() {
      super(
         "COMPATIBLE_MATERIALS",
         Material.COMMAND_BLOCK,
         "Compatible Materials",
         new String[]{"The item materials this skin is", "compatible with."},
         new String[]{"skin"}
      );
   }

   public StringListData whenInitialized(Object obj) {
      Validate.isTrue(obj instanceof List, "Must specify a string list");
      return new StringListData((List<String>)obj);
   }

   @Override
   public void whenClicked(@NotNull EditionInventory editionInventory, @NotNull InventoryClickEvent event) {
      if (event.getAction() == InventoryAction.PICKUP_ALL) {
         new StatEdition(editionInventory, this).enable("Write in the chat the name of the material you want to add.");
      }

      if (event.getAction() == InventoryAction.PICKUP_HALF && editionInventory.getEditedSection().contains(this.getPath())) {
         List list = editionInventory.getEditedSection().getStringList(this.getPath());
         if (!list.isEmpty()) {
            String text = (String)list.removeLast();
            editionInventory.getEditedSection().set(this.getPath(), list);
            editionInventory.registerTemplateEdition();
            editionInventory.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Successfully removed '" + text + "'.");
         }
      }
   }

   @Override
   public void whenInput(@NotNull EditionInventory editionInventory, @NotNull String text, Object... objs) {
      Material material = Material.valueOf(UtilityMethods.enumName(text));
      List list = editionInventory.getEditedSection().contains(this.getPath()) ? editionInventory.getEditedSection().getStringList(this.getPath()) : new ArrayList();
      list.add(material.name());
      editionInventory.getEditedSection().set(this.getPath(), list);
      editionInventory.registerTemplateEdition();
      editionInventory.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Material " + material.name() + " successfully added.");
   }

   @Override
   public void whenDisplayed(List<String> texts, Optional<StringListData> optional) {
      if (optional.isPresent()) {
         texts.add(ChatColor.GRAY + "Current Value:");
         ((StringListData)optional.get()).getList().forEach(var1x -> texts.add(ChatColor.GRAY + "* " + var1x));
      } else {
         texts.add(ChatColor.GRAY + "Current Value: " + ChatColor.RED + "Compatible with any material.");
      }

      texts.add("");
      texts.add(ChatColor.YELLOW + "►" + " Click to add a new material.");
      texts.add(ChatColor.YELLOW + "►" + " Right click to remove the last material.");
   }

   @NotNull
   public StringListData getClearStatData() {
      return new StringListData();
   }

   public void whenApplied(@NotNull ItemStackBuilder itemStackBuilder, @NotNull StringListData stringListData) {
      ArrayList list = new ArrayList<>(stringListData.getList());
      itemStackBuilder.getLore().insert(this.getPath(), list);
      itemStackBuilder.addItemTag(this.getAppliedNBT(stringListData));
   }

   @NotNull
   public ArrayList<ItemTag> getAppliedNBT(@NotNull StringListData stringListData) {
      JsonArray jsonArray = new JsonArray();

      for (String text : stringListData.getList()) {
         jsonArray.add(text);
      }

      ArrayList list = new ArrayList();
      list.add(new ItemTag(this.getNBTPath(), jsonArray.toString()));
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
   public StringListData getLoadedNBT(@NotNull ArrayList<ItemTag> itemTags) {
      ItemTag tagAtPath = ItemTag.getTagAtPath(this.getNBTPath(), itemTags);
      if (tagAtPath == null) {
         return null;
      }

      try {
         JsonArray asJsonArray = new JsonParser().parse((String)tagAtPath.getValue()).getAsJsonArray();
         return new StringListData(asJsonArray);
      } catch (JsonSyntaxException | IllegalStateException exception) {
         return null;
      }
   }
}
