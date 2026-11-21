package com.ticxo.modelengine.core.model.bone.manager;

import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehavior;
import com.ticxo.modelengine.api.model.bone.behavior.BoneBehaviorType;
import com.ticxo.modelengine.api.model.bone.manager.AbstractBehaviorManager;
import com.ticxo.modelengine.api.model.bone.manager.LeashManager;
import com.ticxo.modelengine.api.model.bone.type.Leash;
import com.ticxo.modelengine.core.model.bone.behavior.LeashImpl;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import org.bukkit.entity.Entity;

public class LeashManagerImpl extends AbstractBehaviorManager<LeashImpl> implements LeashManager {
   private final Map<String, ?> main = new LinkedHashMap<>();
   private final Map<String, ?> leashes = new LinkedHashMap<>();

   public LeashManagerImpl(ActiveModel activeModel, BoneBehaviorType<LeashImpl> type) {
      super(activeModel, type);
   }

   @Override
   public <T extends Leash & BoneBehavior> void registerLeash(T leash) {
      this.<T>getLeashes().put(leash.getBone().getUniqueBoneId(), leash);
      if (leash.isMainLeash()) {
         this.<T>getMainLeashes().put(leash.getBone().getUniqueBoneId(), leash);
      }
   }

   @Override
   public <T extends Leash & BoneBehavior> Map<String, T> getMainLeashes() {
      return (Map<String, T>)this.main;
   }

   @Override
   public <T extends Leash & BoneBehavior> Map<String, T> getLeashes() {
      return (Map<String, T>)this.leashes;
   }

   @Override
   public <T extends Leash & BoneBehavior> Optional<T> getLeash(String boneId) {
      return Optional.ofNullable(this.<T>getLeashes().get(boneId));
   }

   @Override
   public void connectMainLeashes(Entity leashHolder) {
      this.getMainLeashes().values().forEach(leash -> ((Leash)leash).connect(leashHolder));
   }

   @Override
   public void connectMainLeashes(String from) {
      this.getLeash(from).ifPresent(leash -> this.getMainLeashes().values().forEach(mainLeash -> ((Leash)mainLeash).connect(leash)));
   }

   @Override
   public void disconnectMainLeashes() {
      this.getMainLeashes().values().forEach(rec$ -> ((Leash)rec$).disconnect());
   }

   @Override
   public void connectLeash(Entity leashHolder, String to) {
      this.getLeash(to).ifPresent(leash -> ((Leash)leash).connect(leashHolder));
   }

   @Override
   public void connectLeash(String from, String to) {
      this.getLeash(to).ifPresent(toLeash -> this.getLeash(from).ifPresent(fromLeash -> ((Leash)toLeash).connect(fromLeash)));
   }

   @Override
   public void disconnect(String bone) {
      this.getLeash(bone).ifPresent(rec$ -> ((Leash)rec$).disconnect());
   }

   @Override
   public Entity getLeashHolder(String bone) {
      return this.getLeash(bone).map(rec$ -> ((Leash)rec$).getConnectedEntity()).orElse(null);
   }

   @Override
   public <T extends Leash & BoneBehavior> T getLeashConnection(String bone) {
      return (T)this.getLeash(bone).map(rec$ -> ((Leash)rec$).getConnectedLeash()).orElse(null);
   }
}
