package org.example;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class VideoDownloader {


    private static final String OUTPUT_DIR = "C:\\Users\\INDIA\\My Projects\\Backend\\email-sender-standalone"; // Directory to store the downloaded chunks
    private static final String FILE_EXTENSION = ".ts"; // File extension for video chunks
    private static final String BASE_URL = "https://media.scaler.com/production/protected-recordings/911724/927058/__segment:1001011010100101010101011010010110101001011012/stream_0/"; // Base URL without chunk part
    private static final String KEY_PAIR_ID = "K4IMAQNEJMDV1"; // Your Key-Pair-Id
    private static final String POLICY = "ewogICAgICAgICJTdGF0ZW1lbnQiOiBbCiAgICAgICAgICB7CiAgICAgICAgICAgICJSZXNvdXJjZSI6ICJodHRwczovL21lZGlhLnNjYWxlci5jb20vcHJvZHVjdGlvbi9wcm90ZWN0ZWQtcmVjb3JkaW5ncy85MTE3MjQvOTI3MDU4L19fc2VnbWVudDoxMDAxMDExMDEwMTAwMTAxMDEwMTAxMDExMDEwMDEwMTEwMTAxMDAxMDExMDEyLyoiLAogICAgICAgICAgICAiQ29uZGl0aW9uIjogewogICAgICAgICAgICAgICAiSXBBZGRyZXNzIjogewogICAgICAgICAgICAgICAgICAiQVdTOlNvdXJjZUlwIjogIjAuMC4wLjAvMCIKICAgICAgICAgICAgICAgIH0sCiAgICAgICAgICAgICAgICAiRGF0ZUxlc3NUaGFuIjogewogICAgICAgICAgICAgICAgICAiQVdTOkVwb2NoVGltZSI6IDE3Mzk1NTMzMzYKICAgICAgICAgICAgICAgIH0sCiAgICAgICAgICAgICAgICJEYXRlR3JlYXRlclRoYW4iOiB7CiAgICAgICAgICAgICAgICAgICJBV1M6RXBvY2hUaW1lIjogMTczOTUzODkzNgogICAgICAgICAgICAgICAgfQogICAgICAgICAgICB9CiAgICAgICAgICB9CiAgICAgICAgXQogICAgICB9CiAgICA_"; // Your Policy (truncated for clarity)
    private static final String SIGNATURE = "wLepNf7XwlKvLvhq9Scj4hLXoOlAz2pDv1cU~5eUs8Gk-tO~HSnYliYbd8K8agO2JHTzuA2HhKUO9crVHflPT3Zk8a8xxM7a3y5oSONev7YEK3SopX0goTQEg4fGcqS49MRahrxnH7ukq6kqaPeYkduXyIngQXLHUv2bPqsb82YIFFRZYd4wFGq6yKcudaGVYGQH1hF3P2sJ8LA57uWF4HzUvs5EBCHn5VEweIwdFna4FlygLWZAU3ElOBVJVNBFtrrD1cfLXwMyG7XkK3nqF3kz9f6LqmctqeQ~X15eHlQ7wGTKfcHecAFkPx5hNqiKxFkVvXsrYnjCnTch4-Qvaw__"; // Your Signature

    // Method to download a chunk
    private static void downloadChunk(String url, int chunkIndex) {
        try {
            // Create a URL object with the full URL
            URL chunkUrl = new URL(url);

            // Open a connection to the URL
            HttpURLConnection connection = (HttpURLConnection) chunkUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);

            // Set headers to mimic a browser request

            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Mobile Safari/537.36");
            connection.setRequestProperty("Referer", "https://www.scaler.com/");
            connection.setRequestProperty("Origin", "https://www.scaler.com");

            // Check if the response is successful (HTTP 200)
            int statusCode = connection.getResponseCode();
            if (statusCode == HttpURLConnection.HTTP_OK) {
                // Open an input stream from the connection to read the data
                try (InputStream in = connection.getInputStream()) {
                    // Create an output stream to save the chunk data to a file
                    File outputFile = new File(OUTPUT_DIR, "data" + String.format("%06d", chunkIndex) + FILE_EXTENSION);
                    try (FileOutputStream out = new FileOutputStream(outputFile)) {
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        // Read data from the input stream and write it to the output file
                        while ((bytesRead = in.read(buffer)) != -1) {
                            out.write(buffer, 0, bytesRead);
                        }
                        System.out.println("Downloaded: " + outputFile.getName());
                    }
                }
            } else {
                System.err.println("Failed to download: HTTP " + statusCode);
            }
        } catch (IOException e) {
            System.err.println("IOException occurred while downloading: " + url);
            e.printStackTrace();
        }
    }

    // Method to download all chunks
    public static void downloadAllChunks(int startChunk, int endChunk) {
        for (int i = startChunk; i <= endChunk; i++) {
            String chunkUrl = BASE_URL + "data" + String.format("%06d", i) + FILE_EXTENSION
                    + "?Key-Pair-Id=" + KEY_PAIR_ID
                    + "&Policy=" + POLICY
                    + "&Signature=" + SIGNATURE;
            downloadChunk(chunkUrl, i);
        }
    }

    // Method to combine downloaded chunks using FFmpeg
    public static void combineChunks(int startChunk, int endChunk, String outputFileName) throws IOException {
        // Create a text file with a list of all the downloaded chunks
        File listFile = new File(OUTPUT_DIR, "chunks.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(listFile))) {
            for (int i = startChunk; i <= endChunk; i++) {
                writer.write("file '" + new File(OUTPUT_DIR, "data" + String.format("%06d", i) + FILE_EXTENSION).getAbsolutePath() + "'");
                writer.newLine();
            }
        }

        // Run FFmpeg to combine the chunks
        String ffmpegCommand = "ffmpeg -f concat -safe 0 -i " + listFile.getAbsolutePath() + " -c copy " + OUTPUT_DIR + File.separator + outputFileName;
        Process process = Runtime.getRuntime().exec(ffmpegCommand);

        // Wait for the FFmpeg process to finish
        try {
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Video successfully combined into: " + outputFileName);
            } else {
                System.err.println("FFmpeg command failed with exit code " + exitCode);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            // Download chunks from 340 to 350 (for example)
            downloadAllChunks(340, 350);

            // Combine the downloaded chunks into one video file (output.mp4)
            combineChunks(340, 350, "output_video.mp4");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
