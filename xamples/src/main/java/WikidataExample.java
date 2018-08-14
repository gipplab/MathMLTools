import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import com.formulasearchengine.mathmltools.gold.GoldStandardLoader;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.SiteLink;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataFetcher;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

public class WikidataExample {

    private static final WikibaseDataFetcher FETCHER = WikibaseDataFetcher.getWikidataDataFetcher();
    private static final BidiMap<String, Integer> omCDMap = new DualHashBidiMap<>();
    private static final Map<String, String> allSymbols = new TreeMap<>();
    private static final String LANG = "en";
    private static final String prefix = "wikidata:Q";

    public static void main(String[] args) throws MediaWikiApiErrorException, IOException {
        SymbolListExample.readOmCdMap(omCDMap);
        final WikidataExample example = new WikidataExample();
        //final String omCd = example.getOmCd(example.getItem(1226939));
        //System.out.println(omCd);
        example.processFile();

    }

    private ItemDocument getItem(long qId) throws MediaWikiApiErrorException {
        return (ItemDocument) FETCHER.getEntityDocument("Q" + qId);
    }

    private String getOmCd(ItemDocument item) throws UnsupportedEncodingException {

        final long revisionId = item.getRevisionId();
        final MonolingualTextValue label = item.getLabels().get(LANG);
        final MonolingualTextValue description = item.getDescriptions().get(LANG);
        final SiteLink siteLink = item.getSiteLinks().get(LANG + "wiki");
        final String qIdString = item.getEntityId().getId();
        String descr = "";
        String sortkey = "";
        if (label != null) {
            final String labelText = label.getText();
            descr += labelText + "\n";
            final String[] split = labelText.split(" ");
            sortkey = split[split.length - 1];
        }
        if (description != null) {
            descr += description.getText() + "\n";
        }
        if (siteLink != null) {
            descr += "https://" + LANG + ".wikipedia.org/w/index.php?title=" + URLEncoder.encode(siteLink.getPageTitle(), "utf8") + "\n";
        }
        Integer qId = Integer.valueOf(qIdString.substring(1));
        if (omCDMap.containsValue(qId)) {
            descr += "See also " + omCDMap.getKey(qId) + "\n";
        }
        descr += "\n  This description was generated from http://www.wikidata.org/w/index.php?oldid=" + revisionId;
        descr = "    <CDDefinition>\n"
                + "        <Name>" + qIdString + "</Name>\n"
                + "        <Role>application</Role>\n"
                + "        <Description>\n" + descr
                + "        </Description>\n"
                + "</CDDefinition>";
        sortkey += qId;
        allSymbols.put(sortkey, descr);
        return descr;

    }

    private void processFile() throws IOException, MediaWikiApiErrorException {
        final Path goldPath = GoldStandardLoader.getInstance().getGoldPath();

        FileReader in = new FileReader(goldPath.resolve("../doc/wiki-cd-freqs.csv").toFile());
        Iterable<CSVRecord> records = CSVFormat.RFC4180.parse(in);
        final ArrayList<String> qIds = new ArrayList<>();
        for (CSVRecord record : records) {
            String symbol = record.get(0);
            if (symbol.startsWith(prefix)) {
                final int qId = Integer.parseInt(symbol.substring(prefix.length()));
                qIds.add("Q" + qId);
            }
        }
        final Map<String, EntityDocument> entityDocuments = FETCHER.getEntityDocuments(qIds);
        for (Map.Entry<String, EntityDocument> entry : entityDocuments.entrySet()) {
            getOmCd((ItemDocument) entry.getValue());
        }
        for (String s : allSymbols.keySet()) {
            System.out.println(allSymbols.get(s));
        }

    }

}
