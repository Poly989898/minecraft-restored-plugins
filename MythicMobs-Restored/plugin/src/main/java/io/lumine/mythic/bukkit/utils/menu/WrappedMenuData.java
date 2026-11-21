package io.lumine.mythic.bukkit.utils.menu;

public class WrappedMenuData<T> implements MenuData<T> {
   private final Icon<T> icon;

   public WrappedMenuData(Icon<T> icon) {
      this.icon = icon;
   }

   @Override
   public Icon<T> getIcon() {
      return this.icon;
   }

   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof WrappedMenuData<?> other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$icon = this.getIcon();
         Object other$icon = other.getIcon();
         return this$icon == null ? other$icon == null : this$icon.equals(other$icon);
      }
   }

   protected boolean canEqual(Object other) {
      return other instanceof WrappedMenuData;
   }

   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $icon = this.getIcon();
      return result * 59 + ($icon == null ? 43 : $icon.hashCode());
   }

   @Override
   public String toString() {
      return "WrappedMenuData(icon=" + this.getIcon() + ")";
   }
}
