package com.itsaky.androidide.treesitter.yaml;

import com.itsaky.androidide.treesitter.TSLanguage;
import com.itsaky.androidide.treesitter.TSLanguageCache;

public final class TSLanguageYaml {
  static {
    System.loadLibrary("tree-sitter-cpp");
  }

  private TSLanguageYaml() {
    throw new UnsupportedOperationException();
  }

  public static TSLanguage getInstance() {
    var language = TSLanguageCache.get("yaml");
    if (language != null) {
      return language;
    }
    language = TSLanguage.create("yaml", Native.getInstance());
    TSLanguageCache.cache("yaml", language);
    return language;
  }

  public static class Native {
    public static native long getInstance();
  }
}
