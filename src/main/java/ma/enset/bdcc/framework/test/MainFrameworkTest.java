package ma.enset.bdcc.framework.test;

import ma.enset.bdcc.framework.xml.XmlContext;
import ma.enset.bdcc.framework.annotations.AnnotationContext;

public class MainFrameworkTest {
    public static void main(String[] args) throws Exception {
        System.out.println("--- Test Framework XML (JAXB + Setter Injection) ---");
        XmlContext xmlContext = new XmlContext("framework-context.xml");
        IDummyMetier metierXml = (IDummyMetier) xmlContext.getBean("metier");
        metierXml.process();

        System.out.println("\n--- Test Framework Annotations (ClassLoader + Field Injection) ---");
        AnnotationContext annotationContext = new AnnotationContext("ma.enset.bdcc.framework.test");
        IDummyMetier metierAnnotation = (IDummyMetier) annotationContext.getBean(IDummyMetier.class);
        metierAnnotation.process();
    }
}
