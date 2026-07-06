#include <jni.h>
#include "ts__log.h"

#ifndef _Included_com_itsaky_androidide_treesitter_TreeSitter_Native_METHODS
#define _Included_com_itsaky_androidide_treesitter_TreeSitter_Native_METHODS
#ifdef __cplusplus
extern "C" {
#endif

// getLanguageVersion ()I
JNIEXPORT jint JNICALL Java_com_itsaky_androidide_treesitter_TreeSitter_Native_getLanguageVersion(JNIEnv *, jclass);
static JNINativeMethod TreeSitter_Native_getLanguageVersion = {"getLanguageVersion", "()I", nullptr};
#define TreeSitter_Native_getLanguageVersion__ARR_IDX 0
// getMinimumCompatibleLanguageVersion ()I
JNIEXPORT jint JNICALL Java_com_itsaky_androidide_treesitter_TreeSitter_Native_getMinimumCompatibleLanguageVersion(JNIEnv *, jclass);
static JNINativeMethod TreeSitter_Native_getMinimumCompatibleLanguageVersion = {"getMinimumCompatibleLanguageVersion", "()I", nullptr};
#define TreeSitter_Native_getMinimumCompatibleLanguageVersion__ARR_IDX 1

#ifdef __cplusplus
}
#endif

#ifndef SET_JNI_METHOD
#define SET_JNI_METHOD(_mths, _mth, _func) { _mths[_mth##__ARR_IDX] = _mth; _mths[_mth##__ARR_IDX].fnPtr = reinterpret_cast<void *>(_func); }
#endif
#define TreeSitter_Native__CLASS_NAME "com/itsaky/androidide/treesitter/TreeSitter$Native"
#define TreeSitter_Native__METHOD_COUNT 2
void TreeSitter_Native__SetJniMethods(JNINativeMethod *methods, int count);
#define TreeSitter_Native_AutoRegisterNatives(_env) \
  JNINativeMethod TreeSitter_Native_methods[2]; \
  TreeSitter_Native__SetJniMethods(TreeSitter_Native_methods, 2); \
  { jclass cls = _env->FindClass("com/itsaky/androidide/treesitter/TreeSitter$Native"); if (cls == nullptr) { LOGE(LOG_TAG, "Failed to find class com/itsaky/androidide/treesitter/TreeSitter$Native"); } else { int rc = _env->RegisterNatives(cls, TreeSitter_Native_methods, 2); if (rc != 0) LOGE(LOG_TAG, "Failed to register natives for com/itsaky/androidide/treesitter/TreeSitter$Native: %d", rc); } }
#endif
