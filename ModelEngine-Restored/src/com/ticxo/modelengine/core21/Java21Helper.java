package com.ticxo.modelengine.core21;

import com.ticxo.modelengine.api.nms.NMSHandler;
import com.ticxo.modelengine.api.utils.CompatibilityManager;
import com.ticxo.modelengine.core.ModelEngine;
import com.ticxo.modelengine.core21.mythic.MythicUtils;
import com.ticxo.modelengine.core21.mythic.compatibility.MythicCompatibility;
import com.ticxo.modelengine.v1_20_R4.NMSHandler_v1_20_R4;
import com.ticxo.modelengine.v1_21_R1.NMSHandler_v1_21_R1;
import com.ticxo.modelengine.v1_21_R2.NMSHandler_v1_21_R2;
import com.ticxo.modelengine.v1_21_R3.NMSHandler_v1_21_R3;
import com.ticxo.modelengine.v1_21_R4.NMSHandler_v1_21_R4;
import com.ticxo.modelengine.v1_21_R5.NMSHandler_v1_21_R5;
import com.ticxo.modelengine.v1_21_R6.NMSHandler_v1_21_R6;
import com.ticxo.modelengine.v1_21_R7.NMSHandler_v1_21_R7;
import java.io.File;
import java.util.List;
import lombok.Getter;

public class Java21Helper {
   @Getter
   private static MythicCompatibility mythicCompatibility;

   public static void registerCompatibility(CompatibilityManager compatibilityManager, ModelEngine plugin) {
      compatibilityManager.registerSupport("MythicMobs", () -> mythicCompatibility = new MythicCompatibility(plugin));
   }

   public static NMSHandler createNMSHandler(String version) {
      return switch (version) {
         case "1.20.5", "1.20.6" -> new NMSHandler_v1_20_R4();
         case "1.21", "1.21.1" -> new NMSHandler_v1_21_R1();
         case "1.21.2", "1.21.3" -> new NMSHandler_v1_21_R2();
         case "1.21.4" -> new NMSHandler_v1_21_R3();
         case "1.21.5" -> new NMSHandler_v1_21_R4();
         case "1.21.6", "1.21.7", "1.21.8" -> new NMSHandler_v1_21_R5();
         case "1.21.9", "1.21.10" -> new NMSHandler_v1_21_R6();
         case "1.21.11" -> new NMSHandler_v1_21_R7();
         default -> null;
      };
   }

   public static List<File> getMythicPackModelFiles() {
      return MythicUtils.getPackModelFiles();
   }
}
