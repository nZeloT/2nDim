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

package com.nzelot.therun;

import com.nzelot.engine.game.Game;
import com.nzelot.engine.game.Runtime;
import com.nzelot.engine.graphics.rendering.Texture;
import com.nzelot.engine.graphics.rendering.TextureManager;
import com.nzelot.engine.graphics.scenegraph.AnimatedSprite;
import com.nzelot.engine.graphics.scenegraph.Universe;
import com.nzelot.engine.utils.ResourceUtils;
import com.nzelot.engine.utils.logging.Logger;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Vector2;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

/**
 * @author nZeloT
 */
public class TheRun {

    public static void main(String[] args) {
        Logger.setCurrentOutputLevel(Logger.LEVEL.INFO);
        Game g = new Game(1280, 720, false, "TheRun") {
            @Override
            protected Universe initGame() {
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                GL13.glActiveTexture(GL13.GL_TEXTURE1);

                Universe u = new Universe(this);
                u.setGravity(new Vector2(0, -9.81f * 1/16.0f));

                Texture t = TextureManager.instance.create("sprite_run", ResourceUtils.getResourceStream("res/tex/sprite_run.png"));
                AnimatedSprite s = new AnimatedSprite("Runner", 3.1, 4.025, t, 0, 9, 3, 4, 124/512.0f, 161/512.0f);
                //Sprite s = new Sprite("Runner", 12.4, 16.1, t, 0, 4, 124/512.0f, 161/512.0f);
                //TexturedRectangle s = new TexturedRectangle("runner", 20, 20, t);
                //ColoredRectangle s = new ColoredRectangle("runner", 20, 20, new Color(0.4f, 0.8f, 0.2f));
                u.addObject(s);
                s.setMass(Mass.Type.INFINITE);
                s.translate(0,-2.5);
                return u;
            }

            @Override
            protected void endGame() {
                //NOP
            }

            @Override
            protected void updateGame(double delta) {
                //NOP
            }

            @Override
            protected void renderGame() {
                //NOP
            }
        };

        Runtime.runGame(g);
    }

}
