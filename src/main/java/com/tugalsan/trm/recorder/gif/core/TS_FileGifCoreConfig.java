package com.tugalsan.trm.recorder.gif.core;

import javax.imageio.*;
import javax.imageio.metadata.*;
import javax.imageio.stream.*;
import java.awt.image.*;
import java.io.*;
import java.nio.file.*;

public class TS_FileGifCoreConfig {

    private TS_FileGifCoreConfig(Path file, int imageType, int timeBetweenFramesMS, boolean loopContinuously) throws IIOException, IOException {
        this.timeBetweenFramesMS = timeBetweenFramesMS;
        var outputStream = new FileImageOutputStream(file.toFile());
        gifWriter = TS_FileGifCoreUtils.getImageWriter_forGif();
        imageWriteParam = gifWriter.getDefaultWriteParam();
        var imageTypeSpecifier = ImageTypeSpecifier.createFromBufferedImageType(imageType);
        imageMetaData = gifWriter.getDefaultImageMetadata(imageTypeSpecifier, imageWriteParam);
        var metaFormatName = imageMetaData.getNativeMetadataFormatName();
        var root = (IIOMetadataNode) imageMetaData.getAsTree(metaFormatName);
        var graphicsControlExtensionNode = TS_FileGifCoreUtils.findIIOMetadataNode_orElseCreateNew(root, "GraphicControlExtension");
        graphicsControlExtensionNode.setAttribute("disposalMethod", "none");
        graphicsControlExtensionNode.setAttribute("userInputFlag", "FALSE");
        graphicsControlExtensionNode.setAttribute("transparentColorFlag", "FALSE");
        graphicsControlExtensionNode.setAttribute("delayTime", Integer.toString(timeBetweenFramesMS / 10));
        graphicsControlExtensionNode.setAttribute("transparentColorIndex", "0");
        var commentsNode = TS_FileGifCoreUtils.findIIOMetadataNode_orElseCreateNew(root, "CommentExtensions");
        commentsNode.setAttribute("CommentExtension", "Created by " + TS_FileGifCoreConfig.class.getName());
        var appEntensionsNode = TS_FileGifCoreUtils.findIIOMetadataNode_orElseCreateNew(root, "ApplicationExtensions");
        var child = new IIOMetadataNode("ApplicationExtension");
        child.setAttribute("applicationID", "NETSCAPE");
        child.setAttribute("authenticationCode", "2.0");
        var loop = loopContinuously ? 0 : 1;
        child.setUserObject(new byte[]{0x1, (byte) (loop & 0xFF), (byte) ((loop >> 8) & 0xFF)});
        appEntensionsNode.appendChild(child);
        imageMetaData.setFromTree(metaFormatName, root);
        gifWriter.setOutput(outputStream);
        gifWriter.prepareWriteSequence(null);
        imageWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        imageWriteParam.setCompressionType("LZW");
    }
    public ImageWriter gifWriter;
    public ImageWriteParam imageWriteParam;
    public IIOMetadata imageMetaData;

    public long timeBetweenFramesMS() {
        return timeBetweenFramesMS;
    }
    final private long timeBetweenFramesMS;

    public static TS_FileGifCoreConfig ofARGB(Path file, int timeBetweenFramesMS, boolean loopContinuously) throws IIOException, IOException {
        return new TS_FileGifCoreConfig(file, BufferedImage.TYPE_INT_ARGB, timeBetweenFramesMS, loopContinuously);
    }

    public void append(RenderedImage img) throws IOException {
        gifWriter.writeToSequence(new IIOImage(img, null, imageMetaData), imageWriteParam);
    }

    public void close() throws IOException {
        gifWriter.endWriteSequence();
    }
}
