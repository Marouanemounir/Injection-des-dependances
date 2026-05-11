# Activité Pratique N°1 - Injection des dépendances

**Auteur :** Marouane Mounir  
**Filière :** BDCC -II  

---

## 📖 Introduction : Inversion de Contrôle (IoC) et Couplage Faible

Le principe d'**Inversion de Contrôle (IoC)** et de **couplage faible** permet de créer des applications maintenables et évolutives.
Dans un couplage fort, une classe instancie directement ses dépendances avec le mot clé `new`. Cela la rend fermée à l'extension et complique les tests. 
Le couplage faible, en revanche, consiste à utiliser des interfaces. La classe métier ne connaît que l'interface de sa dépendance, et non son implémentation exacte. C'est le framework (ou le développeur) qui va "injecter" l'implémentation au moment de l'exécution : c'est l'Inversion de Contrôle (IoC).

---

## 🛠️ Partie 1 : Implémentation de Base et Injection des Dépendances

Dans cette première partie, nous avons créé une architecture basée sur des interfaces (`IDao` et `IMetier`) pour garantir le couplage faible.

### 1. Par Instanciation Statique (Version 1)
L'injection se fait directement dans le code source via le mot-clé `new`.
```java
DaoImpl dao = new DaoImpl();
MetierImpl metier = new MetierImpl(dao);
System.out.println("Résultat = " + metier.calcul());
```


### 2. Par Instanciation Dynamique (Version 2)
On utilise un fichier `config.txt` contenant les noms des classes. L'API `Reflection` de Java (`Class.forName()`) nous permet d'instancier les objets dynamiquement à l'exécution sans toucher au code principal.
```java
Scanner scanner = new Scanner(new File("config.txt"));
String daoClassName = scanner.nextLine();
// ... Instanciation avec Class.forName(daoClassName).newInstance()
```


### 3. Par Framework Spring : Version XML
Configuration via un fichier `config.xml` où l'on déclare nos Beans. Spring se charge de l'injection.
```xml
<bean id="dao" class="ma.enset.bdcc.ioc.dao.DaoImpl"/>
<bean id="metier" class="ma.enset.bdcc.ioc.metier.MetierImpl">
    <property name="dao" ref="dao"/>
</bean>
```
```java
ApplicationContext context = new ClassPathXmlApplicationContext("config.xml");
IMetier metier = (IMetier) context.getBean("metier");
```


### 4. Par Framework Spring : Version Annotations
L'injection se fait en utilisant les annotations `@Component` et `@Autowired`. L`AnnotationConfigApplicationContext` scanne les packages spécifiés.
```java
ApplicationContext context = new AnnotationConfigApplicationContext("ma.enset.bdcc.ioc");
IMetier metier = context.getBean(IMetier.class);

## 🚀 Partie 2 : Création d'un Mini-Framework IoC "Spring-Like"

Dans cette partie avancée, nous avons développé depuis zéro un mini-framework capable de faire de l'injection de dépendances (via constructeur, setter et attribut) en deux versions : XML et Annotations.

### 1. Structure et Choix Techniques
- **API Reflection :** Utilisée intensivement pour analyser les classes, lire les attributs/méthodes, et instancier dynamiquement via `getDeclaredConstructor().newInstance()` ou invoquer des setters avec `Method.invoke()`.
- **JAXB (Java Architecture for XML Binding) :** Utilisé pour transformer le fichier de configuration `framework-context.xml` en un graphe d'objets Java (`BeansConfig`, `BeanConfig`, `PropertyConfig`) à l'aide des annotations (`@XmlRootElement`, `@XmlElement`, `@XmlAttribute`).
- **ClassLoader :** Utilisé dans l'`AnnotationContext` pour scanner un package et récupérer toutes les classes complilées (`.class`).

### 2. Version XML (OXM avec JAXB)
Le parseur `XmlContext` lit un fichier XML définissant les dépendances. Il instancie d'abord tous les Beans spécifiés, puis résout les références (`ref`) en injectant les dépendances via les méthodes Setter identifiées par réflexion.
```java
// Extrait de XmlContext - Injection Setter
Method setter = beanInstance.getClass().getMethod(setterName, dependency.getClass().getInterfaces()[0]);
setter.invoke(beanInstance, dependency);
```

### 3. Version Annotations avec Scanner Personnalisé
Nous avons créé deux annotations personnalisées :
- `@MyComponent` : Marque une classe comme étant gérée par le framework.
- `@MyAutowired` : Indique qu'une injection (Constructeur, Setter ou Attribut) est requise.

L'`AnnotationContext` scanne le répertoire du package fourni. Il instancie d'abord les objets (en gérant l'injection par constructeur le cas échéant), puis boucle sur tous les objets créés pour injecter les dépendances manquantes, de façon dynamique sur les champs (Fields) ou les Setters.

```java
// Extrait de l'injection d'attribut dans AnnotationContext
for (Field field : aClass.getDeclaredFields()) {
    if (field.isAnnotationPresent(MyAutowired.class)) {
        field.setAccessible(true); // Permet l'accès aux champs privés
        field.set(instance, getBeanOrInstantiate(field.getType()));
    }
}
```

### 4. Tests du Framework
Le fichier `MainFrameworkTest.java` (situé dans le package `ma.enset.bdcc.framework.test`) instancie avec succès `DummyMetierImpl` qui consomme `DummyDaoImpl` via les deux contextes: XML (JAXB) et Annotations.

--- Test Framework XML (JAXB + Setter Injection) ---
Processing in DummyMetierImpl -> Message from DummyDaoImpl injected by Custom Framework!

--- Test Framework Annotations (ClassLoader + Field Injection) ---
Processing in DummyMetierImpl -> Message from DummyDaoImpl injected by Custom Framework!

