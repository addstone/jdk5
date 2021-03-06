/*
 * @(#)DuctusShapeRenderer.java	1.15 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package sun.java2d.pipe;

import java.awt.BasicStroke;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.PathIterator;
import sun.awt.SunHints;
import sun.java2d.SunGraphics2D;
import sun.dc.pr.Rasterizer;
import sun.dc.pr.PRException;

/**
 * This class is used to convert raw geometry into 8-bit alpha tiles
 * using the Ductus Rasterizer for application by the next stage of
 * the pipeline.
 * This class sets up the Rasterizer and computes the alpha tiles
 * and then passes them on to a CompositePipe object for painting.
 */
public class DuctusShapeRenderer extends DuctusRenderer
				 implements ShapeDrawPipe
{
    CompositePipe outpipe;

    public DuctusShapeRenderer(CompositePipe pipe) {
	outpipe = pipe;
    }

    public void draw(SunGraphics2D sg, Shape s) {
	BasicStroke bs;

	if (sg.stroke instanceof BasicStroke) {
	    bs = (BasicStroke) sg.stroke;
	} else {
	    s = sg.stroke.createStrokedShape(s);
	    bs = null;
	}

	renderPath(sg, s, bs);
    }

    public void fill(SunGraphics2D sg, Shape s) {
	renderPath(sg, s, null);
    }

    public void renderPath(SunGraphics2D sg, Shape s, BasicStroke bs) {
	PathIterator pi = s.getPathIterator(sg.transform);
	boolean adjust = (bs != null &&
			  sg.strokeHint != SunHints.INTVAL_STROKE_PURE);
	boolean thin = (sg.strokeState <= sg.STROKE_THINDASHED);
	Rasterizer r = createShapeRasterizer(pi, sg.transform, bs, thin,
					     adjust, 0.5f);
	Object context = null;
	byte alpha[] = null;

	try {
	    int abox[] = new int[4];
	    r.getAlphaBox(abox);
	    Rectangle devR = new Rectangle(abox[0], abox[1],
					   abox[2] - abox[0],
					   abox[3] - abox[1]);
	    sg.getCompClip().clipBoxToBounds(abox);
	    if (abox[0] >= abox[2] || abox[1] >= abox[3]) {
		return;
	    }
	    r.setOutputArea(abox[0], abox[1],
			    abox[2] - abox[0], abox[3] - abox[1]);
	    context = outpipe.startSequence(sg, s, devR, abox);

	    int tsize = Rasterizer.TILE_SIZE;
	    alpha = getAlphaTile();

	    byte[] atile;

	    for (int y = abox[1]; y < abox[3]; y += tsize) {
		for (int x = abox[0]; x < abox[2]; x += tsize) {
		    int w = Math.min(tsize, abox[2] - x);
		    int h = Math.min(tsize, abox[3] - y);

		    int state = r.getTileState();
		    if (state == Rasterizer.TILE_IS_ALL_0 ||
			outpipe.needTile(context, x, y, w, h) == false)
		    {
			r.nextTile();
			outpipe.skipTile(context, x, y);
			continue;
		    }
		    if (state == Rasterizer.TILE_IS_GENERAL) {
			atile = alpha;
			getAlpha(r, alpha, 1, tsize, 0);
		    } else {
			atile = null;
			r.nextTile();
		    }

		    outpipe.renderPathTile(context, atile, 0, tsize,
					   x, y, w, h);
		}
	    }
	} catch (PRException e) {
	    e.printStackTrace();
	} finally {
	    dropRasterizer(r);
	    if (context != null) {
		outpipe.endSequence(context);
	    }
	    if (alpha != null) {
		dropAlphaTile(alpha);
	    }
	}
    }
}
