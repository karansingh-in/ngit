package ngit.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import java.io.ByteArrayOutputStream;

import static CompressUtil.decompress;

public class CompressUtil {
    public static byte[] compress(byte[] contents) throws IOException{
        Deflater compressor = new Deflater();
        compressor.setInput(contents);
        compressor.finish();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(contents.length);
        byte[] buffer = new byte[2048];

        while(!compressor.finished()){
            int byteCompressed = compressor.deflate(buffer);
            outputStream.write(buffer, 0, byteCompressed);
        }
        outputStream.close();
        compressor.end();
        return outputStream.toByteArray();
    }

    public static byte[] decompress(byte[] contents) throws IOException, DataFormatException {
        Inflater decompressor = new Inflater();
        decompressor.setInput(contents);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[2048];

        while (!decompressor.finished()){
            int byteDecompressed = decompressor.inflate(buffer);
            outputStream.write(buffer, 0, byteDecompressed);
        }
        outputStream.close();
        decompressor.end();
        return outputStream.toByteArray();
    }

    public static String decompressToString(Path path) throws IOException, DataFormatException {
        byte[] compressedData = Files.readAllBytes(path);
        byte[] decompressedData = decompress(compressedData);
        return new String(decompressedData);
    }
}
