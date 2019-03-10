package de.jensklingenberg.extensiongenerator

import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.*
import de.jensklingenberg.extensiongenerator.annotation.Extension
import de.jensklingenberg.extensiongenerator.utils.*
import java.io.File
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement


@AutoService(Processor::class)
class ExtensionProcessor : AbstractProcessor() {

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
        const val EXTENSION_CLASS_PREFIX = "EXT"
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


    private fun generateClass(targetClassElements: List<Element>, sourceClassElement: Element) {
        val pack = processingEnv.elementUtils.getPackageOf(sourceClassElement).toString()
        val kaptKotlinGeneratedDir = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]
        val soureFileName = sourceClassElement.simpleName
        val fileName = "$soureFileName" + EXTENSION_CLASS_PREFIX

        val functions = targetClassElements.map { targetParameterElement ->
            val targetClassName = targetParameterElement.simpleName()


            val sourceClassProperties = sourceClassElement
                    .methods()
                    .map { functionNameAsPropertyName(it.simpleName()) }

            val identicalFieldNames = targetParameterElement.fields()
                    .filter { sourceClassProperties.contains(it.simpleName().toLowerCase()) }

            val codeBuilder = CodeBlock.builder()
            identicalFieldNames.forEachIndexed { index, field ->
                var string = field.simpleName() + " = " + field.simpleName()

                if (index < targetParameterElement.fields().size - 1) {
                    string = string + ","
                }

                codeBuilder.addStatement(string)
            }


            val variableName = targetClassName.toLowerCase()

            val toExtensionFunction = FunSpec.builder("to$targetClassName")
                    .returns(ClassName("", targetClassName))
                    .receiver(ClassName("", "$soureFileName"))
                    .addStatement("val $variableName = $targetClassName(")
                    .addCode(codeBuilder.build())
                    .addStatement(")")
                    .addStatement("return $variableName", ClassName("", targetClassName))
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


    private fun getElement(typeName: MutableSet<out Element>, name: String): Element =
            typeName.first { it.asType().asTypeName().toString() == name }


    private fun functionNameAsPropertyName(name: String): String {
        return name.replace("get", "").replace("()", "").toLowerCase()
    }
}

