package io.lumine.mythic.core.skills.cinematic;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class CameraPathRegistry {
   private final Map<String, CameraPath> paths = new ConcurrentHashMap<>();

   public void register(String name, CameraPath path) {
      this.paths.put(name.toLowerCase(), path);
   }

   public Optional<CameraPath> get(String name) {
      return name == null ? Optional.empty() : Optional.ofNullable(this.paths.get(name.toLowerCase()));
   }

   public void clear() {
      this.paths.clear();
   }

   public int size() {
      return this.paths.size();
   }

   public Set<String> keys() {
      return this.paths.keySet();
   }
}
