/*
 * Yafaray Gestalt
 *
 * Copyright (C) 2009
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * {@link http://www.gnu.org/licenses/lgpl.html}
 *
 */


package gestalt.util.yafaray.demo;


import data.Resource;
import gestalt.G;
import gestalt.model.Model;
import gestalt.render.SketchRenderer;
import gestalt.util.yafaray.YafaraySceneWriter;


public class SketchMeshWriter
        extends SketchRenderer {

    public void setup() {
        light().enable = true;
        light().position().set(100, 200, 300);

        cameramover(true);

        final Model mModel = G.model(Resource.getStream("venusBody.obj"));
        mModel.mesh().material().lit = true;
    }

    public void keyPressed() {
        if (key == ' ') {
            new YafaraySceneWriter("test-yafaray", bin());
        }
    }

    public void loop(float pDeltaTime) {
//        backgroundcolor().set(random());
    }

    public static void main(String[] args) {
        G.init(SketchMeshWriter.class, 1024, 768);
    }
}
