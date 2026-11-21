package io.lumine.mythic.bukkit.utils.maven;

import java.net.URL;
import javax.annotation.Nonnull;

interface URLInjector {
   void addURL(@Nonnull ClassLoader classLoader, @Nonnull URL url) throws Exception;
}
