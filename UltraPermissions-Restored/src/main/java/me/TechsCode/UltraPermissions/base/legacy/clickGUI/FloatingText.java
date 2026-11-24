package me.TechsCode.UltraPermissions.base.legacy.clickGUI;

import org.bukkit.entity.ArmorStand;

public abstract class FloatingText extends FloatingElement {
   public FloatingText() {
   }

   public abstract String getText();

   @Override
   public void apply(ArmorStand armorStand) {
      String text = this.getText();
      this.setShown(text != null);
      if (armorStand != null && text != null) {
         armorStand.setCustomName(text);
         armorStand.setCustomNameVisible(true);
      }
   }

   @Override
   public double getYOffset() {
      return 2.5;
   }
}
