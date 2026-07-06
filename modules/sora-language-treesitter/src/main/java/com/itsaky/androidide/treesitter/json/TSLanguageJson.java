package com.itsaky.androidide.treesitter.json;

import com.itsaky.androidide.treesitter.TSLanguage;
import com.itsaky.androidide.treesitter.TSLanguageCache;

public final class TSLanguageJson {
  static {
    System.loadLibrary("tree-sitter-cpp");
  }

  private TSLanguageJson() {
    throw new UnsupportedOperationException();
  }

  public static TSLanguage getInstance() {
    var language = TSLanguageCache.get("json");
    if (language != null) {
      return language;
    }
    language = TSLanguage.create("json", Native.getInstance());
    TSLanguageCache.cache("json", language);
    return language;
  }

  public static class Native {
    public static native long getInstance();
  }
}
