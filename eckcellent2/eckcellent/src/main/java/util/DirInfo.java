package util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Extends FileInfo by allowing subsidiary directories and files.
 */
public class DirInfo extends FileInfo {
    private final Set<FileInfo> _subs = new LinkedHashSet<>();
    private boolean _subsLoaded = false;

    /**
     * Gets the FileInfos of immediate sub directories and files
     *
     * @return  copy of set of subsidiary FileInfos
     */
    @Nonnull
    public Set<FileInfo> getSubs() {
        if (!_subsLoaded) reloadSubs();

        return new LinkedHashSet<>(_subs);
    }

    /**
     * Populates the {@link DirInfo#_subs} field. Should only be called once unless the file system changes.
     */
    private void reloadSubs() {
        _subs.clear();

        File[] files = _file.listFiles();

        if (files != null) {
            for (File file : files) {
                FileInfo newInfo = file.isDirectory() ? new DirInfo(file) : new FileInfo(file);

                _subs.add(newInfo);
            }
        }

        _subsLoaded = true;
    }

    protected final List<BigInteger> _lengths = new ArrayList<>();
    private boolean _lengthsLoaded = false;

    /**
     * Recursively gets all lengths of subsidiary picture files.
     *
     * @return  list of lengths
     */
    @Nonnull
    public List<BigInteger> getLengths() {
        if (!_lengthsLoaded) reloadLengths();

        return new ArrayList<>(_lengths);
    }

    /**
     * Populates the {@link DirInfo#_lengths} field. Should only be called once unless the file system changes.
     */
    private void reloadLengths() {
        _lengths.clear();

        for (FileInfo fileInfo : getSubs()) {
            if (fileInfo instanceof DirInfo) _lengths.addAll(((DirInfo) fileInfo).getLengths());
            else {
                BigInteger length = fileInfo.getTotalLength();

                _lengths.add(length);
            }
        }

        _lengthsLoaded = true;
    }

    /**
     * Populates the {@link FileInfo#_totalLength} field. Should only be called once unless the file system changes.
     */
    @Override
    protected void reloadTotalLength() {
        _totalLength = getLengths().stream().reduce(BigInteger.ZERO, BigInteger::add);
        _totalLengthLoaded = true;
    }

    private final List<java.awt.Dimension> _dims = new ArrayList<>();
    private boolean _dimsLoaded = false;

    /**
     * Recursively gets all dims of subsidiary picture files.
     *
     * @return  list of dims
     */
    @Nonnull
    public List<java.awt.Dimension> getDims() {
        if (!_dimsLoaded) reloadDims();

        return new ArrayList<>(_dims);
    }

    /**
     * Populates the {@link DirInfo#_dims} field. Should only be called once unless the file system changes.
     */
    private void reloadDims() {
        _dims.clear();

        for (FileInfo fileInfo : getSubs()) {
            if (fileInfo instanceof DirInfo) _dims.addAll(((DirInfo) fileInfo).getDims());
            else {
                java.awt.Dimension dim = fileInfo.getAvgDim();

                if (dim != null) _dims.add(dim);
            }
        }

        _dimsLoaded = true;
    }

    /**
     * Populates the {@link FileInfo#_avgDim} field. Should only be called once unless file system changes.
     */
    @Override
    protected void reloadAvgDim() {
        List<java.awt.Dimension> dims = getDims();

        if (dims.isEmpty()) {
            _avgDim = null;

            return;
        }

        List<BigDecimal> widths = new ArrayList<>();
        List<BigDecimal> heights = new ArrayList<>();

        for (java.awt.Dimension dim : dims) {
            widths.add(BigDecimal.valueOf(dim.getWidth()));
            heights.add(BigDecimal.valueOf(dim.getHeight()));
        }

        BigDecimal totalWidth = widths.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalHeight = heights.stream().reduce(BigDecimal.ZERO, BigDecimal::add);

        java.awt.Dimension avgDim = new java.awt.Dimension(0, 0);

        avgDim.setSize(
                totalWidth.divide(BigDecimal.valueOf(widths.size()), BigDecimal.ROUND_FLOOR).doubleValue(),
                totalHeight.divide(BigDecimal.valueOf(heights.size()), BigDecimal.ROUND_FLOOR).doubleValue()
        );

        _avgDim = avgDim;
        _avgDimLoaded = true;
    }

    private final List<Double> _compressions = new ArrayList<>();
    private boolean _compressionsLoaded = false;

    /**
     * Recursively gets all compression rates of subsidiary picture files.
     *
     * @return  list of compression rates
     */
    @Nonnull
    public List<Double> getCompressions() {
        if (!_compressionsLoaded) reloadCompressions();

        return new ArrayList<>(_compressions);
    }

    /**
     * Populates the {@link DirInfo#_compressions} field. Should only be called once unless file system changes.
     */
    private void reloadCompressions() {
        _compressions.clear();

        for (FileInfo fileInfo : getSubs()) {
            if (fileInfo instanceof DirInfo) _compressions.addAll(((DirInfo) fileInfo).getCompressions());
            else {
                Double compression = fileInfo.getAvgCompression();

                if (compression != null) _compressions.add(compression);
            }
        }

        _compressionsLoaded = true;
    }

    /**
     * Populates the {@link FileInfo#_avgCompression} field. Should only be called once unless file system changes.
     */
    @Override
    protected void reloadAvgCompression() {
        List<Double> compressions = getCompressions();

        if (compressions.isEmpty()) {
            _avgCompression = null;

            return;
        }

        _avgCompression = compressions.stream().mapToDouble(Double::doubleValue).sum() / compressions.size();
        _avgCompressionLoaded = true;
    }

    /**
     * Creates a new DirInfo from a given file path.
     *
     * @param file  file system path
     */
    public DirInfo(@Nonnull File file) {
        super(file);
    }
}
