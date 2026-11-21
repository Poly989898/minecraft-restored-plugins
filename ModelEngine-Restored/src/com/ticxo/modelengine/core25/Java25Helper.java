package com.ticxo.modelengine.core25;

import com.ticxo.modelengine.api.nms.NMSHandler;
import com.ticxo.modelengine.v26_1.NMSHandler_v26_1;
import com.ticxo.modelengine.v26_2.NMSHandler_v26_2;
import com.ticxo.modelengine.v26_3.NMSHandler_v26_3;

public class Java25Helper {
   public static NMSHandler createNMSHandler(String version) {
      return switch (version) {
         case "26.1", "26.1.1", "26.1.2" -> new NMSHandler_v26_1();
         case "26.2", "26.2.1", "26.2.2" -> new NMSHandler_v26_2();
         case "26.3", "26.3.1", "26.3.2" -> new NMSHandler_v26_3();
         default -> null;
      };
   }
}
