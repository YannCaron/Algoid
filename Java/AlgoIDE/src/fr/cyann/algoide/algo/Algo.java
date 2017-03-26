/* 
 * Copyright (C) 2014 cyann
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.cyann.algoide.algo;

import fr.cyann.algoide.AlgoIDEConstant;
import fr.cyann.algoide.algo.shapes.FillGraphic;
import fr.cyann.algoide.algo.shapes.PathGraphic;
import fr.cyann.algoide.algo.shapes.Graphic;
import fr.cyann.algoide.algo.shapes.ImageGraphic;
import fr.cyann.algoide.algo.shapes.TextGraphic;
import fr.cyann.algoide.runtime.Colors;
import fr.cyann.tools.ColorTools;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * The Algo class. Creation date: 20 mai 2013.
 * <p>
 * @author CyaNn
 * @version v0.1
 */
public class Algo extends JPanel {

    // const
    public static final int MAX_SHAPES = 10000;
    public static final float DEFAULT_STROKE = 1;
    public static final float STROKE_FACTOR = 3;
    public static final Color DEFAULT_COLOR = Colors.LTGRAY.getColor();
    public static final Color DEFAULT_BGCOLOR = Colors.DKGRAY.getColor();// AlgoIDEConstant.RUN_BACKGROUND_COLOR;
    public static final float SCALE_FACTOR = 0.7f;
    public static final float DEFAULT_TEXT_SIZE = 20;
    // values
    private int maxShapes;
    private int shapePerFrameCount = 0;
    private float angle = 0;
    private float turtleX = 0, turtleY = 0;
    private Color c, color, bgColor;
    private Stroke stroke;
    private float alpha;
    private boolean turtleVisible;
    private AffineTransform rotation;
    private Font uniFont;
    private float textSize;
    // objets
    private List<Graphic> shapes;
    private Map<Integer, BufferedImage> sprites;
    private Image tip = null, background = null;
    private boolean hasBgChanged = false;

