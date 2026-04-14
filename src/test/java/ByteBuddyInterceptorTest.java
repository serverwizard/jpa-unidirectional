import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;
import net.bytebuddy.matcher.ElementMatchers;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

public class ByteBuddyInterceptorTest {

    // 원본 클래스
    public static class User {
        private Long id;
        private String name;

        public User(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    // 인터셉터 (메서드 호출을 가로챔)
    public static class LazyLoadingInterceptor {
        private boolean initialized = false;
        private User realUser;

        public LazyLoadingInterceptor(Long id) {
            System.out.println("Proxy 생성 (id: " + id + ")");
        }

        @RuntimeType  // 반환 타입 자동 추론
        public Object intercept(@Origin Method method,
                                @AllArguments Object[] args,
                                @This Object proxy) throws Exception {

            System.out.println("메서드 호출 감지: " + method.getName());

            if (!initialized) {
                System.out.println("DB에서 User 로딩...");
                realUser = new User(1L, "John");  // DB 조회 시뮬레이션
                initialized = true;
            }

            return method.invoke(realUser, args);
        }
    }

    @Test
    public void Lazy_Loading_Proxy_구현() throws Exception {
        LazyLoadingInterceptor interceptor = new LazyLoadingInterceptor(1L);

        Class<? extends User> proxyClass = new ByteBuddy()
                .subclass(User.class)
                .method(ElementMatchers.any())  // 모든 메서드에 적용
                .intercept(MethodDelegation.to(interceptor))
                .make()
                .load(getClass().getClassLoader())
                .getLoaded();

        User proxy = proxyClass
                .getConstructor(Long.class, String.class)
                .newInstance(null, null);

        System.out.println("=== Proxy 생성 완료 ===\n");

        String name = proxy.getName();
        System.out.println("\n결과: " + name);

        String name2 = proxy.getName();
        System.out.println("결과: " + name2);
    }
}
