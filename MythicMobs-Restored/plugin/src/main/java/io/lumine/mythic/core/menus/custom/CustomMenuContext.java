package io.lumine.mythic.core.menus.custom;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.core.players.PlayerData;
import io.lumine.mythic.core.skills.SkillMetadataImpl;
import io.lumine.mythic.core.skills.SkillTriggers;
import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;
import java.util.Objects;
import java.util.Optional;
import lombok.Getter;

@Getter
public class CustomMenuContext implements PlaceholderMeta {
   private final PlayerData profile;
   private volatile SkillMetadata skillMetadata;

   public CustomMenuContext(PlayerData profile) {
      this.profile = Objects.requireNonNull(profile, "profile");
      AbstractPlayer entity = profile.getEntity();
      this.skillMetadata = new SkillMetadataImpl(SkillTriggers.API, profile, entity);
      this.skillMetadata.setEntityTarget(entity);
   }

   @Override
   public SkillCaster getCaster() {
      return this.profile;
   }

   @Override
   public AbstractEntity getTrigger() {
      return this.profile.getEntity();
   }

   public void setMenuData(String key, Object value) {
      Objects.requireNonNull(key, "key");
      if (value != null) {
         this.skillMetadata.setMetadata(key, value);
      }
   }

   public <T> T getMenuData(Class<T> clazz, String key) {
      Objects.requireNonNull(clazz, "clazz");
      Objects.requireNonNull(key, "key");
      return this.skillMetadata.getMetadata(clazz, key).orElse(null);
   }

   @Override
   public PlaceholderMeta setMetadata(String key, Object value) {
      return this.skillMetadata.setMetadata(key, value);
   }

   @Override
   public Optional<Object> getMetadata(String key) {
      return this.skillMetadata.getMetadata(key);
   }

   @Override
   public <T> Optional<T> getMetadata(Class<T> clazz, String key) {
      return this.skillMetadata.getMetadata(clazz, key);
   }

   @Override
   public Optional<Object> removeMetadata(String key) {
      return this.skillMetadata.removeMetadata(key);
   }

   @Override
   public <T> Optional<T> removeMetadata(Class<T> clazz, String key) {
      return this.skillMetadata.removeMetadata(clazz, key);
   }

   @Override
   public void clearMetadata() {
      this.skillMetadata.clearMetadata();
   }

   public CustomMenuContext copy() {
      return new CustomMenuContext(this.profile);
   }
}