    /*
     Image offScreenBuffer;

     @Override
     public void update(Graphics g) {
     Graphics gr;
     if (offScreenBuffer == null ||
     (!(offScreenBuffer.getWidth(this) == this.size().width &&
     offScreenBuffer.getHeight(this) == this.size().height))) {
     offScreenBuffer = this.createImage(size().width, size().height);
     }
     gr = offScreenBuffer.getGraphics();
     paint(gr);
     g.drawImage(offScreenBuffer, 0, 0, this);
     }*/
    // constructor
    public Algo() {
        super(new BorderLayout());
        setBackground(AlgoIDEConstant.RUN_BACKGROUND_COLOR);
        shapes = new ArrayList<Graphic>();
        sprites = new TreeMap<Integer, BufferedImage>();
        URL imageurl = getClass().getResource("/images/ic_tip.png");
        tip = Toolkit.getDefaultToolkit().getImage(imageurl);

        InputStream is = this.getClass().getResourceAsStream("/fonts/emulogic.ttf");
        uniFont = AlgoIDEConstant.INVIT_FONT;
        try {
            uniFont = Font.createFont(Font.TRUETYPE_FONT, is);
        } catch (FontFormatException ex) {
            Logger.getLogger(Algo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Algo.class.getName()).log(Level.SEVERE, null, ex);
        }

        initialize();

        try {
            background = ImageIO.read(new java.net.URL(getClass().getResource(AlgoIDEConstant.RUN_BACKGROUND_IMAGE), AlgoIDEConstant.RUN_BACKGROUND_IMAGE_NAME));
        } catch (IOException ex) {
            Logger.getLogger(Algo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // properties
    public float getTurtleX() {
        return turtleX / SCALE_FACTOR;
    }

    public float getTurtleY() {
        return turtleY / SCALE_FACTOR;
    }

    public float getRawTurtleX() {
        return turtleX;
    }

    public float getRawTurtleY() {
        return turtleY;
    }

    public void setColor(Color color) {
        this.c = color;
        applyColor();
    }

    public void setBgColor(Color color) {
        this.bgColor = color;
        hasBgChanged = true;
        this.repaint();
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
        applyColor();
    }

    public void setStroke(float stroke) {
        this.stroke = new BasicStroke(stroke * STROKE_FACTOR, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    }

    public void setMaxShapes(int maxShapes) {
        this.maxShapes = maxShapes;
    }

    public float getAngle() {
        return angle;
    }

    public void showTurtle() {
        turtleVisible = true;
        // refresh
        this.repaint();
    }

    public void hideTurtle() {
        turtleVisible = false;
        // refresh
        this.repaint();
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }
    // tools
    Image bgImg, buffer;

    private void initBackground() {

        if (hasBgChanged || bgImg == null || bgImg.getWidth(this) != getWidth() || bgImg.getHeight(this) != getHeight()) {
            bgImg = this.createImage(getWidth(), getHeight());
            Graphics2D g = (Graphics2D) bgImg.getGraphics();

            if (bgColor.getAlpha() == 0) {
                ColorTools.drawHorizontalTile(g, background, this);
            } else {
                g.setColor(bgColor);
                g.fill(new Rectangle2D.Float(0, 0, this.getWidth(), this.getHeight()));
            }

            hasBgChanged = false;
        }

    }

    public AffineTransform getRotation() {
        return rotation;
    }

    private void initBuffer() {
        if (buffer == null || buffer.getWidth(this) != getWidth() || buffer.getHeight(this) != getHeight()) {
            buffer = this.createImage(getWidth(), getHeight());
        }
    }

    private void applyColor() {
        int a = (int) (alpha * 255);
        color = new Color(c.getRed(), c.getGreen(), c.getBlue(), a);
    }

    private synchronized void addShape(Graphic shape) {
        if (maxShapes > 0) {
            while (shapes.size() >= maxShapes) {
                shapes.remove(0);
            }
            shapes.add(shape);
        }
        shapePerFrameCount++;
    }

    // methodes
    @Override
    public void paint(Graphics g) {
        initBuffer();

        Graphics2D g2 = (Graphics2D) buffer.getGraphics();

        // for antialising geometric shapes
        g2.addRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON));
        // for antialiasing text
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        // interpolation for image
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        if (!AlgoIDEConstant.DEBUG_MODE) {
            initBackground();
            g2.drawImage(bgImg, 0, 0, this);
        }

        g2.translate(this.getWidth() / 2, this.getHeight() / 2);

        synchronized (this) {
            for (Graphic shape : shapes) {
                shape.draw(g2);
            }
        }

        if (!AlgoIDEConstant.DEBUG_MODE && turtleVisible) {
            AffineTransform trans = new AffineTransform();
            trans.translate(turtleX, turtleY);
            trans.concatenate(rotation);
            trans.translate(-tip.getWidth(this) / 2, -tip.getHeight(this) / 2);

            g2.drawImage(tip, trans, this);
        }

        g.drawImage(buffer, 0, 0, this);

    }

    public final void initialize() {
        sprites.clear();
        clear();
        maxShapes = MAX_SHAPES;
        c = DEFAULT_COLOR;
        bgColor = DEFAULT_BGCOLOR;
        setStroke(DEFAULT_STROKE);
        textSize = DEFAULT_TEXT_SIZE;
        alpha = 1.0f;
        turtleX = 0;
        turtleY = 0;
        shapePerFrameCount = 0;
        rotate(0);
        turtleVisible = true;
        applyColor();
        hasBgChanged = true;
        this.repaint();
    }

    public synchronized final void clear() {
        shapes.clear();
    }

    public synchronized final void autoClear() {
        maxShapes = shapePerFrameCount;
        shapePerFrameCount = 0;
    }

    public synchronized void removeFirst() {
        if (!shapes.isEmpty()) {
            shapes.remove(0);
        }
    }

    public synchronized void removeLast() {
        if (!shapes.isEmpty()) {
            shapes.remove(shapes.size() - 1);
        }
    }

    // shapes
    public float toRelativeX(float x) {
        return (x - getWidth() / 2) / SCALE_FACTOR;
    }

    public float toRelativeY(float y) {
        return (y - getHeight() / 2) / SCALE_FACTOR;
    }

    public void go(float l) {
        l *= SCALE_FACTOR;

        float x = (float) (turtleX + l * Math.cos(Math.toRadians(angle - 90)));
        float y = (float) (turtleY + l * Math.sin(Math.toRadians(angle - 90)));

        Line2D line = new Line2D.Float(turtleX, turtleY, x, y);
        addShape(new PathGraphic(line, color, stroke));

        turtleX = x;
        turtleY = y;

        this.repaint();
    }

    public void jump(float l) {
        l *= SCALE_FACTOR;

        turtleX = (float) (turtleX + l * Math.cos(Math.toRadians(angle - 90)));
        turtleY = (float) (turtleY + l * Math.sin(Math.toRadians(angle - 90)));

        this.repaint();
    }

    public void rotate(float a) {
        angle = a;
        rotation = new AffineTransform();
        rotation.rotate(Math.toRadians(angle));

        this.repaint();
    }

    public void goTo(float x, float y) {
        x *= SCALE_FACTOR;
        y *= SCALE_FACTOR;

        turtleX = x;
        turtleY = y;

        this.repaint();
    }

    public void lineTo(float x, float y) {
        x *= SCALE_FACTOR;
        y *= SCALE_FACTOR;

        Line2D line = new Line2D.Float(turtleX, turtleY, x, y);
        addShape(new PathGraphic(line, color, stroke));

        turtleX = x;
        turtleY = y;

        this.repaint();
    }

    private void shape(Shape shape, boolean fill) {

        // rotate to turtle
        AffineTransform t = new AffineTransform();
        t.translate(turtleX, turtleY);
        t.concatenate(rotation);

        shape = t.createTransformedShape(shape);

        // fill or not
        if (fill) {
            addShape(new FillGraphic(shape, color, stroke));
        } else {
            addShape(new PathGraphic(shape, color, stroke));
        }

        // refrech
        this.repaint();
    }

    public void rect(float w, float h, boolean fill) {
        w *= SCALE_FACTOR;
        h *= SCALE_FACTOR;

        Rectangle2D rect = new Rectangle2D.Float(-w / 2, -h / 2, w, h);
        shape(rect, fill);
    }

    public void ellipse(float radiusX, float radiusY, boolean fill) {
        radiusX *= SCALE_FACTOR;
        radiusY *= SCALE_FACTOR;

        Ellipse2D ellipse = new Ellipse2D.Float(-radiusX, -radiusY, radiusX * 2, radiusY * 2);
        shape(ellipse, fill);
    }

    public void path(GeneralPath shape, boolean fill) {

        // fill or not
        if (fill) {
            addShape(new FillGraphic(shape, color, stroke));
        } else {
            addShape(new PathGraphic(shape, color, stroke));
        }

        // refrech
        this.repaint();
    }

    public void text(String message) {
        Font f = uniFont.deriveFont(textSize * SCALE_FACTOR);
        TextGraphic text = new TextGraphic(turtleX, turtleY, message, f, color, stroke);
        addShape(text);
    }

    // sprites
    public int createSprite(Color[][] colors, int scale) {

        int id = sprites.size();
        int h = colors.length;

        if (h > 0) {
            int w = colors[0].length;

            BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR);
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    image.setRGB(x, y, colors[y][x].getRGB());
                }
            }

            float scl = SCALE_FACTOR * scale;
            int width = (int) (w * scl);
            int height = (int) (h * scl);

            BufferedImage scaledImage = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);

