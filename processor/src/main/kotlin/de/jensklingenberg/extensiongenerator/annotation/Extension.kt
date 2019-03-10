package de.jensklingenberg.extensiongenerator.annotation

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)

annotation class Extension(val to: Array<KClass<*>>)
