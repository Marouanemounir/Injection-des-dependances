package ma.enset.bdcc.ioc.presentation;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import ma.enset.bdcc.ioc.metier.IMetier;

public class PresentationSpringXML {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("config.xml");
        IMetier metier = (IMetier) context.getBean("metier");
        System.out.println("Résultat (Spring XML) = " + metier.calcul());
        context.close();
    }
}
