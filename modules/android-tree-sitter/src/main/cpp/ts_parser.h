#include <jni.h>
#include "ts__log.h"

#ifndef _Included_com_itsaky_androidide_treesitter_TSParser_Native_METHODS
#define _Included_com_itsaky_androidide_treesitter_TSParser_Native_METHODS
#ifdef __cplusplus
extern "C" {
#endif

// newParser ()J
JNIEXPORT jlong JNICALL Java_com_itsaky_androidide_treesitter_TSParser_Native_newParser(JNIEnv *, jclass);
static JNINativeMethod TSParser_Native_newParser = {"newParser", "()J", nullptr};
#define TSParser_Native_newParser__ARR_IDX 0
// delete (J)V
JNIEXPORT void JNICALL Java_com_itsaky_androidide_treesitter_TSParser_Native_delete(JNIEnv *, jclass, jlong);
static JNINativeMethod TSParser_Native_delete = {"delete", "(J)V", nullptr};
#define TSParser_Native_delete__ARR_IDX 1
// setLanguage (JJ)V
JNIEXPORT void JNICALL Java_com_itsaky_androidide_treesitter_TSParser_Native_setLanguage(JNIEnv *, jclass, jlong, jlong);
static JNINativeMethod TSParser_Native_setLanguage = {"setLanguage", "(JJ)V", nullptr};
#define TSParser_Native_setLanguage__ARR_IDX 2
// getLanguage (J)J
JNIEXPORT jlong JNICALL Java_com_itsaky_androidide_treesitter_TSParser_Native_getLanguage(JNIEnv *, jclass, jlong);
static JNINativeMethod TSParser_Native_getLanguage = {"getLanguage", "(J)J", nullptr};
#define TSParser_Native_getLanguage__ARR_IDX 3
// reset (J)V
JNIEXPORT void JNICALL Java_com_itsaky_androidide_treesitter_TSParser_Native_reset(JNIEnv *, jclass, jlong);
static JNINativeMethod TSParser_Native_reset = {"reset", "(J)V", nullptr};
#define TSParser_Native_reset__ARR_IDX 4
// setTimeout (JJ)V
JNIEXPORT void JNICALL Java_com_itsaky_androidide_treesitter_TSParser_Native_setTimeout(JNIEnv *, jclass, jlong, jlong);
static JNINativeMethod TSParser_Native_setTimeout = {"setTimeout", "(JJ)V", nullptr};
#define TSParser_Native_setTimeout__ARR_IDX 5
// getTimeout (J)J
JNIEXPORT jlong JNICALL Java_com_itsaky_androidide_treesitter_TSParser_Native_getTimeout(JNIEnv *, jclass, jlong);
static JNINativeMethod TSParser_Native_getTimeout = {"getTimeout", "(J)J", nullptr};
#define TSParser_Native_getTimeout__ARR_IDX 6
// setIncludedRanges (J[Lcom/itsaky/androidide/treesitter/TSRange;)Z
JNIEXPORT jboolean JNICALL Java_com_itsaky_androidide_treesitter_TSParser_Native_setIncludedRanges(JNIEnv *, jclass, jlong, jobjectArray);
static JNINativeMethod TSParser_Native_setIncludedRanges = {"setIncludedRanges", "(J[Lcom/itsaky/androidide/treesitter/TSRange;)Z", nullptr};
#define TSParser_Native_setIncludedRanges__ARR_IDX 7
// getIncludedRanges (J)[Lcom/itsaky/androidide/treesitter/TSRange;
JNIEXPORT jobjectArray JNICALL Java_com_itsaky_androidide_treesitter_TSParser_Native_getIncludedRanges(JNIEnv *, jclass, jlong);
static JNINativeMethod TSParser_Native_getIncludedRanges = {"getIncludedRanges", "(J)[Lcom/itsaky/androidide/treesitter/TSRange;", nullptr};
#define TSParser_Native_getIncludedRanges__ARR_IDX 8
// parse (JJJ)J
JNIEXPORT jlong JNICALL Java_com_itsaky_androidide_treesitter_TSParser_Native_parse(JNIEnv *, jclass, jlong, jlong, jlong);
static JNINativeMethod TSParser_Native_parse = {"parse", "(JJJ)J", nullptr};
#define TSParser_Native_parse__ARR_IDX 9
// requestCancellation (J)Z
JNIEXPORT jboolean JNICALL Java_com_itsaky_androidide_treesitter_TSParser_Native_requestCancellation(JNIEnv *, jclass, jlong);
static JNINativeMethod TSParser_Native_requestCancellation = {"requestCancellation", "(J)Z", nullptr};
#define TSParser_Native_requestCancellation__ARR_IDX 10

#ifdef __cplusplus
}
#endif

#ifndef SET_JNI_METHOD
#define SET_JNI_METHOD(_mths, _mth, _func) { _mths[_mth##__ARR_IDX] = _mth; _mths[_mth##__ARR_IDX].fnPtr = reinterpret_cast<void *>(_func); }
#endif
#define TSParser_Native__CLASS_NAME "com/itsaky/androidide/treesitter/TSParser$Native"
#define TSParser_Native__METHOD_COUNT 11
void TSParser_Native__SetJniMethods(JNINativeMethod *methods, int count);
#define TSParser_Native_AutoRegisterNatives(_env) \
  JNINativeMethod TSParser_Native_methods[11]; \
  TSParser_Native__SetJniMethods(TSParser_Native_methods, 11); \
  { jclass cls = _env->FindClass("com/itsaky/androidide/treesitter/TSParser$Native"); if (cls == nullptr) { LOGE(LOG_TAG, "Failed to find class com/itsaky/androidide/treesitter/TSParser$Native"); } else { int rc = _env->RegisterNatives(cls, TSParser_Native_methods, 11); if (rc != 0) LOGE(LOG_TAG, "Failed to register natives for com/itsaky/androidide/treesitter/TSParser$Native: %d", rc); } }
#endif
