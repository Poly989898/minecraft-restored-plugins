package me.TechsCode.UltraPermissions.gui.dialogs;

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
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public abstract class GroupChangeIconView extends PageableGUI<XMaterial> {
   private static final Phrase TITLE = Phrase.create("groupChangeIconView.title", "Click to change");
   private String title;
   private XMaterial[] materials;

   public GroupChangeIconView(Player player, SpigotTechPlugin spigotTechPlugin, String text) {
      super(player, spigotTechPlugin);
      this.title = text;
      ArrayList<XMaterial> xMaterials = new ArrayList<>();
      Inventory inventory = Bukkit.createInventory((InventoryHolder)null, 9, "Testing");

      for (XMaterial xMaterial : XMaterial.values()) {
         Optional asItemStack = xMaterial.getAsItemStack();
         if (asItemStack.isPresent()) {
            inventory.setItem(0, (ItemStack)asItemStack.get());
            if (inventory.getItem(0) != null) {
               inventory.setItem(0, (ItemStack)null);
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
      button.material(xMaterial).name(Animation.wave(xMaterial.getHumanName(), Colors.GREEN, Colors.WHITE)).lore(TITLE.get());
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
