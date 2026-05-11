package ma.enset.bdcc.ioc.presentation;

import ma.enset.bdcc.ioc.dao.DaoImpl;
import ma.enset.bdcc.ioc.metier.MetierImpl;

public class PresentationV1 {
    public static void main(String[] args) {
        DaoImpl dao = new DaoImpl();
        MetierImpl metier = new MetierImpl(dao);
        System.out.println("Résultat (Statique) = " + metier.calcul());
    }
}
