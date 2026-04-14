import net.bytebuddy.ByteBuddy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ByteBuddyBasicTest {

    // 원본 클래스
    public static class Person {
        private String name;

        public Person(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public String greet() {
            return "Hello, " + name;
        }
    }

    @Test
    public void ByteBuddy로_서브클래스_생성() throws Exception {
        // 1. ByteBuddy로 Person의 서브클래스 생성
        Class<? extends Person> proxyClass = new ByteBuddy()
                .subclass(Person.class)  // Person을 상속
                .name("PersonProxy")     // 클래스 이름
                .make()
                .load(getClass().getClassLoader())
                .getLoaded();

        // 2. Proxy 인스턴스 생성
        Person proxy = proxyClass
                .getConstructor(String.class)
                .newInstance("John");

        // 3. 동작 확인
        System.out.println("클래스: " + proxy.getClass().getName());
        System.out.println("슈퍼클래스: " + proxy.getClass().getSuperclass().getName());
        System.out.println("greet(): " + proxy.greet());

        assertTrue(proxy instanceof Person);
    }
}