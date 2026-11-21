package com.nexomc.nexo.pack;

import com.nexomc.nexo.decomp.ResultShim;
import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.configs.Settings;
import com.nexomc.nexo.glyphs.Glyph;
import com.nexomc.nexo.glyphs.ShaderAnimatedGlyph;
import com.nexomc.nexo.utils.CollectionUtilsKt;
import com.nexomc.nexo.utils.KeyUtils;
import com.nexomc.nexo.utils.PluginUtils;
import com.nexomc.nexo.utils.ResourcePackUtilsKt;
import com.nexomc.nexo.utils.UtilsKt;
import com.nexomc.nexo.utils.VectorUtilsKt;
import com.nexomc.nexo.utils.VersionUtil;
import com.nexomc.nexo.utils.logs.Logs;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.MemoryCacheImageInputStream;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.io.CloseableKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.math.MathKt;
import kotlin.ranges.RangesKt;
import kotlin.text.StringsKt;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.ResourcePack;
import team.unnamed.creative.atlas.Atlas;
import team.unnamed.creative.atlas.PalettedPermutationsAtlasSource;
import team.unnamed.creative.atlas.SingleAtlasSource;
import team.unnamed.creative.base.Vector3Float;
import team.unnamed.creative.base.Writable;
import team.unnamed.creative.font.BitMapFontProvider;
import team.unnamed.creative.font.Font;
import team.unnamed.creative.lang.Language;
import team.unnamed.creative.model.Element;
import team.unnamed.creative.model.ElementFace;
import team.unnamed.creative.model.ElementRotation;
import team.unnamed.creative.model.Model;
import team.unnamed.creative.model.ModelTexture;
import team.unnamed.creative.model.ModelTextures;
import team.unnamed.creative.overlay.Overlay;
import team.unnamed.creative.overlay.ResourceContainer;
import team.unnamed.creative.texture.Texture;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000n\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010#\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010%\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010 \n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0012\n\u0002\b\u0007\n\u0002\u0010\u000e\n\u0002\b\u0004\u0018\u00002\u00020\u0001:\u00016B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0006\u0010\u0010\u001a\u00020\u0011J\u000e\u0010\u0015\u001a\u0004\u0018\u00010\u0014*\u00020\tH\u0002J\u0010\u0010%\u001a\u00020\u00112\u0006\u0010&\u001a\u00020\tH\u0002J\f\u0010'\u001a\u00020!*\u00020(H\u0002J\u000e\u0010)\u001a\u0004\u0018\u00010(*\u00020(H\u0002J8\u0010,\u001a\u00020\u00112\u0006\u0010&\u001a\u00020\t2\b\b\u0002\u0010-\u001a\u00020\"2\b\b\u0002\u0010.\u001a\u00020\"2\b\b\u0002\u0010/\u001a\u00020!2\b\b\u0002\u00100\u001a\u00020!H\u0002J \u00101\u001a\u00020\u00112\u0006\u00102\u001a\u0002032\u0006\u00104\u001a\u00020\r2\u0006\u00105\u001a\u00020\rH\u0002R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u0015\u0010\b\u001a\t\u0018\u00010\t¢\u0006\u0002\b\nX\u0082\u0004¢\u0006\u0002\n\u0000R\u0017\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\r0\f¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u001a\u0010\u0012\u001a\u000e\u0012\u0004\u0012\u00020\r\u0012\u0004\u0012\u00020\u00140\u0013X\u0082\u0004¢\u0006\u0002\n\u0000R&\u0010\u0016\u001a\r\u0012\t\u0012\u00070\r¢\u0006\u0002\b\u00170\f8BX\u0082\u0084\u0002¢\u0006\f\n\u0004\b\u0019\u0010\u001a\u001a\u0004\b\u0018\u0010\u000fR!\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\r0\u001c8BX\u0082\u0084\u0002¢\u0006\f\n\u0004\b\u001f\u0010\u001a\u001a\u0004\b\u001d\u0010\u001eR\u0018\u0010 \u001a\u00020!*\u00020\"8BX\u0082\u0004¢\u0006\u0006\u001a\u0004\b \u0010#R\u0018\u0010$\u001a\u00020!*\u00020\"8BX\u0082\u0004¢\u0006\u0006\u001a\u0004\b$\u0010#R\u000e\u0010*\u001a\u00020+X\u0082\u0004¢\u0006\u0002\n\u0000¨\u00067"},
   d2 = {"Lcom/nexomc/nexo/pack/PackValidator;", "", "resourcePack", "Lteam/unnamed/creative/ResourcePack;", "<init>", "(Lteam/unnamed/creative/ResourcePack;)V", "getResourcePack", "()Lteam/unnamed/creative/ResourcePack;", "requiredTexture", "Lteam/unnamed/creative/texture/Texture;", "Lorg/jetbrains/annotations/Nullable;", "invalidTextures", "", "Lnet/kyori/adventure/key/Key;", "getInvalidTextures", "()Ljava/util/Set;", "validatePack", "", "textureDimensionCache", "", "Ljava/awt/Dimension;", "dimensions", "uvTextures", "Lorg/jetbrains/annotations/NotNull;", "getUvTextures", "uvTextures$delegate", "Lkotlin/Lazy;", "gifTextures", "", "getGifTextures", "()Ljava/util/List;", "gifTextures$delegate", "isPowerOfTwo", "", "", "(I)Z", "isMipmapCompatible", "validateModelTexture", "texture", "isValidPng", "Lteam/unnamed/creative/base/Writable;", "toValidPng", "pngMagicNumbers", "", "validateTexture", "maxWidth", "maxHeight", "checkMipmap", "checkMaxResolution", "logMissingTexture", "prefix", "", "parentKey", "key", "TranslationEntry", "com.nexomc:core"}
)
public final class PackValidator {
   @NotNull
   private final ResourcePack resourcePack;
   @Nullable
   private final Texture requiredTexture;
   @NotNull
   private final Set<Key> invalidTextures;
   @NotNull
   private final Map<Key, Dimension> textureDimensionCache;
   @NotNull
   private final Lazy uvTextures$delegate;
   @NotNull
   private final Lazy gifTextures$delegate;
   @NotNull
   private final byte[] pngMagicNumbers;

