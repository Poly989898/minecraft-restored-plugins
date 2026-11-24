package net.Indyuce.mmocore.experience.source;

import org.bukkit.inventory.ItemStack;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.experience.source.type.ExperienceSource;
import net.Indyuce.mmocore.manager.profession.ExperienceSourceManager;

public class RepairItemExperienceSource extends ExperienceSource<ItemStack> {
    public RepairItemExperienceSource() { super(null); }
    public boolean matchesParameter(PlayerData player, ItemStack item) { return false; }
    public ExperienceSourceManager<RepairItemExperienceSource> newManager() { return null; }
}
