/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 nZeloT
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.nzelot.engine.utils.logging;

import lombok.NonNull;

/**
 * The Logging System. made accessible through <code>static</code>-Methods. To change the output set a new <code>currentLogger</code>.
 *
 * @author nZeloT
 */
//doc
public abstract class Logger {

    private static LEVEL currentOutputLevel;
    private static LEVEL defaultLogLevel;
    private static Logger currentLogger;

    static {
        Logger.currentOutputLevel = LEVEL.WARNING;
        Logger.defaultLogLevel = LEVEL.WARNING;
        Logger.currentLogger = new ConsoleLogger();
    }

    public static void log(Class<?> cls, String s) {
        log(cls, s, defaultLogLevel);
    }

    public static void log(Class<?> cls, String s, LEVEL level) {
        currentLogger.log(cls, s, level, currentOutputLevel);
    }

    protected abstract void log(Class<?> cls, String s, LEVEL logLevel, LEVEL currentOutputLevel);

    public static void initLogging(Logger logger){
        currentLogger = logger;
    }

    public static void setCurrentLogger(@NonNull Logger currentLogger) {
        Logger.currentLogger = currentLogger;
    }

    public static void setDefaultLogLevel(LEVEL defaultLogLevel) {
        Logger.defaultLogLevel = defaultLogLevel;
    }

    public static void setCurrentOutputLevel(LEVEL currentOutputLevel) {
        Logger.currentOutputLevel = currentOutputLevel;
    }

    public enum LEVEL {
        INFO(3),
        DEBUG(2),
        WARNING(1),
        ERROR(0);

        private int priority;

        LEVEL(int priority) {
            this.priority = priority;
        }

        public int getPriority() {
            return priority;
        }
    }

}
