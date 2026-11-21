package com.nexomc.nexo.pack;

import com.nexomc.nexo.configs.Settings;
import com.nexomc.nexo.utils.CollectionUtilsKt;
import com.nexomc.nexo.utils.KeyUtils;
import com.nexomc.nexo.utils.ResourcePackUtilsKt;
import com.nexomc.nexo.utils.logs.Logs;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import javax.imageio.ImageIO;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Pair;
import kotlin.Triple;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.math.MathKt;
import kotlin.ranges.IntRange;
import kotlin.ranges.RangesKt;
import kotlin.text.StringsKt;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.atlas.Atlas;
import team.unnamed.creative.atlas.PalettedPermutationsAtlasSource;
import team.unnamed.creative.base.CubeFace;
import team.unnamed.creative.base.Vector2Float;
import team.unnamed.creative.base.Vector3Float;
import team.unnamed.creative.base.Writable;
import team.unnamed.creative.model.Element;
import team.unnamed.creative.model.ElementFace;
import team.unnamed.creative.model.Model;
import team.unnamed.creative.model.ModelTexture;
import team.unnamed.creative.model.ModelTextures;
import team.unnamed.creative.overlay.ResourceContainer;
import team.unnamed.creative.texture.Texture;
import team.unnamed.creative.texture.TextureUV;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000ª\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010$\n\u0002\u0018\u0002\n\u0002\u0010\u0012\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0011\n\u0002\u0010\u0018\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\b\u0018\u0000 E2\u00020\u0001:\u0007DEFGHIJB\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0006\u0010\n\u001a\u00020\u000bJ\b\u0010\f\u001a\u00020\u000bH\u0002J\b\u0010\r\u001a\u00020\u000bH\u0002J\u0016\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00100\u000f2\u0006\u0010\u0011\u001a\u00020\u0010H\u0002J\u0018\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0011\u001a\u00020\u00102\u0006\u0010\u0014\u001a\u00020\u0015H\u0002J\u0014\u0010\u0016\u001a\u000e\u0012\u0004\u0012\u00020\u0018\u0012\u0004\u0012\u00020\u00190\u0017H\u0002J\u000e\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u001b0\u000fH\u0002J9\u0010\u001c\u001a\u0010\u0012\u0004\u0012\u00020\u001e\u0012\u0004\u0012\u00020\u001e\u0018\u00010\u001d2\f\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020!0 2\u0006\u0010\"\u001a\u00020\u001e2\u0006\u0010#\u001a\u00020\u001eH\u0002¢\u0006\u0002\u0010$J;\u0010%\u001a\u00020&2\f\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020!0 2\u0006\u0010'\u001a\u00020\u001e2\u0006\u0010(\u001a\u00020\u001e2\u0006\u0010\"\u001a\u00020\u001e2\u0006\u0010#\u001a\u00020\u001eH\u0002¢\u0006\u0002\u0010)J\"\u0010*\u001a\u000e\u0012\u0004\u0012\u00020\u001e\u0012\u0004\u0012\u00020+0\u00172\f\u0010,\u001a\b\u0012\u0004\u0012\u00020\u001b0\u000fH\u0002J(\u00104\u001a\u0004\u0018\u0001052\u0006\u00106\u001a\u0002072\u0014\u00108\u001a\u0010\u0012\u0004\u0012\u00020\u0018\u0012\u0006\u0012\u0004\u0018\u00010\u001809H\u0002J\u001a\u0010:\u001a\u0004\u0018\u00010\u00182\u0006\u00106\u001a\u0002072\u0006\u0010;\u001a\u00020<H\u0002J\u0016\u0010=\u001a\b\u0012\u0004\u0012\u00020\u00100\u000f2\u0006\u00106\u001a\u000207H\u0002J\u0016\u0010>\u001a\u00020\u000b2\f\u0010,\u001a\b\u0012\u0004\u0012\u00020\u001b0\u000fH\u0002J\u0012\u0010?\u001a\b\u0012\u0004\u0012\u00020\u00180\u000f*\u00020@H\u0002J\f\u0010A\u001a\u00020B*\u00020CH\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR3\u0010-\u001a\u001a\u0012\u0016\u0012\u0014 /*\t\u0018\u00010\u0018¢\u0006\u0002\b.0\u0018¢\u0006\u0002\b.0\u000f8BX\u0082\u0084\u0002¢\u0006\f\n\u0004\b2\u00103\u001a\u0004\b0\u00101¨\u0006K"},
   d2 = {"Lcom/nexomc/nexo/pack/TextureMerger;", "", "resourcePack", "Lteam/unnamed/creative/overlay/ResourceContainer;", "<init>", "(Lteam/unnamed/creative/overlay/ResourceContainer;)V", "mergeType", "Lcom/nexomc/nexo/pack/TextureMerger$Type;", "getMergeType", "()Lcom/nexomc/nexo/pack/TextureMerger$Type;", "stitchTextures", "", "stitchSimple", "stitchFull", "splitElementIntoQuadrants", "", "Lteam/unnamed/creative/model/Element;", "element", "defaultUVForFace", "Lteam/unnamed/creative/texture/TextureUV;", "face", "Lteam/unnamed/creative/base/CubeFace;", "collectMergeableTextures", "", "Lnet/kyori/adventure/key/Key;", "", "assignTexturesToGrid", "Lcom/nexomc/nexo/pack/TextureMerger$TextureAssignment;", "findPlacement", "Lkotlin/Pair;", "", "grid", "", "", "cellsWide", "cellsHigh", "([[ZII)Lkotlin/Pair;", "canPlaceTexture", "", "row", "col", "([[ZIIII)Z", "createAtlases", "Ljava/awt/image/BufferedImage;", "assignments", "palettedPermutations", "Lorg/jetbrains/annotations/NotNull;", "kotlin.jvm.PlatformType", "getPalettedPermutations", "()Ljava/util/List;", "palettedPermutations$delegate", "Lkotlin/Lazy;", "remapParticle", "Lteam/unnamed/creative/model/ModelTexture;", "model", "Lteam/unnamed/creative/model/Model;", "atlasKey", "Lkotlin/Function1;", "resolveTextureKeyFromModel", "textureRef", "", "collectElements", "updateModelTextureReferences", "flatten", "Lteam/unnamed/creative/model/ModelTextures;", "toBuilder", "Lteam/unnamed/creative/model/ElementFace$Builder;", "Lteam/unnamed/creative/model/ElementFace;", "Type", "Companion", "GridPosition", "TextureAssignment", "CropKey", "CropPlacement", "Quadrant", "com.nexomc:core"}
)
public final class TextureMerger {
   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   private final ResourceContainer resourcePack;
   @NotNull
   private final Type mergeType;
   @NotNull
   private final Lazy palettedPermutations$delegate;
   public static final int ATLAS_SIZE = 512;
   public static final int CELL_SIZE = 16;
   public static final int GRID_SIZE = 32;
   private static final float ITEM_GEN_MIN_Z = 7.5F;
   private static final float ITEM_GEN_MAX_Z = 8.5F;

   public TextureMerger(@NotNull ResourceContainer resourcePack) {
      super();
      this.resourcePack = resourcePack;
      this.mergeType = Settings.PACK_MERGE_TEXTURES.toEnumOrGet(Type.class, TextureMerger::mergeType$lambda$0);
      this.palettedPermutations$delegate = LazyKt.lazy((() -> TextureMerger.palettedPermutations_delegate$lambda$0(this)));
   }

   @NotNull
   public final Type getMergeType() {
      return this.mergeType;
   }

   public final void stitchTextures() {
      if (this.mergeType != TextureMerger.Type.NONE) {
         Logs.logInfo("Merging all Textures into shared Atlas...");
         if (this.mergeType == TextureMerger.Type.SIMPLE) {
            this.stitchSimple();
         } else {
            this.stitchFull();
         }

         Logs.logSuccess("Finished merging all textures!", false);
      }
   }

