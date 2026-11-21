package com.ticxo.modelengine.api.animation.keyframe.data;

import com.ticxo.modelengine.api.utils.logger.TLogger;
import com.ticxo.modelengine.api.utils.registry.TUnaryRegistry;
import java.util.function.Function;

public class KeyframeReaderRegistry extends TUnaryRegistry<Function<String, IKeyframeData>> {
   public IKeyframeData tryParse(String data) {
      if (data == null) {
         return IKeyframeData.EMPTY;
      }

      data = data.trim();
      if (data.isEmpty()) {
         return IKeyframeData.EMPTY;
      }

      try {
         return new DoubleData(Double.parseDouble(data));
      } catch (NumberFormatException ex) {
         String[] value = data.split(":", 2);
         if (value.length == 1) {
            for (Function<String, IKeyframeData> func : this.registry.values()) {
               try {
                  return func.apply(value[0]);
               } catch (Throwable ex2) {
               }
            }
         } else {
            Function<String, IKeyframeData> func = this.get(value[0]);
            if (func != null) {
               try {
                  return func.apply(value[1]);
               } catch (Throwable e) {
                  TLogger.error(2, "------An error occurred while parsing the keyframe: " + data);
                  e.printStackTrace();
               }
            } else {
               for (Function<String, IKeyframeData> func2 : this.registry.values()) {
                  try {
                     return func2.apply(data);
                  } catch (Throwable ex3) {
                  }
               }
            }
         }

         TLogger.warn(2, "------Unknown keyframe data: " + data);
         return IKeyframeData.EMPTY;
      }
   }
}
