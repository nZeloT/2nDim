package com.nzelot.engine.graphics;

import com.nzelot.engine.graphics.rendering.Renderer;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;

import static org.junit.Assert.*;

public class EntityTest {

    private Entity      root;
    public Entity[]    children;

    @Before
    public void setUp() throws Exception {

        root = new Entity(new Point(100,50), new Point(135,95), null ) {
            @Override
            public void update(long time) {

            }

            @Override
            public void render(Renderer renderer) {

            }
        };

        children = new Entity[10];
        for (int i = 0; i < 10; i++) {
            Entity child = new Entity(new Point(10 * i, 5 * i), new Point(20 - i, 20 + i), null) {
                @Override
                public void update(long time) {

                }

                @Override
                public void render(Renderer renderer) {

                }
            };

            root.addChild(child);
            children[i] = child;
        }
    }

    @Test
    public void testClearChildren1() throws Exception {

        root.clearChildren();

        assertEquals(0, root.getChildrenCount());
    }


    @Test
    public void testClearChildren2() throws Exception {

        root.clearChildren();

        assertNull(children[0].getParent());
    }

    @Test
    public void testMove() throws Exception {

        root.move(
                new Point(42, 7)
        );

        assertEquals(
                new Point(142, 57),
                root.getPosition()
        );

    }

    @Test
    public void testMoveChildren() throws Exception {

        Entity child = children[7];

        child.move(
                new Point(42, 7)
        );

        assertEquals(
                new Point(112, 42),
                child.getPosition()
        );

    }

    @Test
    public void testMoveToPoint() throws Exception {

        root.moveToPoint(
                new Point(42, 7)
        );

        assertEquals(
                new Point(42, 7),
                root.getPosition()
        );

    }

    @Test
    public void testMoveToPointChildren() throws Exception {

        Entity child = children[7];

        child.moveToPoint(
                new Point(42, 7)
        );

        assertEquals(
                new Point(142, 57),
                child.getPosition()
        );

    }

    @Test
    public void testGetSize() throws Exception {

        Point size = root.getSize();

        assertEquals(new Point(135, 95), size);
    }

    @Test
    public void testResize() throws Exception {

        root.resize( new Point(42, 7) );

        assertEquals( new Point(42, 7), root.getSize());
    }

}