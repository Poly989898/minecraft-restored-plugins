package com.nexomc.nexo.pack;

import com.nexomc.nexo.items.ItemBuilder;
import com.nexomc.nexo.items.NexoMeta;
import com.nexomc.nexo.utils.KeyUtils;
import com.nexomc.nexo.utils.UtilsKt;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.collections.CollectionsKt;
import net.kyori.adventure.key.Key;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.ResourcePack;
import team.unnamed.creative.model.ItemOverride;
import team.unnamed.creative.model.ItemPredicate;
import team.unnamed.creative.model.Model;
import team.unnamed.creative.model.ModelTexture;
import team.unnamed.creative.model.ModelTextures;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\"\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u00072\u0006\u0010\t\u001a\u00020\n2\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\f0\u0007J \u0010\r\u001a\u00020\u000e2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0010H\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0012"},
   d2 = {"Lcom/nexomc/nexo/pack/PredicateGenerator;", "", "resourcePack", "Lteam/unnamed/creative/ResourcePack;", "<init>", "(Lteam/unnamed/creative/ResourcePack;)V", "generateBaseModelOverrides", "", "Lteam/unnamed/creative/model/ItemOverride;", "material", "Lorg/bukkit/Material;", "items", "Lcom/nexomc/nexo/items/ItemBuilder;", "addMissingOverrideModel", "", "modelKey", "Lnet/kyori/adventure/key/Key;", "parentKey", "com.nexomc:core"}
)
public final class PredicateGenerator {
   @NotNull
   private final ResourcePack resourcePack;

   public PredicateGenerator(@NotNull ResourcePack resourcePack) {
      super();
      this.resourcePack = resourcePack;
   }

