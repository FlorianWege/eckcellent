package util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.math.BigInteger;

/**
 * Exposes attributes for a given file on the file system.
 */
public class FileInfo {
    @Nonnull
    protected final File _file;

    /**
     * Accessor method for the assigned file/directory path.
     *
     * @return  assigned file
     */
    @Nonnull
    public File getFile() {
        return _file;
    }

    private int _fileType;
    private boolean _fileTypeLoaded;

    /**
     * Gets the type of the file.
     *
     * @return  file type as indicated by {@link PictureInfo#DIRECTORY} et seq.
     */
    public int getFileType() {
        if (!_fileTypeLoaded) {
            _fileType = new PictureInfo().getFileType(_file);

            _fileTypeLoaded = true;
        }

        return _fileType;
    }

    /**
     * Determines if the assigned file is a picture file.
     *
     * @return  true if the file is a picture.
     */
    public boolean isPictureFile() {
        int fileType = getFileType();

        return (fileType == PictureInfo.JPEG_PICTURE || fileType == PictureInfo.GIF_PICTURE);
    }

    @Nonnull
    protected BigInteger _totalLength;
    protected boolean _totalLengthLoaded;
    @Nullable
    protected java.awt.Dimension _avgDim;
    protected boolean _avgDimLoaded;
    @Nullable
    protected Double _avgCompression;
    protected boolean _avgCompressionLoaded;

    /**
     * Gets the total length of underlying picture file(s).
     *
     * @return  length in bytes, null if neither a picture nor a directory containing pictures
     */
    @Nonnull
    public BigInteger getTotalLength() {
        if (!_totalLengthLoaded) reloadTotalLength();

        return _totalLength;
    }

    /**
     * Populates the {@link FileInfo#_totalLength} field. Should only be called once unless the file system changes.
     */
    protected void reloadTotalLength() {
        _totalLength = BigInteger.valueOf(new PictureInfo().getFileSize(_file));

        _totalLengthLoaded = true;
    }

    /**
     * Gets the average dimensions of underlying picture file(s).
     *
     * @return  dimensions, null if neither a picture nor a directory containing pictures
     */
    @Nullable
    public java.awt.Dimension getAvgDim() {
        if (!_avgDimLoaded) reloadAvgDim();

        return _avgDim;
    }

    /**
     * Populates the {@link FileInfo#_avgDim} field. Should only be called once unless the file system changes.
     */
    protected void reloadAvgDim() {
        _avgDim = new PictureInfo().getDim(_file);

        _avgDimLoaded = true;
    }

    /**
     * Gets the average compression rate of underlying picture file(s).
     *
     * @return  compression rate in %, null if neither a picture nor a directory containing pictures
     */
    @Nullable
    public Double getAvgCompression() {
        if (!_avgCompressionLoaded) reloadAvgCompression();

        return _avgCompression;
    }

    /**
     * Populates the {@link FileInfo#_avgCompression} field. Should only be called once unless the file system changes.
     */
    protected void reloadAvgCompression() {
        _avgCompression = new PictureInfo().getCompression(_file);

        _avgCompressionLoaded = true;
    }

    /**
     * Creates a new FileInfo from a given file path.
     *
     * @param file  file system path
     */
    public FileInfo(@Nonnull File file) {
        _file = file;
    }
}
