import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.formulasearchengine.mathmltools.gold.GoldStandardLoader;
import com.formulasearchengine.mathmltools.gold.GoldUtils;
import com.formulasearchengine.mathmltools.gold.pojo.JsonGouldiBean;
import com.formulasearchengine.mathmltools.io.XmlDocumentReader;
import com.formulasearchengine.mathmltools.mml.CMMLInfo;
import com.formulasearchengine.mathmltools.mml.MathDoc;
import com.formulasearchengine.mathmltools.utils.mml.CSymbol;
import com.google.common.collect.Multiset;
import com.google.common.collect.TreeMultiset;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class SymbolListExample {
    private SymbolListExample() {
    }

    public static void main(String[] args) throws Exception {
        final GoldStandardLoader gold = GoldStandardLoader.getInstance();
        gold.initLocally();
        final Map<String,Integer> omcdMap = new HashMap<>();
        final TreeMultiset<CSymbol> allSymbols = getcSymbols(gold);
        final TreeMultiset<String> normalizedSymbols = TreeMultiset.create();

        System.out.println(allSymbols);
        readOmCdMap(omcdMap);
        normalizeSymbols(omcdMap, allSymbols, normalizedSymbols);

        System.out.println(normalizedSymbols);


    }

    private static void normalizeSymbols(Map<String, Integer> omcdMap, TreeMultiset<CSymbol> allSymbols, TreeMultiset<String> normalizedSymbols) {
        for (Multiset.Entry<CSymbol> cSymbolEntry : allSymbols.entrySet()) {
            final String elem = cSymbolEntry.getElement().toString();
            if (omcdMap.containsKey(elem)){
                normalizedSymbols.add("wikidata:Q"+ omcdMap.get(elem),cSymbolEntry.getCount());
                continue;
            }
            if (cSymbolEntry.getElement().getCd().equals("latexml")){
                if (cSymbolEntry.getElement().getCName().startsWith("Q")) {
                    normalizedSymbols.add("wikidata:"+ cSymbolEntry.getElement().getCName(),cSymbolEntry.getCount());
                    continue;
                }
            }
            normalizedSymbols.add(elem,cSymbolEntry.getCount());
        }
    }

    private static void readOmCdMap(Map<String, Integer> omcdMap) throws IOException {
        final Path goldPath = GoldStandardLoader.getInstance().getGoldPath();

        FileReader in = new FileReader(goldPath.resolve("../doc/openMathSymbols.csv").toFile());
        Iterable<CSVRecord> records = CSVFormat.RFC4180.parse(in);
        for (CSVRecord record : records) {
            String omcd = record.get(0);
            String wikidata = record.get(2);
            int wikidataInt = Integer.parseInt(wikidata.replaceAll("Q(\\d+)", "$1"));
            omcdMap.put(omcd,wikidataInt);
        }
    }

    public static TreeMultiset<CSymbol> getcSymbols(GoldStandardLoader gold) throws IOException {
        final TreeMultiset<CSymbol> allSymbols = TreeMultiset.create();
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
        }
        return allSymbols;
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
//        System.out.println(gouldiJson.getMml());
//        System.out.println(e.getMessage());
//        System.err.println(i);
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
