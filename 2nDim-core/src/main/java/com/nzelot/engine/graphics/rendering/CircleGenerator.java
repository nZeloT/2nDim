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

package com.nzelot.engine.graphics.rendering;

import lombok.Getter;
import org.joml.Matrix4d;
import org.joml.Vector4d;

/**
 * @author nZeloT
 */
public class CircleGenerator {

    private @Getter final float[] verts;
    private @Getter final byte[] inds;
    private @Getter final float[] tcs;

    public CircleGenerator(int num){
        verts = vertGen(num);
        inds = indGen(num);
        tcs = tcsGen(num);
    }

    public VertexArray createVertexArray(){
        return new VertexArray(verts, inds, tcs);
    }

    static float[] vertGen(int num) {
        ++num;
        float[] verts = new float[num * 3];

        Matrix4d m = new Matrix4d().identity();
        m.rotateZ(2 * Math.PI / (num - 1));

        Vector4d v = new Vector4d(0, 1, 0, 0);

        verts[0] = 0;
        verts[1] = 0;
        verts[2] = 0;

        for (int i = 3; i < verts.length; i += 3) {
            verts[i + 0] = (float) v.x;
            verts[i + 1] = (float) v.y;
            verts[i + 2] = 0;

            v.mul(m);
        }

        return verts;
    }

    static byte[] indGen(int num) {
        byte[] inds = new byte[num * 3];
        for (int i = 0; i < num; i++) {
            inds[i * 3 + 0] = 0;
            inds[i * 3 + 1] = (byte) (i + 1);
            inds[i * 3 + 2] = (byte) ((i + 2) % num);

            //ugly but works -.-
            if (i + 2 == num)
                inds[i * 3 + 2] = (byte) num;
        }

        return inds;
    }

    static float[] tcsGen(int num) {
        ++num;
        float[] tcs = new float[num * 2];

        Matrix4d m = new Matrix4d().identity();
        m.rotateZ(2 * Math.PI / (num - 1));

        Vector4d v = new Vector4d(0, 0.5, 0, 0);
        Vector4d t = new Vector4d(0.5, 0.5, 0, 0);

        tcs[0] = 0.5f;
        tcs[1] = 0.5f;

        for (int i = 0; i < tcs.length; i += 2) {
            tcs[i + 0] = (float) (t.x + v.x);
            tcs[i + 1] = (float) (t.y + v.y);

            v.mul(m);
        }

        return tcs;
    }
}
