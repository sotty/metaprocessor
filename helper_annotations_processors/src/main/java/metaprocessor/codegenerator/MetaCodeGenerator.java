package metaprocessor.codegenerator;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;

import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;

public class MetaCodeGenerator {

	public void generateCode(Writer writer, TypeElement classElement)
			throws IOException {
		PackageElement packageElement = (PackageElement) classElement
				.getEnclosingElement();
		BufferedWriter bw = new BufferedWriter(writer);
		bw.append("package ");
		bw.append(packageElement.getQualifiedName());
		bw.append(";");
		bw.newLine();
		bw.newLine();
		bw.append("import metaprocessor.helper.MetadataContainer;");
		bw.append("import " + classElement.getQualifiedName() + ";");
		bw.newLine();
		bw.newLine();
		bw.append("public class " + classElement.getSimpleName() + "_ "
				+ "extends MetadataContainer<" + classElement.getSimpleName()
				+ ">{");
		bw.newLine();
		bw.append("public " + classElement.getSimpleName() + "_ " + "("
				+ classElement.getSimpleName() + " original) {");
		bw.newLine();
		bw.append("super(original);");
		bw.newLine();
		bw.append("}");
		bw.newLine();

		bw.append("public static " + classElement.getSimpleName() + "_ "
				+ " getMetadata(Person person) {");
		bw.newLine();
		bw.append("	return new " + classElement.getSimpleName() + "_ "
				+ "(person);");
		bw.newLine();
		bw.append("}	");
		bw.newLine();

		bw.append("}");
		bw.newLine();
		bw.newLine();
		bw.toString();
		bw.flush();
		// bw.close();

		// rest of generated class contents
	}

}
