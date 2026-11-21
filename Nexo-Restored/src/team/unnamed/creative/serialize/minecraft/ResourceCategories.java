package team.unnamed.creative.serialize.minecraft;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import team.unnamed.creative.metadata.pack.PackFormat;
import team.unnamed.creative.serialize.minecraft.atlas.AtlasSerializer;
import team.unnamed.creative.serialize.minecraft.blockstate.BlockStateSerializer;
import team.unnamed.creative.serialize.minecraft.equipment.EquipmentCategory;
import team.unnamed.creative.serialize.minecraft.font.FontSerializer;
import team.unnamed.creative.serialize.minecraft.item.ItemSerializer;
import team.unnamed.creative.serialize.minecraft.language.LanguageSerializer;
import team.unnamed.creative.serialize.minecraft.model.ModelSerializer;
import team.unnamed.creative.serialize.minecraft.sound.SoundSerializer;
import team.unnamed.creative.serialize.minecraft.waypoint.WaypointStyleSerializer;

public class ResourceCategories {
   private static final List<ResourceCategory<?>> CATEGORIES = new ArrayList();

   private ResourceCategories() {
   }

   private static void registerCategory(ResourceCategory<?> category) {
      CATEGORIES.add(category);
   }

   public static Collection<ResourceCategory<?>> categories() {
      return CATEGORIES;
   }

   public static Map<String, ResourceCategory<?>> buildCategoryMapByFolder(PackFormat packFormat) {
      Map<String, ResourceCategory<?>> map = new HashMap();

      for(ResourceCategory<?> category : categories()) {
         map.put(category.folder(packFormat), category);
      }

      return map;
   }

   static {
      registerCategory(AtlasSerializer.CATEGORY);
      registerCategory(SoundSerializer.CATEGORY);
      registerCategory(ModelSerializer.CATEGORY);
      registerCategory(LanguageSerializer.CATEGORY);
      registerCategory(BlockStateSerializer.CATEGORY);
      registerCategory(FontSerializer.CATEGORY);
      registerCategory(EquipmentCategory.INSTANCE);
      registerCategory(ItemSerializer.CATEGORY);
      registerCategory(WaypointStyleSerializer.CATEGORY);
   }
}
