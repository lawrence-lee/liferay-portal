package com.liferay.project.templates.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.CompletableFuture;

import edu.emory.mathcs.backport.java.util.Collections;

public class StringCaptureWriter extends Writer implements Runnable {
	private Collection<String> captureCollection;
	private Collection<String> resultsCollection = new ArrayList<>();
	private PipedOutputStream pos;
	private PipedInputStream pis;
	private PrintWriter writer;
	public StringCaptureWriter(String... strings) {
		try
		{
			pos = new PipedOutputStream();
			pis = new PipedInputStream(pos);
			writer = new PrintWriter(pos);
			captureCollection =  new HashSet<String>();
			Collections.addAll(captureCollection, strings);
			CompletableFuture.runAsync(this);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public void close() throws IOException {
		writer.close();
	}

	public Collection<String> getResultsCollection() {
		return resultsCollection;
	}

	@Override
	public void flush() throws IOException {
		writer.flush();
	}
	
	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		writer.write(cbuf, off, len);
	}

	@Override
	public void run() {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(pis))) {

			String line = null;
			while ((line = br.readLine()) != null) {
				for (String capture : captureCollection) {
					if (line.contains(capture)) {
						resultsCollection.add(line);
						break;
					}
				}
			}
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
