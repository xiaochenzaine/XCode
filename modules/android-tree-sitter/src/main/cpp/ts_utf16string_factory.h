#include <jni.h>
#include "ts__log.h"

#ifndef _Included_com_itsaky_androidide_treesitter_string_UTF16StringFactory_Native_METHODS
#define _Included_com_itsaky_androidide_treesitter_string_UTF16StringFactory_Native_METHODS
#ifdef __cplusplus
extern "C" {
#endif

// newString (Ljava/lang/String;)J
JNIEXPORT jlong JNICALL Java_com_itsaky_androidide_treesitter_string_UTF16StringFactory_Native_newString(JNIEnv *, jclass, jstring);
static JNINativeMethod UTF16StringFactory_Native_newString = {"newString", "(Ljava/lang/String;)J", nullptr};
#define UTF16StringFactory_Native_newString__ARR_IDX 0
// newStringBytes ([BII)J
JNIEXPORT jlong JNICALL Java_com_itsaky_androidide_treesitter_string_UTF16StringFactory_Native_newStringBytes(JNIEnv *, jclass, jbyteArray, jint, jint);
static JNINativeMethod UTF16StringFactory_Native_newStringBytes = {"newStringBytes", "([BII)J", nullptr};
#define UTF16StringFactory_Native_newStringBytes__ARR_IDX 1

#ifdef __cplusplus
}
#endif

#ifndef SET_JNI_METHOD
#define SET_JNI_METHOD(_mths, _mth, _func) { _mths[_mth##__ARR_IDX] = _mth; _mths[_mth##__ARR_IDX].fnPtr = reinterpret_cast<void *>(_func); }
#endif
#define UTF16StringFactory_Native__CLASS_NAME "com/itsaky/androidide/treesitter/string/UTF16StringFactory$Native"
#define UTF16StringFactory_Native__METHOD_COUNT 2
void UTF16StringFactory_Native__SetJniMethods(JNINativeMethod *methods, int count);
#define UTF16StringFactory_Native_AutoRegisterNatives(_env) \
  JNINativeMethod UTF16StringFactory_Native_methods[2]; \
  UTF16StringFactory_Native__SetJniMethods(UTF16StringFactory_Native_methods, 2); \
  { jclass cls = _env->FindClass("com/itsaky/androidide/treesitter/string/UTF16StringFactory$Native"); if (cls == nullptr) { LOGE(LOG_TAG, "Failed to find class com/itsaky/androidide/treesitter/string/UTF16StringFactory$Native"); } else { int rc = _env->RegisterNatives(cls, UTF16StringFactory_Native_methods, 2); if (rc != 0) LOGE(LOG_TAG, "Failed to register natives for com/itsaky/androidide/treesitter/string/UTF16StringFactory$Native: %d", rc); } }
#endif
