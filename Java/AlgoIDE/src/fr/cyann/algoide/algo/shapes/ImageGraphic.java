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
package fr.cyann.algoide.algo.shapes;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

/**
 * The Line class. Creation date: 23 mai 2013.
 *
 * @author CyaNn
 * @version v0.1
 */
public class ImageGraphic extends Graphic {

    private final float x, y, angle;
    private final BufferedImage image;
    private final ImageObserver observer;

    public ImageGraphic(float x, float y, float angle, BufferedImage image, ImageObserver algo, Color color, Stroke stroke) {
        super(color, stroke);
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.image = image;
        this.observer = algo;
    }

    @Override
    public void drawShape(Graphics2D g) {
        float decX = image.getWidth() / 2;
        float decY = image.getHeight() / 2;

        AffineTransform trans = new AffineTransform();
        trans.translate(x + decX, y + decY);
        trans.rotate(angle);
        trans.translate(-decX, -decY);

        g.drawImage(image, trans, observer);
    }
}
