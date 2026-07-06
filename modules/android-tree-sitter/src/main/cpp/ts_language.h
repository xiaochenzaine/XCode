#include <jni.h>
#include "ts__log.h"

#ifndef _Included_com_itsaky_androidide_treesitter_TSLanguage_Native_METHODS
#define _Included_com_itsaky_androidide_treesitter_TSLanguage_Native_METHODS
#ifdef __cplusplus
extern "C" {
#endif

// symCount (J)I
JNIEXPORT jint JNICALL Java_com_itsaky_androidide_treesitter_TSLanguage_Native_symCount(JNIEnv *, jclass, jlong);
static JNINativeMethod TSLanguage_Native_symCount = {"symCount", "(J)I", nullptr};
#define TSLanguage_Native_symCount__ARR_IDX 0
// fldCount (J)I
JNIEXPORT jint JNICALL Java_com_itsaky_androidide_treesitter_TSLanguage_Native_fldCount(JNIEnv *, jclass, jlong);
static JNINativeMethod TSLanguage_Native_fldCount = {"fldCount", "(J)I", nullptr};
#define TSLanguage_Native_fldCount__ARR_IDX 1
// symForName (J[BIZ)I
JNIEXPORT jint JNICALL Java_com_itsaky_androidide_treesitter_TSLanguage_Native_symForName(JNIEnv *, jclass, jlong, jbyteArray, jint, jboolean);
static JNINativeMethod TSLanguage_Native_symForName = {"symForName", "(J[BIZ)I", nullptr};
#define TSLanguage_Native_symForName__ARR_IDX 2
// symName (JI)Ljava/lang/String;
JNIEXPORT jstring JNICALL Java_com_itsaky_androidide_treesitter_TSLanguage_Native_symName(JNIEnv *, jclass, jlong, jint);
static JNINativeMethod TSLanguage_Native_symName = {"symName", "(JI)Ljava/lang/String;", nullptr};
#define TSLanguage_Native_symName__ARR_IDX 3
// fldNameForId (JI)Ljava/lang/String;
JNIEXPORT jstring JNICALL Java_com_itsaky_androidide_treesitter_TSLanguage_Native_fldNameForId(JNIEnv *, jclass, jlong, jint);
static JNINativeMethod TSLanguage_Native_fldNameForId = {"fldNameForId", "(JI)Ljava/lang/String;", nullptr};
#define TSLanguage_Native_fldNameForId__ARR_IDX 4
// fldIdForName (J[BI)I
JNIEXPORT jint JNICALL Java_com_itsaky_androidide_treesitter_TSLanguage_Native_fldIdForName(JNIEnv *, jclass, jlong, jbyteArray, jint);
static JNINativeMethod TSLanguage_Native_fldIdForName = {"fldIdForName", "(J[BI)I", nullptr};
#define TSLanguage_Native_fldIdForName__ARR_IDX 5
// symType (JI)I
JNIEXPORT jint JNICALL Java_com_itsaky_androidide_treesitter_TSLanguage_Native_symType(JNIEnv *, jclass, jlong, jint);
static JNINativeMethod TSLanguage_Native_symType = {"symType", "(JI)I", nullptr};
#define TSLanguage_Native_symType__ARR_IDX 6
// langVer (J)I
JNIEXPORT jint JNICALL Java_com_itsaky_androidide_treesitter_TSLanguage_Native_langVer(JNIEnv *, jclass, jlong);
static JNINativeMethod TSLanguage_Native_langVer = {"langVer", "(J)I", nullptr};
#define TSLanguage_Native_langVer__ARR_IDX 7
// loadLanguage (Ljava/lang/String;Ljava/lang/String;)[J
JNIEXPORT jlongArray JNICALL Java_com_itsaky_androidide_treesitter_TSLanguage_Native_loadLanguage(JNIEnv *, jclass, jstring, jstring);
static JNINativeMethod TSLanguage_Native_loadLanguage = {"loadLanguage", "(Ljava/lang/String;Ljava/lang/String;)[J", nullptr};
#define TSLanguage_Native_loadLanguage__ARR_IDX 8
// dlclose (J)V
JNIEXPORT void JNICALL Java_com_itsaky_androidide_treesitter_TSLanguage_Native_dlclose(JNIEnv *, jclass, jlong);
static JNINativeMethod TSLanguage_Native_dlclose = {"dlclose", "(J)V", nullptr};
#define TSLanguage_Native_dlclose__ARR_IDX 9
// stateCount (J)I
JNIEXPORT jint JNICALL Java_com_itsaky_androidide_treesitter_TSLanguage_Native_stateCount(JNIEnv *, jclass, jlong);
static JNINativeMethod TSLanguage_Native_stateCount = {"stateCount", "(J)I", nullptr};
#define TSLanguage_Native_stateCount__ARR_IDX 10
// nextState (JSS)S
JNIEXPORT jshort JNICALL Java_com_itsaky_androidide_treesitter_TSLanguage_Native_nextState(JNIEnv *, jclass, jlong, jshort, jshort);
static JNINativeMethod TSLanguage_Native_nextState = {"nextState", "(JSS)S", nullptr};
#define TSLanguage_Native_nextState__ARR_IDX 11

#ifdef __cplusplus
}
#endif

#ifndef SET_JNI_METHOD
#define SET_JNI_METHOD(_mths, _mth, _func) { _mths[_mth##__ARR_IDX] = _mth; _mths[_mth##__ARR_IDX].fnPtr = reinterpret_cast<void *>(_func); }
#endif
#define TSLanguage_Native__CLASS_NAME "com/itsaky/androidide/treesitter/TSLanguage$Native"
#define TSLanguage_Native__METHOD_COUNT 12
void TSLanguage_Native__SetJniMethods(JNINativeMethod *methods, int count);
#define TSLanguage_Native_AutoRegisterNatives(_env) \
  JNINativeMethod TSLanguage_Native_methods[12]; \
  TSLanguage_Native__SetJniMethods(TSLanguage_Native_methods, 12); \
  { jclass cls = _env->FindClass("com/itsaky/androidide/treesitter/TSLanguage$Native"); if (cls == nullptr) { LOGE(LOG_TAG, "Failed to find class com/itsaky/androidide/treesitter/TSLanguage$Native"); } else { int rc = _env->RegisterNatives(cls, TSLanguage_Native_methods, 12); if (rc != 0) LOGE(LOG_TAG, "Failed to register natives for com/itsaky/androidide/treesitter/TSLanguage$Native: %d", rc); } }
#endif
