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


package gestalt.util.yafaray;


import gestalt.impl.jogl.render.plugin.JoglCamera;
import gestalt.render.Drawable;
import gestalt.render.plugin.Camera;
import mathematik.Vector3f;
import nanoxml.XMLElement;


public class YafarayCameraTranslator
        implements YafarayDrawableTranslator {

    public boolean isClass(final Drawable theDrawable) {
        return theDrawable instanceof JoglCamera;
    }

    public void parse(final YafaraySceneWriter pParent, final Drawable pDrawable) {
        final Camera mCamera = (Camera)pDrawable;
        final XMLElement mNode = YafaraySceneWriter.createXMLElement();

        mNode.setName("camera");
        mNode.setAttribute("name", "cam");

        {
            final XMLElement mPosition = YafaraySceneWriter.createXMLElement();
            mPosition.setName("from");
            YafaraySceneWriter.setVector3f(mPosition, mCamera.position());
            mNode.addChild(mPosition);
        }
        {
            final XMLElement mResX = YafaraySceneWriter.createXMLElement();
            mResX.setName("resx");
            mResX.setAttribute("ival", mCamera.viewport().width);
            mNode.addChild(mResX);
        }
        {
            final XMLElement mResY = YafaraySceneWriter.createXMLElement();
            mResY.setName("resy");
            mResY.setAttribute("ival", mCamera.viewport().height);
            mNode.addChild(mResY);
        }
        {
            final XMLElement mLookAt = YafaraySceneWriter.createXMLElement();
            mLookAt.setName("to");
            YafaraySceneWriter.setVector3f(mLookAt, mCamera.lookat());
            mNode.addChild(mLookAt);
        }
        {
            final XMLElement mType = YafaraySceneWriter.createXMLElement();
            mType.setName("type");
            mType.setAttribute("sval", "perspective");
            mNode.addChild(mType);
        }
        {
            final XMLElement mUp = YafaraySceneWriter.createXMLElement();
            mUp.setName("up");
            YafaraySceneWriter.setVector3f(mUp, mCamera.upvector());
            mNode.addChild(mUp);
        }

        pParent.addNode(mNode);
    }
}
