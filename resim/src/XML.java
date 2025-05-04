import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import java.io.IOException;

/**
 * Represents an XML file
 */
public class XML {
    private final java.io.File file;
    private int resourceCount;
    private int[] groupCounts;

    private void parse() throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(this.file);
        doc.getDocumentElement().normalize();

        // Get resource count
        NodeList resourceList = doc.getElementsByTagName("resources");
        this.resourceCount = Integer.parseInt(resourceList.item(0).getTextContent());

        // Get group sizes
        NodeList groupList = doc.getElementsByTagName("group");
        int groupCount = groupList.getLength();
        this.groupCounts = new int[groupCount];
        for (int i = 0; i < groupCount; i++) {
            this.groupCounts[i] = Integer.parseInt(groupList.item(i).getTextContent());
        }
    }

    public XML(java.io.File file) throws ParserConfigurationException, SAXException, IOException {
        this.file = file;
        this.parse();
    }

    public int getResourceCount() {
        return this.resourceCount;
    }

    public int[] getGroupCounts() {
        return this.groupCounts;
    }
}
