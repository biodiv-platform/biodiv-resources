package com.strandls.resource.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceUtil {

	private static final List<String> PREVENTIVE_TOKENS = Arrays.asList("&", "|", "`", "$", ";");

	private static final Logger logger = LoggerFactory.getLogger(ResourceUtil.class);

	private static final String CONTENT = "content";

	public enum BASE_FOLDERS {
		OBSERVATIONS("observations"), IMG("img"), SPECIES("species"), USER_GROUPS("userGroups"), USERS("users"),
		PAGES("pages"), SIGNATURE("signature"), TRAITS("traits"), MY_UPLOADS("myUploads"), THUMBNAILS("thumbnails"),
		LANDSCAPE("landscape"), DOCUMENTS(String.join(String.valueOf(File.separatorChar), CONTENT, "documents")),
		TEMP("temp"), DATATABLES(String.join(String.valueOf(File.separatorChar), CONTENT, "dataTables")),
		DATASETS(String.join(String.valueOf(File.separatorChar), CONTENT, "datasets")),
		CURATION(String.join(String.valueOf(File.separatorChar), CONTENT, "curation")), HOME_PAGE("homePage"),
		RESOURCES("resources");

		private String folder;

		private BASE_FOLDERS(String folder) {
			this.folder = folder;
		}

		public String getFolder() {
			return folder;
		}
	}

	public static BASE_FOLDERS getFolder(String directory) {
		if (directory == null || directory.isEmpty()) {
			return null;
		}
		for (BASE_FOLDERS folders : BASE_FOLDERS.values()) {
			if (folders.name().equalsIgnoreCase(directory)) {
				return folders;
			}
		}
		return null;
	}

	public static File findFile(String filePath) throws IOException {
		File expectedFile = null;
		String dir = filePath.substring(0, filePath.lastIndexOf(File.separatorChar));
		String fileName = filePath.substring(filePath.lastIndexOf(File.separatorChar) + 1, filePath.lastIndexOf("."));
		File file = new File(dir);
		if (file.exists()) {
			for (File f : file.listFiles()) {
				String name = f.getCanonicalFile().getName();
				if (!f.isDirectory() && name.contains(".")
						&& name.substring(0, name.lastIndexOf(".")).equalsIgnoreCase(fileName)) {
					expectedFile = f;
					break;
				}
			}
		}
		return expectedFile;
	}

	private static int calculatePointSizeBasedOnDimensions(Integer width, Integer height) {
		double percentage = 0.05;
		if (width != null && height != null) {
			int minDimension = Math.min(width, height);
			return (int) (minDimension * percentage);
		} else if (width != null) {
			return (int) (width * percentage);
		} else if (height != null) {
			return (int) (height * percentage);
		} else {
			return 30;
		}
	}

	public static String generateImageCommand(String filePath, String outputFilePath, Integer w, Integer h,
			String preserveFormat, Integer quality, String fit, String watermark) {
		List<String> commands = new ArrayList<>();
		StringBuilder command = new StringBuilder();
		String fileName = getFileName(filePath);
		String finalFilePath = getFinalFilePath(outputFilePath, fileName, watermark);

		command.append("convert").append(" ");
		appendFilePath(command, filePath);
		appendResizeOptions(command, w, h, fit);
		appendWatermark(command, watermark, w, h);
		appendQualityAndOutput(command, quality, finalFilePath, w, h, fit, preserveFormat);

		commands.add(command.toString());
		return String.join(" ", commands).trim();
	}

	private static String getFileName(String filePath) {
		String fileName = filePath.substring(0, filePath.lastIndexOf("."));
		return fileName.substring(fileName.lastIndexOf(File.separatorChar));
	}

	private static void appendFilePath(StringBuilder command, String filePath) {
		if (filePath.contains(" ")) {
			command.append("'").append(filePath).append("'");
		} else {
			command.append(filePath);
		}
	}

	private static void appendResizeOptions(StringBuilder command, Integer w, Integer h, String fit) {
		if (w != null || h != null) {
			command.append(" ").append("-auto-orient").append(" ").append("-resize").append(" ");
			if (h != null && w != null && fit.equalsIgnoreCase("center")) {
				command.append(w).append("x").append(h).append("^");
				command.append(" ").append("-gravity").append(" ").append("center").append(" ").append("-extent")
						.append(" ");
				command.append(w).append("x").append(h).append(" ");
			} else {
				if (h != null && w != null) {
					command.append(w).append("x").append(h).append("!");
				} else if (h != null) {
					command.append("x").append(h);
				} else {
					command.append(w);
				}
			}
		} else {
			command.append(" ").append("-auto-orient").append(" ").append(" ");
		}
	}

	private static void appendWatermark(StringBuilder command, String watermark, Integer w, Integer h) {
		if (watermark != null && !"".equals(watermark)) {
			int calculatedPointSize = calculatePointSizeBasedOnDimensions(w, h);
			command.append(" ").append("-gravity").append(" ").append("SouthEast").append(" ").append("-fill")
					.append(" ").append("white").append(" ").append("-pointsize").append(" ")
					.append(calculatedPointSize).append(" ").append("-annotate").append(" ").append("+10+10")
					.append(" ").append("'").append(watermark).append("'");
		}
	}

	private static void appendQualityAndOutput(StringBuilder command, Integer quality, String finalFilePath, Integer w,
			Integer h, String fit, String preserveFormat) {
		command.append(" ").append("-quality").append(" ").append(quality).append(" ");
		if (w != null || h != null) {
			if (finalFilePath.contains(" ")) {
				command.append("'").append(finalFilePath).append("_").append(w).append("x").append(h).append("_")
						.append(fit).append(".").append(preserveFormat).append("'");
			} else {
				command.append(finalFilePath).append("_").append(w).append("x").append(h).append("_").append(fit)
						.append(".").append(preserveFormat);
			}
		} else {
			if (finalFilePath.contains(" ")) {
				command.append("'").append(finalFilePath).append("_").append(fit).append(".").append(preserveFormat)
						.append("'");
			} else {
				command.append(finalFilePath).append(".").append(preserveFormat);
			}
		}
	}

	private static String getFinalFilePath(String outputFilePath, String fileName, String watermark) {
		String suffix = (watermark != null) ? "-" + watermark + "-mod" : "-mod";
		return outputFilePath + fileName + suffix;
	}

	public static boolean generateFile(String command) {
		boolean isFileGenerated = false;
		try {
			isFileGenerated = executeCommandWithExitValue(command);
		} catch (Exception ex) {
			logger.error(ex.getMessage());
		}
		return isFileGenerated;
	}

	public static File getResizedImage(String command, int imageQuality) {
		File resizedImage = null;
		try {
			command = command.replace("'", "");
			String delimiter = "-quality " + imageQuality;
			resizedImage = new File(command.substring(command.indexOf(delimiter) + delimiter.length()).trim());
		} catch (Exception ex) {
			logger.error(ex.getMessage());
		}
		return resizedImage;
	}

	public static boolean executeCommandWithExitValue(String command) {
		Process p = null;
		boolean output = false;
		try {
			if (PREVENTIVE_TOKENS.stream().noneMatch(command::contains)) {
				String[] commands = { "/bin/sh", "-c", command };
				p = Runtime.getRuntime().exec(commands);
				output = p.waitFor(5, TimeUnit.SECONDS);
			}
		} catch (InterruptedException ie) {
			logger.error("InterruptedException: ", ie);
			Thread.currentThread().interrupt();
		} catch (Exception ex) {
			logger.error(ex.getMessage());
		}
		return output;
	}

	public static CacheControl getCacheControl() {
		CacheControl cache = new CacheControl();
		cache.setMaxAge(365 * 24 * 60 * 60);
		return cache;
	}

	public static Response fromFileToStream(File src, String contentType) throws IOException {
		InputStream in = new FileInputStream(src);
		try {
			StreamingOutput sout = new StreamingOutput() {
				@Override
				public void write(OutputStream out) throws IOException, WebApplicationException {
					byte[] buf = new byte[8192];
					int c;
					while ((c = in.read(buf, 0, buf.length)) > 0) {
						out.write(buf, 0, c);
						out.flush();
					}
					in.close();
					out.close();
				}
			};

			return Response.ok(sout).type(contentType).header("Content-Length", src.length())
					.cacheControl(getCacheControl()).build();
		} catch (Exception e) {
			in.close();
			return Response.serverError().entity(e.getMessage()).build();

		}
	}

	public static String determineContentType(boolean preserve, String format, String detactedContentType) {
		String contentType;
		if (preserve) {
			contentType = detactedContentType;
		} else {
			if ("webp".equalsIgnoreCase(format)) {
				contentType = "image/webp";
			} else {
				contentType = detactedContentType;
			}
		}
		return contentType;
	}

	public static Map<String, String> getResourceContextMap() {
		Map<String, String> resourceCtxMap = new HashMap<>();
		resourceCtxMap.put("MY_UPLOADS", "myUploads");
		resourceCtxMap.put("OBSERVATION", "observations");
		resourceCtxMap.put("PAGES", "pages");
		resourceCtxMap.put("SPECIES_FIELD", "img");
		resourceCtxMap.put("SPECIES", "img");
		resourceCtxMap.put("USERGROUPS", "userGroups");
		resourceCtxMap.put("RESOURCE", "resources");
		return resourceCtxMap;
	}

	public static String folderPrefix(String context) {
		Map<String, String> javaResourceCtxMap = getResourceContextMap();
		if (javaResourceCtxMap.containsKey(context)) {
			context = "/" + javaResourceCtxMap.get(context) + "/";
		}
		return context;
	}

}
