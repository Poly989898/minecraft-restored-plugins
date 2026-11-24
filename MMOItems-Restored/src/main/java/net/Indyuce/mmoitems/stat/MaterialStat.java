package net.Indyuce.mmoitems.stat;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.item.ItemTag;
import io.lumine.mythic.lib.api.util.EnumUtils;
import io.lumine.mythic.lib.version.VMaterial;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.edition.StatEdition;
import net.Indyuce.mmoitems.api.item.build.ItemStackBuilder;
import net.Indyuce.mmoitems.api.item.mmoitem.ReadMMOItem;
import net.Indyuce.mmoitems.gui.edition.EditionInventory;
import net.Indyuce.mmoitems.stat.data.MaterialData;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MaterialStat extends ItemStat<MaterialData, MaterialData> {
   public MaterialStat() {
      super("MATERIAL", VMaterial.GRASS_BLOCK.get(), "Material", new String[]{"Your item material."}, new String[0]);
   }

   public MaterialData whenInitialized(Object obj) {
      Validate.isTrue(obj instanceof String, "Must specify material name as string");
      return new MaterialData(Material.valueOf(((String)obj).toUpperCase().replace("-", "_").replace(" ", "_")));
   }

   @Override
   public void whenClicked(@NotNull EditionInventory editionInventory, @NotNull InventoryClickEvent event) {
      new StatEdition(editionInventory, ItemStats.MATERIAL).enable("Write in the chat the material you want.");
   }

   @Override
   public void whenInput(@NotNull EditionInventory editionInventory, @NotNull String text, Object... objs) {
      Optional ifPresent = EnumUtils.getIfPresent(Material.class, text.toUpperCase().replace("-", "_").replace(" ", "_"));
      if (ifPresent.isPresent()) {
         editionInventory.getEditedSection().set("material", ((Material)ifPresent.get()).name());
         editionInventory.registerTemplateEdition();
         editionInventory.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Material successfully changed to " + ((Material)ifPresent.get()).name() + ".");
      } else {
         editionInventory.getPlayer()
            .spigot()
            .sendMessage(
               new ComponentBuilder("Invalid material! (Click for a list of valid materials)")
                  .color(ChatColor.RED)
                  .event(new ClickEvent(Action.OPEN_URL, "https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html"))
                  .create()
            );
      }
   }

   public void whenApplied(@NotNull ItemStackBuilder itemStackBuilder, @NotNull MaterialData materialData) {
   }

   @Override
   public void whenLoaded(@NotNull ReadMMOItem readMMOItem) {
      readMMOItem.setData(this, new MaterialData(readMMOItem.getNBT().getItem().getType()));
   }

   @Override
   public void whenDisplayed(List<String> texts, Optional<MaterialData> optional) {
      texts.add(
         ChatColor.GRAY
            + "Current Value: "
            + (
               optional.isPresent()
                  ? ChatColor.GREEN + UtilityMethods.caseOnWords(((MaterialData)optional.get()).getMaterial().name().toLowerCase().replace("_", " "))
                  : ChatColor.RED + "None"
            )
      );
      texts.add("");
      texts.add(ChatColor.YELLOW + "►" + " Left click to change this value.");
      texts.add(ChatColor.YELLOW + "►" + " Right click to remove this value.");
   }

   @NotNull
   public MaterialData getClearStatData() {
      return new MaterialData(Material.IRON_ORE);
   }

   @NotNull
   public ArrayList<ItemTag> getAppliedNBT(@NotNull MaterialData materialData) {
      return new ArrayList<>();
   }

   @Nullable
   public MaterialData getLoadedNBT(@NotNull ArrayList<ItemTag> itemTags) {
      return null;
   }
}
