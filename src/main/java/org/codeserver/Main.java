package org.codeserver;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.codeserver.main.CodeClient;
import org.codeserver.main.CodeServer;

public class Main {
    public static void main(String[] args) {
        System.out.println("\n" +
                "                                                                                              \n" +
                "                            ,,                                                                \n" +
                "  .g8\"\"\"bgd               `7MM           .M\"\"\"bgd                                             \n" +
                ".dP'     `M                 MM          ,MI    \"Y                                             \n" +
                "dM'       ` ,pW\"Wq.    ,M\"\"bMM  .gP\"Ya  `MMb.      .gP\"Ya `7Mb,od8 `7M'   `MF'.gP\"Ya `7Mb,od8 \n" +
                "MM         6W'   `Wb ,AP    MM ,M'   Yb   `YMMNq. ,M'   Yb  MM' \"'   VA   ,V ,M'   Yb  MM' \"' \n" +
                "MM.        8M     M8 8MI    MM 8M\"\"\"\"\"\" .     `MM 8M\"\"\"\"\"\"  MM        VA ,V  8M\"\"\"\"\"\"  MM     \n" +
                "`Mb.     ,'YA.   ,A9 `Mb    MM YM.    , Mb     dM YM.    ,  MM         VVV   YM.    ,  MM     \n" +
                "  `\"bmmmd'  `Ybmd9'   `Wbmd\"MML.`Mbmmd' P\"Ybmmd\"   `Mbmmd'.JMML.        W     `Mbmmd'.JMML.   \n" +
                "                                                                                              \n" +
                "                                                                                              \n");
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