            AffineTransform transform = new AffineTransform();
            transform.scale(scl, scl);

            AffineTransformOp operation = new AffineTransformOp(transform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
            operation.filter(image, scaledImage);

            sprites.put(id, scaledImage);

        }
        return id;

    }

    public int createSprite(BufferedImage image) {

        int id = sprites.size();

        sprites.put(id, image);

        return id;

    }

    public void removeSprite(int id) {
        if (sprites.containsKey(id)) {
            sprites.remove(id);
        }
    }

    public void sprite(int id) {
        if (sprites.containsKey(id)) {
            int w = sprites.get(id).getWidth();
            int h = sprites.get(id).getHeight();
            int x = (int) turtleX - (w / 2);
            int y = (int) turtleY - (h / 2);

            ImageGraphic sprite = new ImageGraphic(x, y, angle, sprites.get(id), this, color, stroke);
            addShape(sprite);

            // refresh
            this.repaint();
        }
    }

    public int getSpriteWidth(int id) {
        if (sprites.containsKey(id)) {
            return sprites.get(id).getWidth();
        }
        return 0;
    }
    // events
    List<MouseListener> mouseListeners = new ArrayList<MouseListener>();
    List<MouseMotionListener> mouseMotionListeners = new ArrayList<MouseMotionListener>();
    List<MouseWheelListener> mouseWheelListeners = new ArrayList<MouseWheelListener>();
    List<KeyEventDispatcher> keyEventDispatchers = new ArrayList<KeyEventDispatcher>();

    @Override
    public synchronized void addMouseListener(MouseListener l) {
        super.addMouseListener(l); //To change body of generated methods, choose Tools | Templates.
        mouseListeners.add(l);
    }

    @Override
    public synchronized void removeMouseListener(MouseListener l) {
        super.removeMouseListener(l);
        mouseListeners.remove(l);
    }

    @Override
    public synchronized void addMouseMotionListener(MouseMotionListener l) {
        super.addMouseMotionListener(l); //To change body of generated methods, choose Tools | Templates.
        mouseMotionListeners.add(l);
    }

    @Override
    public synchronized void removeMouseMotionListener(MouseMotionListener l) {
        super.removeMouseMotionListener(l);
        mouseMotionListeners.remove(l);
    }

    @Override
    public synchronized void addMouseWheelListener(MouseWheelListener l) {
        super.addMouseWheelListener(l);
        mouseWheelListeners.add(l);
    }

    @Override
    public synchronized void removeMouseWheelListener(MouseWheelListener l) {
        super.removeMouseWheelListener(l);
        mouseWheelListeners.remove(l);
    }

    public synchronized void addKeyListener(KeyEventDispatcher l) {
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(l);
        keyEventDispatchers.add(l);
    }

    public synchronized void removeKeyListener(KeyEventDispatcher l) {
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.removeKeyEventDispatcher(l);
        keyEventDispatchers.remove(l);
    }

    public void clearEvents() {
        for (int i = mouseListeners.size() - 1; i >= 0; i--) {
            this.removeMouseListener(mouseListeners.get(i));
        }

        for (int i = mouseMotionListeners.size() - 1; i >= 0; i--) {
            this.removeMouseMotionListener(mouseMotionListeners.get(i));
        }

        for (int i = mouseWheelListeners.size() - 1; i >= 0; i--) {
            this.removeMouseWheelListener(mouseWheelListeners.get(i));
        }

        for (int i = keyEventDispatchers.size() - 1; i >= 0; i--) {
            this.removeKeyListener(keyEventDispatchers.get(i));
        }
    }
}
