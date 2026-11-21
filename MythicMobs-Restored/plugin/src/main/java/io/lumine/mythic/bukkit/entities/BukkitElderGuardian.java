package io.lumine.mythic.bukkit.entities;

import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import lombok.Getter;
import org.bukkit.entity.EntityType;

@Getter
public class BukkitElderGuardian extends BukkitEntityType {
   private EntityType bukkitType = EntityType.ELDER_GUARDIAN;
   private static final int HEIGHT = 1;
}