   @NotNull
   public final List<ItemOverride> generateBaseModelOverrides(@NotNull Material material, @NotNull List<ItemBuilder> items) {
      ObjectArrayList objectArrayList;
      block: {
         ResourcePack resourcePack2 = VanillaResourcePack.INSTANCE.getResourcePack();
         String str = material.toString().toLowerCase(Locale.ROOT);
         Model model2 = resourcePack2.model(Key.key("item/" + str));
         if (model2 != null) {
            List list = model2.overrides();
            if (list != null) {
               Collection p0 = (Collection)list;
               int value = 0;
               objectArrayList = new ObjectArrayList(p0);
               break block;
            }
         }

         objectArrayList = new ObjectArrayList();
      }

      ObjectArrayList overrides = objectArrayList;
      Iterable iterable = (Iterable)items;

      for(Object element : iterable) {
         ItemBuilder itemBuilder = (ItemBuilder)element;
         int value2 = 0;
         if (itemBuilder.getType() == material && itemBuilder.getNexoMeta().getContainsPackInfo()) {
            NexoMeta nexoMeta = itemBuilder.getNexoMeta();
            Integer customModelData = nexoMeta.getCustomModelData();
            if (customModelData != null) {
               ItemPredicate cmdPredicate = ItemPredicate.customModelData(customModelData);
               Collection collection = (Collection)overrides;
               Key key2 = nexoMeta.getModel();
               ItemPredicate[] itemPredicates = new ItemPredicate[]{cmdPredicate};
               collection.add(ItemOverride.of(key2, itemPredicates));
               Key parentModel = nexoMeta.getParentModel();
               if (nexoMeta.hasBlockingModel()) {
                  Material type = itemBuilder.getType();
                  Key blockingModel = nexoMeta.getBlockingModel();
                  this.addMissingOverrideModel(type, blockingModel, parentModel);
               }

               if (nexoMeta.hasChargedModel()) {
                  Material material2 = itemBuilder.getType();
                  Key chargedModel = nexoMeta.getChargedModel();
                  this.addMissingOverrideModel(material2, chargedModel, parentModel);
               }

               if (nexoMeta.hasCastModel()) {
                  Material material3 = itemBuilder.getType();
                  Key castModel = nexoMeta.getCastModel();
                  this.addMissingOverrideModel(material3, castModel, parentModel);
               }

               if (nexoMeta.hasBrokenModel()) {
                  Material material4 = itemBuilder.getType();
                  Key brokenModel = nexoMeta.getBrokenModel();
                  this.addMissingOverrideModel(material4, brokenModel, parentModel);
               }

               if (nexoMeta.hasFireworkModel()) {
                  Material material5 = itemBuilder.getType();
                  Key fireworkModel = nexoMeta.getFireworkModel();
                  this.addMissingOverrideModel(material5, fireworkModel, parentModel);
               }

               for(Key pullingKey : nexoMeta.getPullingModels()) {
                  this.addMissingOverrideModel(itemBuilder.getType(), pullingKey, parentModel);
               }

               for(Key damagedKey : nexoMeta.getDamagedModels()) {
                  this.addMissingOverrideModel(itemBuilder.getType(), damagedKey, parentModel);
               }

               if (nexoMeta.hasBlockingModel()) {
                  Key key3 = nexoMeta.getBlockingModel();
                  itemPredicates = new ItemPredicate[]{ItemPredicate.blocking(), cmdPredicate};
                  overrides.add(ItemOverride.of(key3, itemPredicates));
               }

               if (nexoMeta.hasChargedModel()) {
                  Key key4 = nexoMeta.getChargedModel();
                  itemPredicates = new ItemPredicate[]{ItemPredicate.charged(), cmdPredicate};
                  overrides.add(ItemOverride.of(key4, itemPredicates));
               }

               if (nexoMeta.hasCastModel()) {
                  Key key5 = nexoMeta.getCastModel();
                  itemPredicates = new ItemPredicate[]{ItemPredicate.cast(), cmdPredicate};
                  overrides.add(ItemOverride.of(key5, itemPredicates));
               }

               if (nexoMeta.hasBrokenModel()) {
                  Key key6 = nexoMeta.getBrokenModel();
                  itemPredicates = new ItemPredicate[]{ItemPredicate.broken(), cmdPredicate};
                  overrides.add(ItemOverride.of(key6, itemPredicates));
               }

               if (nexoMeta.hasFireworkModel()) {
                  Key key7 = nexoMeta.getFireworkModel();
                  itemPredicates = new ItemPredicate[]{ItemPredicate.firework(), cmdPredicate};
                  overrides.add(ItemOverride.of(key7, itemPredicates));
               }

               Iterable pullingModels = (Iterable)nexoMeta.getPullingModels();
               int index = 0;

               for(Object item : pullingModels) {
                  int i = index++;
                  if (i < 0) {
                     CollectionsKt.throwIndexOverflow();
                  }

                  Key model = (Key)item;
                  int value3 = 0;
                  float pull = i == 0 ? 0.0F : Math.min(UtilsKt.roundPredicate((double)(((float)i + 1.0F) / (float)nexoMeta.getPullingModels().size()), 0.05f), 0.9F);
                  Collection collection2 = (Collection)overrides;
                  ItemPredicate[] itemPredicates2 = new ItemPredicate[]{ItemPredicate.pulling(), ItemPredicate.pull(pull), cmdPredicate};
                  collection2.add(ItemOverride.of(model, itemPredicates2));
               }

               pullingModels = (Iterable)CollectionsKt.drop((Iterable)nexoMeta.getDamagedModels(), 1);
               Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(pullingModels, 10)));
               int value5 = 0;

               for(Object obj : pullingModels) {
                  int damage = value5++;
                  if (damage < 0) {
                     CollectionsKt.throwIndexOverflow();
                  }

                  Key key = (Key)obj;
                  int value4 = 0;
                  destination.add(TuplesKt.to(damage + 1, key));
               }

               pullingModels = (Iterable)((List)destination);

               for(Object obj2 : pullingModels) {
                  Pair pair = (Pair)obj2;
                  value5 = 0;
                  int i = ((Number)pair.component1()).intValue();
                  Key model = (Key)pair.component2();
                  float damage = Math.min(UtilsKt.roundPredicate((double)((float)i / (float)nexoMeta.getDamagedModels().size()), 0.05f), 0.99F);
                  Collection collection3 = (Collection)overrides;
                  ItemPredicate[] itemPredicates3 = new ItemPredicate[]{ItemPredicate.pulling(), ItemPredicate.damage(damage), cmdPredicate};
                  collection3.add(ItemOverride.of(model, itemPredicates3));
               }
            }
         }
      }

      return (List)overrides;
   }

   private final void addMissingOverrideModel(Material material, Key modelKey, Key parentKey) {
      ResourcePack resourcePack2 = this.resourcePack;
      Model model = this.resourcePack.model(modelKey);
      if (model == null) {
         Model.Builder builder = Model.model().key(modelKey).parent(parentKey).display(DisplayProperties.INSTANCE.fromMaterial(material));
         ModelTextures.Builder builder2 = ModelTextures.builder();
         ModelTexture[] modelTextures = new ModelTexture[]{ModelTexture.ofKey(KeyUtils.dropExtension(modelKey))};
         model = builder.textures(builder2.layers(modelTextures).build()).build();
      }

      resourcePack2.model(model);
   }
}
