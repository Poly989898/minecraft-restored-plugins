package com.nexomc.nexo.mechanics.custom_block.noteblock.beacon;

import com.nexomc.nexo.decomp.ResultShim;
import com.google.gson.JsonObject;
import com.nexomc.nexo.utils.JsonBuilder;
import com.nexomc.nexo.utils.UtilsKt;
import java.io.File;
import java.nio.charset.Charset;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.io.FilesKt;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007¨\u0006\b"},
   d2 = {"Lcom/nexomc/nexo/mechanics/custom_block/noteblock/beacon/BeaconTagDatapack;", "", "<init>", "()V", "writeTagFile", "", "datapackFile", "Ljava/io/File;", "com.nexomc:core"}
)
public final class BeaconTagDatapack {
   @NotNull
   public static final BeaconTagDatapack INSTANCE = new BeaconTagDatapack();

   private BeaconTagDatapack() {
   }

   public final void writeTagFile(@NotNull File datapackFile) {
      BeaconTagDatapack beaconTagDatapack = this;

      Object obj;
      try {
         BeaconTagDatapack beaconTagDatapack2 = beaconTagDatapack;
         int value = 0;
         File tagFile = FilesKt.resolve(datapackFile, "data/minecraft/tags/blocks/beacon_base_blocks.json");
         tagFile.getParentFile().mkdirs();
         JsonObject content = JsonBuilder.INSTANCE.plus(JsonBuilder.INSTANCE.plus(JsonBuilder.INSTANCE.getJsonObject(), "replace", false), "values", JsonBuilder.INSTANCE.plus(JsonBuilder.INSTANCE.getJsonArray(), "minecraft:note_block"));
         String str = content.toString();
         FilesKt.writeText(tagFile, str, kotlin.text.Charsets.UTF_8);
         obj = ResultShim.ok(Unit.INSTANCE);
      } catch (Throwable e) {
         obj = ResultShim.ok(ResultKt.createFailure(e));
      }

      UtilsKt.printOnFailure(obj, false);
   }
}
