package com.nexomc.nexo.nms.commons;

import com.nexomc.nexo.nms.NMSHandler;
import com.nexomc.nexo.nms.NMSHandlers;
import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 2,
   xi = 48,
   d1 = {"\u0000\n\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\"\u0011\u0010\u0000\u001a\u00020\u00018F¢\u0006\u0006\u001a\u0004\b\u0002\u0010\u0003¨\u0006\u0004"},
   d2 = {"entityUtils", "Lcom/nexomc/nexo/nms/commons/EntityUtils;", "getEntityUtils", "()Lcom/nexomc/nexo/nms/commons/EntityUtils;", "com.nexomc:commons"}
)
public final class EntityUtilsKt {
   @NotNull
   public static final EntityUtils getEntityUtils() {
      NMSHandler handler = NMSHandlers.handler();
      return ((CommonNMSHandler)handler).entityUtils();
   }
}