   public PackValidator(@NotNull ResourcePack resourcePack) {
      super();
      this.resourcePack = resourcePack;
      this.requiredTexture = this.resourcePack.texture(Glyph.Companion.getREQUIRED_GLYPH());
      this.invalidTextures = (Set)(new LinkedHashSet());
      this.textureDimensionCache = (Map)(new LinkedHashMap());
      this.uvTextures$delegate = LazyKt.lazy((() -> PackValidator.uvTextures_delegate$lambda$0(this)));
      this.gifTextures$delegate = LazyKt.lazy(PackValidator::gifTextures_delegate$lambda$0);
      byte[] bs = new byte[]{-119, 80, 78, 71, 13, 10, 26, 10};
      this.pngMagicNumbers = bs;
   }

   @NotNull
   public final ResourcePack getResourcePack() {
      return this.resourcePack;
   }

   @NotNull
   public final Set<Key> getInvalidTextures() {
      return this.invalidTextures;
   }

   public final void validatePack() {
      java.util.List list;
      team.unnamed.creative.model.Element element;
      List list2;
      block: {
         this.invalidTextures.clear();
         Logs.logInfo("Validating ResourcePack files...");
         Atlas atlas2 = this.resourcePack.atlas(Atlas.BLOCKS);
         if (atlas2 != null) {
            List sources = atlas2.sources();
            if (sources != null) {
               Iterable iterable2 = (Iterable)sources;
               Collection destination = (Collection)(new ObjectArrayList());

               for(Object obj7 : iterable2) {
                  if (obj7 instanceof PalettedPermutationsAtlasSource) {
                     destination.add(obj7);
                  }
               }

               Iterable iterable3 = (Iterable)((ObjectArrayList)destination);
               Collection collection = (Collection)(new ObjectArrayList());
               int value24 = 0;

               for(Object obj8 : iterable3) {
                  PalettedPermutationsAtlasSource source = (PalettedPermutationsAtlasSource)obj8;
                  int value2 = 0;
                  Iterable textures = (Iterable)source.textures();
                  Collection collection2 = (Collection)(new ObjectArrayList((textures instanceof Collection ? (Collection)textures : null) != null ? (textures instanceof Collection ? (Collection)textures : null).size() : 10));
                  int value25 = 0;

                  for(Object item : textures) {
                     Key it = (Key)item;
                     int value3 = 0;
                     KeyUtils keyUtils = KeyUtils.INSTANCE;
                     collection2.add(keyUtils.appendPng(it));
                  }

                  textures = (Iterable)((ObjectArrayList)collection2);
                  collection2 = (Collection)(new ObjectArrayList());
                  value25 = 0;

                  for(Object obj9 : textures) {
                     Key texture = (Key)obj9;
                     int value4 = 0;
                     ResourceContainer resourceContainer = this.resourcePack;
                     Texture texture2 = ResourcePackUtilsKt.textureOrVanilla(resourceContainer, texture);
                     Iterable iterable;
                     if (texture2 == null) {
                        PackValidator packValidator = this;
                        int value26 = 0;
                        Key key2 = Atlas.BLOCKS.key();
                        packValidator.logMissingTexture("Atlas", key2, texture);
                        iterable = (Iterable)CollectionsKt.emptyList();
                     } else {
                        Texture texture_36 = texture2;
                        Iterable keySet = (Iterable)source.permutations().keySet();
                        Collection collection3 = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(keySet, 10)));

                        for(Object obj10 : keySet) {
                           String permutation = (String)obj10;
                           int value5 = 0;
                           KeyUtils keyUtils2 = KeyUtils.INSTANCE;
                           KeyUtils keyUtils3 = KeyUtils.INSTANCE;
                           Key key3 = texture_36.key();
                           collection3.add(keyUtils2.appendSuffix(keyUtils3.removeSuffix(key3, ".png"), "_" + permutation + ".png"));
                        }

                        iterable = (Iterable)((List)collection3);
                     }

                     Iterable iterable4 = iterable;
                     CollectionsKt.addAll(collection2, iterable4);
                  }

                  Iterable iterable5 = (Iterable)((ObjectArrayList)collection2);
                  CollectionsKt.addAll(collection2, iterable5);
               }

               list2 = (List)((ObjectArrayList)collection);
               break block;
            }
         }

