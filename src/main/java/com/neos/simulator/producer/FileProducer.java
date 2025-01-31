package com.neos.simulator.producer;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Queue;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
	{
    	"type": "file",
    	"output.directory": "/tmp/dropbox/test2",
    	"file.prefix": "MYPREFIX_",
    	"file.extension":".json"
	}
 * 
 */
public class FileProducer extends EventProducer {

    private static final Logger log = LogManager.getLogger(FileProducer.class);
    public static final String OUTPUT_DIRECTORY_PROP_NAME = "output.directory";
    public static final String FILE_PREFIX_PROP_NAME = "file.prefix";
    public static final String FILE_EXTENSION_PROP_NAME = "file.extension";

    private File outputDirectory;
    private String filePrefix;
    private String fileExtension;

    public FileProducer(Map<String, Object> producerConfig) throws IOException {
    	super();        
    	String outputDir = (String) producerConfig.get(OUTPUT_DIRECTORY_PROP_NAME);
        outputDirectory = new File(outputDir);
        if (!outputDirectory.exists()) {
            if (!outputDirectory.mkdir()) {
                if (!outputDirectory.mkdirs()) {
                    throw new IOException("Output directory does not exist and we are unable to create it");
                }
            }
        }
        filePrefix = (String) producerConfig.get(FILE_PREFIX_PROP_NAME);
        fileExtension = (String) producerConfig.get(FILE_EXTENSION_PROP_NAME);	
    }
  
	
    @Override
    public void publish(String event) {
    	try {
            File f = File.createTempFile(filePrefix, fileExtension, outputDirectory);
            FileUtils.writeStringToFile(f, event, "UTF-8");
        } catch (IOException ioe) {
            log.error("Unable to create temp file");
        }
    }

    @Override
    public void publish(String event, String topic) {

    }

    @Override
	    public void stop() {
	    }


}
