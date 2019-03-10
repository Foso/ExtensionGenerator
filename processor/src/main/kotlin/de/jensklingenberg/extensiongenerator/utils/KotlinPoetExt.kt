package de.jensklingenberg.extensiongenerator.utils

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec


fun FileSpec.Builder.addFunctions(functions: List<FunSpec>): FileSpec.Builder {
    functions.forEach {
        addFunction(it)
    }
    return this
}

