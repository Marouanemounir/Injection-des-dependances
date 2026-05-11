package ma.enset.bdcc.ioc.presentation;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ma.enset.bdcc.ioc.metier.IMetier;

public class PresentationSpringAnnotation {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext("ma.enset.bdcc.ioc");
        IMetier metier = context.getBean(IMetier.class);
        System.out.println("Résultat (Spring Annotations) = " + metier.calcul());
        context.close();
    }
}
