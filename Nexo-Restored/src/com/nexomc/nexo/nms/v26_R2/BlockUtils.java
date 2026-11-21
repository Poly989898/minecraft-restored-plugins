package com.nexomc.nexo.nms.v26_R2;

import com.nexomc.nexo.nms.IBlockUtils;
import java.util.List;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import org.bukkit.Instrument;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003¨\u0006\u0004"},
   d2 = {"Lcom/nexomc/nexo/nms/v26_R2/BlockUtils;", "Lcom/nexomc/nexo/nms/IBlockUtils;", "<init>", "()V", "com.nexomc:v26_R2"}
)
public final class BlockUtils extends IBlockUtils {
   public BlockUtils() {
      Instrument[] instruments = new Instrument[]{Instrument.TRUMPET, Instrument.TRUMPET_EXPOSED, Instrument.TRUMPET_OXIDIZED, Instrument.TRUMPET_WEATHERED};
      List list = CollectionsKt.listOf(instruments);
      Pair[] pairs = new Pair[]{TuplesKt.to(Instrument.TRUMPET, CollectionsKt.listOf("copper")), TuplesKt.to(Instrument.TRUMPET_EXPOSED, CollectionsKt.listOf("exposed_copper")), TuplesKt.to(Instrument.TRUMPET_WEATHERED, CollectionsKt.listOf("weathered_copper")), TuplesKt.to(Instrument.TRUMPET_OXIDIZED, CollectionsKt.listOf("oxidized_copper"))};
      super(list, MapsKt.mapOf(pairs));
   }
}
