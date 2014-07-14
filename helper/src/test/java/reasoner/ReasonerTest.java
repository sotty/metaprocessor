package reasoner;

import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import static junit.framework.Assert.fail;

public class ReasonerTest {


    @Test
    public void testReasoner() {
        KieSession kieSession = loadSession( "reasoner/rules_with_metaclass.drl" );

        kieSession.fireAllRules();
    }

    private KieSession loadSession( String path ) {
        KieServices kieServices = KieServices.Factory.get();
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();

        ReleaseId releaseId = kieServices.newReleaseId( "metaprocessor", "test", "0.1" );

        kieFileSystem.generateAndWritePomXML( releaseId );
        kieFileSystem.write( kieServices.getResources().newClassPathResource( path ).setSourcePath( path ).setResourceType( ResourceType.DRL ) );

        KieBuilder kieBuilder = kieServices.newKieBuilder( kieFileSystem );
        kieBuilder.buildAll();

        if ( kieBuilder.getResults().hasMessages( Message.Level.ERROR ) ) {
            fail( kieBuilder.getResults().getMessages( ).toString() );
        }

        KieContainer kieContainer = kieServices.newKieContainer( releaseId );
        return kieContainer.newKieSession();

    }

}
