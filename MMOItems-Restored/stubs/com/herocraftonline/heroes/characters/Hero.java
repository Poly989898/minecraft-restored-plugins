package com.herocraftonline.heroes.characters;

import org.bukkit.entity.Player;
import com.herocraftonline.heroes.characters.classes.HeroClass;

public class Hero {
    public Player getPlayer() { return null; }
    public void removeMaxMana(String key) { }
    public void addMaxMana(String key, int v) { }
    public void removeMaxStamina(String key) { }
    public void addMaxStamina(String key, int v) { }
    public void removeMaxHealth(String key) { }
    public int getHeroLevel(HeroClass c) { return 0; }
    public HeroClass getHeroClass() { return null; }
    public HeroClass getSecondaryClass() { return null; }
    public int getMana() { return 0; }
    public int getStamina() { return 0; }
    public void setMana(int v) { }
    public void setStamina(int v) { }
}
