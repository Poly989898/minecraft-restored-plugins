package com.ticxo.modelengine.api.generator.skin;

import com.destroystokyo.paper.profile.PlayerProfile;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerTextures;
import org.bukkit.profile.PlayerTextures.SkinModel;
import org.joml.Vector3f;

public record LimbData(String limbType, Vector3f translation, Vector3f scale, boolean isHead) {
   public ItemStack createItem(String url) {
      ItemStack head = new ItemStack(Material.PLAYER_HEAD);
      head.editMeta(itemMeta -> {
         PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());

         try {
            PlayerTextures texture = profile.getTextures();
            texture.setSkin(new URL(url), SkinModel.CLASSIC);
            profile.setTextures(texture);
         } catch (MalformedURLException e) {
            throw new RuntimeException(e);
         }

         ((SkullMeta)itemMeta).setPlayerProfile(profile);
      });
      return head;
   }
}
