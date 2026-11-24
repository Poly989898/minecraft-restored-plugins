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

   public MaterialData whenInitialized(Object var1) {
      Validate.isTrue(var1 instanceof String, "Must specify material name as string");
      return new MaterialData(Material.valueOf(((String)var1).toUpperCase().replace("-", "_").replace(" ", "_")));
   }

   @Override
   public void whenClicked(@NotNull EditionInventory var1, @NotNull InventoryClickEvent var2) {
      new StatEdition(var1, ItemStats.MATERIAL).enable("Write in the chat the material you want.");
   }

   @Override
   public void whenInput(@NotNull EditionInventory var1, @NotNull String var2, Object... var3) {
      Optional var4 = EnumUtils.getIfPresent(Material.class, var2.toUpperCase().replace("-", "_").replace(" ", "_"));
      if (var4.isPresent()) {
         var1.getEditedSection().set("material", ((Material)var4.get()).name());
         var1.registerTemplateEdition();
         var1.getPlayer().sendMessage(MMOItems.plugin.getPrefix() + "Material successfully changed to " + ((Material)var4.get()).name() + ".");
      } else {
         var1.getPlayer()
            .spigot()
            .sendMessage(
               new ComponentBuilder("Invalid material! (Click for a list of valid materials)")
                  .color(ChatColor.RED)
                  .event(new ClickEvent(Action.OPEN_URL, "https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html"))
                  .create()
            );
      }
   }

   public void whenApplied(@NotNull ItemStackBuilder var1, @NotNull MaterialData var2) {
   }

   @Override
   public void whenLoaded(@NotNull ReadMMOItem var1) {
      var1.setData(this, new MaterialData(var1.getNBT().getItem().getType()));
   }

   @Override
   public void whenDisplayed(List<String> var1, Optional<MaterialData> var2) {
      var1.add(
         ChatColor.GRAY
            + "Current Value: "
            + (
               var2.isPresent()
                  ? ChatColor.GREEN + UtilityMethods.caseOnWords(((MaterialData)var2.get()).getMaterial().name().toLowerCase().replace("_", " "))
                  : ChatColor.RED + "None"
            )
      );
      var1.add("");
      var1.add(ChatColor.YELLOW + "►" + " Left click to change this value.");
      var1.add(ChatColor.YELLOW + "►" + " Right click to remove this value.");
   }

   @NotNull
   public MaterialData getClearStatData() {
      return new MaterialData(Material.IRON_ORE);
   }

   @NotNull
   public ArrayList<ItemTag> getAppliedNBT(@NotNull MaterialData var1) {
      return new ArrayList<>();
   }

   @Nullable
   public MaterialData getLoadedNBT(@NotNull ArrayList<ItemTag> var1) {
      return null;
   }
}
