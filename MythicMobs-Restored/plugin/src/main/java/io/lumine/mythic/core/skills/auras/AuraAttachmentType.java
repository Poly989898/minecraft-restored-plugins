package io.lumine.mythic.core.skills.auras;

import com.google.common.collect.Maps;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.auras.attachment.MEGAttachment;
import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;

public final class AuraAttachmentType {
   private static final Map<String, AuraAttachmentType> attachmentTypes = Maps.newConcurrentMap();
   public static final AuraAttachmentType ME = new AuraAttachmentType("ME", MEGAttachment.class, "MEG", "MODELENGINE");
   @Getter
   private final String name;
   @Getter
   private final String[] aliases;
   private final Class<? extends AuraAttachment> clazz;
   private final Constructor constructor;

   private AuraAttachmentType(String name, Class<? extends AuraAttachment> clazz, String... aliases) {
      this.name = name;
      this.aliases = aliases;
      this.clazz = clazz;

      Constructor constructor;
      try {
         constructor = clazz.getConstructor(AuraAttachmentType.class, SkillMechanic.class, MythicLineConfig.class);
      } catch (Throwable e) {
         e.printStackTrace();
         constructor = null;
      }

      this.constructor = constructor;
      if (this.constructor != null) {
         registerAttachmentType(this);
      }
   }

   public AuraAttachment create(SkillMechanic aura, MythicLineConfig config) {
      try {
         return (AuraAttachment)this.constructor.newInstance(this, aura, config);
      } catch (Throwable e) {
         MythicLogger.error("Failed to construct Aura Attachment");
         e.printStackTrace();
         return null;
      }
   }

   public static Optional<AuraAttachmentType> get(String name) {
      return Optional.ofNullable(attachmentTypes.getOrDefault(name.toUpperCase(), null));
   }

   public static void registerAttachmentType(AuraAttachmentType type) {
      attachmentTypes.put(type.getName(), type);
      if (type.getAliases() != null && type.getAliases().length > 0) {
         for (String alias : type.getAliases()) {
            attachmentTypes.put(alias, type);
         }
      }
   }
}
