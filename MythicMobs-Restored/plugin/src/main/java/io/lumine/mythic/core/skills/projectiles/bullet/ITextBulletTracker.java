package io.lumine.mythic.core.skills.projectiles.bullet;

import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.core.skills.projectiles.ProjectileBulletableTracker;

public interface ITextBulletTracker {
   static ITextBulletTracker of(SkillMetadata data) {
      if (data.getCallingEvent() instanceof ProjectileBulletableTracker projectile && projectile.getBullet() instanceof ITextBulletTracker bullet) {
         return bullet;
      } else {
         return data.getMetadata("bullet").orElse(null) instanceof ITextBulletTracker bullet ? bullet : null;
      }
   }

   void setBulletText(String str);

   void setTextOpacity(int value, int value2);
}
