package com.ticxo.modelengine.api.generator.skin;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public interface SkinSplitter {
   void reloadMapping();

   LimbData getLimb(String id, boolean slim);

   Map<String, byte[]> splitSkin(byte[] skinData, boolean slim);

   default Map<String, byte[]> splitSkin(byte[] skinData, boolean slim, Collection<String> limbTypes) {
      HashMap<String, byte[]> map = new HashMap<>(this.splitSkin(skinData, slim));
      map.keySet().retainAll(limbTypes);
      return map;
   }
}
