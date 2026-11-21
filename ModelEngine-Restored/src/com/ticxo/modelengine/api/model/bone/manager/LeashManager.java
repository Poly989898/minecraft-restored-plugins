package com.ticxo.modelengine.api.model.bone.manager;

import com.ticxo.modelengine.api.model.bone.behavior.BoneBehavior;
import com.ticxo.modelengine.api.model.bone.type.Leash;
import java.util.Map;
import java.util.Optional;
import org.bukkit.entity.Entity;

public interface LeashManager {
   <T extends Leash & BoneBehavior> void registerLeash(T leash);

   <T extends Leash & BoneBehavior> Map<String, T> getMainLeashes();

   <T extends Leash & BoneBehavior> Map<String, T> getLeashes();

   <T extends Leash & BoneBehavior> Optional<T> getLeash(String boneId);

   void connectMainLeashes(Entity leashHolder);

   void connectMainLeashes(String from);

   void disconnectMainLeashes();

   void connectLeash(Entity leashHolder, String to);

   void connectLeash(String from, String to);

   void disconnect(String bone);

   Entity getLeashHolder(String bone);

   <T extends Leash & BoneBehavior> T getLeashConnection(String bone);
}
