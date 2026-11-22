package com.Zrips.CMI.Modules.Painting;

import com.Zrips.CMI.CMI;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Painting;

public class PaintingManager {
  private CMI plugin;
  HashMap<UUID, Entity> map = new HashMap<>();

  public PaintingManager(CMI cmi) {
    this.plugin = cmi;
  }

  public boolean isEditing(UUID uuid) {
    return this.map.containsKey(uuid);
  }

  public void addEditor(UUID uuid, Entity entity) {
    this.map.put(uuid, entity);
  }

  public Painting getPainting(UUID uuid) {
    Entity entity = this.map.get(uuid);
    return entity instanceof Painting ? (Painting) entity : null;
  }

  public void removeEditor(UUID uuid) {
    this.map.remove(uuid);
  }

  public void removeEditor(Entity entity) {
    var hashMap = new HashMap<>(this.map);

    for (Entry entry : hashMap.entrySet()) {
      if (((Entity) entry.getValue()).getUniqueId().equals(entity.getUniqueId())) {
        this.map.remove(entry.getKey());
        break;
      }
    }
  }
}
