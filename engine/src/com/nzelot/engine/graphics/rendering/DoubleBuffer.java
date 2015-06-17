/*******************************************************************************
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
 ******************************************************************************/

package com.nzelot.engine.graphics.rendering;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * swing concept implementation for a possible double buffering
 */
public abstract class DoubleBuffer extends JPanel{

    private BufferedImage[] buffer;

    private boolean swap;

    public DoubleBuffer(int width, int height){
        buffer      = new BufferedImage[2];
        buffer[0]   = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        buffer[1]   = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        swap    = true;

        setPreferredSize(new Dimension(width, height));
    }

    private void swapBuffers(){
        swap = !swap;
        repaint();
    }

    private BufferedImage getFront() {
        return swap ? buffer[0] : buffer[1];
    }

    private BufferedImage getBack(){
        return !swap ? buffer[0] : buffer[1];
    }

    protected abstract void render(Graphics g, int width, int height);

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        BufferedImage img = getFront();

        if(img != null) {
            g.drawImage(img, 0, 0, null);
        }

        //Resized?
        if(getWidth() != img.getWidth() || getHeight() != img.getHeight()){
            int idx = swap ? 0 : 1;
            buffer[idx] = new BufferedImage(getWidth(), getHeight(), buffer[0].getType());
            buffer[idx].getGraphics().drawImage(img, 0, 0, null);
        }
    }
}
