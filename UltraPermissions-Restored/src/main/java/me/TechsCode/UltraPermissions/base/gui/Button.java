package me.TechsCode.UltraPermissions.base.gui;

import java.util.ArrayList;
import java.util.List;
import me.TechsCode.UltraPermissions.base.item.CustomItem;
import me.TechsCode.UltraPermissions.base.item.XMaterial;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Button {
   private final List<Action> actions = new ArrayList<>();
   private CustomItem item;

   public Button() {
   }

   public void setItem(CustomItem customItem) {
      this.item = customItem;
   }

   public CustomItem material(XMaterial xMaterial) {
      this.item = new CustomItem(xMaterial);
      return this.item;
   }

   public CustomItem material(XMaterial xMaterial, int count) {
      this.item = new CustomItem(xMaterial).addModelId(count);
      return this.item;
   }

   public CustomItem material(Material material) {
      this.item = new CustomItem(material);
      return this.item;
   }

   public CustomItem material(Material material, int count) {
      this.item = new CustomItem(material).addModelId(count);
      return this.item;
   }

   public CustomItem stack(ItemStack item) {
      this.item = new CustomItem(item, false);
      return this.item;
   }

   public CustomItem item() {
      return this.item;
   }

   public void action(Action action) {
      this.actions.add(action);
   }

   public void action(ActionType actionType, Runnable task) {
      this.actions.add(performedType -> {
         if (performedType == actionType) {
            task.run();
         }
      });
   }

   public List<Action> getActions() {
      return this.actions;
   }
}
