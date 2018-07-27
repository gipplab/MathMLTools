import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.formulasearchengine.mathmltools.gold.GoldStandardLoader;
import com.formulasearchengine.mathmltools.gold.GoldUtils;
import com.formulasearchengine.mathmltools.gold.pojo.JsonGouldiBean;
import com.formulasearchengine.mathmltools.io.XmlDocumentReader;
import com.formulasearchengine.mathmltools.mml.CMMLInfo;
import com.formulasearchengine.mathmltools.mml.MathDoc;
import com.formulasearchengine.mathmltools.utils.mml.CSymbol;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.TreeMultiset;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class SymbolListExample {
    private SymbolListExample() {
    }

    public static void main(String[] args) throws IOException {
        final GoldStandardLoader gold = GoldStandardLoader.getInstance();
        gold.initLocally();
        final HashMultiset<CSymbol> allSymbols = HashMultiset.create();
        // TODO file a bug that gold should implement iterable
        for (int i = 1; i < 305; i++) {
            final JsonGouldiBean gouldiJson = gold.getGouldiJson(i);
            List<CSymbol> cSymbols;
            try {
                cSymbols = getCSymbols(gouldiJson);
            } catch (SAXException e) {
                cSymbols = fixException(i, e, gouldiJson);
            }
            TreeMultiset<CSymbol> currentSymbols = TreeMultiset.create(cSymbols);
            allSymbols.addAll(currentSymbols);
            //System.out.println(currentSymbols);
        }

    }

    private static List<CSymbol> fixException(int i, SAXException e, JsonGouldiBean gouldiJson) {
        List<CSymbol> cSymbols;
        cSymbols = new ArrayList<>();
        if (e.getMessage().equals("Attribute \"xmlns:m\" must be declared for element type \"power\".")) {
            final String newMml = gouldiJson.getMml().replaceAll("xmlns:m=\"http://www.w3.org/1998/Math/MathML\"", "");
            gouldiJson.setMml(newMml);
            final Path goldPath = GoldStandardLoader.getInstance().getGoldPath();

            GoldUtils.writeGoldFile(goldPath.resolve(i + ".json"), gouldiJson);
        }
        System.out.println(e.getMessage());
        System.err.println(i);
        return cSymbols;
    }


    private static List<CSymbol> getCSymbols(JsonGouldiBean gouldiJson) throws IOException, SAXException {
        final String mmlString = gouldiJson.getMml();
        final Document doc = XmlDocumentReader.parse(mmlString);
        final CMMLInfo mml = new CMMLInfo(doc);
        final CMMLInfo strictCmml = mml.toStrictCmml();
        final MathDoc mathDoc = new MathDoc(strictCmml);
        return mathDoc.getCSymbols();
    }
}
