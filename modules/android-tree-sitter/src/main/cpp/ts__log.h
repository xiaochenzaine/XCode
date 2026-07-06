#ifndef ATS_TS_LOG_H
#define ATS_TS_LOG_H
#define LOG_TAG "android-tree-sitter"
#ifdef __ANDROID__
#include <android/log.h>
#define LOGE(TAG, ...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)
#define LOGW(TAG, ...) __android_log_print(ANDROID_LOG_WARN, TAG, __VA_ARGS__)
#define LOGD(TAG, ...) __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__)
#define LOGI(TAG, ...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)
#define LOGV(TAG, ...) __android_log_print(ANDROID_LOG_VERBOSE, TAG, __VA_ARGS__)
#else
#include <cstdio>
#define LOGE(TAG, ...) printf("[%s] ERROR: ", TAG); printf(__VA_ARGS__); printf("\n")
#define LOGW(TAG, ...) printf("[%s] WARN: ", TAG); printf(__VA_ARGS__); printf("\n")
#define LOGD(TAG, ...) printf("[%s] DEBUG: ", TAG); printf(__VA_ARGS__); printf("\n")
#define LOGI(TAG, ...) printf("[%s] INFO: ", TAG); printf(__VA_ARGS__); printf("\n")
#define LOGV(TAG, ...) printf("[%s] VERBOSE: ", TAG); printf(__VA_ARGS__); printf("\n")
#endif
#endif
