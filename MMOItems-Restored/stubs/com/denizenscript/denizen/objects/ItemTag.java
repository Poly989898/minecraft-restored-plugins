package com.denizenscript.denizen.objects;

import com.denizenscript.denizencore.objects.ObjectTag;
import com.denizenscript.denizencore.tags.Attribute;
import org.bukkit.inventory.ItemStack;

public class ItemTag implements ObjectTag {
    public String getPrefix() { return null; }
    public ObjectTag setPrefix(String prefix) { return this; }
    public boolean isUnique() { return false; }
    public String getObjectType() { return null; }
    public String identify() { return null; }
    public String identifySimple() { return null; }
    public ObjectTag getObjectAttribute(Attribute attribute) { return null; }
    public ItemTag(ItemStack item) { }
    public ItemStack getItemStack() { return null; }
}