         list2 = CollectionsKt.emptyList();
      }

      List palettedPermutations = list2;
      Writable icon = this.resourcePack.icon();
      if (icon != null ? !this.isValidPng(icon) : false) {
         icon = this.resourcePack.icon();
         Writable converted = icon != null ? this.toValidPng(icon) : null;
         String debugSuffix = converted != null ? "— it has been auto-converted" : "and could not be converted";
         Logs.logWarn("Pack-Icon is not a valid PNG " + debugSuffix, false);
         Logs.logWarn("Renaming a file to .png does not convert it — re-export it as PNG from an image editor", false);
         if (converted == null) {
            Logs.logWarn("It has been temporarily replaced with a placeholder-image to not break the pack", false);
         }

         icon = converted;
         if (converted == null) {
            Texture texture3 = this.requiredTexture;
            icon = texture3 != null ? texture3.data() : null;
         }

         if (icon != null) {
            Writable writable = icon;
            ResourcePack model = this.resourcePack;
            int value27 = 0;
            model.icon(writable);
            Unit unit = Unit.INSTANCE;
            unit = Unit.INSTANCE;
         } else {
            Object obj = null;
         }
      }

      if (Settings.PACK_VALIDATE_MODELS.toBool()) {
         Collection models = this.resourcePack.models();
         Iterable iterable6 = (Iterable)CollectionsKt.toList((Iterable)models);

         for(Object obj11 : iterable6) {
            Model model = (Model)obj11;
            List layers = model.textures().layers();
            Iterable iterable7 = (Iterable)layers;

            for(Object obj12 : iterable7) {
               ModelTexture layer = (ModelTexture)obj12;
               int value6 = 0;
               Key key4 = layer.key();
               if (key4 != null) {
                  key4 = KeyUtils.INSTANCE.appendPng(key4);
                  if (key4 != null) {
                     Key key = key4;
                     if (!palettedPermutations.contains(key) && VanillaResourcePack.INSTANCE.getResourcePack().texture(key) == null) {
                        Texture texture4 = this.resourcePack.texture(key);
                        if (texture4 != null) {
                           Texture texture6 = texture4;
                           int value28 = 0;
                           this.validateModelTexture(texture6);
                           if (texture6 != null) {
                              continue;
                           }
                        }

                        Key key5 = model.key();
                        this.logMissingTexture("Model", key5, key);
                        Unit unit2 = Unit.INSTANCE;
                     }
                  }
               }
            }

            iterable7 = (Iterable)model.textures().variables().entrySet();

            for(Object obj13 : iterable7) {
               Map.Entry variable = (Map.Entry)obj13;
               int value7 = 0;
               Key key6 = ((ModelTexture)variable.getValue()).key();
               if (key6 != null) {
                  key6 = KeyUtils.INSTANCE.appendPng(key6);
                  if (key6 != null) {
                     Key key = key6;
                     if (!palettedPermutations.contains(key) && VanillaResourcePack.INSTANCE.getResourcePack().texture(key) == null) {
                        Texture texture5 = this.resourcePack.texture(key);
                        if (texture5 != null) {
                           Texture texture7 = texture5;
                           int value29 = 0;
                           this.validateModelTexture(texture7);
                           if (texture7 != null) {
                              continue;
                           }
                        }

                        Key key7 = model.key();
                        this.logMissingTexture("Model", key7, key);
                        Unit unit3 = Unit.INSTANCE;
                     }
                  }
               }
            }

            iterable7 = (Iterable)model.elements();
            boolean flag;
            if (iterable7 instanceof Collection && ((Collection)iterable7).isEmpty()) {
               flag = false;
            } else {
               Iterator iterator2 = iterable7.iterator();

               while(true) {
                  if (!iterator2.hasNext()) {
                     flag = false;
                     break;
                  }

                  block: {
                     Object obj14 = iterator2.next();
                     Element it = (Element)obj14;
                     int value8 = 0;
                     ElementRotation elementRotation = it.rotation();
                     if (elementRotation != null) {
                        Vector3Float vector3Float = elementRotation.rotation();
                        if (vector3Float != null) {
                           Iterable iterable8 = vector3Float;
                           float p0 = 0.0F;
                           Collection collection4 = (Collection)(new ArrayList());

                           for(Object obj15 : iterable8) {
                              int value9 = 0;
                              if (!Float.valueOf(p0).equals(obj15)) {
                                 collection4.add(obj15);
                              }
                           }

                           list = (List)collection4;
                           break block;
                        }
                     }

                     list = null;
                  }

                  if (list == null) {
                     list = CollectionsKt.emptyList();
                  }

                  if (list.size() > 1) {
                     flag = true;
                     break;
                  }
               }
            }

            if (flag && (VersionUtil.INSTANCE.below("1.21.11") || PluginUtils.INSTANCE.isViaBackwardsEnabled())) {
               Logs.logWarn("Model " + model.key() + " is using 1.21.11+ format for rotations (multiple angles, -45/45 limit gone, etc..)", false);
               Logs.logWarn("This is not something Nexo cannot automatically fix as it is core to the model itself", false);
               Logs.logWarn("Nexo will replace this with a placeholder model for earlier versions which might look broken", false);
               Overlay overlay = this.resourcePack.overlay(NexoOverlay.V1_21_11.getId());
               if (overlay != null) {
                  overlay.model(model);
                  Unit unit4 = Unit.INSTANCE;
               } else {
                  Object obj2 = null;
               }

               Iterable iterable9 = (Iterable)model.elements();
               Collection collection5 = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable9, 10)));
               int value30 = 0;

               for(Object obj16 : iterable9) {
                  block: {
                     Element p0 = (Element)obj16;
                     int value10 = 0;
                     ElementRotation elementRotation2 = p0.rotation();
                     if (elementRotation2 != null) {
                        Vector3Float vector3Float2 = elementRotation2.rotation();
                        if (vector3Float2 != null) {
                           vector3Float2 = VectorUtilsKt.clampToFirstAxis(vector3Float2, (Function1)null, PackValidator::validatePack$lambda$1$3$0);
                           if (vector3Float2 != null) {
                              Vector3Float rotation = vector3Float2;
                              ElementRotation elementRotation3 = p0.rotation();
                              if (Intrinsics.areEqual(elementRotation3 != null ? elementRotation3.rotation() : null, rotation)) {
                                 element = p0;
                              } else {
                                 Element.Builder toBuilder = p0.toBuilder();
                                 ElementRotation elementRotation4 = p0.rotation();
                                 element = toBuilder.rotation(elementRotation4 != null ? elementRotation4.rotation(rotation) : null).build();
                              }
                              break block;
                           }
                        }
                     }

                     element = p0;
                  }

                  collection5.add(element);
               }

               List elements = (List)collection5;
               if (!Intrinsics.areEqual(elements, model.elements())) {
                  this.resourcePack.model(model.toBuilder().elements(elements).build());
               }
            }
         }
      }

      if (Settings.PACK_VALIDATE_FONTS.toBool()) {
         Collection fonts = this.resourcePack.fonts();
         Iterable iterable10 = (Iterable)fonts;
         Collection collection6 = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable10, 10)));
         int value31 = 0;

         for(Object obj17 : iterable10) {
            Font font = (Font)obj17;
            int value32 = 0;
            List providers = font.providers();
            Iterable iterable11 = (Iterable)providers;
            Collection collection7 = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable11, 10)));

            for(Object obj18 : iterable11) {
               int p0 = 0;
               Object obj3;
               if (!(obj18 instanceof BitMapFontProvider)) {
                  obj3 = obj18;
               } else {
                  BitMapFontProvider provider = (BitMapFontProvider)obj18;
                  int value11 = 0;
                  KeyUtils keyUtils4 = KeyUtils.INSTANCE;
                  Key file = provider.file();
                  Key key = keyUtils4.appendPng(file);
                  if (palettedPermutations.contains(key)) {
                     obj3 = provider;
                  } else if (VanillaResourcePack.INSTANCE.getResourcePack().texture(key) != null) {
                     obj3 = provider;
                  } else {
                     Texture texture = this.resourcePack.texture(key);
                     Iterable maxHeight = (Iterable)provider.characters();
                     short value12 = 256;
                     Iterator iterator = maxHeight.iterator();
                     if (!iterator.hasNext()) {
                        throw new NoSuchElementException();
                     }

                     String it = (String)iterator.next();
                     String it_25 = it;
                     char[] toCharArray = it_25.toCharArray();
                     int it_24 = toCharArray.length;

                     while(iterator.hasNext()) {
                        String it_23 = (String)iterator.next();
                        int value13 = 0;
                        toCharArray = it_23.toCharArray();
                        int value14 = toCharArray.length;
                        if (it_24 < value14) {
                           it_24 = value14;
                        }
                     }

                     int maxWidth = value12 * it_24;
                     int maxHeight_22 = 256 * provider.characters().size();
                     if (texture != null) {
                        it_24 = 0;
                        this.validateTexture(texture, maxWidth, maxHeight_22, false, true);
                        obj3 = provider;
                     } else {
                        Key key8 = font.key();
                        this.logMissingTexture("Font", key8, key);
                        Logs.logWarn("It has been temporarily replaced with a placeholder-image to not break the pack", false);
                        obj3 = provider.file(Glyph.Companion.getREQUIRED_GLYPH());
                     }
                  }
               }

               collection7.add(obj3);
            }

            List list4 = (List)collection7;
            collection6.add(font.providers(list4));
         }

         iterable10 = (Iterable)((List)collection6);
         ResourcePack resourcePack2 = this.resourcePack;

         for(Object obj19 : iterable10) {
            Font p0 = (Font)obj19;
            int value33 = 0;
            resourcePack2.font(p0);
         }
      }

      if (Settings.PACK_VALIDATE_ATLAS.toBool()) {
         Atlas atlas3 = this.resourcePack.atlas(Atlas.BLOCKS);
         if (atlas3 == null) {
            Object obj4 = null;
         } else {
            Atlas atlas = atlas3;
            List list5 = atlas.sources();
            Iterable iterable12 = (Iterable)list5;

            for(Object obj20 : iterable12) {
               int value34 = 0;
               if (obj20 instanceof SingleAtlasSource) {
                  SingleAtlasSource source = (SingleAtlasSource)obj20;
                  int value35 = 0;
                  KeyUtils keyUtils5 = KeyUtils.INSTANCE;
                  Key resource = source.resource();
                  Key key = keyUtils5.appendPng(resource);
                  if (VanillaResourcePack.INSTANCE.getResourcePack().texture(key) == null && !palettedPermutations.contains(key) && this.resourcePack.texture(key) == null) {
                     Key key9 = Atlas.BLOCKS.key();
                     this.logMissingTexture("Atlas", key9, key);
                  }
               }
            }

            if (!Settings.PACK_PREFER_ITEMMODELS.toBool() && (VersionUtil.atleast("1.21.11") || PluginUtils.INSTANCE.isEnabled("ViaVersion"))) {
               Logs.logWarn("Due to changes in how the 1.21.11+ clients handle textures,", false);
               Logs.logWarn("it is heavily advised to swap away from CustomModelData to ItemModels", false);
               Logs.logWarn("for custom NexoItems, as it will easily break Models & Textures.", false);
               Logs.logWarn("This can easily be done by following this guide here: https://docs.nexomc.com/configuration/items-advanced/itemmodels-vs.-custommodeldata", false);
            }

            Unit unit5 = Unit.INSTANCE;
            unit5 = Unit.INSTANCE;
         }
      }

      if (Settings.PACK_VALIDATE_LANGUAGES.toBool()) {
         Iterable iterable13 = (Iterable)NexoPlugin.Companion.instance().fontManager().glyphs();
         Collection collection8 = (Collection)(new ArrayList());

         for(Object obj21 : iterable13) {
            Glyph it = (Glyph)obj21;
            if (!Intrinsics.areEqual(it.getFont(), Font.MINECRAFT_DEFAULT)) {
               collection8.add(obj21);
            }
         }

         List glyphs = (List)collection8;
         Iterable iterable14 = (Iterable)glyphs;
         int capacity = RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault(iterable14, 10)), 16);
         Map map = (Map)(new LinkedHashMap(capacity));

         for(Object obj22 : iterable14) {
            Glyph it = (Glyph)obj22;
            int value15 = 0;
            String str = it.getId();
            Iterable iterable15 = (Iterable)it.getUnicodes();
            String str2 = str;
            Collection collection9 = (Collection)(new ArrayList());
            int value36 = 0;

            for(Object obj23 : iterable15) {
               String it_17 = (String)obj23;
               int value16 = 0;
               char[] ches = it_17.toCharArray();
               Iterable iterable16 = (Iterable)ArraysKt.toList(ches);
               CollectionsKt.addAll(collection9, iterable16);
            }

            Pair pair2 = TuplesKt.to(str2, (List)collection9);
            map.put(pair2.getFirst(), pair2.getSecond());
         }

         Map unicodes = map;
         int langCount = this.resourcePack.languages().size();
         Collection languages = this.resourcePack.languages();
         Iterable iterable17 = (Iterable)languages;
         Collection collection10 = (Collection)(new ArrayList());
         int value37 = 0;

         for(Object obj24 : iterable17) {
            Language lang = (Language)obj24;
            int value17 = 0;
            Map translations = lang.translations();
            Map map2 = translations;
            Collection collection11 = (Collection)(new ArrayList(map2.size()));
            int value38 = 0;

            for(Map.Entry entry5 : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)map2.entrySet()) {
               int value18 = 0;
               String key = (String)entry5.getKey();
               String value = (String)entry5.getValue();
               Key key10 = lang.key();
               collection11.add(new TranslationEntry(key10, key, value));
            }

            Iterable iterable18 = (Iterable)((List)collection11);
            CollectionsKt.addAll(collection11, iterable18);
         }

         iterable17 = (Iterable)((List)collection10);
         Map map3 = (Map)(new LinkedHashMap());
         value37 = 0;

         for(Object obj25 : iterable17) {
            TranslationEntry it = (TranslationEntry)obj25;
            int value39 = 0;
            Object obj26 = TuplesKt.to(it.getKey(), it.getValue());
            Object obj27 = map3.get(obj26);
            Object obj5;
            if (obj27 == null) {
               int value40 = 0;
               Object answer = (List)(new ArrayList());
               map3.put(obj26, answer);
               obj5 = answer;
            } else {
               obj5 = obj27;
            }

            List list6 = (List)obj5;
            list6.add(obj25);
         }

         Collection collection12 = (Collection)(new ArrayList());

         for(Object entryObj : map3.entrySet()) {
            Map.Entry entry6 = (Map.Entry)entryObj;
            int value19 = 0;
            Pair pair = (Pair)entry6.getKey();
            List entries = (List)entry6.getValue();
            int value20 = 0;
            List list3 = langCount != entries.size() ? entries : null;
            if (list3 == null) {
               Key key11 = Key.key("global");
               list3 = CollectionsKt.listOf(new TranslationEntry(key11, pair));
            }

            Iterable iterable19 = (Iterable)list3;
            CollectionsKt.addAll(collection12, iterable19);
         }

         List result = (List)collection12;
         Iterable iterable20 = (Iterable)result;

         for(Object obj28 : iterable20) {
            TranslationEntry entry = (TranslationEntry)obj28;
            int value21 = 0;
            Iterable entrySet = (Iterable)unicodes.entrySet();
            Iterator p0 = entrySet.iterator();

            Map.Entry entry2;
            while(true) {
               if (!p0.hasNext()) {
                  entry2 = null;
                  break;
               }

               Object obj29 = p0.next();
               Map.Entry entry7 = (Map.Entry)obj29;
               int value22 = 0;
               List chars = (List)entry7.getValue();
               Iterable iterable21 = (Iterable)chars;
               boolean flag2;
               if (iterable21 instanceof Collection && ((Collection)iterable21).isEmpty()) {
                  flag2 = false;
               } else {
                  Iterator iterator3 = iterable21.iterator();

                  while(true) {
                     if (!iterator3.hasNext()) {
                        flag2 = false;
                        break;
                     }

                     Object obj30 = iterator3.next();
                     char it = (Character)obj30;
                     int key = 0;
                     if (StringsKt.contains((CharSequence)entry.getValue(), it, false)) {
                        flag2 = true;
                        break;
                     }
                  }
               }

               if (flag2) {
                  entry2 = (Map.Entry)obj29;
                  break;
               }
            }

            Map.Entry entry3 = entry2;
            if (entry3 != null) {
               Map.Entry entry4 = entry3;
               int value23 = 0;
               String id = (String)entry4.getKey();
               Logs.logWarn("Non Default-font Glyph <i>" + id + "</i> detected in " + entry.getLanguage() + " at " + entry.getKey(), false);
               Logs.logWarn("If this is for the Escape Menu, the Glyph must use 'font: minecraft:default'", false);
               Unit unit6 = Unit.INSTANCE;
               unit6 = Unit.INSTANCE;
            } else {
               Object obj6 = null;
            }
         }
      }

   }

   private final Dimension dimensions(Texture texture) {
      try {
      if (this.textureDimensionCache.containsKey(texture.key())) {
         return null;
      } else {
         byte[] toByteArray = texture.data().toByteArray();
         byte[] bs = toByteArray;
         Closeable closeable = (Closeable)(new MemoryCacheImageInputStream((InputStream)(new ByteArrayInputStream(bs))));
         Throwable throwable = null;

         Dimension dimension;
         try {
            MemoryCacheImageInputStream input = (MemoryCacheImageInputStream)closeable;
            int value = 0;
            Iterator it = ImageIO.getImageReaders(input);
            int value2 = 0;
            Iterator iterator = it.hasNext() ? it : null;
            if (iterator == null) {
               Unit unit = Unit.INSTANCE;
               return null;
            }

            Iterator readers = iterator;
            Object it_8 = readers.next();
            ImageReader imageReader = (ImageReader)it_8;
            value2 = 0;
            imageReader.setInput(input);
            ImageReader reader = (ImageReader)it_8;
            Dimension it_7 = new Dimension(reader.getWidth(0), reader.getHeight(0));
            value2 = 0;
            this.textureDimensionCache.put(texture.key(), it_7);
            dimension = it_7;
         } catch (Throwable e) {
            throwable = e;
            throw e;
         } finally {
            CloseableKt.closeFinally(closeable, throwable);
         }

         return dimension;
      }
      } catch (Exception ex) {
         throw com.nexomc.nexo.utils.SneakyThrow.sneaky(ex);
      }
   }

   private final Set<Key> getUvTextures() {
      Lazy lazy = this.uvTextures$delegate;
      return (Set)lazy.getValue();
   }

   private final List<Key> getGifTextures() {
      Lazy lazy = this.gifTextures$delegate;
      return (List)lazy.getValue();
   }

   private final boolean isPowerOfTwo(int value2) {
      double it = (double)value2;
      int value = 0;
      return Math.ceil(MathKt.log2(it)) == Math.floor(MathKt.log2(it)) && it != (double)0.0F;
   }

   private final boolean isMipmapCompatible(int value) {
      return value % 16 == 0 || this.isPowerOfTwo(value);
   }

   private final void validateModelTexture(Texture texture) {
      this.validateTexture(texture, 256, 256, true, false);
   }

   private final boolean isValidPng(Writable writable) {
      try {
      byte[] toByteArray = writable.toByteArray();
      byte[] bytes = toByteArray;
      return bytes.length < 8 ? false : Arrays.equals(ArraysKt.copyOfRange(bytes, 0, 8), this.pngMagicNumbers);
      } catch (Exception e) {
         throw com.nexomc.nexo.utils.SneakyThrow.sneaky(e);
      }
   }

   private final Writable toValidPng(Writable writable2) {
      Writable writable3 = writable2;

      Object obj;
      try {
         int value = 0;
         byte[] toByteArray = writable3.toByteArray();
         byte[] bs = toByteArray;
         BufferedImage bufferedImage = ImageIO.read((InputStream)(new ByteArrayInputStream(bs)));
         if (bufferedImage == null) {
            return null;
         }

         BufferedImage image = bufferedImage;
         Closeable closeable = (Closeable)(new ByteArrayOutputStream());
         Throwable throwable = null;

         Writable writable;
         try {
            ByteArrayOutputStream it = (ByteArrayOutputStream)closeable;
            int value2 = 0;
            ImageIO.write((RenderedImage)image, "png", (OutputStream)it);
            writable = Writable.bytes(it.toByteArray());
         } catch (Throwable e) {
            throwable = e;
            throw e;
         } finally {
            CloseableKt.closeFinally(closeable, throwable);
         }

         obj = ResultShim.ok(writable);
      } catch (Throwable ex) {
         obj = ResultShim.ok(ResultKt.createFailure(ex));
      }

      Object obj2 = UtilsKt.printOnFailure(obj, false);
      return (Writable)(ResultShim.isFailure(obj2) ? null : obj2);
   }

   private final void validateTexture(Texture texture, int maxWidth, int maxHeight, boolean checkMipmap, boolean checkMaxResolution) {
      team.unnamed.creative.texture.Texture.Builder builder;
      team.unnamed.creative.base.Writable writable;
      Key key2 = texture.key();
      Key key = key2;
      PackValidator packValidator = this;

      Object obj2;
      try {
         block: {
            PackValidator packValidator2 = packValidator;
            int value = 0;
            if (packValidator2.getUvTextures().contains(KeyUtils.INSTANCE.removeSuffix(key, ".png"))) {
               return;
            }

            if (packValidator2.getGifTextures().contains(key) || texture.hasMetadata()) {
               return;
            }

            ResourcePack resourcePack2 = packValidator2.resourcePack;
            String namespace = key.namespace();
            if (resourcePack2.unknownFile("assets/" + namespace + "/textures/" + key.value() + ".mcmeta") != null) {
               return;
            }

            Writable data = texture.data();
            if (!packValidator2.isValidPng(data)) {
               data = texture.data();
               Writable converted = packValidator2.toValidPng(data);
               String debugSuffix = converted != null ? "— it has been auto-converted" : "and could not be converted";
               Logs.logWarn("Texture <#E24D47><i>" + key + "</i></#E24D47> is not a valid PNG " + debugSuffix, false);
               Logs.logWarn("Renaming a file to .png does not convert it — re-export it as PNG from an image editor", false);
               if (converted == null) {
                  Logs.logWarn("It has been temporarily replaced with a placeholder-image to not break the pack", false);
               }

               resourcePack2 = packValidator2.resourcePack;
               Writable writable2 = converted;
               if (converted == null) {
                  Texture texture2 = packValidator2.requiredTexture;
                  writable2 = texture2 != null ? texture2.data() : null;
                  if (writable2 == null) {
                     return;
                  }
               }

               resourcePack2.texture(texture.data(writable2));
               return;
            }

            Dimension dimension = packValidator2.dimensions(texture);
            if (dimension != null) {
               Dimension it = dimension;
               int value2 = 0;
               Pair pair = TuplesKt.to(it.width, it.height);
               if (pair != null) {
                  Pair converted = pair;
                  int width = ((Number)converted.component1()).intValue();
                  int height = ((Number)converted.component2()).intValue();
                  ResourcePack resourcePack3 = packValidator2.resourcePack;
                  Key key3 = Atlas.ITEMS;
                  Collection itemAtlas = (Collection)ResourcePackUtilsKt.atlasOrOverlays(resourcePack3, key3);
                  Object obj;
                  if (itemAtlas.isEmpty()) {
                     value2 = 0;
                     Atlas atlas = VanillaResourcePack.INSTANCE.getResourcePack().atlas(Atlas.ITEMS);
                     obj = atlas != null ? atlas.sources() : null;
                  } else {
                     obj = itemAtlas;
                  }

                  List list = (List)obj;
                  if (list == null) {
                     list = CollectionsKt.emptyList();
                  }

                  List itemSources = list;
                  Atlas atlas2 = Atlas.atlas(Atlas.ITEMS, itemSources);
                  Atlas itemAtlas_3 = atlas2;
                  if (Settings.PACK_VALIDATE_MIPMAP.toBool() && checkMipmap && (!packValidator2.isMipmapCompatible(width) || !packValidator2.isMipmapCompatible(height)) && !itemAtlas_3.contains(texture)) {
                     Logs.logWarn("Texture <#E24D47><i>" + key + "</i></#E24D47> has bad resolution-ratio " + width + "x" + height, false);
                     Logs.logWarn("Textures not using ^2 will lower mipmap-level, causing blurry visuals", false);
                     Logs.logWarn("Recommend converting it to a resolution in ^2, 2x2, 4x4, 8x8, 16x16, 32x32...", false);
                  }

                  if (checkMaxResolution && (width > maxWidth || height > maxHeight)) {
                     block2: {
                        Logs.logError("Texture <#E24D47><i>" + key + "</i></#E24D47> is above allowed " + maxWidth + "x" + maxHeight + " resolution...", false);
                        Logs.logWarn("It has been temporarily replaced with a placeholder-image to not break the pack", false);
                        builder = texture.toBuilder();
                        Texture texture3 = packValidator2.requiredTexture;
                        if (texture3 != null) {
                           writable = texture3.data();
                           if (writable != null) {
                              break block2;
                           }
                        }

                        writable = texture.data();
                     }

                     builder.data(writable).build();
                  }

                  obj2 = ResultShim.ok(Unit.INSTANCE);
                  break block;
               }
            }

            return;
         }
      } catch (Throwable e) {
         obj2 = ResultShim.ok(ResultKt.createFailure(e));
      }

      UtilsKt.printOnFailure(obj2, true);
   }

   private final void logMissingTexture(String prefix, Key parentKey, Key key) {
      if (this.invalidTextures.add(key)) {
         Logs.logError(prefix + " <#E24D47><i>" + parentKey + "</i></#E24D47> is trying to use texture <#E24D47><i>" + key + "</i></#E24D47>, but it does not exist within Nexo's ResourcePacks", false);
      }

   }

   private static final float validatePack$lambda$1$3$0(float it) {
      return RangesKt.coerceIn(it, -45.0F, 45.0F);
   }

   private static final Set uvTextures_delegate$lambda$0(PackValidator packValidator) {
      net.kyori.adventure.key.Key key;
      Collection models = packValidator.resourcePack.models();
      Iterable iterable = (Iterable)models;
      int capacity = RangesKt.coerceAtLeast(CollectionUtilsKt.mapCapacity((Collection)iterable != null ? ((Collection)iterable).size() : 10), 16);
      Map destination = (Map)(new Object2ObjectOpenHashMap(capacity));

      for(Object element : iterable) {
         Model it = (Model)element;
         int value = 0;
         Pair pair = TuplesKt.to(it.textures(), it.elements());
         destination.put(pair.getFirst(), pair.getSecond());
      }

      Map map = (Map)((Object2ObjectOpenHashMap)destination);
      Collection collection = (Collection)((Set)(new LinkedHashSet()));
      capacity = 0;

      for(Map.Entry entry : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)map.entrySet()) {
         int value2 = 0;
         ModelTextures textures = (ModelTextures)entry.getKey();
         List elements = (List)entry.getValue();
         Iterable iterable2 = (Iterable)elements;
         Collection collection2 = (Collection)(new ObjectArrayList());
         int value8 = 0;

         for(Object obj : iterable2) {
            Element it = (Element)obj;
            int value9 = 0;
            Iterable iterable3 = (Iterable)it.faces().values();
            CollectionsKt.addAll(collection2, iterable3);
         }

         iterable2 = (Iterable)((ObjectArrayList)collection2);
         collection2 = (Collection)(new ObjectArrayList());
         value8 = 0;

         for(Object obj2 : iterable2) {
            ElementFace it = (ElementFace)obj2;
            int value3 = 0;
            if (it.uv() != null) {
               collection2.add(obj2);
            }
         }

         iterable2 = (Iterable)((ObjectArrayList)collection2);
         collection2 = (Collection)(new ArrayList());
         value8 = 0;

         for(Object obj3 : iterable2) {
            block: {
               int value4 = 0;
               ElementFace face = (ElementFace)obj3;
               int value5 = 0;
               Map variables = textures.variables();
               String texture = face.texture();
               ModelTexture modelTexture = (ModelTexture)variables.get(StringsKt.removePrefix(texture, (CharSequence)"#"));
               if (modelTexture != null) {
                  Key key2 = modelTexture.key();
                  if (key2 != null) {
                     key = KeyUtils.INSTANCE.appendPng(key2);
                     if (key != null) {
                        break block;
                     }
                  }
               }

               List layers = textures.layers();
               List list = layers;
               String str = face.texture();
               Integer number = StringsKt.toIntOrNull(StringsKt.substringAfter(str, "layer", str));
               int value6 = number != null ? number : -1;
               ModelTexture orNull = (ModelTexture)CollectionsKt.getOrNull(list, value6);
               if (orNull != null) {
                  Key key3 = orNull.key();
                  if (key3 != null) {
                     key = KeyUtils.INSTANCE.appendPng(key3);
                     break block;
                  }
               }

               key = null;
            }

            if (key != null) {
               Object obj4 = key;
               int value7 = 0;
               collection2.add(obj4);
            }
         }

         Iterable iterable4 = (Iterable)((List)collection2);
         CollectionsKt.addAll(collection, iterable4);
      }

      return (Set)collection;
   }

   private static final List gifTextures_delegate$lambda$0() {
      Iterable glyphs = (Iterable)NexoPlugin.Companion.instance().fontManager().glyphs();
      Collection destination = (Collection)(new ArrayList());

      for(Object element : glyphs) {
         if (element instanceof ShaderAnimatedGlyph) {
            destination.add(element);
         }
      }

      glyphs = (Iterable)((List)destination);
      destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(glyphs, 10)));

      for(Object item : glyphs) {
         ShaderAnimatedGlyph p0 = (ShaderAnimatedGlyph)item;
         int value = 0;
         destination.add(p0.getTexture());
      }

      return (List)destination;
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0082\b\u0018\u00002\u00020\u0001B\u001f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005¢\u0006\u0004\b\u0007\u0010\bB%\b\u0016\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0012\u0010\t\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00050\n¢\u0006\u0004\b\u0007\u0010\u000bJ\t\u0010\u0011\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0012\u001a\u00020\u0005HÆ\u0003J\t\u0010\u0013\u001a\u00020\u0005HÆ\u0003J'\u0010\u0014\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u0005HÆ\u0001J\u0014\u0010\u0015\u001a\u00020\u00162\b\u0010\u0017\u001a\u0004\u0018\u00010\u0001HÖ\u0083\u0004J\n\u0010\u0018\u001a\u00020\u0019HÖ\u0081\u0004J\n\u0010\u001a\u001a\u00020\u0005HÖ\u0081\u0004R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\u0006\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u000f¨\u0006\u001b"},
      d2 = {"Lcom/nexomc/nexo/pack/PackValidator$TranslationEntry;", "", "language", "Lnet/kyori/adventure/key/Key;", "key", "", "value", "<init>", "(Lnet/kyori/adventure/key/Key;Ljava/lang/String;Ljava/lang/String;)V", "pair", "Lkotlin/Pair;", "(Lnet/kyori/adventure/key/Key;Lkotlin/Pair;)V", "getLanguage", "()Lnet/kyori/adventure/key/Key;", "getKey", "()Ljava/lang/String;", "getValue", "component1", "component2", "component3", "copy", "equals", "", "other", "hashCode", "", "toString", "com.nexomc:core"}
   )
   private static final class TranslationEntry {
      @NotNull
      private final Key language;
      @NotNull
      private final String key;
      @NotNull
      private final String value;

      public TranslationEntry(@NotNull Key language, @NotNull String key, @NotNull String value) {
         super();
         this.language = language;
         this.key = key;
         this.value = value;
      }

      @NotNull
      public final Key getLanguage() {
         return this.language;
      }

      @NotNull
      public final String getKey() {
         return this.key;
      }

      @NotNull
      public final String getValue() {
         return this.value;
      }

      public TranslationEntry(@NotNull Key language, @NotNull Pair<String, String> pair) {
         this(language, pair.getFirst(), pair.getSecond());
      }

      @NotNull
      public final Key component1() {
         return this.language;
      }

      @NotNull
      public final String component2() {
         return this.key;
      }

      @NotNull
      public final String component3() {
         return this.value;
      }

      @NotNull
      public final TranslationEntry copy(@NotNull Key language, @NotNull String key, @NotNull String value) {
         return new TranslationEntry(language, key, value);
      }

      @NotNull
      public String toString() {
         return "TranslationEntry(language=" + this.language + ", key=" + this.key + ", value=" + this.value + ")";
      }

      public int hashCode() {
         int result = this.language.hashCode();
         result = result * 31 + this.key.hashCode();
         result = result * 31 + this.value.hashCode();
         return result;
      }

      public boolean equals(@Nullable Object other) {
         if (this == other) {
            return true;
         } else if (!(other instanceof TranslationEntry)) {
            return false;
         } else {
            TranslationEntry translationEntry = (TranslationEntry)other;
            if (!Intrinsics.areEqual(this.language, translationEntry.language)) {
               return false;
            } else if (!Intrinsics.areEqual(this.key, translationEntry.key)) {
               return false;
            } else {
               return Intrinsics.areEqual(this.value, translationEntry.value);
            }
         }
      }
   }
}
