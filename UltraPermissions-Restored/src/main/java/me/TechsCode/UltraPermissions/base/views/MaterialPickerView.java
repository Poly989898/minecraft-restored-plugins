package me.TechsCode.UltraPermissions.base.views;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;
import me.TechsCode.UltraPermissions.base.SpigotTechPlugin;
import me.TechsCode.UltraPermissions.base.gui.Button;
import me.TechsCode.UltraPermissions.base.gui.pageableview.BasicSearch;
import me.TechsCode.UltraPermissions.base.gui.pageableview.PageableGUI;
import me.TechsCode.UltraPermissions.base.gui.pageableview.SearchFeature;
import me.TechsCode.UltraPermissions.base.item.XMaterial;
import me.TechsCode.UltraPermissions.base.translations.Phrase;
import me.TechsCode.UltraPermissions.base.visual.Animation;
import me.TechsCode.UltraPermissions.base.visual.Colors;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public abstract class MaterialPickerView extends PageableGUI<XMaterial> {
   private static final Phrase CHOOSE_ACTION = Phrase.create("materialPickerView.choose.action", "**Click** to choose", Colors.GRAY, Colors.AQUA);
   private String title;
   private XMaterial[] materials;

   public MaterialPickerView(Player player, SpigotTechPlugin spigotTechPlugin, String text) {
      super(player, spigotTechPlugin);
      this.title = text;
      ArrayList<XMaterial> xMaterials = new ArrayList<>();
      Inventory inventory = Bukkit.createInventory(null, 9, "Testing");

      for (XMaterial xMaterial : XMaterial.values()) {
         Optional optional;
         if (xMaterial == XMaterial.AIR) {
            optional = Optional.of(XMaterial.BARRIER.parseItem());
            ((ItemStack)optional.get()).getItemMeta().setDisplayName("Air");
         } else {
            try {
               optional = xMaterial.getAsItemStack();
            } catch (IllegalArgumentException exception) {
               continue;
            }
         }

         if (optional.isPresent()) {
            inventory.setItem(0, (ItemStack)optional.get());
            if (inventory.getItem(0) != null) {
               inventory.setItem(0, null);
               xMaterials.add(xMaterial);
            }
         }
      }

      this.materials = xMaterials.stream().sorted(Comparator.comparing(Enum::name)).toArray(XMaterial[]::new);
   }

   @Override
   public String getTitle() {
      return this.title;
   }

   public void construct(Button button, XMaterial xMaterial) {
      button.material(xMaterial == XMaterial.AIR ? XMaterial.BARRIER : xMaterial).name(Animation.wave(xMaterial.name(), Colors.GREEN, Colors.WHITE)).lore(CHOOSE_ACTION.get());
      button.action(actionType -> this.choose(this.p, xMaterial));
   }

   public XMaterial[] getObjects() {
      return this.materials;
   }

   public abstract void choose(Player player, XMaterial xMaterial);

   @Override
   public SearchFeature<XMaterial> getSearch() {
      return new BasicSearch<XMaterial>() {
         public String[] getSearchableText(XMaterial xMaterial) {
            return new String[]{xMaterial.getHumanName()};
         }
      };
   }
}
