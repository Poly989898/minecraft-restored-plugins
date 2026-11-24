package me.TechsCode.UltraPermissions.base.gui.pageableview;

import me.TechsCode.UltraPermissions.base.item.XMaterial;

public abstract class SearchFeature<OBJ> {
   private final XMaterial icon;
   private final String title;
   private final String action;

   public SearchFeature(XMaterial xMaterial, String text, String action) {
      this.icon = xMaterial;
      this.title = text;
      this.action = action;
   }

   public abstract String[] getSearchableText(OBJ value);

   public XMaterial getIcon() {
      return this.icon;
   }

   public String getTitle() {
      return this.title;
   }

   public String getAction() {
      return this.action;
   }
}
