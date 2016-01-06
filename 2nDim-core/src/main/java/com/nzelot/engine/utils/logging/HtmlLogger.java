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

import com.nzelot.engine.graphics.rendering.Color;
import lombok.Getter;

import java.io.FileWriter;
import java.io.IOException;

/**
 * @author nZeloT
 */
public class HtmlLogger extends Logger{

    private FileWriter writer;

    public HtmlLogger(String fileName) {
        try {
            this.writer = new FileWriter(fileName);
        }catch(IOException e){
            e.printStackTrace();
        }

        Runtime.getRuntime().addShutdownHook(new Thread(this::exit));

        try {
            this.writer.write(
                    "<!Doctype html>" +
                    "<html>" +
                    "   <head>" +
                    "       <title>2nDim Html Log File</title>" +
                    "   <head>" +
                    "   <body>" +
                    "       <ul>"
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void log(Class<?> cls, String s, LEVEL logLevel, LEVEL currentOutputLevel) {
        if(logLevel.getPriority() <= currentOutputLevel.getPriority()){
            StringBuilder b = new StringBuilder("<li><font color=\"#");
            b.append(LogColor.fromLEVEL(logLevel).getColor().asRGBHexCode());
            b.append("\">");
            b.append(cls.getName());
            b.append(": ");
            b.append(s);
            b.append("</font></li>");

            try {
                writer.write(b.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void exit(){
        if(writer != null){

            try {
                writer.write(
                        "       </ul>" +
                        "   </body>" +
                        "</html>"
                );
                writer.flush();
                writer.close();
            }catch(IOException e){
                e.printStackTrace();
            }

            writer = null;
        }
    }

    private enum LogColor {
        INFO(3, new Color(46, 204, 64)),
        DEBUG(2, new Color(0, 116, 217)),
        WARNING(1, new Color(255, 133, 27)),
        ERROR(0, new Color(255, 65, 54));

        private @Getter int priority;
        private @Getter Color color;

        LogColor(int prio, Color color) {
            this.priority = prio;
            this.color = color;
        }

        public static LogColor fromLEVEL(LEVEL lvl){
            if(lvl == LEVEL.INFO)       return INFO;
            if(lvl == LEVEL.DEBUG)      return DEBUG;
            if(lvl == LEVEL.WARNING)    return WARNING;
            if(lvl == LEVEL.ERROR)      return ERROR;

            //this default should not be reached!
            return ERROR;
        }
    }
}
