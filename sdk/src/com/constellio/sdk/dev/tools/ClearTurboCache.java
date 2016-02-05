package com.constellio.sdk.dev.tools;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.constellio.model.conf.FoldersLocator;

public class ClearTurboCache {

	public static void main(String argv[]) {
		File sdkFolder = new FoldersLocator().getSDKProject();
		File turboCache = new File(sdkFolder, "turboCache");
		try {
			FileUtils.deleteDirectory(turboCache);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		System.out.println("Done!");
	}

}
