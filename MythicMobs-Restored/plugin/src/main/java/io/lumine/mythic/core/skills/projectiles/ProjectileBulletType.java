package io.lumine.mythic.core.skills.projectiles;

import com.google.common.collect.Maps;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.skills.projectiles.bullet.ArmorStandBullet;
import io.lumine.mythic.core.skills.projectiles.bullet.ArmorStandPacketBullet;
import io.lumine.mythic.core.skills.projectiles.bullet.ArrowBullet;
import io.lumine.mythic.core.skills.projectiles.bullet.BlockBullet;
import io.lumine.mythic.core.skills.projectiles.bullet.DisplayPacketBullet;
import io.lumine.mythic.core.skills.projectiles.bullet.ItemBullet;
import io.lumine.mythic.core.skills.projectiles.bullet.MEGBullet;
import io.lumine.mythic.core.skills.projectiles.bullet.MobBullet;
import io.lumine.mythic.core.skills.projectiles.bullet.SmallBlockBullet;
import io.lumine.mythic.core.skills.projectiles.bullet.TextDisplayPacketBullet;
import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;

public final class ProjectileBulletType {
   private static final Map<String, ProjectileBulletType> bulletTypes = Maps.newConcurrentMap();
   public static final ProjectileBulletType ARROW = new ProjectileBulletType("ARROW", ArrowBullet.class);
   public static final ProjectileBulletType ARMOR_STAND = new ProjectileBulletType(
      "RSTAND", ArmorStandBullet.class, "RTRACKING", "REALTRACKING", "REAL_ARMOR_STAND", "REALARMORSTAND"
   );
   public static final ProjectileBulletType ARMOR_STAND_PACKET = new ProjectileBulletType(
      "STAND", ArmorStandPacketBullet.class, "TRACKING", "ARMOR_STAND", "ARMORSTAND"
   );
   public static final ProjectileBulletType BLOCK = new ProjectileBulletType("BLOCK", BlockBullet.class);
   public static final ProjectileBulletType DISPLAY = new ProjectileBulletType("DISPLAY", DisplayPacketBullet.class);
   public static final ProjectileBulletType ITEM = new ProjectileBulletType("ITEM", ItemBullet.class, "MYTHICITEM");
   public static final ProjectileBulletType MOB = new ProjectileBulletType("MOB", MobBullet.class);
   public static final ProjectileBulletType ME = new ProjectileBulletType("ME", MEGBullet.class, "MEG", "MODELENGINE");
   public static final ProjectileBulletType SMALL_BLOCK = new ProjectileBulletType("SMALLBLOCK", SmallBlockBullet.class);
   public static final ProjectileBulletType TEXT = new ProjectileBulletType("TEXT", TextDisplayPacketBullet.class);
   @Getter
   private final String name;
   @Getter
   private final String[] aliases;
   private final Class<? extends ProjectileBullet> clazz;
   private final Constructor constructor;

   private ProjectileBulletType(String name, Class<? extends ProjectileBullet> clazz, String... aliases) {
      this.name = name;
      this.aliases = aliases;
      this.clazz = clazz;

      Constructor constructor;
      try {
         constructor = clazz.getConstructor(ProjectileBulletType.class, SkillMechanic.class, MythicLineConfig.class);
      } catch (Throwable e) {
         e.printStackTrace();
         constructor = null;
      }

      this.constructor = constructor;
      if (this.constructor != null) {
         registerBulletType(this);
      }
   }

   public ProjectileBullet create(SkillMechanic projectile, MythicLineConfig config) {
      try {
         return (ProjectileBullet)this.constructor.newInstance(this, projectile, config);
      } catch (Throwable e) {
         e.printStackTrace();
         return null;
      }
   }

   public static final Optional<ProjectileBulletType> get(String name) {
      return Optional.ofNullable(bulletTypes.getOrDefault(name.toUpperCase(), null));
   }

   public static final void registerBulletType(ProjectileBulletType type) {
      bulletTypes.put(type.getName(), type);
      if (type.getAliases() != null && type.getAliases().length > 0) {
         for (String alias : type.getAliases()) {
            bulletTypes.put(alias, type);
         }
      }
   }
}
