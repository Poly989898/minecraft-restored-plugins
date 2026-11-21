package io.lumine.mythic.core.skills.projectiles;

import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.adapters.AbstractVector;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.bukkit.utils.terminable.Terminable;
import io.lumine.mythic.bukkit.utils.terminable.TerminableConsumer;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;

public interface ProjectileBulletableTracker extends Terminable, TerminableConsumer {
   ProjectileBullet.BulletTracker getBullet();

   AbstractLocation getCurrentLocation();

   AbstractLocation getPreviousLocation();

   AbstractVector getCurrentVelocity();

   double getVelocityMagnitude();

   SkillMetadata getData();

   PlaceholderContext getContext();

   int getTimesRan();
}