   private final void stitchSimple() {
      try {
      List assignments = this.assignTexturesToGrid();
      Map atlasImages = this.createAtlases(assignments);

      for(Map.Entry element : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)atlasImages.entrySet()) {
         int value = 0;
         int index = ((Number)element.getKey()).intValue();
         BufferedImage image = (BufferedImage)element.getValue();
         ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
         int value2 = 0;
         ImageIO.write((RenderedImage)image, "png", (OutputStream)byteArrayOutputStream);
         byte[] output = byteArrayOutputStream.toByteArray();
         Texture texture = Texture.texture(Key.key("nexo", "atlases/atlas_" + index + ".png"), Writable.bytes(output));
         Texture atlasTexture = texture;
         this.resourcePack.texture(atlasTexture);
      }

      this.updateModelTextureReferences(assignments);
      Iterable iterable = (Iterable)assignments;

      for(Object obj : iterable) {
         TextureAssignment it = (TextureAssignment)obj;
         int value3 = 0;
         this.resourcePack.removeTexture(it.getKey());
      }

      } catch (Exception e) {
         throw com.nexomc.nexo.utils.SneakyThrow.sneaky(e);
      }
   }

   private final void stitchFull() {
      try {
      int value;
      int value2;
      Map textures = this.collectMergeableTextures();
      if (!textures.isEmpty()) {
         Map destination = (Map)(new LinkedHashMap(MapsKt.mapCapacity(textures.size())));
         Iterable entrySet = (Iterable)textures.entrySet();
         Map map2 = destination;
         int value36 = 0;

         for(Object obj13 : entrySet) {
            Map.Entry entry4 = (Map.Entry)obj13;
            int value3 = 0;
            Object obj = entry4.getKey();
            Map.Entry entry = (Map.Entry)obj13;
            Object obj2 = obj;
            int value37 = 0;
            byte[] data = (byte[])entry.getValue();
            BufferedImage bufferedImage = ImageIO.read((InputStream)(new ByteArrayInputStream(data)));
            map2.put(obj2, bufferedImage);
         }

         SortedMap textureImages = MapsKt.toSortedMap(map2);
         Iterable keySet = (Iterable)textures.keySet();
         Collection collection = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(keySet, 10)));

         for(Object item : keySet) {
            Key key13 = (Key)item;
            int value4 = 0;
            Key key2 = KeyUtils.INSTANCE.removeSuffix(key13, ".png");
            collection.add(key2);
         }

         SortedSet textureKeySet = CollectionsKt.toSortedSet((Iterable)((List)collection));
         Collection models = this.resourcePack.models();
         Iterable iterable = (Iterable)models;
         iterable = (Iterable)CollectionsKt.sortedWith(iterable, new TextureMerger$stitchFull$$inlined$sortedBy$1());
         Collection collection2 = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

         @Metadata(
            mv = {2, 4, 0},
            k = 1,
            xi = 48,
            d1 = {"\u00001\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000*\u0001\u0000\b\u008a\b\u0018\u00002\u00020\u0001B\u001d\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005¢\u0006\u0004\b\u0007\u0010\bJ\t\u0010\r\u001a\u00020\u0003HÆ\u0003J\u000f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005HÆ\u0003J(\u0010\u000f\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u000e\b\u0002\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005HÆ\u0001¢\u0006\u0002\u0010\u0010J\u0014\u0010\u0011\u001a\u00020\u00122\b\u0010\u0013\u001a\u0004\u0018\u00010\u0001HÖ\u0083\u0004J\n\u0010\u0014\u001a\u00020\u0015HÖ\u0081\u0004J\n\u0010\u0016\u001a\u00020\u0017HÖ\u0081\u0004R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0017\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\f¨\u0006\u0018"},
            d2 = {"com/nexomc/nexo/pack/TextureMerger$stitchFull$ModelData", "", "model", "Lteam/unnamed/creative/model/Model;", "elements", "", "Lteam/unnamed/creative/model/Element;", "<init>", "(Lteam/unnamed/creative/model/Model;Ljava/util/List;)V", "getModel", "()Lteam/unnamed/creative/model/Model;", "getElements", "()Ljava/util/List;", "component1", "component2", "copy", "(Lteam/unnamed/creative/model/Model;Ljava/util/List;)Lcom/nexomc/nexo/pack/TextureMerger$stitchFull$ModelData;", "equals", "", "other", "hashCode", "", "toString", "", "com.nexomc:core"}
         )
         final class ModelData {
            private final Model model;
            private final List<Element> elements;

            public ModelData(Model model, List<Element> elements) {
               super();
               this.model = model;
               this.elements = elements;
            }

            public final Model getModel() {
               return this.model;
            }

            public final List<Element> getElements() {
               return this.elements;
            }

            public final Model component1() {
               return this.model;
            }

            public final List<Element> component2() {
               return this.elements;
            }

            public final ModelData copy(Model model, List<Element> elements) {
               return new ModelData(model, elements);
            }

            public String toString() {
               return "ModelData(model=" + this.model + ", elements=" + this.elements + ")";
            }

            public int hashCode() {
               int result = this.model.hashCode();
               result = result * 31 + this.elements.hashCode();
               return result;
            }

            public boolean equals(Object other) {
               if (this == other) {
                  return true;
               } else if (!(other instanceof ModelData)) {
                  return false;
               } else {
                  ModelData modelData = (ModelData)other;
                  if (!Intrinsics.areEqual(this.model, modelData.model)) {
                     return false;
                  } else {
                     return Intrinsics.areEqual(this.elements, modelData.elements);
                  }
               }
            }
         }

         for(Object obj14 : iterable) {
            Model model = (Model)obj14;
            int value5 = 0;
            List elements = this.collectElements(model);
            Iterable iterable2 = (Iterable)elements;
            Collection collection3 = (Collection)(new ArrayList());

            for(Object obj15 : iterable2) {
               Element element = (Element)obj15;
               int value6 = 0;
               Map faces = element.faces();
               boolean flag;
               if (faces.isEmpty()) {
                  flag = false;
               } else {
                  Iterator iterator = faces.entrySet().iterator();

                  while(true) {
                     if (!iterator.hasNext()) {
                        flag = false;
                        break;
                     }

                     Map.Entry entry5 = (Map.Entry)iterator.next();
                     int value7 = 0;
                     CubeFace cubeFace = (CubeFace)entry5.getKey();
                     ElementFace face = (ElementFace)entry5.getValue();
                     String str = face.texture();
                     Key key3 = this.resolveTextureKeyFromModel(model, str);
                     boolean flag2;
                     if (key3 == null) {
                        flag2 = false;
                     } else {
                        Key resolvedKey = key3;
                        if (!textureKeySet.contains(resolvedKey)) {
                           flag2 = false;
                        } else {
                           TextureUV textureUV = face.uv();
                           if (textureUV == null) {
                              textureUV = this.defaultUVForFace(element, cubeFace);
                           }

                           TextureUV uv = textureUV;
                           float uSpan = Math.abs(uv.to().x() - uv.from().x());
                           float vSpan = Math.abs(uv.to().y() - uv.from().y());
                           flag2 = uSpan > 0.5F && vSpan > 0.5F;
                        }
                     }

                     if (flag2) {
                        flag = true;
                        break;
                     }
                  }
               }

               boolean hasLargeUV = flag;
               Iterable iterable3 = (Iterable)(hasLargeUV ? this.splitElementIntoQuadrants(element) : CollectionsKt.listOf(element));
               CollectionsKt.addAll(collection3, iterable3);
            }

            List splitElements = (List)collection3;
            ModelData modelData2 = new ModelData(model, splitElements);
            collection3.add(modelData2);
         }

         List modelDataList = (List)collection2;
         Set cropSet = (Set)(new LinkedHashSet());
         Iterable iterable4 = (Iterable)modelDataList;

         for(Object obj16 : iterable4) {
            ModelData modelData4 = (ModelData)obj16;
            int crop = 0;
            Model model = modelData4.component1();
            List elements = modelData4.component2();
            Iterable iterable5 = (Iterable)elements;

            for(Object obj17 : iterable5) {
               Element element = (Element)obj17;
               int value8 = 0;
               Map map3 = element.faces();

               for(Map.Entry entry6 : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)map3.entrySet()) {
                  int value9 = 0;
                  CubeFace cubeFace = (CubeFace)entry6.getKey();
                  ElementFace face = (ElementFace)entry6.getValue();
                  String str2 = face.texture();
                  Key key4 = this.resolveTextureKeyFromModel(model, str2);
                  if (key4 != null) {
                     Key resolvedKey = key4;
                     if (textures.containsKey(KeyUtils.INSTANCE.appendPng(resolvedKey))) {
                        TextureUV textureUV2 = face.uv();
                        if (textureUV2 == null) {
                           textureUV2 = this.defaultUVForFace(element, cubeFace);
                        }

                        TextureUV uv = textureUV2;
                        ((Collection)cropSet).add(new CropKey(resolvedKey, Math.min(uv.from().x(), uv.to().x()), Math.min(uv.from().y(), uv.to().y()), Math.max(uv.from().x(), uv.to().x()), Math.max(uv.from().y(), uv.to().y())));
                     }
                  }
               }
            }

            if (elements.isEmpty()) {
               List list = model.textures().layers();
               iterable5 = (Iterable)list;

               for(Object obj18 : iterable5) {
                  ModelTexture layer = (ModelTexture)obj18;
                  int value10 = 0;
                  Key key5 = layer.key();
                  if (key5 != null) {
                     Key key = key5;
                     if (textures.containsKey(KeyUtils.INSTANCE.appendPng(key))) {
                        ((Collection)cropSet).add(new CropKey(key, 0.0F, 0.0F, 1.0F, 1.0F));
                     }
                  }
               }
            }
         }

         if (!cropSet.isEmpty()) {
            Map cropImages = (Map)(new LinkedHashMap());
            Iterable iterable6 = (Iterable)cropSet;

            for(Object obj19 : iterable6) {
               CropKey crop = (CropKey)obj19;
               int value11 = 0;
               BufferedImage bufferedImage2 = (BufferedImage)textureImages.get(KeyUtils.INSTANCE.appendPng(crop.getTextureKey()));
               if (bufferedImage2 != null) {
                  BufferedImage image = bufferedImage2;
                  int px0 = RangesKt.coerceIn(MathKt.roundToInt(crop.getU0() * (float)image.getWidth()), 0, image.getWidth() - 1);
                  int py0 = RangesKt.coerceIn(MathKt.roundToInt(crop.getV0() * (float)image.getHeight()), 0, image.getHeight() - 1);
                  int px1 = RangesKt.coerceIn(MathKt.roundToInt(crop.getU1() * (float)image.getWidth()), 1, image.getWidth());
                  int py1 = RangesKt.coerceIn(MathKt.roundToInt(crop.getV1() * (float)image.getHeight()), 1, image.getHeight());
                  int w = RangesKt.coerceAtLeast(px1 - px0, 1);
                  int h = RangesKt.coerceAtLeast(py1 - py0, 1);
                  BufferedImage cropImage = new BufferedImage(w, h, 2);
                  Graphics2D g = cropImage.createGraphics();
                  g.drawImage((Image)image.getSubimage(px0, py0, w, h), 0, 0, (ImageObserver)null);
                  g.dispose();
                  cropImages.put(crop, cropImage);
               }
            }

            Iterable iterable7 = (Iterable)cropImages.entrySet();
            Map map4 = (Map)(new LinkedHashMap());

            for(Object obj20 : iterable7) {
               Map.Entry it = (Map.Entry)obj20;
               int value38 = 0;
               Object height = ((BufferedImage)it.getValue()).getHeight();
               Object obj21 = map4.get(height);
               Object obj3;
               if (obj21 == null) {
                  int answer = 0;
                  Object obj22 = (List)(new ArrayList());
                  map4.put(height, obj22);
                  obj3 = obj22;
               } else {
                  obj3 = obj21;
               }

               List list4 = (List)obj3;
               list4.add(obj20);
            }

            iterable7 = (Iterable)map4.entrySet();
            iterable7 = (Iterable)CollectionsKt.sortedWith(iterable7, new TextureMerger$stitchFull$$inlined$sortedByDescending$1());
            Collection collection4 = (Collection)(new ArrayList());

            for(Object obj23 : iterable7) {
               Map.Entry entry7 = (Map.Entry)obj23;
               int value39 = 0;
               List group = (List)entry7.getValue();
               Iterable iterable8 = (Iterable)group;
               Iterable iterable9 = (Iterable)CollectionsKt.sortedWith(iterable8, new TextureMerger$stitchFull$lambda$8$$inlined$sortedBy$1());
               CollectionsKt.addAll(collection4, iterable9);
            }

            List sortedCrops = (List)collection4;
            Map cropPlacements = (Map)(new LinkedHashMap());
            Pair pair = TuplesKt.to(0, 0);
            value36 = 0;
            value36 = ((Number)pair.component1()).intValue();
            int shelfY = 0;
            shelfY = ((Number)pair.component2()).intValue();
            Pair pair2 = TuplesKt.to(0, 0);
            int shelfHeight = 0;
            shelfHeight = ((Number)pair2.component1()).intValue();
            int atlasIndex = 0;
            atlasIndex = ((Number)pair2.component2()).intValue();
            Iterable iterable10 = (Iterable)sortedCrops;

            for(Object obj24 : iterable10) {
               Map.Entry entry8 = (Map.Entry)obj24;
               int value12 = 0;
               CropKey crop = (CropKey)entry8.getKey();
               BufferedImage image = (BufferedImage)entry8.getValue();
               Pair pair5 = TuplesKt.to(image.getWidth(), image.getHeight());
               int w = ((Number)pair5.component1()).intValue();
               int h = ((Number)pair5.component2()).intValue();
               if (value36 + w > 512) {
                  shelfY += shelfHeight;
                  value36 = 0;
                  shelfHeight = 0;
               }

               if (shelfY + h > 512) {
                  ++atlasIndex;
                  value36 = 0;
                  shelfY = 0;
                  shelfHeight = 0;
               }

               cropPlacements.put(crop, new CropPlacement(value36, shelfY, w, h, atlasIndex));
               value36 += w;
               shelfHeight = Math.max(shelfHeight, h);
            }

            Iterable i = (Iterable)cropPlacements.values();
            Iterator iterator4 = i.iterator();
            Comparable comparable;
            if (!iterator4.hasNext()) {
               comparable = null;
            } else {
               CropPlacement it = (CropPlacement)iterator4.next();
               CropPlacement it_93 = it;
               Comparable atlas = (Comparable)it_93.getAtlasIndex();

               while(iterator4.hasNext()) {
                  CropPlacement it_92 = (CropPlacement)iterator4.next();
                  int value13 = 0;
                  Comparable it_91 = (Comparable)it_92.getAtlasIndex();
                  if (atlas.compareTo(it_91) < 0) {
                     atlas = it_91;
                  }
               }

               comparable = atlas;
            }

            Integer number = (Integer)comparable;
            int atlasCount = (number != null ? number : -1) + 1;

            for(int j = 0; j < atlasCount; ++j) {
               int i_90 = j;
               int value14 = 0;
               BufferedImage atlas = new BufferedImage(512, 512, 2);
               Graphics2D g = atlas.createGraphics();

               for(Map.Entry entry9 : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)cropPlacements.entrySet()) {
                  CropKey crop = (CropKey)entry9.getKey();
                  CropPlacement placement = (CropPlacement)entry9.getValue();
                  if (placement.getAtlasIndex() == i_90) {
                     BufferedImage bufferedImage3 = (BufferedImage)cropImages.get(crop);
                     if (bufferedImage3 != null) {
                        BufferedImage it = bufferedImage3;
                        int value15 = 0;
                        g.drawImage((Image)it, placement.getX(), placement.getY(), (ImageObserver)null);
                        Unit unit = Unit.INSTANCE;
                        unit = Unit.INSTANCE;
                     } else {
                        Object obj4 = null;
                     }
                  }
               }

               g.dispose();
               ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
               int value16 = 0;
               ImageIO.write((RenderedImage)atlas, "png", (OutputStream)byteArrayOutputStream);
               byte[] output = byteArrayOutputStream.toByteArray();
               this.resourcePack.texture(Texture.texture(Key.key("nexo", "atlases/atlas_" + i_90 + ".png"), Writable.bytes(output)));
            }

            Iterable iterable11 = (Iterable)modelDataList;

            for(Object obj25 : iterable11) {
               ModelData modelData3 = (ModelData)obj25;
               int output = 0;
               Model model = modelData3.component1();
               List elements = modelData3.component2();
               Set neededAtlases = (Set)(new LinkedHashSet());
               Iterable iterable12 = (Iterable)elements;

               for(Object obj26 : iterable12) {
                  Element element = (Element)obj26;
                  int value17 = 0;
                  Map map5 = element.faces();

                  for(Map.Entry entry10 : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)map5.entrySet()) {
                     int value18 = 0;
                     CubeFace cubeFace = (CubeFace)entry10.getKey();
                     ElementFace face = (ElementFace)entry10.getValue();
                     String str3 = face.texture();
                     Key key6 = this.resolveTextureKeyFromModel(model, str3);
                     if (key6 != null) {
                        Key resolvedKey = key6;
                        if (textureKeySet.contains(resolvedKey)) {
                           TextureUV textureUV3 = face.uv();
                           if (textureUV3 == null) {
                              textureUV3 = this.defaultUVForFace(element, cubeFace);
                           }

                           TextureUV it = textureUV3;
                           int value19 = 0;
                           Pair pair3 = TuplesKt.to(it.from(), it.to());
                           Object component1 = pair3.component1();
                           Vector2Float from = (Vector2Float)component1;
                           component1 = pair3.component2();
                           Vector2Float to = (Vector2Float)component1;
                           CropKey crop = new CropKey(resolvedKey, Math.min(from.x(), to.x()), Math.min(from.y(), to.y()), Math.max(from.x(), to.x()), Math.max(from.y(), to.y()));
                           CropPlacement cropPlacement = (CropPlacement)cropPlacements.get(crop);
                           if (cropPlacement != null) {
                              CropPlacement it_77 = cropPlacement;
                              int p0 = 0;
                              ((Collection)neededAtlases).add(it_77.getAtlasIndex());
                              Unit unit2 = Unit.INSTANCE;
                              unit2 = Unit.INSTANCE;
                           } else {
                              Object obj5 = null;
                           }
                        }
                     }
                  }
               }

               if (elements.isEmpty()) {
                  List list2 = model.textures().layers();
                  iterable12 = (Iterable)list2;

                  for(Object obj27 : iterable12) {
                     ModelTexture layer = (ModelTexture)obj27;
                     int value20 = 0;
                     Key key7 = layer.key();
                     if (key7 != null) {
                        Key key14 = key7;
                        int value21 = 0;
                        key7 = textureKeySet.contains(key14) ? key14 : null;
                        if (key7 != null) {
                           Key key = key7;
                           CropKey crop = new CropKey(key, 0.0F, 0.0F, 1.0F, 1.0F);
                           CropPlacement cropPlacement2 = (CropPlacement)cropPlacements.get(crop);
                           if (cropPlacement2 != null) {
                              CropPlacement it = cropPlacement2;
                              value21 = 0;
                              ((Collection)neededAtlases).add(it.getAtlasIndex());
                              Unit unit3 = Unit.INSTANCE;
                              unit3 = Unit.INSTANCE;
                           } else {
                              Object obj6 = null;
                           }
                        }
                     }
                  }
               }

               if (!neededAtlases.isEmpty()) {
                  Iterable iterable13 = (Iterable)CollectionsKt.sorted((Iterable)neededAtlases);
                  Collection collection5 = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable13, 10)));

                  for(Object obj28 : iterable13) {
                     int intValue = ((Number)obj28).intValue();
                     int value22 = 0;
                     collection5.add(ModelTexture.ofKey(Key.key("nexo", "atlases/atlas_" + intValue)));
                  }

                  List layers = (List)collection5;
                  Map map = model.textures().variables();
                  Map map6 = map;
                  Map map7 = (Map)(new LinkedHashMap());
                  int value40 = 0;

                  for(Map.Entry entry11 : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)map6.entrySet()) {
                     int value23 = 0;
                     Key key8 = ((ModelTexture)entry11.getValue()).key();
                     boolean flag3;
                     if (key8 == null) {
                        flag3 = true;
                     } else {
                        Key key = key8;
                        if (textureKeySet.contains(key)) {
                           flag3 = false;
                        } else {
                           Texture texture2 = this.resourcePack.texture(KeyUtils.INSTANCE.appendPng(key));
                           if (texture2 == null) {
                              flag3 = false;
                           } else {
                              Texture texture = texture2;
                              flag3 = texture.hasMetadata() || this.getPalettedPermutations().contains(texture.key());
                           }
                        }
                     }

                     if (flag3) {
                        map7.put(entry11.getKey(), entry11.getValue());
                     }
                  }

                  ModelTexture particle = this.remapParticle(model, (a0_ -> TextureMerger.stitchFull$lambda$12$4(cropPlacements, a0_)));
                  ModelTextures updatedTextures = ModelTextures.of(layers, particle, map7);
                  Iterable iterable14 = (Iterable)elements;
                  value40 = 0;
                  Collection collection6 = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable14, 10)));

                  for(Object obj29 : iterable14) {
                     Element element = (Element)obj29;
                     int value24 = 0;
                     Map map8 = element.faces();
                     Map map9 = (Map)(new LinkedHashMap(MapsKt.mapCapacity(map8.size())));
                     Iterable iterable15 = (Iterable)map8.entrySet();
                     Map map10 = map9;

                     for(Object obj30 : iterable15) {
                        Map.Entry entry12 = (Map.Entry)obj30;
                        int value25 = 0;
                        Object obj7 = entry12.getKey();
                        Map.Entry entry2 = (Map.Entry)obj30;
                        Object obj8 = obj7;
                        int value26 = 0;
                        CubeFace cubeFace = (CubeFace)entry2.getKey();
                        ElementFace face = (ElementFace)entry2.getValue();
                        String str4 = face.texture();
                        Key key9 = this.resolveTextureKeyFromModel(model, str4);
                        ElementFace elementFace;
                        if (key9 == null) {
                           elementFace = face;
                        } else {
                           Key resolvedKey = key9;
                           if (!textureKeySet.contains(resolvedKey)) {
                              elementFace = face;
                           } else {
                              Texture texture = this.resourcePack.texture(KeyUtils.INSTANCE.appendPng(resolvedKey));
                              if (!(texture != null ? texture.hasMetadata() : false) && !this.getPalettedPermutations().contains(texture != null ? texture.key() : null)) {
                                 TextureUV textureUV4 = face.uv();
                                 if (textureUV4 == null) {
                                    textureUV4 = this.defaultUVForFace(element, cubeFace);
                                 }

                                 TextureUV it = textureUV4;
                                 int patchedFaces = 0;
                                 Pair pair4 = TuplesKt.to(it.from(), it.to());
                                 Object obj9 = pair4.component1();
                                 Vector2Float from = (Vector2Float)obj9;
                                 obj9 = pair4.component2();
                                 Vector2Float to = (Vector2Float)obj9;
                                 CropKey crop = new CropKey(resolvedKey, Math.min(from.x(), to.x()), Math.min(from.y(), to.y()), Math.max(from.x(), to.x()), Math.max(from.y(), to.y()));
                                 CropPlacement cropPlacement3 = (CropPlacement)cropPlacements.get(crop);
                                 if (cropPlacement3 == null) {
                                    elementFace = face;
                                 } else {
                                    CropPlacement placement = cropPlacement3;
                                    float newMinU = (float)placement.getX() / (float)512;
                                    float newMinV = (float)placement.getY() / (float)512;
                                    float newMaxU = (float)(placement.getX() + placement.getWidth()) / (float)512;
                                    float newMaxV = (float)(placement.getY() + placement.getHeight()) / (float)512;
                                    float newU0 = from.x() <= to.x() ? newMinU : newMaxU;
                                    float newV0 = from.y() <= to.y() ? newMinV : newMaxV;
                                    float newU1 = from.x() <= to.x() ? newMaxU : newMinU;
                                    float newV1 = from.y() <= to.y() ? newMaxV : newMinV;
                                    Key key10 = Key.key("nexo", "atlases/atlas_" + placement.getAtlasIndex());
                                    Key atlasKey = key10;
                                    int value41 = 0;
                                    int index = 0;
                                    Iterator iterator2 = layers.iterator();

                                    while(true) {
                                       if (!iterator2.hasNext()) {
                                          value = -1;
                                          break;
                                       }

                                       Object obj31 = iterator2.next();
                                       ModelTexture it_61 = (ModelTexture)obj31;
                                       int value27 = 0;
                                       if (Intrinsics.areEqual(it_61.key(), atlasKey)) {
                                          value = index;
                                          break;
                                       }

                                       ++index;
                                    }

                                    Integer number4 = value;
                                    value41 = ((Number)number4).intValue();
                                    index = 0;
                                    Integer number2 = value41 != -1 ? number4 : null;
                                    if ((value41 != -1 ? number4 : null) != null) {
                                       int layerIndex = number2;
                                       elementFace = this.toBuilder(face).uv(TextureUV.uv(newU0, newV0, newU1, newV1)).texture("#layer" + layerIndex).build();
                                    } else {
                                       elementFace = face;
                                    }
                                 }
                              } else {
                                 elementFace = face;
                              }
                           }
                        }

                        ElementFace elementFace2 = elementFace;
                        map10.put(obj8, elementFace2);
                     }

                     collection6.add(element.toBuilder().faces(map10).build());
                  }

                  Collection variables = (Collection)((List)collection6);
                  Object obj10;
                  if (variables.isEmpty()) {
                     value40 = 0;
                     List list3 = model.textures().layers();
                     Iterable iterable16 = (Iterable)list3;
                     Collection collection7 = (Collection)(new ArrayList());

                     for(Object obj32 : iterable16) {
                        Triple triple;
                        block: {
                           int image = 0;
                           ModelTexture layerTex = (ModelTexture)obj32;
                           int value28 = 0;
                           Key key11 = layerTex.key();
                           if (key11 != null) {
                              Key atlasKey = key11;
                              int value42 = 0;
                              key11 = textureKeySet.contains(atlasKey) ? atlasKey : null;
                              if (key11 != null) {
                                 Key key = key11;
                                 CropPlacement cropPlacement4 = (CropPlacement)cropPlacements.get(new CropKey(key, 0.0F, 0.0F, 1.0F, 1.0F));
                                 if (cropPlacement4 == null) {
                                    triple = null;
                                    break block;
                                 }

                                 CropPlacement placement = cropPlacement4;
                                 BufferedImage bufferedImage4 = (BufferedImage)textureImages.get(KeyUtils.INSTANCE.appendPng(key));
                                 if (bufferedImage4 == null) {
                                    triple = null;
                                    break block;
                                 }

                                 BufferedImage image_57 = bufferedImage4;
                                 Key key12 = Key.key("nexo", "atlases/atlas_" + placement.getAtlasIndex());
                                 atlasKey = key12;
                                 int value43 = 0;
                                 int value44 = 0;
                                 Iterator iterator3 = layers.iterator();

                                 while(true) {
                                    if (!iterator3.hasNext()) {
                                       value2 = -1;
                                       break;
                                    }

                                    Object obj33 = iterator3.next();
                                    ModelTexture it = (ModelTexture)obj33;
                                    int value29 = 0;
                                    if (Intrinsics.areEqual(it.key(), atlasKey)) {
                                       value2 = value44;
                                       break;
                                    }

                                    ++value44;
                                 }

                                 Integer number5 = value2;
                                 value43 = ((Number)number5).intValue();
                                 value44 = 0;
                                 Integer number3 = value43 != -1 ? number5 : null;
                                 if ((value43 != -1 ? number5 : null) != null) {
                                    int p0 = number3;
                                    triple = new Triple(p0, image_57, placement);
                                 } else {
                                    triple = null;
                                 }
                                 break block;
                              }
                           }

                           triple = null;
                        }

                        if (triple != null) {
                           Object obj34 = triple;
                           int value30 = 0;
                           collection7.add(obj34);
                        }
                     }

                     List layerInfos = (List)collection7;
                     if (layerInfos.isEmpty()) {
                        continue;
                     }

                     iterable16 = (Iterable)layerInfos;
                     collection7 = (Collection)(new ArrayList());

                     for(Object obj35 : iterable16) {
                        Triple triple2 = (Triple)obj35;
                        int value31 = 0;
                        int atlasLayerIdx = ((Number)triple2.component1()).intValue();
                        BufferedImage image = (BufferedImage)triple2.component2();
                        CropPlacement placement = (CropPlacement)triple2.component3();
                        Iterable iterable17 = (Iterable)Companion.generateLayerElements(atlasLayerIdx, image);
                        Collection collection8 = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable17, 10)));

                        for(Object obj36 : iterable17) {
                           Element element = (Element)obj36;
                           int value32 = 0;
                           Map map11 = element.faces();
                           Map map12 = (Map)(new LinkedHashMap(MapsKt.mapCapacity(map11.size())));
                           Iterable iterable18 = (Iterable)map11.entrySet();
                           Map map13 = map12;

                           for(Object obj37 : iterable18) {
                              Map.Entry entry13 = (Map.Entry)obj37;
                              int value33 = 0;
                              Object obj11 = entry13.getKey();
                              Map.Entry entry3 = (Map.Entry)obj37;
                              Object obj12 = obj11;
                              int value34 = 0;
                              ElementFace face = (ElementFace)entry3.getValue();
                              TextureUV textureUV5 = face.uv();
                              ElementFace elementFace3;
                              if (textureUV5 == null) {
                                 elementFace3 = face;
                              } else {
                                 TextureUV uv = textureUV5;
                                 float u0 = ((float)placement.getX() + uv.from().x() * (float)placement.getWidth()) / (float)512;
                                 float v0 = ((float)placement.getY() + uv.from().y() * (float)placement.getHeight()) / (float)512;
                                 float u1 = ((float)placement.getX() + uv.to().x() * (float)placement.getWidth()) / (float)512;
                                 float v1 = ((float)placement.getY() + uv.to().y() * (float)placement.getHeight()) / (float)512;
                                 elementFace3 = this.toBuilder(face).uv(TextureUV.uv(u0, v0, u1, v1)).build();
                              }

                              ElementFace elementFace4 = elementFace3;
                              map13.put(obj12, elementFace4);
                           }

                           collection8.add(element.toBuilder().faces(map13).build());
                        }

                        Iterable iterable19 = (Iterable)((List)collection8);
                        CollectionsKt.addAll(collection8, iterable19);
                     }

                     obj10 = (List)collection7;
                  } else {
                     obj10 = variables;
                  }

                  List patchedElements = (List)obj10;
                  model.toBuilder().textures(updatedTextures).elements(patchedElements).build().addTo(this.resourcePack);
               }
            }

            iterable11 = (Iterable)textures.keySet();

            for(Object obj38 : iterable11) {
               Key it = (Key)obj38;
               int value35 = 0;
               this.resourcePack.removeTexture(it);
            }

         }
      }
      } catch (Exception e) {
         throw com.nexomc.nexo.utils.SneakyThrow.sneaky(e);
      }
   }

   private final List<Element> splitElementIntoQuadrants(Element element) {
      Pair pair = TuplesKt.to(element.from(), element.to());
      Object component1 = pair.component1();
      Vector3Float from = (Vector3Float)component1;
      component1 = pair.component2();
      Vector3Float to = (Vector3Float)component1;
      float midX = (from.x() + to.x()) / 2.0F;
      float midZ = (from.z() + to.z()) / 2.0F;
      Quadrant[] quadrants2 = new Quadrant[]{new Quadrant(from, new Vector3Float(midX, to.y(), midZ), true, true), new Quadrant(new Vector3Float(midX, from.y(), from.z()), new Vector3Float(to.x(), to.y(), midZ), false, true), new Quadrant(new Vector3Float(from.x(), from.y(), midZ), new Vector3Float(midX, to.y(), to.z()), true, false), new Quadrant(new Vector3Float(midX, from.y(), midZ), to, false, false)};
      List quadrants = CollectionsKt.listOf(quadrants2);
      Iterable iterable = (Iterable)quadrants;
      Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

      for(Object item : iterable) {
         Quadrant quad = (Quadrant)item;
         int value = 0;
         Map faces = (Map)(new LinkedHashMap());

         for(Map.Entry entry : element.faces().entrySet()) {
            CubeFace cubeFace = (CubeFace)entry.getKey();
            ElementFace face = (ElementFace)entry.getValue();
            TextureUV textureUV = face.uv();
            if (textureUV == null) {
               textureUV = this.defaultUVForFace(element, cubeFace);
            }

            TextureUV uv = textureUV;
            Pair pair2 = TuplesKt.to(uv.from().x(), uv.from().y());
            float u0 = ((Number)pair2.component1()).floatValue();
            float v0 = ((Number)pair2.component2()).floatValue();
            Pair pair3 = TuplesKt.to(uv.to().x(), uv.to().y());
            float u1 = ((Number)pair3.component1()).floatValue();
            float v1 = ((Number)pair3.component2()).floatValue();
            Pair pair4 = TuplesKt.to((u0 + u1) / 2.0F, (v0 + v1) / 2.0F);
            float midU = ((Number)pair4.component1()).floatValue();
            float midV = ((Number)pair4.component2()).floatValue();
            switch (cubeFace == null ? -1 : TextureMerger.WhenMappings.$EnumSwitchMapping$0[cubeFace.ordinal()]) {
               case 1 -> textureUV = TextureUV.uv(quad.getLowX() ? u0 : midU, v0, quad.getLowX() ? midU : u1, v1);
               case 2 -> textureUV = TextureUV.uv(quad.getLowX() ? midU : u0, v0, quad.getLowX() ? u1 : midU, v1);
               case 3 -> textureUV = TextureUV.uv(quad.getLowZ() ? midU : u0, v0, quad.getLowZ() ? u1 : midU, v1);
               case 4 -> textureUV = TextureUV.uv(quad.getLowZ() ? u0 : midU, v0, quad.getLowZ() ? midU : u1, v1);
               case 5 -> textureUV = TextureUV.uv(quad.getLowX() ? u0 : midU, quad.getLowZ() ? v0 : midV, quad.getLowX() ? midU : u1, quad.getLowZ() ? midV : v1);
               case 6 -> textureUV = TextureUV.uv(quad.getLowX() ? u0 : midU, quad.getLowZ() ? midV : v0, quad.getLowX() ? midU : u1, quad.getLowZ() ? v1 : midV);
               default -> throw new NoWhenBranchMatchedException();
            }

            TextureUV newUV = textureUV;
            faces.put(cubeFace, this.toBuilder(face).uv(newUV).build());
         }

         destination.add(element.toBuilder().from(quad.getQFrom()).to(quad.getQTo()).faces(faces).build());
      }

      return (List)destination;
   }

   private final TextureUV defaultUVForFace(Element element, CubeFace face) {
      Vector3Float vector3Float = element.from();
      Vector3Float from = vector3Float;
      vector3Float = element.to();
      Vector3Float to = vector3Float;
      TextureUV textureUV;
      switch (TextureMerger.WhenMappings.$EnumSwitchMapping$0[face.ordinal()]) {
         case 1:
            textureUV = TextureUV.uv(from.x() / 16.0F, (16.0F - to.y()) / 16.0F, to.x() / 16.0F, (16.0F - from.y()) / 16.0F);
            break;
         case 2:
            textureUV = TextureUV.uv((16.0F - to.x()) / 16.0F, (16.0F - to.y()) / 16.0F, (16.0F - from.x()) / 16.0F, (16.0F - from.y()) / 16.0F);
            break;
         case 3:
            textureUV = TextureUV.uv((16.0F - to.z()) / 16.0F, (16.0F - to.y()) / 16.0F, (16.0F - from.z()) / 16.0F, (16.0F - from.y()) / 16.0F);
            break;
         case 4:
            textureUV = TextureUV.uv(from.z() / 16.0F, (16.0F - to.y()) / 16.0F, to.z() / 16.0F, (16.0F - from.y()) / 16.0F);
            break;
         case 5:
            textureUV = TextureUV.uv(from.x() / 16.0F, from.z() / 16.0F, to.x() / 16.0F, to.z() / 16.0F);
            break;
         case 6:
            textureUV = TextureUV.uv(from.x() / 16.0F, (16.0F - to.z()) / 16.0F, to.x() / 16.0F, (16.0F - from.z()) / 16.0F);
            break;
         default:
            throw new NoWhenBranchMatchedException();
      }

      return textureUV;
   }

   private final Map<Key, byte[]> collectMergeableTextures() {
      try {
      boolean flag;
      Collection models = this.resourcePack.models();
      Iterable iterable = (Iterable)models;
      Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

      for(Object item : iterable) {
         Model it = (Model)item;
         int value = 0;
         ModelTextures textures = it.textures();
         Iterable iterable2 = (Iterable)this.flatten(textures);
         Collection collection = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable2, 10)));

         for(Object obj : iterable2) {
            Key it_40 = (Key)obj;
            int element = 0;
            collection.add(KeyUtils.INSTANCE.appendPng(it_40));
         }

         collection.add((List)collection);
      }

      iterable = (Iterable)((List)destination);
      destination = (Collection)(new ArrayList());

      for(Object obj2 : iterable) {
         List it = (List)obj2;
         int value2 = 0;
         Iterable iterable3 = (Iterable)it;
         ResourceContainer resourceContainer = this.resourcePack;
         Collection collection2 = (Collection)(new ArrayList());

         for(Object obj3 : iterable3) {
            int value3 = 0;
            Key p0 = (Key)obj3;
            int value4 = 0;
            Texture texture = resourceContainer.texture(p0);
            if (texture != null) {
               Object obj4 = texture;
               int value5 = 0;
               collection2.add(obj4);
            }
         }

         iterable3 = (Iterable)((List)collection2);
         HashSet hashSet = new HashSet();
         ArrayList list = new ArrayList();

         for(Object obj5 : iterable3) {
            Texture it_37 = (Texture)obj5;
            Object obj6 = it_37.key();
            if (hashSet.add(obj6)) {
               list.add(obj5);
            }
         }

         Iterable iterable4 = (Iterable)((List)list);
         CollectionsKt.addAll(collection2, iterable4);
      }

      iterable = (Iterable)((List)destination);
      destination = (Collection)(new ArrayList());

      for(Object obj7 : iterable) {
         block: {
            Texture it = (Texture)obj7;
            int value6 = 0;
            if (!it.hasMetadata()) {
               List palettedPermutations = this.getPalettedPermutations();
               KeyUtils keyUtils = KeyUtils.INSTANCE;
               Key key = it.key();
               if (!palettedPermutations.contains(keyUtils.appendPng(key))) {
                  flag = false;
                  break block;
               }
            }

            flag = true;
         }

         if (!flag) {
            destination.add(obj7);
         }
      }

      iterable = (Iterable)((List)destination);
      int capacity = RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault(iterable, 10)), 16);
      Map map = (Map)(new LinkedHashMap(capacity));

      for(Object obj8 : iterable) {
         Texture it = (Texture)obj8;
         int value7 = 0;
         Pair pair = TuplesKt.to(it.key(), it.data().toByteArray());
         map.put(pair.getFirst(), pair.getSecond());
      }

      return map;
      } catch (Exception e) {
         throw com.nexomc.nexo.utils.SneakyThrow.sneaky(e);
      }
   }

   private final List<TextureAssignment> assignTexturesToGrid() {
      try {
      Map textures = this.collectMergeableTextures();
      List grids = (List)(new ArrayList());
      List assignments = (List)(new ArrayList());

      for(Map.Entry element : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)textures.entrySet()) {
         int value = 0;
         Key key = (Key)element.getKey();
         byte[] data = (byte[])element.getValue();
         BufferedImage image = ImageIO.read((InputStream)(new ByteArrayInputStream(data)));
         int cellsWide = RangesKt.coerceAtLeast(image.getWidth() / 16, 1);
         int cellsHigh = RangesKt.coerceAtLeast(image.getHeight() / 16, 1);
         boolean assigned = false;
         Set positions = (Set)(new LinkedHashSet());
         Iterator iterator = ((Iterable)grids).iterator();
         int atlasIndex = 0;

         while(iterator.hasNext()) {
            int atlasIndex_35 = atlasIndex++;
            boolean[][] grid = (boolean[][])iterator.next();
            Pair pair = this.findPlacement(grid, cellsWide, cellsHigh);
            if (pair != null) {
               Pair pair2 = pair;
               int row = ((Number)pair2.component1()).intValue();
               int col = ((Number)pair2.component2()).intValue();
               int r = row;

               for(int i = row + cellsHigh; r < i; ++r) {
                  int c = col;

                  for(int j = col + cellsWide; c < j; ++c) {
                     grid[r][c] = true;
                     ((Collection)positions).add(new GridPosition(r, c));
                  }
               }

               Collection collection = (Collection)assignments;
               int width = image.getWidth();
               int height = image.getHeight();
               collection.add(new TextureAssignment(key, positions, width, height, atlasIndex_35, image));
               assigned = true;
               break;
            }
         }

         if (!assigned) {
            atlasIndex = 0;

            boolean[][] r;
            for(r = new boolean[32][]; atlasIndex < 32; ++atlasIndex) {
               r[atlasIndex] = new boolean[32];
            }

            boolean[][] newGrid = r;
            grids.add(r);
            atlasIndex = grids.size() - 1;
            if (cellsWide <= 32 && cellsHigh <= 32) {
               for(int r_34 = 0; r_34 < cellsHigh; ++r_34) {
                  for(int c = 0; c < cellsWide; ++c) {
                     newGrid[r_34][c] = true;
                     positions.add(new GridPosition(r_34, c));
                  }
               }

               int value2 = image.getWidth();
               int value3 = image.getHeight();
               assignments.add(new TextureAssignment(key, positions, value2, value3, atlasIndex, image));
            }
         }
      }

      return assignments;
      } catch (Exception e) {
         throw com.nexomc.nexo.utils.SneakyThrow.sneaky(e);
      }
   }

   private final Pair<Integer, Integer> findPlacement(boolean[][] grid, int cellsWide, int cellsHigh) {
      int row = 0;
      int value = 32 - cellsHigh;
      if (row <= value) {
         while(true) {
            int col = 0;
            int value2 = 32 - cellsWide;
            if (col <= value2) {
               while(true) {
                  if (this.canPlaceTexture(grid, row, col, cellsWide, cellsHigh)) {
                     return TuplesKt.to(row, col);
                  }

                  if (col == value2) {
                     break;
                  }

                  ++col;
               }
            }

            if (row == value) {
               break;
            }

            ++row;
         }
      }

      return null;
   }

   private final boolean canPlaceTexture(boolean[][] grid, int row, int col, int cellsWide, int cellsHigh) {
      int r = row;

      for(int i = row + cellsHigh; r < i; ++r) {
         int c = col;

         for(int j = col + cellsWide; c < j; ++c) {
            if (r >= 32 || c >= 32 || grid[r][c]) {
               return false;
            }
         }
      }

      return true;
   }

   private final Map<Integer, BufferedImage> createAtlases(List<TextureAssignment> assignments) {
      Iterable iterable = (Iterable)assignments;
      Map destination = (Map)(new LinkedHashMap());

      for(Object element : iterable) {
         TextureAssignment it = (TextureAssignment)element;
         int value = 0;
         Object key = it.getAtlasIndex();
         Object obj3 = destination.get(key);
         Object obj;
         if (obj3 == null) {
            int value2 = 0;
            Object answer = (List)(new ArrayList());
            destination.put(key, answer);
            obj = answer;
         } else {
            obj = obj3;
         }

         List list = (List)obj;
         list.add(element);
      }

      Map map3 = destination;
      int capacity = RangesKt.coerceAtLeast(CollectionUtilsKt.mapCapacity(destination.size()), 16);
      Object2ObjectLinkedOpenHashMap result = new Object2ObjectLinkedOpenHashMap(capacity);

      for(Map.Entry entry : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)map3.entrySet()) {
         Object obj4 = entry.getKey();
         Object obj5 = entry.getValue();
         Map map = (Map)result;
         List textureAssignments = (List)obj5;
         int atlasIndex = ((Number)obj4).intValue();
         Map map2 = map;
         int value6 = 0;
         BufferedImage atlas = new BufferedImage(512, 512, 2);
         Graphics2D graphics = atlas.createGraphics();
         Iterable iterable2 = (Iterable)textureAssignments;

         for(Object obj6 : iterable2) {
            TextureAssignment assignment = (TextureAssignment)obj6;
            int value3 = 0;
            BufferedImage image = assignment.getImage();
            Iterable positions = (Iterable)assignment.getPositions();
            int value7 = 0;
            Iterator iterator = positions.iterator();
            Object obj2;
            if (!iterator.hasNext()) {
               obj2 = null;
            } else {
               Object minElem = iterator.next();
               if (!iterator.hasNext()) {
                  obj2 = minElem;
               } else {
                  GridPosition it = (GridPosition)minElem;
                  int value4 = 0;
                  int minValue = it.getRow() * 32 + it.getCol();

                  do {
                     Object obj7 = iterator.next();
                     GridPosition it_33 = (GridPosition)obj7;
                     int value5 = 0;
                     int value8 = it_33.getRow() * 32 + it_33.getCol();
                     if (minValue > value8) {
                        minElem = obj7;
                        minValue = value8;
                     }
                  } while(iterator.hasNext());

                  obj2 = minElem;
               }
            }

            GridPosition firstPos = (GridPosition)obj2;
            int x = firstPos.getCol() * 16;
            value7 = firstPos.getRow() * 16;
            graphics.drawImage((Image)image, x, value7, (ImageObserver)null);
         }

         graphics.dispose();
         map2.put(obj4, atlas);
      }

      return (Map)result;
   }

   private final List<Key> getPalettedPermutations() {
      Lazy lazy = this.palettedPermutations$delegate;
      return (List)lazy.getValue();
   }

   private final ModelTexture remapParticle(Model model, Function1<? super Key, ? extends Key> atlasKey) {
      ModelTexture modelTexture = model.textures().particle();
      if (modelTexture == null) {
         return null;
      } else {
         ModelTexture particle = modelTexture;
         Key directKey = particle.key();
         if (directKey == null) {
            String reference = particle.reference();
            if (reference != null) {
               String it = reference;
               int value = 0;
               Key key = this.resolveTextureKeyFromModel(model, it);
               if (key != null) {
                  Key resolvedKey = key;
                  key = (Key)atlasKey.invoke(resolvedKey);
                  ModelTexture modelTexture2;
                  if (key != null) {
                     Key p0 = key;
                     value = 0;
                     modelTexture2 = ModelTexture.ofKey(p0);
                     if (modelTexture2 != null) {
                        return modelTexture2;
                     }
                  }

                  modelTexture2 = particle;
                  return modelTexture2;
               }
            }

            return particle;
         } else if (atlasKey.invoke(directKey) != null) {
            return null;
         } else {
            Texture texture2 = this.resourcePack.texture(KeyUtils.INSTANCE.appendPng(directKey));
            if (texture2 == null) {
               return null;
            } else {
               Texture texture = texture2;
               int value2 = 0;
               return texture.hasMetadata() || this.getPalettedPermutations().contains(texture.key()) ? particle : null;
            }
         }
      }
   }

   private final Key resolveTextureKeyFromModel(Model model, String textureRef) {
      String name = StringsKt.removePrefix(textureRef, (CharSequence)"#");
      if (StringsKt.startsWith(name, "layer", false)) {
         Integer index = StringsKt.toIntOrNull(StringsKt.removePrefix(name, (CharSequence)"layer"));
         if (index != null) {
            int value = ((Collection)model.textures().layers()).size();
            int value2 = index;
            if (0 <= value2 ? value2 < value : false) {
               return (model.textures().layers().get(index)).key();
            }
         }
      }

      if (Intrinsics.areEqual(name, "particle")) {
         ModelTexture particle = model.textures().particle();
         return particle != null ? particle.key() : null;
      } else {
         Model currentModel = model;
         Set visited = (Set)(new LinkedHashSet());
         Object currentRef = null;
         Object obj = name;

         while(currentModel != null && !visited.contains(obj)) {
            ((Collection)visited).add(obj);
            ModelTexture texture = currentModel.textures().variables().get(obj);
            if (texture != null) {
               Key it = texture.key();
               if (it != null) {
                  int value3 = 0;
                  return it;
               }

               String it_31 = texture.reference();
               if (it_31 == null) {
                  return null;
               }

               int value4 = 0;
               obj = it_31;
            } else {
               Key parent = currentModel.parent();
               if (parent == null) {
                  return null;
               }

               Key parentKey = parent;
               Model model2 = this.resourcePack.model(parentKey);
               if (model2 == null) {
                  model2 = VanillaResourcePack.INSTANCE.getResourcePack().model(parentKey);
               }

               currentModel = model2;
            }
         }

         return null;
      }
   }

   private final List<Element> collectElements(Model model) {
      team.unnamed.creative.model.ModelTexture modelTexture;
      team.unnamed.creative.model.Model model2;
      if (!((Collection)model.elements()).isEmpty()) {
         return model.elements();
      } else {
         ModelTextures textures = model.textures();
         Iterable iterable = (Iterable)this.flatten(textures);
         List palettedPermutations = this.getPalettedPermutations();
         boolean flag;
         if (iterable instanceof Collection && ((Collection)iterable).isEmpty()) {
            flag = false;
         } else {
            Iterator iterator = iterable.iterator();

            while(true) {
               if (!iterator.hasNext()) {
                  flag = false;
                  break;
               }

               Object obj2 = iterator.next();
               Key p0 = (Key)obj2;
               int value = 0;
               if (palettedPermutations.contains(p0)) {
                  flag = true;
                  break;
               }
            }
         }

         if (flag) {
            return model.elements();
         } else {
            Key parent = model.parent();
            if (parent == null) {
               return CollectionsKt.emptyList();
            } else {
               Key parentKey = parent;
               Model model3 = this.resourcePack.model(parentKey);
               if (model3 == null) {
                  model3 = VanillaResourcePack.INSTANCE.getResourcePack().model(parentKey);
                  if (model3 == null) {
                     return CollectionsKt.emptyList();
                  }
               }

               Model parentModel = model3;
               Iterable iterable2 = (Iterable)this.collectElements(parentModel);
               Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable2, 10)));

               for(Object item : iterable2) {
                  Element element = (Element)item;
                  int value2 = 0;
                  Map faces = element.faces();
                  Map map = (Map)(new LinkedHashMap(MapsKt.mapCapacity(faces.size())));
                  Iterable entrySet = (Iterable)faces.entrySet();
                  Map map2 = map;
                  int value6 = 0;

                  for(Object obj3 : entrySet) {
                     Map.Entry it = (Map.Entry)obj3;
                     int value3 = 0;
                     Object key = it.getKey();
                     Map.Entry entry = (Map.Entry)obj3;
                     Object obj = key;
                     int value4 = 0;
                     ElementFace face = (ElementFace)entry.getValue();
                     String str = face.texture();
                     String newTexture = str;
                     if (StringsKt.startsWith(newTexture, "#", false)) {
                        Model currentModel = model;
                        Set visited = (Set)(new LinkedHashSet());
                        String resolvedRef = StringsKt.removePrefix(newTexture, (CharSequence)"#");

                        while(resolvedRef != null && !visited.contains(resolvedRef)) {
                           block: {
                              ((Collection)visited).add(resolvedRef);
                              if (currentModel != null) {
                                 ModelTextures modelTextures = currentModel.textures();
                                 if (modelTextures != null) {
                                    Map variables = modelTextures.variables();
                                    if (variables != null) {
                                       modelTexture = (ModelTexture)variables.get(resolvedRef);
                                       break block;
                                    }
                                 }
                              }

                              modelTexture = null;
                           }

                           ModelTexture texture = modelTexture;
                           if (texture != null) {
                              if (texture.key() != null && !this.getPalettedPermutations().contains(texture.key())) {
                                 newTexture = resolvedRef;
                              } else if (texture.reference() != null) {
                                 newTexture = "#" + texture.reference();
                                 resolvedRef = texture.reference();
                              }
                           } else {
                              block: {
                                 if (currentModel != null) {
                                    Key key2 = currentModel.parent();
                                    if (key2 != null) {
                                       Key p0 = key2;
                                       ResourceContainer resourceContainer = this.resourcePack;
                                       int value5 = 0;
                                       model2 = ResourcePackUtilsKt.modelOrVanilla(resourceContainer, p0);
                                       break block;
                                    }
                                 }

                                 model2 = null;
                              }

                              currentModel = model2;
                           }
                        }
                     }

                     ElementFace elementFace = this.toBuilder(face).texture(newTexture).build();
                     map2.put(obj, elementFace);
                  }

                  destination.add(element.toBuilder().faces(map2).build());
               }

               return (List)destination;
            }
         }
      }
   }

   private final void updateModelTextureReferences(List<TextureAssignment> assignments) {
      int value;
      int value2;
      team.unnamed.creative.model.ModelTexture modelTexture;
      Iterable iterable = (Iterable)assignments;
      int capacity = RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault(iterable, 10)), 16);
      Map destination = (Map)(new LinkedHashMap(capacity));
      int value20 = 0;

      for(Object obj6 : iterable) {
         TextureAssignment it = (TextureAssignment)obj6;
         int variables = 0;
         destination.put(KeyUtils.INSTANCE.removeSuffix(it.getKey(), ".png"), obj6);
      }

      Map textureMap = destination;
      Collection models = this.resourcePack.models();
      iterable = (Iterable)CollectionsKt.toList((Iterable)models);

      for(Object obj7 : iterable) {
         Model model = (Model)obj7;
         value20 = 0;
         ModelTextures textures = model.textures();
         Iterable iterable2 = (Iterable)this.flatten(textures);
         Collection collection2 = (Collection)(new ArrayList());

         for(Object obj8 : iterable2) {
            block: {
               int value3 = 0;
               Key textureKey = (Key)obj8;
               int value4 = 0;
               TextureAssignment textureAssignment = (TextureAssignment)textureMap.get(textureKey);
               if (textureAssignment != null) {
                  int it = textureAssignment.getAtlasIndex();
                  int value21 = 0;
                  Key key2 = Key.key("nexo", "atlases/atlas_" + it);
                  if (key2 != null) {
                     Key p0 = key2;
                     value21 = 0;
                     modelTexture = ModelTexture.ofKey(p0);
                     break block;
                  }
               }

               modelTexture = null;
            }

            if (modelTexture != null) {
               Object obj9 = modelTexture;
               int value5 = 0;
               collection2.add(obj9);
            }
         }

         List particle = CollectionsKt.distinct((Iterable)((List)collection2));
         int value22 = 0;
         List list = !particle.isEmpty() ? particle : null;
         if (list != null) {
            List layers = list;
            Map map = model.textures().variables();
            Map map2 = map;
            Map map3 = (Map)(new LinkedHashMap());

            for(Map.Entry entry4 : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)map2.entrySet()) {
               int value6 = 0;
               Key key3 = ((ModelTexture)entry4.getValue()).key();
               boolean flag;
               if (key3 == null) {
                  flag = true;
               } else {
                  block: {
                     Key key = key3;
                     if (textureMap.containsKey(key)) {
                        Texture texture2 = this.resourcePack.texture(KeyUtils.INSTANCE.appendPng(key));
                        boolean flag2;
                        if (texture2 == null) {
                           flag2 = false;
                        } else {
                           block2: {
                              Texture t = texture2;
                              int value7 = 0;
                              if (!t.hasMetadata()) {
                                 List palettedPermutations = this.getPalettedPermutations();
                                 KeyUtils keyUtils = KeyUtils.INSTANCE;
                                 Key key4 = t.key();
                                 if (!palettedPermutations.contains(keyUtils.appendPng(key4))) {
                                    flag2 = false;
                                    break block2;
                                 }
                              }

                              flag2 = true;
                           }

                           flag2 = flag2;
                        }

                        if (!flag2) {
                           flag = false;
                           break block;
                        }
                     }

                     flag = true;
                  }
               }

               if (flag) {
                  map3.put(entry4.getKey(), entry4.getValue());
               }
            }

            ModelTexture particle_27 = this.remapParticle(model, (a0_ -> TextureMerger.updateModelTextureReferences$lambda$1$3(textureMap, a0_)));
            ModelTextures updatedModelTextures = ModelTextures.of(layers, particle_27, map3);
            List modelElements = this.collectElements(model);
            Iterable iterable3 = (Iterable)modelElements;
            Collection collection3 = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable3, 10)));

            for(Object item : iterable3) {
               Element element = (Element)item;
               int value8 = 0;
               Map faces = element.faces();
               Map map4 = (Map)(new LinkedHashMap(MapsKt.mapCapacity(faces.size())));
               Iterable entrySet = (Iterable)faces.entrySet();
               Map map5 = map4;

               for(Object obj10 : entrySet) {
                  Map.Entry entry5 = (Map.Entry)obj10;
                  int value9 = 0;
                  Object obj = entry5.getKey();
                  Map.Entry entry = (Map.Entry)obj10;
                  Object obj2 = obj;
                  int value10 = 0;
                  ElementFace face = (ElementFace)entry.getValue();
                  String str = face.texture();
                  Key key5 = this.resolveTextureKeyFromModel(model, str);
                  ElementFace elementFace;
                  if (key5 == null) {
                     elementFace = face;
                  } else {
                     Key resolvedKey = key5;
                     Texture texture = this.resourcePack.texture(KeyUtils.INSTANCE.appendPng(resolvedKey));
                     if (!(texture != null ? texture.hasMetadata() : false) && !this.getPalettedPermutations().contains(texture != null ? texture.key() : null)) {
                        TextureAssignment textureAssignment2 = (TextureAssignment)textureMap.get(resolvedKey);
                        if (textureAssignment2 == null) {
                           elementFace = face;
                        } else {
                           TextureAssignment assignment = textureAssignment2;
                           Key key6 = Key.key("nexo", "atlases/atlas_" + assignment.getAtlasIndex());
                           Key assignmentKey = key6;
                           int value23 = 0;
                           int value24 = 0;
                           Iterator iterator = layers.iterator();

                           while(true) {
                              if (!iterator.hasNext()) {
                                 value = -1;
                                 break;
                              }

                              Object obj11 = iterator.next();
                              ModelTexture it = (ModelTexture)obj11;
                              int value11 = 0;
                              if (Intrinsics.areEqual(it.key(), assignmentKey)) {
                                 value = value24;
                                 break;
                              }

                              ++value24;
                           }

                           Integer number3 = value;
                           value23 = ((Number)number3).intValue();
                           value24 = 0;
                           Integer number = value23 != -1 ? number3 : null;
                           if ((value23 != -1 ? number3 : null) != null) {
                              int index = number;
                              TextureUV uvs = assignment.calculateUVs(face.uv());
                              elementFace = this.toBuilder(face).uv(uvs).texture("#layer" + index).build();
                           } else {
                              elementFace = face;
                           }
                        }
                     } else {
                        elementFace = face;
                     }
                  }

                  ElementFace elementFace2 = elementFace;
                  map5.put(obj2, elementFace2);
               }

               collection3.add(element.toBuilder().faces(map5).build());
            }

            Collection collection = (Collection)((List)collection3);
            Object obj3;
            if (collection.isEmpty()) {
               List list2 = model.textures().layers();
               Iterable iterable4 = (Iterable)list2;
               Collection collection4 = (Collection)(new ArrayList());

               for(Object obj12 : iterable4) {
                  int value12 = 0;
                  ModelTexture layerTex = (ModelTexture)obj12;
                  int value13 = 0;
                  Key key7 = layerTex.key();
                  Pair pair;
                  if (key7 == null) {
                     pair = null;
                  } else {
                     Key layerKey = key7;
                     TextureAssignment textureAssignment3 = (TextureAssignment)textureMap.get(layerKey);
                     if (textureAssignment3 == null) {
                        pair = null;
                     } else {
                        TextureAssignment assignment = textureAssignment3;
                        Key key8 = Key.key("nexo", "atlases/atlas_" + assignment.getAtlasIndex());
                        Key atlasKey = key8;
                        int value25 = 0;
                        int value26 = 0;
                        Iterator iterator2 = layers.iterator();

                        while(true) {
                           if (!iterator2.hasNext()) {
                              value2 = -1;
                              break;
                           }

                           Object obj13 = iterator2.next();
                           ModelTexture it = (ModelTexture)obj13;
                           int value14 = 0;
                           if (Intrinsics.areEqual(it.key(), atlasKey)) {
                              value2 = value26;
                              break;
                           }

                           ++value26;
                        }

                        Integer number4 = value2;
                        value25 = ((Number)number4).intValue();
                        value26 = 0;
                        Integer number2 = value25 != -1 ? number4 : null;
                        if ((value25 != -1 ? number4 : null) != null) {
                           int atlasLayerIdx = number2;
                           pair = TuplesKt.to(atlasLayerIdx, assignment);
                        } else {
                           pair = null;
                        }
                     }
                  }

                  if (pair != null) {
                     Object obj14 = pair;
                     int value15 = 0;
                     collection4.add(obj14);
                  }
               }

               List layerInfos = (List)collection4;
               if (layerInfos.isEmpty()) {
                  continue;
               }

               iterable4 = (Iterable)layerInfos;
               collection4 = (Collection)(new ArrayList());

               for(Object obj15 : iterable4) {
                  Pair pair2 = (Pair)obj15;
                  int value16 = 0;
                  int atlasLayerIdx = ((Number)pair2.component1()).intValue();
                  TextureAssignment assignment = (TextureAssignment)pair2.component2();
                  Iterable iterable5 = (Iterable)Companion.generateLayerElements(atlasLayerIdx, assignment.getImage());
                  Collection collection5 = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable5, 10)));

                  for(Object obj16 : iterable5) {
                     Element element2 = (Element)obj16;
                     int value17 = 0;
                     Map map6 = element2.faces();
                     Map map7 = (Map)(new LinkedHashMap(MapsKt.mapCapacity(map6.size())));
                     Iterable iterable6 = (Iterable)map6.entrySet();
                     Map map8 = map7;

                     for(Object obj17 : iterable6) {
                        Map.Entry entry2 = (Map.Entry)obj17;
                        int value18 = 0;
                        Object obj4 = entry2.getKey();
                        Map.Entry entry3 = (Map.Entry)obj17;
                        Object obj5 = obj4;
                        int value19 = 0;
                        ElementFace face = (ElementFace)entry3.getValue();
                        TextureUV textureUV = face.uv();
                        ElementFace elementFace3;
                        if (textureUV == null) {
                           elementFace3 = face;
                        } else {
                           TextureUV uv = textureUV;
                           elementFace3 = this.toBuilder(face).uv(assignment.calculateUVs(uv)).build();
                        }

                        ElementFace elementFace4 = elementFace3;
                        map8.put(obj5, elementFace4);
                     }

                     collection5.add(element2.toBuilder().faces(map8).build());
                  }

                  Iterable iterable7 = (Iterable)((List)collection5);
                  CollectionsKt.addAll(collection5, iterable7);
               }

               obj3 = (List)collection4;
            } else {
               obj3 = collection;
            }

            List patchedElements = (List)obj3;
            model.toBuilder().textures(updatedModelTextures).elements(patchedElements).build().addTo(this.resourcePack);
         }
      }

   }

   private final List<Key> flatten(ModelTextures modelTextures) {
      List layers = modelTextures.layers();
      Iterable iterable = (Iterable)layers;
      Collection destination = (Collection)(new ArrayList());

      for(Object element : iterable) {
         int value = 0;
         ModelTexture p0 = (ModelTexture)element;
         int value2 = 0;
         Key key = p0.key();
         if (key != null) {
            Object it = key;
            int value3 = 0;
            destination.add(it);
         }
      }

      Collection collection = (Collection)((List)destination);
      iterable = (Iterable)modelTextures.variables().values();
      Collection collection2 = collection;
      destination = (Collection)(new ArrayList());

      for(Object obj : iterable) {
         int value4 = 0;
         ModelTexture p0 = (ModelTexture)obj;
         int value5 = 0;
         Key key2 = p0.key();
         if (key2 != null) {
            Object obj2 = key2;
            int value6 = 0;
            destination.add(obj2);
         }
      }

      collection = (Collection)CollectionsKt.plus(collection2, (Iterable)((List)destination));
      ModelTexture particle = modelTextures.particle();
      return CollectionsKt.distinct((Iterable)CollectionsKt.plus(collection, (Iterable)CollectionsKt.listOfNotNull(particle != null ? particle.key() : null)));
   }

   private final ElementFace.Builder toBuilder(ElementFace elementFace) {
      ElementFace.Builder builder = ElementFace.face().uv(elementFace.uv()).texture(elementFace.texture()).rotation(elementFace.rotation()).cullFace(elementFace.cullFace()).tintIndex(elementFace.tintIndex());
      return builder;
   }

   private static final Type mergeType$lambda$0(String it) {
      Logs.logError("Invalid TextureAtlasMerger type: " + it + ", defaulting to " + TextureMerger.Type.SIMPLE, true);
      Logs.logError("Valid options are: " + CollectionsKt.joinToString((Iterable)TextureMerger.Type.getEntries(), (CharSequence)", ", (CharSequence)"", (CharSequence)"", -1, (CharSequence)"...", (Function1)null), true);
      return TextureMerger.Type.SIMPLE;
   }

   private static final Key stitchFull$lambda$12$4(Map cropPlacements, Key key) {
      CropPlacement cropPlacement = (CropPlacement)cropPlacements.get(new CropKey(key, 0.0F, 0.0F, 1.0F, 1.0F));
      if (cropPlacement == null) {
         Iterable entrySet = (Iterable)cropPlacements.entrySet();
         Iterator iterator = entrySet.iterator();

         Object obj;
         while(true) {
            if (!iterator.hasNext()) {
               obj = null;
               break;
            }

            Object element = iterator.next();
            Map.Entry it = (Map.Entry)element;
            int value = 0;
            if (Intrinsics.areEqual(((CropKey)it.getKey()).getTextureKey(), key)) {
               obj = element;
               break;
            }
         }

         cropPlacement = (Map.Entry)obj != null ? (CropPlacement)((Map.Entry)obj).getValue() : null;
      }

      CropPlacement placement = cropPlacement;
      Key key2;
      if (placement != null) {
         int value2 = 0;
         key2 = Key.key("nexo", "atlases/atlas_" + placement.getAtlasIndex());
      } else {
         key2 = null;
      }

      return key2;
   }

   private static final List palettedPermutations_delegate$lambda$0(TextureMerger textureMerger) {
      java.lang.Iterable iterable;
      Atlas atlas = textureMerger.resourcePack.atlas(Atlas.BLOCKS);
      List list;
      if (atlas != null) {
         list = atlas.sources();
         if (list != null) {
            Iterable iterable2 = (Iterable)list;
            Collection destination = (Collection)(new ObjectArrayList());

            for(Object element : iterable2) {
               if (element instanceof PalettedPermutationsAtlasSource) {
                  destination.add(element);
               }
            }

            Iterable iterable3 = (Iterable)((ObjectArrayList)destination);
            Collection collection = (Collection)(new ObjectArrayList());

            for(Object obj : iterable3) {
               PalettedPermutationsAtlasSource source = (PalettedPermutationsAtlasSource)obj;
               int value = 0;
               Iterable textures = (Iterable)source.textures();
               Collection collection2 = (Collection)(new ObjectArrayList((textures instanceof Collection ? (Collection)textures : null) != null ? (textures instanceof Collection ? (Collection)textures : null).size() : 10));

               for(Object item : textures) {
                  Key it = (Key)item;
                  int value2 = 0;
                  KeyUtils keyUtils = KeyUtils.INSTANCE;
                  collection2.add(keyUtils.appendPng(it));
               }

               textures = (Iterable)((ObjectArrayList)collection2);
               collection2 = (Collection)(new ObjectArrayList());

               for(Object obj2 : textures) {
                  block: {
                     Key texture = (Key)obj2;
                     int value3 = 0;
                     Texture texture2 = textureMerger.resourcePack.texture(texture);
                     if (texture2 == null) {
                        texture2 = VanillaResourcePack.INSTANCE.getResourcePack().texture(texture);
                        if (texture2 == null) {
                           iterable = (Iterable)CollectionsKt.emptyList();
                           break block;
                        }
                     }

                     Texture texture_9 = texture2;
                     Iterable keySet = (Iterable)source.permutations().keySet();
                     Collection collection3 = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(keySet, 10)));

                     for(Object obj3 : keySet) {
                        String permutation = (String)obj3;
                        int value4 = 0;
                        KeyUtils keyUtils2 = KeyUtils.INSTANCE;
                        KeyUtils keyUtils3 = KeyUtils.INSTANCE;
                        Key key = texture_9.key();
                        collection3.add(keyUtils2.appendSuffix(keyUtils3.removeSuffix(key, ".png"), "_" + permutation + ".png"));
                     }

                     iterable = (Iterable)((List)collection3);
                  }

                  Iterable iterable4 = iterable;
                  CollectionsKt.addAll(collection2, iterable4);
               }

               Iterable iterable5 = (Iterable)((ObjectArrayList)collection2);
               CollectionsKt.addAll(collection2, iterable5);
            }

            list = (List)((ObjectArrayList)collection);
            return list;
         }
      }

      list = CollectionsKt.emptyList();
      return list;
   }

   private static final Key updateModelTextureReferences$lambda$1$3(Map textureMap, Key key) {
      TextureAssignment textureAssignment = (TextureAssignment)textureMap.get(key);
      Key key2;
      if (textureAssignment != null) {
         TextureAssignment it = textureAssignment;
         int value = 0;
         key2 = Key.key("nexo", "atlases/atlas_" + it.getAtlasIndex());
      } else {
         key2 = null;
      }

      return key2;
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0002\b\u0002\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u0011\u0010\u0007\u001a\u00020\b8F¢\u0006\u0006\u001a\u0004\b\u0007\u0010\tj\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006¨\u0006\n"},
      d2 = {"Lcom/nexomc/nexo/pack/TextureMerger$Type;", "", "<init>", "(Ljava/lang/String;I)V", "SIMPLE", "FULL", "NONE", "isNone", "", "()Z", "com.nexomc:core"}
   )
   public static enum Type {
      SIMPLE,
      FULL,
      NONE;

      public final boolean isNone() {
         return this == NONE;
      }

      @NotNull
      public static EnumEntries<Type> getEntries() {
         return EnumEntriesKt.enumEntries(values());
      }
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u001c\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\r0\f2\u0006\u0010\u000e\u001a\u00020\u00052\u0006\u0010\u000f\u001a\u00020\u0010R\u000e\u0010\u0004\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\tX\u0082T¢\u0006\u0002\n\u0000¨\u0006\u0011"},
      d2 = {"Lcom/nexomc/nexo/pack/TextureMerger$Companion;", "", "<init>", "()V", "ATLAS_SIZE", "", "CELL_SIZE", "GRID_SIZE", "ITEM_GEN_MIN_Z", "", "ITEM_GEN_MAX_Z", "generateLayerElements", "", "Lteam/unnamed/creative/model/Element;", "layerIndex", "image", "Ljava/awt/image/BufferedImage;", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final List<Element> generateLayerElements(int layerIndex, @NotNull BufferedImage image) {
         Pair pair = TuplesKt.to(image.getWidth(), image.getHeight());
         int w = ((Number)pair.component1()).intValue();
         int h = ((Number)pair.component2()).intValue();
         Pair pair2 = TuplesKt.to(16.0F / (float)w, 16.0F / (float)h);
         float sX = ((Number)pair2.component1()).floatValue();
         float sY = ((Number)pair2.component2()).floatValue();
         Pair pair3 = TuplesKt.to(RangesKt.until(0, w), RangesKt.until(0, h));
         IntRange wRange = (IntRange)pair3.component1();
         IntRange hRange = (IntRange)pair3.component2();
         String texRef = "#layer" + layerIndex;
         List out = (List)(new ArrayList());
         Collection collection = (Collection)out;
         Element.Builder builder = Element.element().from(new Vector3Float(0.0F, 0.0F, 7.5F)).to(new Vector3Float(16.0F, 16.0F, 8.5F));
         Pair[] pairs = new Pair[]{TuplesKt.to(CubeFace.SOUTH, ElementFace.face(TextureUV.uv(0.0F, 0.0F, 1.0F, 1.0F), texRef, (CubeFace)null, 0, -1)), TuplesKt.to(CubeFace.NORTH, ElementFace.face(TextureUV.uv(1.0F, 0.0F, 0.0F, 1.0F), texRef, (CubeFace)null, 0, -1))};
         Element element = builder.faces(MapsKt.mapOf(pairs)).build();
         collection.add(element);
         int py = 0;

         boolean[][] booleanses;
         for(booleanses = new boolean[w][]; py < w; ++py) {
            int value = py;
            int value2 = 0;

            boolean[] booleans;
            for(booleans = new boolean[h]; value2 < h; ++value2) {
               booleans[value2] = (image.getRGB(value, value2) >>> 24 & 255) != 0;
            }

            booleanses[py] = booleans;
         }

         boolean[][] opaque = booleanses;
         py = hRange.getFirst();
         int last = hRange.getLast();
         if (py <= last) {
            while(true) {
               int px = wRange.getFirst();
               int value3 = wRange.getLast();
               if (px <= value3) {
                  while(true) {
                     if (opaque[px][py]) {
                        Pair pair4 = TuplesKt.to((float)px / (float)w, (float)py / (float)h);
                        float u0 = ((Number)pair4.component1()).floatValue();
                        float v0 = ((Number)pair4.component2()).floatValue();
                        Pair pair5 = TuplesKt.to((float)(px + 1) / (float)w, (float)(py + 1) / (float)h);
                        float u1 = ((Number)pair5.component1()).floatValue();
                        float v1 = ((Number)pair5.component2()).floatValue();
                        TextureUV textureUV = TextureUV.uv(u0, v0, u1, v1);
                        TextureUV pxUv = textureUV;
                        ElementFace elementFace = ElementFace.face(pxUv, texRef, (CubeFace)null, 0, -1);
                        ElementFace face = elementFace;
                        Map faces = MapsKt.createMapBuilder();
                        int value4 = 0;
                        if (!generateLayerElements$isOpaque(wRange, hRange, opaque, px - 1, py)) {
                           faces.put(CubeFace.WEST, face);
                        }

                        if (!generateLayerElements$isOpaque(wRange, hRange, opaque, px + 1, py)) {
                           faces.put(CubeFace.EAST, face);
                        }

                        if (!generateLayerElements$isOpaque(wRange, hRange, opaque, px, py - 1)) {
                           faces.put(CubeFace.UP, face);
                        }

                        if (!generateLayerElements$isOpaque(wRange, hRange, opaque, px, py + 1)) {
                           faces.put(CubeFace.DOWN, face);
                        }

                        faces = MapsKt.build(faces);
                        if (faces.isEmpty()) {
                           int value5 = 0;
                        } else {
                           ((Collection)out).add(Element.element().from(new Vector3Float((float)px * sX, (float)(h - py - 1) * sY, 7.5F)).to(new Vector3Float((float)(px + 1) * sX, (float)(h - py) * sY, 8.5F)).faces(faces).build());
                        }
                     }

                     if (px == value3) {
                        break;
                     }

                     ++px;
                  }
               }

               if (py == last) {
                  break;
               }

               ++py;
            }
         }

         return out;
      }

      private static final boolean generateLayerElements$isOpaque(IntRange wRange, IntRange hRange, boolean[][] opaque, int px, int py) {
         int first = wRange.getFirst();
         boolean flag;
         if (px <= wRange.getLast() ? first <= px : false) {
            first = hRange.getFirst();
            if ((py <= hRange.getLast() ? first <= py : false) && opaque[px][py]) {
               flag = true;
               return flag;
            }
         }

         flag = false;
         return flag;
      }
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\n\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003¢\u0006\u0004\b\u0005\u0010\u0006J\t\u0010\n\u001a\u00020\u0003HÆ\u0003J\t\u0010\u000b\u001a\u00020\u0003HÆ\u0003J\u001d\u0010\f\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0003HÆ\u0001J\u0014\u0010\r\u001a\u00020\u000e2\b\u0010\u000f\u001a\u0004\u0018\u00010\u0001HÖ\u0083\u0004J\n\u0010\u0010\u001a\u00020\u0003HÖ\u0081\u0004J\n\u0010\u0011\u001a\u00020\u0012HÖ\u0081\u0004R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\b¨\u0006\u0013"},
      d2 = {"Lcom/nexomc/nexo/pack/TextureMerger$GridPosition;", "", "row", "", "col", "<init>", "(II)V", "getRow", "()I", "getCol", "component1", "component2", "copy", "equals", "", "other", "hashCode", "toString", "", "com.nexomc:core"}
   )
   public static final class GridPosition {
      private final int row;
      private final int col;

      public GridPosition(int row, int col) {
         this.row = row;
         this.col = col;
      }

      public final int getRow() {
         return this.row;
      }

      public final int getCol() {
         return this.col;
      }

      public final int component1() {
         return this.row;
      }

      public final int component2() {
         return this.col;
      }

      @NotNull
      public final GridPosition copy(int row, int col) {
         return new GridPosition(row, col);
      }

      @NotNull
      public String toString() {
         return "GridPosition(row=" + this.row + ", col=" + this.col + ")";
      }

      public int hashCode() {
         int result = Integer.hashCode(this.row);
         result = result * 31 + Integer.hashCode(this.col);
         return result;
      }

      public boolean equals(@Nullable Object other) {
         if (this == other) {
            return true;
         } else if (!(other instanceof GridPosition)) {
            return false;
         } else {
            GridPosition gridPosition = (GridPosition)other;
            if (this.row != gridPosition.row) {
               return false;
            } else {
               return this.col == gridPosition.col;
            }
         }
      }
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\"\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\r\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B=\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\u0006\u0010\t\u001a\u00020\b\u0012\u0006\u0010\n\u001a\u00020\b\u0012\u0006\u0010\u000b\u001a\u00020\f¢\u0006\u0004\b\r\u0010\u000eJ\u0012\u0010\u0019\u001a\u00020\u001a2\n\b\u0002\u0010\u001b\u001a\u0004\u0018\u00010\u001aJ\t\u0010\u001c\u001a\u00020\u0003HÆ\u0003J\u000f\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005HÆ\u0003J\t\u0010\u001e\u001a\u00020\bHÆ\u0003J\t\u0010\u001f\u001a\u00020\bHÆ\u0003J\t\u0010 \u001a\u00020\bHÆ\u0003J\t\u0010!\u001a\u00020\fHÆ\u0003JK\u0010\"\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u000e\b\u0002\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u00052\b\b\u0002\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\t\u001a\u00020\b2\b\b\u0002\u0010\n\u001a\u00020\b2\b\b\u0002\u0010\u000b\u001a\u00020\fHÆ\u0001J\u0014\u0010#\u001a\u00020$2\b\u0010%\u001a\u0004\u0018\u00010\u0001HÖ\u0083\u0004J\n\u0010&\u001a\u00020\bHÖ\u0081\u0004J\n\u0010'\u001a\u00020(HÖ\u0081\u0004R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0017\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0011\u0010\u0007\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0011\u0010\t\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0014R\u0011\u0010\n\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0014R\u0011\u0010\u000b\u001a\u00020\f¢\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018¨\u0006)"},
      d2 = {"Lcom/nexomc/nexo/pack/TextureMerger$TextureAssignment;", "", "key", "Lnet/kyori/adventure/key/Key;", "positions", "", "Lcom/nexomc/nexo/pack/TextureMerger$GridPosition;", "width", "", "height", "atlasIndex", "image", "Ljava/awt/image/BufferedImage;", "<init>", "(Lnet/kyori/adventure/key/Key;Ljava/util/Set;IIILjava/awt/image/BufferedImage;)V", "getKey", "()Lnet/kyori/adventure/key/Key;", "getPositions", "()Ljava/util/Set;", "getWidth", "()I", "getHeight", "getAtlasIndex", "getImage", "()Ljava/awt/image/BufferedImage;", "calculateUVs", "Lteam/unnamed/creative/texture/TextureUV;", "originalUV", "component1", "component2", "component3", "component4", "component5", "component6", "copy", "equals", "", "other", "hashCode", "toString", "", "com.nexomc:core"}
   )
   public static final class TextureAssignment {
      @NotNull
      private final Key key;
      @NotNull
      private final Set<GridPosition> positions;
      private final int width;
      private final int height;
      private final int atlasIndex;
      @NotNull
      private final BufferedImage image;

      public TextureAssignment(@NotNull Key key, @NotNull Set<GridPosition> positions, int width, int height, int atlasIndex, @NotNull BufferedImage image) {
         super();
         this.key = key;
         this.positions = positions;
         this.width = width;
         this.height = height;
         this.atlasIndex = atlasIndex;
         this.image = image;
      }

      @NotNull
      public final Key getKey() {
         return this.key;
      }

      @NotNull
      public final Set<GridPosition> getPositions() {
         return this.positions;
      }

      public final int getWidth() {
         return this.width;
      }

      public final int getHeight() {
         return this.height;
      }

      public final int getAtlasIndex() {
         return this.atlasIndex;
      }

      @NotNull
      public final BufferedImage getImage() {
         return this.image;
      }

      @NotNull
      public final TextureUV calculateUVs(@Nullable TextureUV originalUV) {
         Iterable iterable = (Iterable)this.positions;
         int value7 = 0;
         Iterator iterator = iterable.iterator();
         if (!iterator.hasNext()) {
            throw new NoSuchElementException();
         } else {
            Object minElem = iterator.next();
            Object obj;
            if (!iterator.hasNext()) {
               obj = minElem;
            } else {
               GridPosition it = (GridPosition)minElem;
               int value = 0;
               int minValue = it.getRow() * 32 + it.getCol();

               do {
                  Object obj2 = iterator.next();
                  GridPosition it_6 = (GridPosition)obj2;
                  int value2 = 0;
                  int value8 = it_6.getRow() * 32 + it_6.getCol();
                  if (minValue > value8) {
                     minElem = obj2;
                     minValue = value8;
                  }
               } while(iterator.hasNext());

               obj = minElem;
            }

            GridPosition gridPosition = (GridPosition)obj;
            int row = gridPosition.component1();
            value7 = gridPosition.component2();
            Pair pair = TuplesKt.to(value7 * 16, row * 16);
            int x = ((Number)pair.component1()).intValue();
            int y = ((Number)pair.component2()).intValue();
            Integer number = this.width;
            int it = ((Number)number).intValue();
            int value3 = 0;
            float validatedWidth = (1 <= it && it < 513) ? (float)it : 16.0F;
            Integer atlasU0 = this.height;
            value3 = ((Number)atlasU0).intValue();
            int value4 = 0;
            float validatedHeight = (1 <= value3 && value3 < 513) ? (float)value3 : 16.0F;
            TextureUV textureUV;
            if (originalUV != null) {
               Vector2Float it_5 = originalUV.from();
               int value5 = 0;
               Pair pair2 = TuplesKt.to(it_5.x(), it_5.y());
               float u0 = ((Number)pair2.component1()).floatValue();
               float v0 = ((Number)pair2.component2()).floatValue();
               Vector2Float it_4 = originalUV.to();
               int value6 = 0;
               Pair atlasV1 = TuplesKt.to(it_4.x(), it_4.y());
               float u1 = ((Number)atlasV1.component1()).floatValue();
               float v1 = ((Number)atlasV1.component2()).floatValue();
               float atlasU0_3 = ((float)x + u0 * validatedWidth) / (float)512;
               float atlasV0 = ((float)y + v0 * validatedHeight) / (float)512;
               float atlasU1 = ((float)x + u1 * validatedWidth) / (float)512;
               float atlasV1_2 = ((float)y + v1 * validatedHeight) / (float)512;
               TextureUV textureUV2 = TextureUV.uv(atlasU0_3, atlasV0, atlasU1, atlasV1_2);
               textureUV = textureUV2;
            } else {
               float atlasU0_1 = (float)x / 512.0F;
               float atlasV0 = (float)y / 512.0F;
               float atlasU1 = ((float)x + validatedWidth) / (float)512;
               float atlasV1 = ((float)y + validatedHeight) / (float)512;
               TextureUV textureUV3 = TextureUV.uv(atlasU0_1, atlasV0, atlasU1, atlasV1);
               textureUV = textureUV3;
            }

            return textureUV;
         }
      }

      @NotNull
      public final Key component1() {
         return this.key;
      }

      @NotNull
      public final Set<GridPosition> component2() {
         return this.positions;
      }

      public final int component3() {
         return this.width;
      }

      public final int component4() {
         return this.height;
      }

      public final int component5() {
         return this.atlasIndex;
      }

      @NotNull
      public final BufferedImage component6() {
         return this.image;
      }

      @NotNull
      public final TextureAssignment copy(@NotNull Key key, @NotNull Set<GridPosition> positions, int width, int height, int atlasIndex, @NotNull BufferedImage image) {
         return new TextureAssignment(key, positions, width, height, atlasIndex, image);
      }

      @NotNull
      public String toString() {
         return "TextureAssignment(key=" + this.key + ", positions=" + this.positions + ", width=" + this.width + ", height=" + this.height + ", atlasIndex=" + this.atlasIndex + ", image=" + this.image + ")";
      }

      public int hashCode() {
         int result = this.key.hashCode();
         result = result * 31 + this.positions.hashCode();
         result = result * 31 + Integer.hashCode(this.width);
         result = result * 31 + Integer.hashCode(this.height);
         result = result * 31 + Integer.hashCode(this.atlasIndex);
         result = result * 31 + this.image.hashCode();
         return result;
      }

      public boolean equals(@Nullable Object other) {
         if (this == other) {
            return true;
         } else if (!(other instanceof TextureAssignment)) {
            return false;
         } else {
            TextureAssignment textureAssignment = (TextureAssignment)other;
            if (!Intrinsics.areEqual(this.key, textureAssignment.key)) {
               return false;
            } else if (!Intrinsics.areEqual(this.positions, textureAssignment.positions)) {
               return false;
            } else if (this.width != textureAssignment.width) {
               return false;
            } else if (this.height != textureAssignment.height) {
               return false;
            } else if (this.atlasIndex != textureAssignment.atlasIndex) {
               return false;
            } else {
               return Intrinsics.areEqual(this.image, textureAssignment.image);
            }
         }
      }
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0013\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0082\b\u0018\u00002\u00020\u0001B/\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\u0006\u0010\u0007\u001a\u00020\u0005\u0012\u0006\u0010\b\u001a\u00020\u0005¢\u0006\u0004\b\t\u0010\nJ\t\u0010\u0012\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0013\u001a\u00020\u0005HÆ\u0003J\t\u0010\u0014\u001a\u00020\u0005HÆ\u0003J\t\u0010\u0015\u001a\u00020\u0005HÆ\u0003J\t\u0010\u0016\u001a\u00020\u0005HÆ\u0003J;\u0010\u0017\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00052\b\b\u0002\u0010\u0007\u001a\u00020\u00052\b\b\u0002\u0010\b\u001a\u00020\u0005HÆ\u0001J\u0014\u0010\u0018\u001a\u00020\u00192\b\u0010\u001a\u001a\u0004\u0018\u00010\u0001HÖ\u0083\u0004J\n\u0010\u001b\u001a\u00020\u001cHÖ\u0081\u0004J\n\u0010\u001d\u001a\u00020\u001eHÖ\u0081\u0004R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0011\u0010\u0006\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u000eR\u0011\u0010\u0007\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u000eR\u0011\u0010\b\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u000e¨\u0006\u001f"},
      d2 = {"Lcom/nexomc/nexo/pack/TextureMerger$CropKey;", "", "textureKey", "Lnet/kyori/adventure/key/Key;", "u0", "", "v0", "u1", "v1", "<init>", "(Lnet/kyori/adventure/key/Key;FFFF)V", "getTextureKey", "()Lnet/kyori/adventure/key/Key;", "getU0", "()F", "getV0", "getU1", "getV1", "component1", "component2", "component3", "component4", "component5", "copy", "equals", "", "other", "hashCode", "", "toString", "", "com.nexomc:core"}
   )
   static final class CropKey {
      @NotNull
      private final Key textureKey;
      private final float u0;
      private final float v0;
      private final float u1;
      private final float v1;

      public CropKey(@NotNull Key textureKey, float u0, float v0, float u1, float v1) {
         super();
         this.textureKey = textureKey;
         this.u0 = u0;
         this.v0 = v0;
         this.u1 = u1;
         this.v1 = v1;
      }

      @NotNull
      public final Key getTextureKey() {
         return this.textureKey;
      }

      public final float getU0() {
         return this.u0;
      }

      public final float getV0() {
         return this.v0;
      }

      public final float getU1() {
         return this.u1;
      }

      public final float getV1() {
         return this.v1;
      }

      @NotNull
      public final Key component1() {
         return this.textureKey;
      }

      public final float component2() {
         return this.u0;
      }

      public final float component3() {
         return this.v0;
      }

      public final float component4() {
         return this.u1;
      }

      public final float component5() {
         return this.v1;
      }

      @NotNull
      public final CropKey copy(@NotNull Key textureKey, float u0, float v0, float u1, float v1) {
         return new CropKey(textureKey, u0, v0, u1, v1);
      }

      @NotNull
      public String toString() {
         return "CropKey(textureKey=" + this.textureKey + ", u0=" + this.u0 + ", v0=" + this.v0 + ", u1=" + this.u1 + ", v1=" + this.v1 + ")";
      }

      public int hashCode() {
         int result = this.textureKey.hashCode();
         result = result * 31 + Float.hashCode(this.u0);
         result = result * 31 + Float.hashCode(this.v0);
         result = result * 31 + Float.hashCode(this.u1);
         result = result * 31 + Float.hashCode(this.v1);
         return result;
      }

      public boolean equals(@Nullable Object other) {
         if (this == other) {
            return true;
         } else if (!(other instanceof CropKey)) {
            return false;
         } else {
            CropKey cropKey = (CropKey)other;
            if (!Intrinsics.areEqual(this.textureKey, cropKey.textureKey)) {
               return false;
            } else if (Float.compare(this.u0, cropKey.u0) != 0) {
               return false;
            } else if (Float.compare(this.v0, cropKey.v0) != 0) {
               return false;
            } else if (Float.compare(this.u1, cropKey.u1) != 0) {
               return false;
            } else {
               return Float.compare(this.v1, cropKey.v1) == 0;
            }
         }
      }
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0013\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\b\u0082\b\u0018\u00002\u00020\u0001B/\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u0012\u0006\u0010\u0007\u001a\u00020\u0003¢\u0006\u0004\b\b\u0010\tJ\t\u0010\u0010\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0011\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0012\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0013\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0014\u001a\u00020\u0003HÆ\u0003J;\u0010\u0015\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\u0003HÆ\u0001J\u0014\u0010\u0016\u001a\u00020\u00172\b\u0010\u0018\u001a\u0004\u0018\u00010\u0001HÖ\u0083\u0004J\n\u0010\u0019\u001a\u00020\u0003HÖ\u0081\u0004J\n\u0010\u001a\u001a\u00020\u001bHÖ\u0081\u0004R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\u000bR\u0011\u0010\u0005\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000bR\u0011\u0010\u0006\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000bR\u0011\u0010\u0007\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u000b¨\u0006\u001c"},
      d2 = {"Lcom/nexomc/nexo/pack/TextureMerger$CropPlacement;", "", "x", "", "y", "width", "height", "atlasIndex", "<init>", "(IIIII)V", "getX", "()I", "getY", "getWidth", "getHeight", "getAtlasIndex", "component1", "component2", "component3", "component4", "component5", "copy", "equals", "", "other", "hashCode", "toString", "", "com.nexomc:core"}
   )
   private static final class CropPlacement {
      private final int x;
      private final int y;
      private final int width;
      private final int height;
      private final int atlasIndex;

      public CropPlacement(int x, int y, int width, int height, int atlasIndex) {
         this.x = x;
         this.y = y;
         this.width = width;
         this.height = height;
         this.atlasIndex = atlasIndex;
      }

      public final int getX() {
         return this.x;
      }

      public final int getY() {
         return this.y;
      }

      public final int getWidth() {
         return this.width;
      }

      public final int getHeight() {
         return this.height;
      }

      public final int getAtlasIndex() {
         return this.atlasIndex;
      }

      public final int component1() {
         return this.x;
      }

      public final int component2() {
         return this.y;
      }

      public final int component3() {
         return this.width;
      }

      public final int component4() {
         return this.height;
      }

      public final int component5() {
         return this.atlasIndex;
      }

      @NotNull
      public final CropPlacement copy(int x, int y, int width, int height, int atlasIndex) {
         return new CropPlacement(x, y, width, height, atlasIndex);
      }

      @NotNull
      public String toString() {
         return "CropPlacement(x=" + this.x + ", y=" + this.y + ", width=" + this.width + ", height=" + this.height + ", atlasIndex=" + this.atlasIndex + ")";
      }

      public int hashCode() {
         int result = Integer.hashCode(this.x);
         result = result * 31 + Integer.hashCode(this.y);
         result = result * 31 + Integer.hashCode(this.width);
         result = result * 31 + Integer.hashCode(this.height);
         result = result * 31 + Integer.hashCode(this.atlasIndex);
         return result;
      }

      public boolean equals(@Nullable Object other) {
         if (this == other) {
            return true;
         } else if (!(other instanceof CropPlacement)) {
            return false;
         } else {
            CropPlacement cropPlacement = (CropPlacement)other;
            if (this.x != cropPlacement.x) {
               return false;
            } else if (this.y != cropPlacement.y) {
               return false;
            } else if (this.width != cropPlacement.width) {
               return false;
            } else if (this.height != cropPlacement.height) {
               return false;
            } else {
               return this.atlasIndex == cropPlacement.atlasIndex;
            }
         }
      }
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0011\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0082\b\u0018\u00002\u00020\u0001B'\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\u0006¢\u0006\u0004\b\b\u0010\tJ\t\u0010\u0010\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0011\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0012\u001a\u00020\u0006HÆ\u0003J\t\u0010\u0013\u001a\u00020\u0006HÆ\u0003J1\u0010\u0014\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\u0006HÆ\u0001J\u0014\u0010\u0015\u001a\u00020\u00062\b\u0010\u0016\u001a\u0004\u0018\u00010\u0001HÖ\u0083\u0004J\n\u0010\u0017\u001a\u00020\u0018HÖ\u0081\u0004J\n\u0010\u0019\u001a\u00020\u001aHÖ\u0081\u0004R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\u000bR\u0011\u0010\u0005\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0011\u0010\u0007\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u000e¨\u0006\u001b"},
      d2 = {"Lcom/nexomc/nexo/pack/TextureMerger$Quadrant;", "", "qFrom", "Lteam/unnamed/creative/base/Vector3Float;", "qTo", "lowX", "", "lowZ", "<init>", "(Lteam/unnamed/creative/base/Vector3Float;Lteam/unnamed/creative/base/Vector3Float;ZZ)V", "getQFrom", "()Lteam/unnamed/creative/base/Vector3Float;", "getQTo", "getLowX", "()Z", "getLowZ", "component1", "component2", "component3", "component4", "copy", "equals", "other", "hashCode", "", "toString", "", "com.nexomc:core"}
   )
   private static final class Quadrant {
      @NotNull
      private final Vector3Float qFrom;
      @NotNull
      private final Vector3Float qTo;
      private final boolean lowX;
      private final boolean lowZ;

      public Quadrant(@NotNull Vector3Float qFrom, @NotNull Vector3Float qTo, boolean lowX, boolean lowZ) {
         super();
         this.qFrom = qFrom;
         this.qTo = qTo;
         this.lowX = lowX;
         this.lowZ = lowZ;
      }

      @NotNull
      public final Vector3Float getQFrom() {
         return this.qFrom;
      }

      @NotNull
      public final Vector3Float getQTo() {
         return this.qTo;
      }

      public final boolean getLowX() {
         return this.lowX;
      }

      public final boolean getLowZ() {
         return this.lowZ;
      }

      @NotNull
      public final Vector3Float component1() {
         return this.qFrom;
      }

      @NotNull
      public final Vector3Float component2() {
         return this.qTo;
      }

      public final boolean component3() {
         return this.lowX;
      }

      public final boolean component4() {
         return this.lowZ;
      }

      @NotNull
      public final Quadrant copy(@NotNull Vector3Float qFrom, @NotNull Vector3Float qTo, boolean lowX, boolean lowZ) {
         return new Quadrant(qFrom, qTo, lowX, lowZ);
      }

      @NotNull
      public String toString() {
         return "Quadrant(qFrom=" + this.qFrom + ", qTo=" + this.qTo + ", lowX=" + this.lowX + ", lowZ=" + this.lowZ + ")";
      }

      public int hashCode() {
         int result = this.qFrom.hashCode();
         result = result * 31 + this.qTo.hashCode();
         result = result * 31 + Boolean.hashCode(this.lowX);
         result = result * 31 + Boolean.hashCode(this.lowZ);
         return result;
      }

      public boolean equals(@Nullable Object other) {
         if (this == other) {
            return true;
         } else if (!(other instanceof Quadrant)) {
            return false;
         } else {
            Quadrant quadrant = (Quadrant)other;
            if (!Intrinsics.areEqual(this.qFrom, quadrant.qFrom)) {
               return false;
            } else if (!Intrinsics.areEqual(this.qTo, quadrant.qTo)) {
               return false;
            } else if (this.lowX != quadrant.lowX) {
               return false;
            } else {
               return this.lowZ == quadrant.lowZ;
            }
         }
      }
   }

   public static final class WhenMappings {
      public static final int[] $EnumSwitchMapping$0;
      static {
         int[] a_0 = new int[team.unnamed.creative.base.CubeFace.values().length];
         try { a_0[team.unnamed.creative.base.CubeFace.SOUTH.ordinal()] = 1; } catch (NoSuchFieldError ex) {}
         try { a_0[team.unnamed.creative.base.CubeFace.NORTH.ordinal()] = 2; } catch (NoSuchFieldError ex) {}
         try { a_0[team.unnamed.creative.base.CubeFace.EAST.ordinal()] = 3; } catch (NoSuchFieldError ex) {}
         try { a_0[team.unnamed.creative.base.CubeFace.WEST.ordinal()] = 4; } catch (NoSuchFieldError ex) {}
         try { a_0[team.unnamed.creative.base.CubeFace.UP.ordinal()] = 5; } catch (NoSuchFieldError ex) {}
         try { a_0[team.unnamed.creative.base.CubeFace.DOWN.ordinal()] = 6; } catch (NoSuchFieldError ex) {}
         $EnumSwitchMapping$0 = a_0;
      }
   }
}
