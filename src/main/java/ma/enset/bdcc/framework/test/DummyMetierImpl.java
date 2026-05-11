package ma.enset.bdcc.framework.test;

import ma.enset.bdcc.framework.annotations.MyAutowired;
import ma.enset.bdcc.framework.annotations.MyComponent;

@MyComponent
public class DummyMetierImpl implements IDummyMetier {

    // On peut tester l'injection via attribut/Field
    @MyAutowired
    private IDummyDao dao;

    // Pour l'injection XML ou Setter
    public void setDao(IDummyDao dao) {
        this.dao = dao;
    }

    @Override
    public void process() {
        System.out.println("Processing in DummyMetierImpl -> " + dao.getMessage());
    }
}
