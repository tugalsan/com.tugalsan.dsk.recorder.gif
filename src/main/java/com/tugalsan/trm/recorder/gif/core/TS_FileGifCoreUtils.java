package com.tugalsan.trm.recorder.gif.core;

import java.util.stream.*;
import javax.imageio.*;
import javax.imageio.metadata.*;

public class TS_FileGifCoreUtils {

    public static ImageWriter getImageWriter_forGif() throws IIOException {
        var iter = ImageIO.getImageWritersBySuffix("gif");
        if (!iter.hasNext()) {
            throw new IIOException("No GIF Image Writers Exist");
        } else {
            return iter.next();
        }
    }

    public static IIOMetadataNode findIIOMetadataNode_orElseCreateNew(IIOMetadataNode rootNode, String nodeName) {
        var nodeSelected = IntStream.range(0, rootNode.getLength())
                .mapToObj(i -> rootNode.item(i))
                .filter(node -> node.getNodeName().compareToIgnoreCase(nodeName) == 0)
                .map(node -> (IIOMetadataNode) node)
                .findAny().orElse(null);
        if (nodeSelected == null) {
            nodeSelected = new IIOMetadataNode(nodeName);
            rootNode.appendChild(nodeSelected);
        }
        return nodeSelected;
    }
}
