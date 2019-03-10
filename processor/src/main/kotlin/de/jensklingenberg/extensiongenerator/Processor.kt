package de.jensklingenberg.extensiongenerator

import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.*
import de.jensklingenberg.extensiongenerator.annotation.Extension
import de.jensklingenberg.extensiongenerator.utils.addFunctions
import de.jensklingenberg.extensiongenerator.utils.filterAnnotationMirror
import java.io.File
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement


@AutoService(Processor::class)
class ExtensionProcessor : AbstractProcessor() {

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> = mutableSetOf(Extension::class.java.name)

    override fun getSupportedSourceVersion(): SourceVersion = SourceVersion.latest()

    override fun process(set: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        println("process")
        roundEnv.getElementsAnnotatedWith(Extension::class.java)
            .forEach { annotatedElement ->
                annotatedElement
                    .filterAnnotationMirror(Extension::class)
                    .map { extensionAnnotation ->
                        readArgumentsFrom(extensionAnnotation, "to")
                    }.map {
                        it.map {
                            getElement(roundEnv.rootElements, it)
                        }
                    }.forEach { elementList ->
                        generateClass(elementList, annotatedElement)
                    }
            }

        return true
    }



    private fun generateClass(annotationParameterElements: List<Element>, annotatedElement: Element) {
        val pack = processingEnv.elementUtils.getPackageOf(annotatedElement).toString()
        val kaptKotlinGeneratedDir = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]
        val fileName = "${annotatedElement.simpleName}" + "Ext" //User

        val functions = annotationParameterElements.map { annotationParameterElement ->
            val annotationParameterClassFields = annotationParameterElement.fields()
            val codeBuilder = CodeBlock.builder()

            val identicalFieldNames = annotationParameterClassFields.filter {     annotatedElement.fields().map { it.simpleName.toString() }.contains(it.simpleName.toString()) }

            identicalFieldNames.forEachIndexed { index, field ->
                // x = this.x
                var string = field.simpleName.toString() + " = this." + field.simpleName.toString()

                if (index < annotationParameterClassFields.size - 1) {
                    string = string + ","
                }

                codeBuilder.addStatement(string)
            }


            val hello = annotationParameterElement.simpleName.toString()

            val userClassName = ClassName("", "${annotatedElement.simpleName}")

            val helloClassName = ClassName("", hello)

            val variableName = hello.toLowerCase()

            val toExtensionFunction = FunSpec.builder("to$hello")
                .returns(helloClassName)
                .receiver(userClassName)
                .addStatement("val $variableName = $hello(")
                .addCode(codeBuilder.build())
                .addStatement(")")
                .addStatement("return $variableName", helloClassName)
                .build()
            toExtensionFunction
        }

        FileSpec
            .builder(pack, fileName)
            .addComment("//Generated")
            .addFunctions(functions)
            .build()
            .writeTo(File(kaptKotlinGeneratedDir))
    }

    private fun readArgumentsFrom(annotationMirror: AnnotationMirror, fieldName: String): List<String> {

        // val isList = annotationMirror.elementValues.values.first().value is List<*>

        return (annotationMirror.elementValues.filter { it.key.simpleName.toString() == fieldName }.values.first().value as Iterable<*>).map {
            it.toString().replace(".class", "")
        }
    }

    fun Element.fields(): List<Element> {
        return this.enclosedElements.filter { it.kind== ElementKind.FIELD }
    }

    private fun getElement(typeName: MutableSet<out Element>, name: String): Element =
        typeName.first { it.asType().asTypeName().toString() == name }
}

