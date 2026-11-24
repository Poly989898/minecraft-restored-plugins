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
import java.util.stream.Collectors;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.edition.StatEdition;
import net.Indyuce.mmoitems.api.item.build.ItemStackBuilder;
import net.Indyuce.mmoitems.api.item.mmoitem.ReadMMOItem;
import net.Indyuce.mmoitems.gui.edition.EditionInventory;
import net.Indyuce.mmoitems.stat.annotation.VersionDependant;
import net.Indyuce.mmoitems.stat.data.StringListData;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@VersionDependant(version = {1, 20, 6})
public class CanBreak extends ItemStat<StringListData, StringListData> {
   public CanBreak() {
      super(
         "CAN_BREAK",
         Material.IRON_PICKAXE,
         "Can Break?",
         new String[]{"The list of materials that this item can break.", "This restriction only applies to adventure mode."},
         new String[]{"tool"}
      );
   }

   public StringListData whenInitialized(Object var1) {
      Validate.isTrue(var1 instanceof List, "Must specify a string list");
      return new StringListData((List<String>)var1);
   }

   @Override
   public void whenClicked(@NotNull EditionInventory var1, @NotNull InventoryClickEvent var2) {
      if (var2.getAction() == InventoryAction.PICKUP_ALL) {
         new StatEdition(var1, this).enable("Write in the chat the name of the material you want to add.");
      }

      if (var2.getAction() == InventoryAction.PICKUP_HALF && var1.getEditedSection().contains(this.getPath())) {
         List var3 = var1.getEditedSection().getStringList(this.getPath());
         if (!var3.isEmpty()) {
            String var4 = (String)var3.removeLast();
            var1.getEditedSection().set(this.getPath(), var3);
            var1.registerTemplateEdition();
            var1.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Successfully removed '" + var4 + "'.");
         }
      }
   }

   @Override
   public void whenInput(@NotNull EditionInventory var1, @NotNull String var2, Object... var3) {
      List var4 = var1.getEditedSection().contains(this.getPath()) ? var1.getEditedSection().getStringList(this.getPath()) : new ArrayList();
      Material var5 = Material.valueOf(UtilityMethods.enumName(var2));
      var4.add(var5.name());
      var1.getEditedSection().set(this.getPath(), var4);
      var1.registerTemplateEdition();
      var1.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Material " + var5.name() + " successfully added.");
   }

   @Override
   public void whenDisplayed(List<String> var1, Optional<StringListData> var2) {
      if (var2.isPresent()) {
         var1.add(ChatColor.GRAY + "Current Value:");
         ((StringListData)var2.get()).getList().forEach(var1x -> var1.add(ChatColor.GRAY + "* " + var1x));
      } else {
         var1.add(ChatColor.GRAY + "Current Value: " + ChatColor.RED + "Cannot mine any item (adventure mode).");
      }

      var1.add("");
      var1.add(ChatColor.YELLOW + "►" + " Click to add a new material.");
      var1.add(ChatColor.YELLOW + "►" + " Right click to remove the last material.");
   }

   @NotNull
   public StringListData getClearStatData() {
      return new StringListData();
   }

   public void whenApplied(@NotNull ItemStackBuilder var1, @NotNull StringListData var2) {
      List<Material> var3 = var2.getList().stream().map(Material::valueOf).collect(Collectors.toList());
      var1.addFutureAction(var1x -> var1x.setCanMine(var3));
      var1.addItemTag(this.getAppliedNBT(var2));
   }

   @NotNull
   public ArrayList<ItemTag> getAppliedNBT(@NotNull StringListData var1) {
      JsonArray var2 = new JsonArray();

      for (String var4 : var1.getList()) {
         var2.add(var4);
      }

      ArrayList var5 = new ArrayList();
      var5.add(new ItemTag(this.getNBTPath(), var2.toString()));
      return var5;
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

      try {
         JsonArray var3 = new JsonParser().parse((String)var2.getValue()).getAsJsonArray();
         return new StringListData(var3);
      } catch (JsonSyntaxException | IllegalStateException var4) {
         return null;
      }
   }
}
