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


import data.Resource;
import gestalt.render.Drawable;
import gestalt.render.bin.AbstractBin;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.Vector;
import mathematik.Vector3f;
import nanoxml.XMLElement;


public class YafaraySceneWriter {

    private final String PATH_TO_RENDERER = "/usr/local/bin/yafaray-xml";

    private final String mCurrentXMLFileName;

    private static final String DEFAULT_SCENE_XML = Resource.getPath("yafaray-src.xml");

    private static final boolean IGNORE_WARNINGS = true;

    private static final boolean BLOCK_PROCESS = true;

    private final Vector<YafarayDrawableTranslator> mTranslators;

    private final XMLElement mXML;

    public YafaraySceneWriter(final String pXMLFileName, final AbstractBin pBin) {
        mCurrentXMLFileName = Resource.getPath("") + "/" + pXMLFileName + "-" + werkzeug.Util.now() + ".xml";
        mXML = loadXML(DEFAULT_SCENE_XML);

        mTranslators = new Vector<YafarayDrawableTranslator>();
        mTranslators.add(new YafarayMeshTranslator());
        mTranslators.add(new YafarayModelTranslator());
        mTranslators.add(new YafarayCameraTranslator());

        parse(pBin);
        saveXML(mCurrentXMLFileName, mXML);
        launchRenderer();
    }

    private static void saveXML(final String pAbsoluteFilePath, final XMLElement pXML) {
        try {
            final FileWriter mWriter = new FileWriter(new File(pAbsoluteFilePath));
//            final String HEADER = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n";
//            mWriter.append(HEADER);
            pXML.write(mWriter);
            mWriter.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    static XMLElement createXMLElement() {
        return new XMLElement(new Hashtable(), false, false);
    }

    private static XMLElement loadXML(final String pAbsoluteFilePath) {
        final XMLElement mLocalXML = new XMLElement(new Hashtable(), false, false);
        try {
            mLocalXML.parseFromReader(new InputStreamReader(werkzeug.Util.getInputStream(pAbsoluteFilePath)));
        } catch (IOException ex) {
            System.err.println("### ERROR / couldn t read XML file." + ex);
        }
        return mLocalXML;
    }

    void addNode(final XMLElement pXMLNode) {
        mXML.addChild(pXMLNode);
    }

    private void parse(final AbstractBin pBin) {
        Drawable[] mySortables = pBin.getDataRef();
        for (int i = 0; i < pBin.size(); i++) {
            final Drawable myDrawable = mySortables[i];
            if (myDrawable instanceof AbstractBin) {
                parse((AbstractBin)myDrawable);
            }
            if (myDrawable != null) {
                if (myDrawable.isActive()) {
                    parseDrawable(myDrawable);
                }
            }
        }
    }

    private void parseDrawable(final Drawable pDrawable) {
        for (final YafarayDrawableTranslator mTranslator : mTranslators) {
            if (mTranslator != null & mTranslator.isClass(pDrawable)) {
                System.out.println("### NOTE parsing " + pDrawable.getClass().getSimpleName());
                mTranslator.parse(this, pDrawable);
                return;
            }
        }

        if (!IGNORE_WARNINGS) {
            System.err.println("### WARNING / drawable type unsupported. / " + pDrawable.getClass());
        }
    }

    private void launchRenderer() {
        try {
            String myCommand = PATH_TO_RENDERER + " " + mCurrentXMLFileName;
            Process mProcess = Runtime.getRuntime().exec(myCommand);
            System.out.println("### EXECUTE: " + myCommand);

            StreamGobbler mErrorGobbler = new StreamGobbler(mProcess.getErrorStream(), "ERR");
            StreamGobbler mOutputGobbler = new StreamGobbler(mProcess.getInputStream(), "OUT");
            mErrorGobbler.start();
            mOutputGobbler.start();

            if (BLOCK_PROCESS) {
                try {
                    mProcess.waitFor();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }

            System.out.println("### DONE EXECUTING PROCESS.");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void setVector3f(final XMLElement pXMLElement, Vector3f p) {
        pXMLElement.setAttribute("x", p.x);
        pXMLElement.setAttribute("y", p.y);
        pXMLElement.setAttribute("z", p.z);
    }


   
    private class StreamGobbler
            extends Thread {

        private InputStream mStream;

        private String mStreamName;

        StreamGobbler(InputStream theStream, String theStreamName) {
            mStream = theStream;
            mStreamName = theStreamName;
        }

        public void run() {
            try {
                final InputStreamReader isr = new InputStreamReader(mStream);
                final BufferedReader br = new BufferedReader(isr);
                String line = null;
                while ((line = br.readLine()) != null) {
                    System.out.println(mStreamName + "> " + line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
