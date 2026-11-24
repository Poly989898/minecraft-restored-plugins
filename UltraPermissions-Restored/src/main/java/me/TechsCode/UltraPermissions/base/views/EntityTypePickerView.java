package me.TechsCode.UltraPermissions.base.views;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;
import me.TechsCode.UltraPermissions.base.SpigotTechPlugin;
import me.TechsCode.UltraPermissions.base.gui.Button;
import me.TechsCode.UltraPermissions.base.gui.pageableview.BasicSearch;
import me.TechsCode.UltraPermissions.base.gui.pageableview.PageableGUI;
import me.TechsCode.UltraPermissions.base.gui.pageableview.SearchFeature;
import me.TechsCode.UltraPermissions.base.item.XEntityType;
import me.TechsCode.UltraPermissions.base.translations.Phrase;
import me.TechsCode.UltraPermissions.base.visual.Animation;
import me.TechsCode.UltraPermissions.base.visual.Colors;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public abstract class EntityTypePickerView extends PageableGUI<XEntityType> {
   private static final Phrase CHOOSE_ACTION = Phrase.create("entityTypePickerView.choose.action", "**Click** to choose", Colors.GRAY, Colors.AQUA);
   private String title;
   private XEntityType[] entityTypes;

   public EntityTypePickerView(Player player, SpigotTechPlugin spigotTechPlugin, String text) {
      super(player, spigotTechPlugin);
      this.title = text;
      ArrayList<XEntityType> xEntityTypes = new ArrayList<>();
      Inventory inventory = Bukkit.createInventory(null, 9, "Testing");

      for (XEntityType xEntityType : XEntityType.values()) {
         Optional asItemStack = xEntityType.getIcon().getAsItemStack();
         if (asItemStack.isPresent()) {
            inventory.setItem(0, (ItemStack)asItemStack.get());
            if (inventory.getItem(0) != null) {
               inventory.setItem(0, null);
               xEntityTypes.add(xEntityType);
            }
         }
      }

      this.entityTypes = xEntityTypes.stream().sorted(Comparator.comparing(Enum::name)).toArray(XEntityType[]::new);
   }

   @Override
   public String getTitle() {
      return this.title;
   }

   public void construct(Button button, XEntityType xEntityType) {
      button.material(xEntityType.getIcon().getAsItemStack().get().getType()).name(Animation.wave(xEntityType.name(), Colors.GREEN, Colors.WHITE)).lore(CHOOSE_ACTION.get());
      button.action(actionType -> this.choose(this.p, xEntityType));
   }

   public XEntityType[] getObjects() {
      return this.entityTypes;
   }

   public abstract void choose(Player player, XEntityType xEntityType);

   @Override
   public SearchFeature<XEntityType> getSearch() {
      return new BasicSearch<XEntityType>() {
         public String[] getSearchableText(XEntityType xEntityType) {
            return new String[]{xEntityType.getIcon().getHumanName()};
         }
      };
   }
}
