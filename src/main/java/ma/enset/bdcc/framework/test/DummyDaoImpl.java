package ma.enset.bdcc.framework.test;

import ma.enset.bdcc.framework.annotations.MyComponent;

@MyComponent
public class DummyDaoImpl implements IDummyDao {
    @Override
    public String getMessage() {
        return "Message from DummyDaoImpl injected by Custom Framework!";
    }
}
