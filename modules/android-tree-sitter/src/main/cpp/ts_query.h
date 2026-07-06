#include <jni.h>
#include "ts__log.h"

#ifndef _Included_com_itsaky_androidide_treesitter_TSQuery_Native_METHODS
#define _Included_com_itsaky_androidide_treesitter_TSQuery_Native_METHODS
#ifdef __cplusplus
extern "C" {
#endif

// newQuery (Lcom/itsaky/androidide/treesitter/TSQuery;JLjava/lang/String;)J
JNIEXPORT jlong JNICALL Java_com_itsaky_androidide_treesitter_TSQuery_Native_newQuery(JNIEnv *, jclass, jobject, jlong, jstring);
static JNINativeMethod TSQuery_Native_newQuery = {"newQuery", "(Lcom/itsaky/androidide/treesitter/TSQuery;JLjava/lang/String;)J", nullptr};
#define TSQuery_Native_newQuery__ARR_IDX 0
// delete (J)V
JNIEXPORT void JNICALL Java_com_itsaky_androidide_treesitter_TSQuery_Native_delete(JNIEnv *, jclass, jlong);
static JNINativeMethod TSQuery_Native_delete = {"delete", "(J)V", nullptr};
#define TSQuery_Native_delete__ARR_IDX 1
// captureCount (J)I
JNIEXPORT jint JNICALL Java_com_itsaky_androidide_treesitter_TSQuery_Native_captureCount(JNIEnv *, jclass, jlong);
static JNINativeMethod TSQuery_Native_captureCount = {"captureCount", "(J)I", nullptr};
#define TSQuery_Native_captureCount__ARR_IDX 2
// patternCount (J)I
JNIEXPORT jint JNICALL Java_com_itsaky_androidide_treesitter_TSQuery_Native_patternCount(JNIEnv *, jclass, jlong);
static JNINativeMethod TSQuery_Native_patternCount = {"patternCount", "(J)I", nullptr};
#define TSQuery_Native_patternCount__ARR_IDX 3
// stringCount (J)I
JNIEXPORT jint JNICALL Java_com_itsaky_androidide_treesitter_TSQuery_Native_stringCount(JNIEnv *, jclass, jlong);
static JNINativeMethod TSQuery_Native_stringCount = {"stringCount", "(J)I", nullptr};
#define TSQuery_Native_stringCount__ARR_IDX 4
// startByteForPattern (JI)I
JNIEXPORT jint JNICALL Java_com_itsaky_androidide_treesitter_TSQuery_Native_startByteForPattern(JNIEnv *, jclass, jlong, jint);
static JNINativeMethod TSQuery_Native_startByteForPattern = {"startByteForPattern", "(JI)I", nullptr};
#define TSQuery_Native_startByteForPattern__ARR_IDX 5
// predicatesForPattern (JI)[Lcom/itsaky/androidide/treesitter/TSQueryPredicateStep;
JNIEXPORT jobjectArray JNICALL Java_com_itsaky_androidide_treesitter_TSQuery_Native_predicatesForPattern(JNIEnv *, jclass, jlong, jint);
static JNINativeMethod TSQuery_Native_predicatesForPattern = {"predicatesForPattern", "(JI)[Lcom/itsaky/androidide/treesitter/TSQueryPredicateStep;", nullptr};
#define TSQuery_Native_predicatesForPattern__ARR_IDX 6
// patternRooted (JI)Z
JNIEXPORT jboolean JNICALL Java_com_itsaky_androidide_treesitter_TSQuery_Native_patternRooted(JNIEnv *, jclass, jlong, jint);
static JNINativeMethod TSQuery_Native_patternRooted = {"patternRooted", "(JI)Z", nullptr};
#define TSQuery_Native_patternRooted__ARR_IDX 7
// patternNonLocal (JI)Z
JNIEXPORT jboolean JNICALL Java_com_itsaky_androidide_treesitter_TSQuery_Native_patternNonLocal(JNIEnv *, jclass, jlong, jint);
static JNINativeMethod TSQuery_Native_patternNonLocal = {"patternNonLocal", "(JI)Z", nullptr};
#define TSQuery_Native_patternNonLocal__ARR_IDX 8
// patternGuaranteedAtStep (JI)Z
JNIEXPORT jboolean JNICALL Java_com_itsaky_androidide_treesitter_TSQuery_Native_patternGuaranteedAtStep(JNIEnv *, jclass, jlong, jint);
static JNINativeMethod TSQuery_Native_patternGuaranteedAtStep = {"patternGuaranteedAtStep", "(JI)Z", nullptr};
#define TSQuery_Native_patternGuaranteedAtStep__ARR_IDX 9
// captureNameForId (JI)Ljava/lang/String;
JNIEXPORT jstring JNICALL Java_com_itsaky_androidide_treesitter_TSQuery_Native_captureNameForId(JNIEnv *, jclass, jlong, jint);
static JNINativeMethod TSQuery_Native_captureNameForId = {"captureNameForId", "(JI)Ljava/lang/String;", nullptr};
#define TSQuery_Native_captureNameForId__ARR_IDX 10
// stringValueForId (JI)Ljava/lang/String;
JNIEXPORT jstring JNICALL Java_com_itsaky_androidide_treesitter_TSQuery_Native_stringValueForId(JNIEnv *, jclass, jlong, jint);
static JNINativeMethod TSQuery_Native_stringValueForId = {"stringValueForId", "(JI)Ljava/lang/String;", nullptr};
#define TSQuery_Native_stringValueForId__ARR_IDX 11
// captureQuantifierForId (JII)I
JNIEXPORT jint JNICALL Java_com_itsaky_androidide_treesitter_TSQuery_Native_captureQuantifierForId(JNIEnv *, jclass, jlong, jint, jint);
static JNINativeMethod TSQuery_Native_captureQuantifierForId = {"captureQuantifierForId", "(JII)I", nullptr};
#define TSQuery_Native_captureQuantifierForId__ARR_IDX 12

#ifdef __cplusplus
}
#endif

#ifndef SET_JNI_METHOD
#define SET_JNI_METHOD(_mths, _mth, _func) { _mths[_mth##__ARR_IDX] = _mth; _mths[_mth##__ARR_IDX].fnPtr = reinterpret_cast<void *>(_func); }
#endif
#define TSQuery_Native__CLASS_NAME "com/itsaky/androidide/treesitter/TSQuery$Native"
#define TSQuery_Native__METHOD_COUNT 13
void TSQuery_Native__SetJniMethods(JNINativeMethod *methods, int count);
#define TSQuery_Native_AutoRegisterNatives(_env) \
  JNINativeMethod TSQuery_Native_methods[13]; \
  TSQuery_Native__SetJniMethods(TSQuery_Native_methods, 13); \
  { jclass cls = _env->FindClass("com/itsaky/androidide/treesitter/TSQuery$Native"); if (cls == nullptr) { LOGE(LOG_TAG, "Failed to find class com/itsaky/androidide/treesitter/TSQuery$Native"); } else { int rc = _env->RegisterNatives(cls, TSQuery_Native_methods, 13); if (rc != 0) LOGE(LOG_TAG, "Failed to register natives for com/itsaky/androidide/treesitter/TSQuery$Native: %d", rc); } }
#endif
