#include <jni.h>
#include <tree_sitter/api.h>

extern "C" const TSLanguage *tree_sitter_json(void);
extern "C" const TSLanguage *tree_sitter_bash(void);
extern "C" const TSLanguage *tree_sitter_cmake(void);
extern "C" const TSLanguage *tree_sitter_yaml(void);

extern "C" JNIEXPORT jlong JNICALL
Java_com_itsaky_androidide_treesitter_json_TSLanguageJson_00024Native_getInstance(
    JNIEnv *, jclass) {
  return reinterpret_cast<jlong>(tree_sitter_json());
}

extern "C" JNIEXPORT jlong JNICALL
Java_com_itsaky_androidide_treesitter_bash_TSLanguageBash_00024Native_getInstance(
    JNIEnv *, jclass) {
  return reinterpret_cast<jlong>(tree_sitter_bash());
}

extern "C" JNIEXPORT jlong JNICALL
Java_com_itsaky_androidide_treesitter_cmake_TSLanguageCmake_00024Native_getInstance(
    JNIEnv *, jclass) {
  return reinterpret_cast<jlong>(tree_sitter_cmake());
}

extern "C" JNIEXPORT jlong JNICALL
Java_com_itsaky_androidide_treesitter_yaml_TSLanguageYaml_00024Native_getInstance(
    JNIEnv *, jclass) {
  return reinterpret_cast<jlong>(tree_sitter_yaml());
}
