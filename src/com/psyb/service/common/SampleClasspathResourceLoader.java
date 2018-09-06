/**
 * 
 */
package com.psyb.service.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

/**
 * @author chao
 */
public class SampleClasspathResourceLoader extends ClasspathResourceLoader {

	@Override
	public synchronized InputStream getResourceStream(String name) throws ResourceNotFoundException {
		try {
			String contextPath = ScriptManager.getScriptManager().getAbsolutePath("classpath:config");
			String appPath = contextPath + File.separator + name;
			return new FileInputStream(new File(appPath));
		} catch (IOException e) {
			throw new ResourceNotFoundException(name);
		}
	}
}
