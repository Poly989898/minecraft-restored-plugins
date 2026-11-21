package com.nexomc.nexo.mechanics.custom_block.noteblock;

import com.nexomc.nexo.decomp.ResultShim;
import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import com.nexomc.nexo.NexoBootstrap;
import com.nexomc.nexo.mechanics.MechanicFactory;
import com.nexomc.nexo.mechanics.MechanicsManager;
import com.nexomc.nexo.mechanics.custom_block.noteblock.beacon.BeaconTagDatapack;
import com.nexomc.nexo.utils.FileUtilsKt;
import com.nexomc.nexo.utils.JsonBuilder;
import com.nexomc.nexo.utils.NexoDatapack;
import com.nexomc.nexo.utils.UtilsKt;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.tag.TagKey;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.io.FilesKt;
import net.kyori.adventure.key.Key;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0006\u0010\u0004\u001a\u00020\u0005J\b\u0010\u0006\u001a\u00020\u0005H\u0002¨\u0006\u0007"},
   d2 = {"Lcom/nexomc/nexo/mechanics/custom_block/noteblock/NoteBlockDatapack;", "Lcom/nexomc/nexo/utils/NexoDatapack;", "<init>", "()V", "createDatapack", "", "removeFromMineableTag", "com.nexomc:core"}
)
public final class NoteBlockDatapack extends NexoDatapack {
   public NoteBlockDatapack() {
      super("nexo_custom_blocks", "Datapack for allowing Custom Blocks in Beacons");
      FilesKt.deleteRecursively(this.getDatapackFile());
   }

   public final void createDatapack() {
      boolean flag;
      if (!NexoBootstrap.Companion.getBootsStrung()) {
         this.writeMCMeta();
         this.removeFromMineableTag();
         MechanicsManager mechanicsManager = MechanicsManager.INSTANCE;
         Iterator iterator = ((Iterable)mechanicsManager.getFACTORIES_BY_MECHANIC_ID().values()).iterator();

         MechanicFactory mechanicFactory;
         while(true) {
            if (iterator.hasNext()) {
               MechanicFactory mechanicFactory2 = (MechanicFactory)iterator.next();
               int it = 0;
               mechanicFactory = mechanicFactory2;
               if (!(mechanicFactory2 instanceof NoteBlockMechanicFactory)) {
                  mechanicFactory = null;
               }

               mechanicFactory2 = (NoteBlockMechanicFactory)mechanicFactory;
               if (mechanicFactory2 == null) {
                  continue;
               }

               mechanicFactory = mechanicFactory2;
               break;
            }

            mechanicFactory = null;
            break;
         }

         block: {
            mechanicFactory = mechanicFactory;
            if (mechanicFactory != null) {
               Int2ObjectLinkedOpenHashMap blocK_PER_VARIATION = ((NoteBlockMechanicFactory)mechanicFactory).getBLOCK_PER_VARIATION();
               if (blocK_PER_VARIATION != null) {
                  Map map = (Map)blocK_PER_VARIATION;
                  boolean flag2;
                  if (map.isEmpty()) {
                     flag2 = false;
                  } else {
                     Iterator iterator2 = map.entrySet().iterator();

                     while(true) {
                        if (!iterator2.hasNext()) {
                           flag2 = false;
                           break;
                        }

                        Map.Entry element = (Map.Entry)iterator2.next();
                        int value = 0;
                        if (((NoteBlockMechanic)element.getValue()).isBeaconBaseBlock()) {
                           flag2 = true;
                           break;
                        }
                     }
                  }

                  flag = flag2;
                  break block;
               }
            }

            flag = false;
         }

         if (flag) {
            BeaconTagDatapack.INSTANCE.writeTagFile(this.getDatapackFile());
         }

         super.enableDatapack$com_nexomc_core(true, false);
      }
   }

   private final void removeFromMineableTag() {
      NoteBlockDatapack noteBlockDatapack = this;

      Object obj;
      try {
         NoteBlockDatapack noteBlockDatapack2 = noteBlockDatapack;
         int value = 0;
         Iterable iterable = (Iterable)RegistryAccess.registryAccess().getRegistry(RegistryKey.BLOCK).getTag(TagKey.create(RegistryKey.BLOCK, Key.key("minecraft:mineable/axe"))).values();
         Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

         for(Object item : iterable) {
            TypedKey it = (TypedKey)item;
            int value2 = 0;
            destination.add(new JsonPrimitive(it.key().asString()));
         }

         List p0 = CollectionsKt.minus((Iterable)((List)destination), new JsonPrimitive("minecraft:note_block"));
         JsonBuilder jsonBuilder = JsonBuilder.INSTANCE;
         int value3 = 0;
         JsonArray tagArray = jsonBuilder.toJsonArray(p0);
         File datapackFile = noteBlockDatapack2.getDatapackFile();
         String[] p0_2 = new String[]{"data", "minecraft", "tags", "block", "mineable", "axe.json"};
         File p0_1 = FileUtilsKt.resolve(datapackFile, p0_2);
         int value4 = 0;
         p0_1.getParentFile().mkdirs();
         p0_1.createNewFile();
         String str = JsonBuilder.INSTANCE.plus(JsonBuilder.INSTANCE.plus(JsonBuilder.INSTANCE.getJsonObject(), "replace", true), "values", tagArray).toString();
         FilesKt.writeText(p0_1, str, kotlin.text.Charsets.UTF_8);
         obj = ResultShim.ok(Unit.INSTANCE);
      } catch (Throwable e) {
         obj = ResultShim.ok(ResultKt.createFailure(e));
      }

      UtilsKt.printOnFailure(obj, false);
   }
}
