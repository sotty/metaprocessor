package metaprocessor.codegenerator;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;

import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;

public class MetaCodeGenerator {
	
	public void generateCode(Writer writer, TypeElement classElement) throws IOException{
		PackageElement packageElement = (PackageElement) classElement
				.getEnclosingElement();
		 BufferedWriter bw = new BufferedWriter(writer);
	     bw.append("package ");
	     bw.append(packageElement.getQualifiedName());
	     bw.append(";");
	     bw.newLine();
	     bw.newLine();
	     bw.append("import metaprocessor.helper.MetadataContainer;");
	     bw.newLine();
	     bw.newLine();
	     bw.append("public class " + classElement.getSimpleName() + "_ " + "extends MetadataContainer{");
	     bw.newLine();
	     bw.append("public boolean test() {");
	     bw.newLine();
	     bw.append("return true;");
	     bw.newLine();
	     bw.append("}");
	     bw.newLine();
	     
	     bw.append("}");
	     bw.newLine();
	     bw.newLine();
	     bw.flush();
	     //bw.close();
	     
	     // rest of generated class contents
	}

}
