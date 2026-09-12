#include <jni.h>

extern "C" {

JNIEXPORT jlong JNICALL
Java_com_porcocliquer_MainActivity_calcularCustoMultiplicadorNativo(JNIEXPORT jenv, jobject thiz, jlong nivelMultiplicador) {
    jlong custoBase = 50;
    return custoBase * nivelMultiplicador;
}

JNIEXPORT jlong JNICALL
Java_com_porcocliquer_MainActivity_calcularCustoFazendaNativo(JNIEXPORT jenv, jobject thiz, jlong nivelFazenda) {
    jlong custoBase = 100;
    return custoBase * (nivelFazenda + 1);
}

}
