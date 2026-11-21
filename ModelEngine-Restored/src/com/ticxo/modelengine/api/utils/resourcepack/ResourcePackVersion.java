package com.ticxo.modelengine.api.utils.resourcepack;

import java.util.Set;
import lombok.Getter;

@Getter
public enum ResourcePackVersion {
   v1_19_4(13, ResourcePackFeature.COLORABLE_ITEM),
   v1_20(15, ResourcePackFeature.COLORABLE_ITEM),
   v1_20_1(15, ResourcePackFeature.COLORABLE_ITEM),
   v1_20_2(18, ResourcePackFeature.COLORABLE_ITEM),
   v1_20_3(22, ResourcePackFeature.COLORABLE_ITEM),
   v1_20_4(22, ResourcePackFeature.COLORABLE_ITEM),
   v1_20_5(32, ResourcePackFeature.COLORABLE_ITEM),
   v1_20_6(32, ResourcePackFeature.COLORABLE_ITEM),
   v1_21(34, ResourcePackFeature.COLORABLE_ITEM),
   v1_21_1(34, ResourcePackFeature.COLORABLE_ITEM),
   v1_21_2(42, ResourcePackFeature.COLORABLE_ITEM),
   v1_21_3(42, ResourcePackFeature.COLORABLE_ITEM),
   v1_21_4(46, ResourcePackFeature.COLORABLE_ITEM, ResourcePackFeature.DATA_DRIVEN, ResourcePackFeature.COMPOSITE_BYPASS);

   private final int version;
   private final Set<ResourcePackFeature> availableFeatures;

   ResourcePackVersion(int version, ResourcePackFeature... features) {
      this.version = version;
      this.availableFeatures = Set.of(features);
   }

   public static ResourcePackVersion getVersion(String value) {
      if (!value.startsWith("1_")) {
         value = "1_" + value;
      }

      if (!value.startsWith("v")) {
         value = "v" + value;
      }

      value = value.replaceAll("\\.", "_");
      if (value.endsWith("_0")) {
         value = value.substring(0, value.length() - 2);
      }

      return get(value);
   }

   public static ResourcePackVersion get(String value) {
      try {
         return valueOf(value);
      } catch (IllegalArgumentException e) {
         e.printStackTrace();
         return v1_19_4;
      }
   }
}
