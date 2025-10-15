package org.example;

import java.lang.reflect.InvocationTargetException;

@Component
class ExampleA implements InitializingBean {
    @Autowired
    private ExampleA exampleA;

    public void performAction() {
        System.out.println("ExampleA is performing an action with ExampleB: " + exampleA);
    }

    @Override
    public void afterPropertiesSet() {
        System.out.println("ExampleA initialized");
    }
}

@Component
class ExampleB implements InitializingBean {
    public void doWork() {
        System.out.println("ExampleB is working");
    }

    @Override
    public void afterPropertiesSet() {
        System.out.println("ExampleB initialized");
    }
}

public class Example {
    public static void main(String[] args) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        MiniApplicationContext context = new MiniApplicationContext("org.example");
        ExampleA serviceA = context.getBean(ExampleA.class);
        serviceA.performAction();
        ExampleB serviceB = context.getBean(ExampleB.class);
        serviceB.doWork();
    }
}