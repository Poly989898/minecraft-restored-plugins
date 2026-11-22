package com.Zrips.CMI.Modules.DataBase;

import com.Zrips.CMI.CMI;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class DBClassLoader extends URLClassLoader {
  public DBClassLoader(CMI cmi) {
    super(new URL[0], cmi.getClass().getClassLoader());
  }

  public void addFile(File file) throws MalformedURLException {
    this.addURL(file.toURI().toURL());
  }

  @Override
  public void addURL(URL url) {
    URL[] urls;
    int value = (urls = this.getURLs()).length;

    for (int index = 0; index < value; index++) {
      URL classUrl = urls[index];
      if (url.sameFile(classUrl)) {
        return;
      }
    }

    super.addURL(url);
  }
}
