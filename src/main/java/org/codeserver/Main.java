package org.codeserver;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.codeserver.main.CodeClient;
import org.codeserver.main.CodeServer;

public class Main {
    public static void main(String[] args) {
        OptionParser parser = new OptionParser();
        parser.accepts("server");
        parser.accepts("port").withRequiredArg();

        OptionSet set = parser.parse(args);

        try {
            if (set.has("server") && set.has("port")) {
                CodeServer server = new CodeServer(Integer.parseInt((String) set.valueOf("port")));
            } else {
                new CodeClient();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}