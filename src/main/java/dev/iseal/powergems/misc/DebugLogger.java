package dev.iseal.powergems.misc;

import java.util.ResourceBundle;
import java.util.logging.Logger;

public class DebugLogger extends Logger{

    /**
     * Protected method to construct a logger for a named subsystem.
     * <p>
     * The logger will be initially configured with a null Level
     * and with useParentHandlers set to true.
     *
     * @param name               A name for the logger.  This should
     *                           be a dot-separated name and should normally
     *                           be based on the package name or class name
     *                           of the subsystem, such as java.net
     *                           or javax.swing.  It may be null for anonymous Loggers.
     * @param resourceBundleName name of ResourceBundle to be used for localizing
     *                           messages for this logger.  May be null if none
     *                           of the messages require localization.
     * @throws MissingResourceException if the resourceBundleName is non-null and
     *                                  no corresponding resource can be found.
     */
    protected DebugLogger(String name, String resourceBundleName) {
        super(name, resourceBundleName);
    }

    public Logger getLogger() {
        new System.Logger() {
            @Override
            public String getName() {
                return "";
            }

            @Override
            public boolean isLoggable(Level level) {
                return false;
            }

            @Override
            public void log(Level level, ResourceBundle bundle, String msg, Throwable thrown) {

            }

            @Override
            public void log(Level level, ResourceBundle bundle, String format, Object... params) {

            }
        }
    }

}
