package de.jensklingenberg.extensiongenerator.utils


import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import kotlin.reflect.KClass


fun Element.filterAnnotationMirror(annotation: KClass<out Annotation>): List<AnnotationMirror> {
    return this.annotationMirrors
        .filter { it.annotationType.asElement().simpleName.toString() == annotation.simpleName.toString() }
}


fun Element?.simpleName(): String {
    return this?.simpleName.toString()
}


fun Element.fields(): List<Element> {
    return this.enclosedElements.filter { it.kind== ElementKind.FIELD }
}

fun Element.methods(): List<Element> {
    return this.enclosedElements.filter { it.kind== ElementKind.METHOD }
}