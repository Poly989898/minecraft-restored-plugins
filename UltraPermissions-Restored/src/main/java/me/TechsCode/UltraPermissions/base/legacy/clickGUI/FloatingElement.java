package me.TechsCode.UltraPermissions.base.legacy.clickGUI;

import org.bukkit.entity.ArmorStand;

public abstract class FloatingElement {
   private boolean shown = true;
   private boolean hovering;

   public FloatingElement() {
   }

   public void hovering(boolean isHovering) {
      if (this.hovering != isHovering) {
         this.hovering = isHovering;
         this.onHoverChange(isHovering);
      }
   }

   public boolean isHovering() {
      return this.hovering;
   }

   public boolean isShown() {
      return this.shown;
   }

   public void setShown(boolean shown) {
      if (this.shown != shown) {
         this.shown = shown;
      }
   }

   public abstract void apply(ArmorStand armorStand);

   public abstract double getYOffset();

   public abstract double getX();

   public abstract double getY();

   public abstract double getRadius();

   public abstract void click();

   protected abstract void onHoverChange(boolean flag);
}
