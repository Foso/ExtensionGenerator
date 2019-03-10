package de.jensklingenberg.extensiongenerator.utils

import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.Element
import kotlin.reflect.KClass


fun Element.filterAnnotationMirror(annotation: KClass<out Annotation>): List<AnnotationMirror> {
    return this.annotationMirrors
        .filter { it.annotationType.asElement().simpleName.toString() == annotation.simpleName.toString() }
}