package com.Zrips.CMI.Modules.Search;

import net.Zrips.CMILib.Items.CMIMC;
import net.Zrips.CMILib.Items.CMIMaterial;
import org.bukkit.Material;

public class SearchTypeItemMaterial extends SearchTypeItem {
  Material material = null;

  public SearchTypeItemMaterial(Material material) {
    this(null, material);
  }

  public SearchTypeItemMaterial(SearchTypeItem searchTypeItem, Material material) {
    super(searchTypeItem);
    this.material = material;
  }

  public Material getMaterial() {
    return this.material;
  }

  @Override
  protected boolean isValid() {
    if (this.getItemToCompare() == null) {
      return false;
    } else if ((!CMIMaterial.isShulkerBox(this.getMaterial())
            || !CMIMaterial.isShulkerBox(this.getItemToCompare().getType()))
        && (!CMIMaterial.get(this.getMaterial()).containsCriteria(CMIMC.BUNDLE)
            || !CMIMaterial.get(this.getItemToCompare().getType())
                .containsCriteria(CMIMC.BUNDLE))) {
      return this.checkShulkerBundle()
          ? false
          : this.material.equals(this.getItemToCompare().getType());
    } else {
      return true;
    }
  }

  @Override
  public String getSearchTypeName() {
    return this.parent != null ? this.parent.getSearchTypeName() : "Material " + this.getMaterial();
  }
}
