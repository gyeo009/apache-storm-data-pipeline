#include "jniGetShm.h"

#include <windows.h>
#include <assert.h>
#include <stdio.h>
#include <conio.h>
#include <tchar.h>
#include <iostream>

JNIEXPORT jbyteArray JNICALL Java_com_stormproject_jniGetShm_getShm(JNIEnv * env, jclass type, jstring dataName, jint startNum, jint dataSize){
    HANDLE hMemoryMap = NULL;
    LPBYTE pMemoryMap = NULL;
    const char* nativeString = env->GetStringUTFChars(dataName, 0);

    hMemoryMap = OpenFileMapping(FILE_MAP_READ, FALSE, nativeString);
    env->ReleaseStringUTFChars(dataName, nativeString);
    if (!hMemoryMap)
    {
        printf("Error1");
        return NULL;
    }
    
    pMemoryMap = (BYTE*)MapViewOfFile(hMemoryMap, FILE_MAP_READ, 0, startNum, dataSize);
    if (!pMemoryMap)
    {
        CloseHandle(hMemoryMap);
        printf("Error2");
        return NULL;
    }
    
    // 자바에서 사용할 jbyteArray 객체를 생성합니다.
    jbyteArray resultArray = env->NewByteArray(dataSize);
    if (!resultArray) { // 메모리 부족 오류 처리
        UnmapViewOfFile(pMemoryMap);
        CloseHandle(hMemoryMap);
        return NULL;
    }
    
    // jbyteArray에 데이터를 복사합니다.
    env->SetByteArrayRegion(resultArray, 0, dataSize, (jbyte*)pMemoryMap);

    UnmapViewOfFile(pMemoryMap);
    CloseHandle(hMemoryMap);

    return resultArray;
}