package com.nexomc.nexo.utils;

import com.nexomc.nexo.pack.VanillaResourcePack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.ResourcePack;
import team.unnamed.creative.atlas.Atlas;
import team.unnamed.creative.atlas.AtlasSource;
import team.unnamed.creative.item.Item;
import team.unnamed.creative.model.Model;
import team.unnamed.creative.overlay.Overlay;
import team.unnamed.creative.overlay.ResourceContainer;
import team.unnamed.creative.texture.Texture;

@Metadata(
   mv = {2, 4, 0},
   k = 2,
   xi = 48,
   d1 = {"\u0000<\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\u001a\u0016\u0010\u0000\u001a\u0004\u0018\u00010\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0004H\u0000\u001a\u001b\u0010\u0005\u001a\t\u0018\u00010\u0006¢\u0006\u0002\b\u0007*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0004H\u0000\u001a\u001b\u0010\b\u001a\t\u0018\u00010\t¢\u0006\u0002\b\u0007*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0004H\u0000\u001a\u001b\u0010\n\u001a\t\u0018\u00010\u000b¢\u0006\u0002\b\u0007*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0004H\u0000\u001a6\u0010\f\u001a$\u0012\f\u0012\n \u000f*\u0004\u0018\u00010\u000e0\u000e \u000f*\u0010\u0012\f\u0012\n \u000f*\u0004\u0018\u00010\u000e0\u000e0\u00100\r*\u00020\u00112\u0006\u0010\u0003\u001a\u00020\u0004H\u0000¨\u0006\u0012"},
   d2 = {"textureOrVanilla", "Lteam/unnamed/creative/texture/Texture;", "Lteam/unnamed/creative/overlay/ResourceContainer;", "key", "Lnet/kyori/adventure/key/Key;", "modelOrVanilla", "Lteam/unnamed/creative/model/Model;", "Lorg/jetbrains/annotations/Nullable;", "itemOrVanilla", "Lteam/unnamed/creative/item/Item;", "atlasOrVanilla", "Lteam/unnamed/creative/atlas/Atlas;", "atlasOrOverlays", "", "Lteam/unnamed/creative/atlas/AtlasSource;", "kotlin.jvm.PlatformType", "", "Lteam/unnamed/creative/ResourcePack;", "com.nexomc:core"}
)
public final class ResourcePackUtilsKt {
   @Nullable
   public static final Texture textureOrVanilla(@NotNull ResourceContainer resourceContainer, @NotNull Key key) {
      Key key_1 = KeyUtils.INSTANCE.removeSuffix(key, ".png");
      Texture texture = resourceContainer.texture(key_1);
      if (texture == null) {
         texture = resourceContainer.texture(KeyUtils.INSTANCE.appendSuffix(key_1, ".png"));
         if (texture == null) {
            texture = VanillaResourcePack.INSTANCE.getResourcePack().texture(key_1);
            if (texture == null) {
               texture = VanillaResourcePack.INSTANCE.getResourcePack().texture(KeyUtils.INSTANCE.appendSuffix(key_1, ".png"));
            }
         }
      }

      return texture;
   }

   @Nullable
   public static final Model modelOrVanilla(@NotNull ResourceContainer resourceContainer, @NotNull Key key) {
      Model model = resourceContainer.model(key);
      if (model == null) {
         model = VanillaResourcePack.INSTANCE.getResourcePack().model(key);
      }

      return model;
   }

   @Nullable
   public static final Item itemOrVanilla(@NotNull ResourceContainer resourceContainer, @NotNull Key key) {
      Item item = resourceContainer.item(key);
      if (item == null) {
         item = VanillaResourcePack.INSTANCE.getResourcePack().item(key);
      }

      return item;
   }

   @Nullable
   public static final Atlas atlasOrVanilla(@NotNull ResourceContainer resourceContainer, @NotNull Key key) {
      Atlas atlas = resourceContainer.atlas(key);
      if (atlas == null) {
         atlas = VanillaResourcePack.INSTANCE.getResourcePack().atlas(key);
      }

      return atlas;
   }

   @NotNull
   public static final List<AtlasSource> atlasOrOverlays(@NotNull ResourcePack resourcePack, @NotNull Key key) {
      Atlas atlas = resourcePack.atlas(key);
      List list;
      if (atlas != null) {
         list = atlas.sources();
         if (list != null) {
            return list;
         }
      }

      Collection overlays = resourcePack.overlays();
      Iterable iterable = (Iterable)overlays;
      ObjectArrayList result = new ObjectArrayList();

      for(Object element : iterable) {
         Overlay it = (Overlay)element;
         int value = 0;
         Atlas atlas2 = resourcePack.atlas(key);
         List list2 = atlas2 != null ? atlas2.sources() : null;
         if (list2 == null) {
            list2 = CollectionsKt.emptyList();
         }

         for(Object item : (Iterable)list2) {
            if (item != null) {
               result.add(item);
            }
         }
      }

      list = (List)result;
      return list;
   }
}
