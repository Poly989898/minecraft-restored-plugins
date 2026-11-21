package com.nexomc.nexo.pack;

import java.util.Locale;
import java.util.Map;
import kotlin.Metadata;
import kotlin.collections.MapsKt;
import net.kyori.adventure.key.Key;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.ResourcePack;
import team.unnamed.creative.model.ItemTransform;
import team.unnamed.creative.model.Model;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u001a\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00070\u00052\u0006\u0010\b\u001a\u00020\tJ\u001a\u0010\n\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00070\u00052\u0006\u0010\u000b\u001a\u00020\f¨\u0006\r"},
   d2 = {"Lcom/nexomc/nexo/pack/DisplayProperties;", "", "<init>", "()V", "fromMaterial", "", "Lteam/unnamed/creative/model/ItemTransform$Type;", "Lteam/unnamed/creative/model/ItemTransform;", "material", "Lorg/bukkit/Material;", "fromModel", "model", "Lteam/unnamed/creative/model/Model;", "com.nexomc:core"}
)
public final class DisplayProperties {
   @NotNull
   public static final DisplayProperties INSTANCE = new DisplayProperties();

   private DisplayProperties() {
   }

   @NotNull
   public final Map<ItemTransform.Type, ItemTransform> fromMaterial(@NotNull Material material) {
      ResourcePack resourcePack = VanillaResourcePack.INSTANCE.getResourcePack();
      String str = material.name().toLowerCase(Locale.ROOT);
      Model model = resourcePack.model(Key.key("item/" + str));
      Map map;
      if (model != null) {
         map = model.display();
         if (map != null) {
            return map;
         }
      }

      map = MapsKt.emptyMap();
      return map;
   }

   @NotNull
   public final Map<ItemTransform.Type, ItemTransform> fromModel(@NotNull Model model) {
      Model model2 = VanillaResourcePack.INSTANCE.getResourcePack().model(model.key());
      Map map;
      if (model2 != null) {
         map = model2.display();
         if (map != null) {
            return map;
         }
      }

      map = MapsKt.emptyMap();
      return map;
   }
}
