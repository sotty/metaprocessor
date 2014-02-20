package metaprocessor;

import java.io.IOException;
import java.io.Writer;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import metaprocessor.codegenerator.MetaCodeGenerator;

@SupportedAnnotationTypes({ "metaprocessor.annotations.Metadata" })
public class MetadataAnnotationProcessor extends AbstractProcessor {

	public boolean process(Set<? extends TypeElement> annotations,
			RoundEnvironment env) {
  
		Messager messager = processingEnv.getMessager();
		messager.printMessage(Diagnostic.Kind.NOTE,
				"Starting annotation processing.");
		for (TypeElement te : annotations) {
			for (Element e : env.getElementsAnnotatedWith(te)) {
				messager.printMessage(Diagnostic.Kind.NOTE,
						"Printing: " + e.toString());
				if (e.getKind() == ElementKind.CLASS) {
					TypeElement classElement = (TypeElement) e;
					
					JavaFileObject jfo;
					try {
						jfo = processingEnv.getFiler().createSourceFile(
								classElement.getQualifiedName() + "_");

						Writer writer = jfo.openWriter();
						new MetaCodeGenerator().generateCode(writer, classElement);
						writer.flush();
						writer.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}

				}
			}
		}
		return true;
	}

	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.latestSupported();
	}

}
