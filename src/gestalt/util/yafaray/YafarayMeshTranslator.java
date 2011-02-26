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


import gestalt.Gestalt;
import gestalt.render.Drawable;
import gestalt.shape.Mesh;
import mathematik.Matrix3f;
import mathematik.TransformMatrix4f;
import mathematik.Vector3f;
import nanoxml.XMLElement;


public class YafarayMeshTranslator
        implements YafarayDrawableTranslator {

    public boolean isClass(final Drawable theDrawable) {
        return theDrawable instanceof Mesh;
    }

    public void parse(final YafaraySceneWriter theParent, final Drawable theDrawable) {
        final Mesh theMesh = (Mesh)theDrawable;
        parseMesh(theParent, theMesh);
    }

    protected void parseMesh(final YafaraySceneWriter pParent, final Mesh pMesh) {
        final XMLElement mNode = YafaraySceneWriter.createXMLElement();

        mNode.setName("mesh");
        mNode.setAttribute("has_orco", false);
        mNode.setAttribute("has_uv", false);
        mNode.setAttribute("type", 0);
        pParent.addNode(mNode);

        final float[] pVertices = pMesh.vertices();
        final TransformMatrix4f theTransform = pMesh.transform();
        final Vector3f theRotation = pMesh.rotation();
        final Vector3f theScale = pMesh.scale();

        /* vertices */
        mNode.setAttribute("vertices", pVertices.length / pMesh.getNumberOfVertexComponents());

        Matrix3f myRotationMatrix = null;
        if (theTransform != null) {
            myRotationMatrix = new Matrix3f(theTransform.rotation);
            myRotationMatrix.invert();
        }

        for (int i = 0; i < pVertices.length; i += 3) {
            final Vector3f myVertex = new Vector3f(pVertices[i + 0],
                                                   pVertices[i + 1],
                                                   pVertices[i + 2]);
            if (theScale != null) {
                myVertex.scale(theScale);
            }
            if (theRotation != null && (theRotation.x != 0 || theRotation.y != 0 || theRotation.z != 0)) {
                final TransformMatrix4f myTempRotationMatrix = new TransformMatrix4f(TransformMatrix4f.IDENTITY);
                myTempRotationMatrix.rotation.setXYZRotation(theRotation);
                myTempRotationMatrix.transform(myVertex);
            }
            if (theTransform != null) {
                myRotationMatrix.transform(myVertex);
                myVertex.add(theTransform.translation);
            }

            /* --- */
            final XMLElement p = YafaraySceneWriter.createXMLElement();
            p.setName("p");
            YafaraySceneWriter.setVector3f(p, myVertex);
            mNode.addChild(p);
        }

        /* material */
        final XMLElement mMaterial = YafaraySceneWriter.createXMLElement();
        mMaterial.setName("set_material");
        mMaterial.setAttribute("sval", "defaultMat");
        mNode.addChild(mMaterial);

        /* faces */
        final int myShape;
        if (pMesh.getPrimitive() == Gestalt.MESH_QUADS) {
            myShape = 4;
        } else if (pMesh.getPrimitive() == Gestalt.MESH_TRIANGLES) {
            myShape = 3;
        } else {
            System.out.println("### WARNING / use quads and triangles only.");
            myShape = 3;
        }

        mNode.setAttribute("faces", pVertices.length / pMesh.getNumberOfVertexComponents() / myShape);

        for (int i = 0; i < pVertices.length / 3; i += myShape) {
            final XMLElement f = YafaraySceneWriter.createXMLElement();
            f.setName("f");
            f.setAttribute("a", i + 0);
            f.setAttribute("b", i + 1);
            f.setAttribute("c", i + 2);
            mNode.addChild(f);
        }

//        /* apply transform */
//        final TransformMatrix4f myTransform = mathematik.Util.getTranslateRotationTransform(pMesh.getTransformMode(),
//                                                                                            pMesh.transform(),
//                                                                                            pMesh.rotation(),
//                                                                                            pMesh.scale());
//
//        /* vertices */
//        mNode.setAttribute("vertices", pMesh.vertices().length / pMesh.getNumberOfVertexComponents());
//        for (int i = 0; i < pMesh.vertices().length; i += pMesh.getNumberOfVertexComponents()) {
//            if (pMesh.getNumberOfVertexComponents() != 3) {
//                System.out.println("### WARNING / use 3 vertex components only.");
//            }
//
//            final Vector3f myPosition = new Vector3f(pMesh.vertices()[i + 0],
//                                                     pMesh.vertices()[i + 1],
//                                                     pMesh.vertices()[i + 2]);
//            myTransform.transform(myPosition);
//
//            final XMLElement p = YafaraySceneWriter.createXMLElement();
//            p.setName("p");
//            YafaraySceneWriter.setVector3f(p, myPosition);
//            mNode.addChild(p);
//        }
//
//        /* material */
//        final XMLElement mMaterial = YafaraySceneWriter.createXMLElement();
//        mMaterial.setName("set_material");
//        mMaterial.setAttribute("sval", "defaultMat");
//        mNode.addChild(mMaterial);
//
//        /* faces */
//        final int myShape;
//        if (pMesh.getPrimitive() == Gestalt.MESH_QUADS) {
//            myShape = 4;
//        } else if (pMesh.getPrimitive() == Gestalt.MESH_TRIANGLES) {
//            myShape = 3;
//        } else {
//            System.out.println("### WARNING / use quads and triangles only.");
//            myShape = 3;
//        }
//
//        mNode.setAttribute("faces", pMesh.vertices().length / pMesh.getNumberOfVertexComponents() / myShape);
//        for (int i = 0; i < pMesh.vertices().length / pMesh.getNumberOfVertexComponents(); i += myShape) {
//            final XMLElement f = YafaraySceneWriter.createXMLElement();
//            f.setName("f");
//            f.setAttribute("a", i + 0);
//            f.setAttribute("b", i + 1);
//            f.setAttribute("c", i + 2);
//            mNode.addChild(f);
//        }
    }
}