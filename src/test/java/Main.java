import co.gongzh.procbridge.server.IDelegate;
import co.gongzh.procbridge.server.Server;
import org.jetbrains.annotations.Nullable;

public class Main {

    public static void main(String[] args) {
        Server server = new Server(25565, new IDelegate() {
            @Override
            public @Nullable Object handleRequest(@Nullable String s, @Nullable Object o) {
                return null;
            }
        });
        server.start();
    }
}
