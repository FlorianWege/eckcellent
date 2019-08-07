package util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.math.BigInteger;
import java.util.Locale;

public class Main {
    private int _nestDepth = -1;

    private String lengthToString(@Nullable BigInteger length) {
        if (length == null) return null;

        return String.format("%dkB", length.divide(BigInteger.valueOf(1024)).intValue());
    }

    private String dimToString(@Nullable java.awt.Dimension dim) {
        if (dim == null) return null;

        return (int) dim.getWidth() + "x" + (int) dim.getHeight();
    }

    private String compressionToString(@Nullable Double compression) {
        if (compression == null) return null;

        return String.format(Locale.US, "%.2f%%", compression);
    }

    private void print(@Nonnull FileInfo fileInfo) {
        _nestDepth++;
        String prefix = new String(new char[_nestDepth]).replaceAll("\0", "\t");

        if (fileInfo instanceof DirInfo) {
            System.out.println(prefix + fileInfo.getFile().getName()
                    + " (" + lengthToString(fileInfo.getTotalLength())
                    + ";" + dimToString(fileInfo.getAvgDim())
                    + ";" + compressionToString(fileInfo.getAvgCompression())
                    + ")");

            for (FileInfo sub : ((DirInfo) fileInfo).getSubs()) {
                print(sub);
            }
        } else {
            int fileType = new PictureInfo().getFileType(fileInfo.getFile());

            if (fileType == PictureInfo.JPEG_PICTURE || fileType == PictureInfo.GIF_PICTURE) {
                System.out.println(prefix + fileInfo.getFile().getName());

                System.out.println(prefix + "\t length: \t" + lengthToString(fileInfo.getTotalLength()));
                System.out.println(prefix + "\t dim: \t" + dimToString(fileInfo.getAvgDim()));
                System.out.println(prefix + "\t compression: \t" + compressionToString(fileInfo.getAvgCompression()));
            }
        }

        _nestDepth--;
    }

    public static void main(String[] args) {
        if (args.length < 1) throw new IllegalArgumentException("no path given");

        DirInfo fileInfo = new DirInfo(new File(args[0]));

        new Main().print(fileInfo);
    }
}
