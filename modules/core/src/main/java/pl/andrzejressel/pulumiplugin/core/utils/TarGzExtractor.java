// SPDX-License-Identifier: LGPL-3.0-or-later
package pl.andrzejressel.pulumiplugin.core.utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.IOUtils;

public class TarGzExtractor {

  private TarGzExtractor() {}

  public static void extractTarGz(Path targzFile, Path extractionDirectory) throws IOException {
    try (InputStream fileInputStream = new FileInputStream(targzFile.toFile());
        InputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
        InputStream gzipInputStream = new GzipCompressorInputStream(bufferedInputStream);
        TarArchiveInputStream tarInputStream = new TarArchiveInputStream(gzipInputStream)) {
      extractTarGz(tarInputStream, extractionDirectory);
    }
  }

  private static void extractTarGz(TarArchiveInputStream tis, Path extractionPath)
      throws IOException {
    TarArchiveEntry entry;
    while ((entry = tis.getNextTarEntry()) != null) {
      var localLocation = extractionPath.resolve(entry.getName());
      if (entry.isDirectory()) {
        Files.createDirectories(localLocation);
      } else {
        try (OutputStream outputFileStream = new FileOutputStream(localLocation.toFile())) {
          IOUtils.copy(tis, outputFileStream);
        }
      }
    }
  }
}
