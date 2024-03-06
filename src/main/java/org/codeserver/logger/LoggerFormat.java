package org.codeserver.logger;

import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class LoggerFormat extends Formatter {
    @Override
    public String format(LogRecord record) {
        StringBuilder builder = new StringBuilder();
        builder.append("\u001B[34m").append("[CODE-SERVER]").append(" ");
        builder.append("["+record.getLevel().getName()+"]").append(" ");
        builder.append("\u001B[37m").append(record.getMessage());
        builder.append("\n");
        return builder.toString();
    }
}
