package connect.app.com.connect.retrofit;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by 44260 on 2016/3/17.
 */
public class Client {
    public static void main(String args[]) {
        Client client = new Client();
        Retro retro = client.create(Retro.class);
        retro.getName();
        retro.execute("---");
    }


    public interface Retro {
        void execute(String args);

        String getName();
    }

    @SuppressWarnings("unchecked")
    public <T> T create(Class<T> service) {
       return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class<?>[]{service}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                System.out.print(method.toString()+"\n"+args);
                return null;
            }
        });
    }
}
