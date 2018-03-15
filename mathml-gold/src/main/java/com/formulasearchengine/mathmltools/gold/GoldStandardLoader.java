package com.formulasearchengine.mathmltools.gold;

import com.formulasearchengine.mathmltools.gold.config.ConfigLoader;
import com.formulasearchengine.mathmltools.gold.pojo.JsonGouldiBean;
import com.formulasearchengine.mathmltools.gold.rest.GitHubFileResponse;
import com.formulasearchengine.mathmltools.gold.rest.RESTPathBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class GoldStandardLoader {
    // Time out is 5sec
    public static final int CONN_TIME_OUT = 5 * 1000;
    // Time out is 2sec
    public static final int READ_TIME_OUT = 2 * 1000;
    //
    public static final int PARALLEL_READING_TIMEOUT = 90;
    public static final TimeUnit PARALLEL_READING_TIMEOUT_UNIT = TimeUnit.SECONDS;
    private static final Logger LOG = LogManager.getLogger(GoldStandardLoader.class.getName());
    private static final GoldStandardLoader loader = new GoldStandardLoader();
    private static JsonGouldiBean[] gouldi;
    private String gitHubApiURL;
    private Properties props;
    private RestTemplate rest;
    private int max;
    private boolean local = false;

    /**
     * @throws RuntimeException if the configurations cannot be loaded from config.properties
     */
    private GoldStandardLoader() {
    }

    public static GoldStandardLoader getInstance() {
        return loader;
    }

    public void init() {
        props = ConfigLoader.CONFIG;
        max = Integer.parseInt(props.getProperty(ConfigLoader.GOULDI_MAXIMUM_NUM));

        String repo = props.getProperty(ConfigLoader.GITHUB_REPO_NAME);
        String owner = props.getProperty(ConfigLoader.GITHUB_REPO_OWNER);
        String path = props.getProperty(ConfigLoader.GITHUB_REPO_PATH);
        String githubLink = props.getProperty(ConfigLoader.GITHUB_URL);

        if (repo == null || owner == null || githubLink == null) {
            LOG.info("Cannot find GitHub access -> switch to local initialization.");
            initLocally();
            return;
        }

        LOG.debug("Load all github properties.");
        gitHubApiURL = new RESTPathBuilder(githubLink)
                .setGithubContent(owner, repo)
                .setInnerPath(path)
                .getURL();

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(CONN_TIME_OUT);
        factory.setReadTimeout(READ_TIME_OUT);

        rest = new RestTemplate(factory);
    }

    public int initLocally() {
        props = ConfigLoader.CONFIG;
        max = Integer.parseInt(props.getProperty(ConfigLoader.GOULDI_MAXIMUM_NUM));

        String goldPath = props.getProperty(ConfigLoader.GOULDI_LOCAL_PATH);
        Path path = Paths.get(goldPath);
        gouldi = new JsonGouldiBean[max];

        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
        for (int i = 1; i <= max; i++) {
            executor.execute(new JSONReader(path, i));
        }

        executor.shutdown();

        try {
            executor.awaitTermination(PARALLEL_READING_TIMEOUT, PARALLEL_READING_TIMEOUT_UNIT);
        } catch (InterruptedException ie) {
            LOG.warn("Executor service exceeds timeouts to read files. It maybe didn't properly load all gouldi-files.");
        }

        this.local = true;
        return max;
    }

    public GitHubFileResponse getResponseFromGouldiRequest(int number) {
        String file = number + ".json";
        return rest.getForObject(
                gitHubApiURL + RESTPathBuilder.BIND + file,
                GitHubFileResponse.class
        );
    }

    public JsonGouldiBean getGouldiJson(int number) {
        if (local) {
            LOG.trace("Local mode. Get Json: " + number);
            return gouldi[number - 1];
        }

        GitHubFileResponse response = getResponseFromGouldiRequest(number);
        // TODO handle response codes here
        try {
            return response.getJsonBeanFromContent();
        } catch (IOException ioe) {
            LOG.error("Cannot provide gouldi bean for qid {} from github.", number, ioe);
            return null;
        }
    }

    private class JSONReader implements Runnable {
        private final Logger inLog = LogManager.getLogger(JSONReader.class.getName());

        private Path path;
        private int number;

        JSONReader(Path goldPath, int number) {
            this.path = goldPath;
            this.number = number;
        }

        @Override
        public void run() {
            try {
                Path p = path.resolve(number + ".json");
                gouldi[number - 1] = GoldUtils.readGoldFile(p);
            } catch (Exception e) {
                inLog.error("Parallel process cannot parse " + path.toString() + number + ".json - " + e.getMessage(), e);
            }
        }
    }
}
