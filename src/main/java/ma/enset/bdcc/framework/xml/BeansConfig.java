package ma.enset.bdcc.framework.xml;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "beans")
public class BeansConfig {
    private List<BeanConfig> beans = new ArrayList<>();

    @XmlElement(name = "bean")
    public List<BeanConfig> getBeans() {
        return beans;
    }

    public void setBeans(List<BeanConfig> beans) {
        this.beans = beans;
    }
}
