package com.example.EMR;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.List;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EmrApplication {

	private static final Logger logger = LogManager.getRootLogger();

	public static void main(String[] args) throws Exception {

		SpringApplication.run(EmrApplication.class, args);
		System.out.println("Starting EMR Service");
	}

}
