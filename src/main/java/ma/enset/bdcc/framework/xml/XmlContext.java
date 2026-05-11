package ma.enset.bdcc.framework.xml;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class XmlContext {
    private Map<String, Object> instances = new HashMap<>();

    public XmlContext(String xmlFile) throws Exception {
        JAXBContext jaxbContext = JAXBContext.newInstance(BeansConfig.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        InputStream is = getClass().getClassLoader().getResourceAsStream(xmlFile);
        BeansConfig beansConfig = (BeansConfig) unmarshaller.unmarshal(is);

        // 1. Instantiation
        for (BeanConfig beanConfig : beansConfig.getBeans()) {
            Class<?> aClass = Class.forName(beanConfig.getClassName());
            Object instance = aClass.getDeclaredConstructor().newInstance();
            instances.put(beanConfig.getId(), instance);
        }

        // 2. Injection via Setter
        for (BeanConfig beanConfig : beansConfig.getBeans()) {
            Object beanInstance = instances.get(beanConfig.getId());
            for (PropertyConfig property : beanConfig.getProperties()) {
                Object dependency = instances.get(property.getRef());
                String setterName = "set" + property.getName().substring(0, 1).toUpperCase()
                        + property.getName().substring(1);
                Method setter = beanInstance.getClass().getMethod(setterName,
                        dependency.getClass().getInterfaces().length > 0 ? dependency.getClass().getInterfaces()[0]
                                : dependency.getClass());
                setter.invoke(beanInstance, dependency);
            }
        }
    }

    public Object getBean(String id) {
        return instances.get(id);
    }
}
