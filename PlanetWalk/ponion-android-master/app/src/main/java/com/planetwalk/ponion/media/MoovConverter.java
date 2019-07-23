package com.planetwalk.ponion.media;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

public class MoovConverter {
    public static boolean sDEBUG = false;

    private static void safeClose(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                printe(e, "Failed to close file: ");
            }
        }
    }

    static long uint32ToLong(int int32) {
        return int32 & 0x00000000ffffffffL;
    }

    static int uint32ToInt(int uint32) throws UnsupportedFileException {
        if (uint32 < 0) {
            throw new UnsupportedFileException("uint32 value is too large");
        }
        return uint32;
    }

    static int uint32ToInt(long uint32) throws UnsupportedFileException {
        if (uint32 > Integer.MAX_VALUE || uint32 < 0) {
            throw new UnsupportedFileException("uint32 value is too large");
        }
        return (int) uint32;
    }

    static long uint64ToLong(long uint64) throws UnsupportedFileException {
        if (uint64 < 0) throw new UnsupportedFileException("uint64 value is too large");
        return uint64;
    }

    private static int fourCcToInt(byte[] byteArray) {
        return ByteBuffer.wrap(byteArray).order(ByteOrder.BIG_ENDIAN).getInt();
    }

    private static void printf(String format, Object... args) {
        if (sDEBUG) System.err.println("Mp4Converter: " + String.format(format, args));
    }

    private static void printe(Throwable e, String format, Object... args) {
        printf(format, args);
        if (sDEBUG) e.printStackTrace();
    }

    private static boolean readAndFill(FileChannel infile, ByteBuffer buffer) throws IOException {
        buffer.clear();
        int size = infile.read(buffer);
        buffer.flip();
        return size == buffer.capacity();
    }

    private static boolean readAndFill(FileChannel infile, ByteBuffer buffer, long position) throws IOException {
        buffer.clear();
        int size = infile.read(buffer, position);
        buffer.flip();
        return size == buffer.capacity();
    }

    /* top level atoms */
    private static final int FREE_ATOM = fourCcToInt(new byte[]{'f', 'r', 'e', 'e'});
    private static final int JUNK_ATOM = fourCcToInt(new byte[]{'j', 'u', 'n', 'k'});
    private static final int MDAT_ATOM = fourCcToInt(new byte[]{'m', 'd', 'a', 't'});
    private static final int MOOV_ATOM = fourCcToInt(new byte[]{'m', 'o', 'o', 'v'});
    private static final int PNOT_ATOM = fourCcToInt(new byte[]{'p', 'n', 'o', 't'});
    private static final int SKIP_ATOM = fourCcToInt(new byte[]{'s', 'k', 'i', 'p'});
    private static final int WIDE_ATOM = fourCcToInt(new byte[]{'w', 'i', 'd', 'e'});
    private static final int PICT_ATOM = fourCcToInt(new byte[]{'P', 'I', 'C', 'T'});
    private static final int FTYP_ATOM = fourCcToInt(new byte[]{'f', 't', 'y', 'p'});
    private static final int UUID_ATOM = fourCcToInt(new byte[]{'u', 'u', 'i', 'd'});

    private static final int CMOV_ATOM = fourCcToInt(new byte[]{'c', 'm', 'o', 'v'});
    private static final int STCO_ATOM = fourCcToInt(new byte[]{'s', 't', 'c', 'o'});
    private static final int CO64_ATOM = fourCcToInt(new byte[]{'c', 'o', '6', '4'});

    private static final int ATOM_PREAMBLE_SIZE = 8;

    public static boolean fastStart(InputStream in, FileOutputStream out) throws IOException, MalformedFileException, UnsupportedFileException {
        boolean ret = false;
        FileInputStream inStream = (FileInputStream) in;
        FileChannel infile = null;
        FileChannel outfile = null;
        try {
            infile = inStream.getChannel();
            outfile = out.getChannel();
            return ret = fastStartImpl(infile, outfile);
        } finally {
            if (!ret) {
                infile.transferTo(0, infile.size(), outfile);
            }
            safeClose(inStream);
            safeClose(out);
        }
    }

    /**
     * @param in  Input file.
     * @param out Output file.
     * @return false if input file is already fast start.
     * @throws IOException
     */
    public static boolean fastStart(String in, String out) throws IOException, MalformedFileException, UnsupportedFileException {
        FileInputStream inputStream = new FileInputStream(in);
        FileOutputStream outputStream = new FileOutputStream(out);
        return fastStart(inputStream, outputStream);
    }

    private static boolean fastStartImpl(FileChannel infile, FileChannel outfile) throws IOException, MalformedFileException, UnsupportedFileException {
        ByteBuffer atomBytes = ByteBuffer.allocate(ATOM_PREAMBLE_SIZE).order(ByteOrder.BIG_ENDIAN);
        int atomType = 0;
        long atomSize = 0;
        long lastOffset;
        ByteBuffer moovAtom;
        ByteBuffer ftypAtom = null;
        int moovAtomSize;
        long startOffset = 0;
        while (readAndFill(infile, atomBytes)) {
            atomSize = uint32ToLong(atomBytes.getInt());
            atomType = atomBytes.getInt();
            if (atomType == FTYP_ATOM) {
                int ftypAtomSize = uint32ToInt(atomSize);
                ftypAtom = ByteBuffer.allocate(ftypAtomSize).order(ByteOrder.BIG_ENDIAN);
                atomBytes.rewind();
                ftypAtom.put(atomBytes);
                if (infile.read(ftypAtom) < ftypAtomSize - ATOM_PREAMBLE_SIZE) break;
                ftypAtom.flip();
                startOffset = infile.position();
            } else {
                if (atomSize == 1) {
                    atomBytes.clear();
                    if (!readAndFill(infile, atomBytes)) break;
                    atomSize = uint64ToLong(atomBytes.getLong());
                    infile.position(infile.position() + atomSize - ATOM_PREAMBLE_SIZE * 2);
                } else {
                    infile.position(infile.position() + atomSize - ATOM_PREAMBLE_SIZE);
                }
            }
            if (sDEBUG) printf("%c%c%c%c %10d %d",
                    (atomType >> 24) & 255,
                    (atomType >> 16) & 255,
                    (atomType >> 8) & 255,
                    (atomType >> 0) & 255,
                    infile.position() - atomSize,
                    atomSize);
            if ((atomType != FREE_ATOM)
                    && (atomType != JUNK_ATOM)
                    && (atomType != MDAT_ATOM)
                    && (atomType != MOOV_ATOM)
                    && (atomType != PNOT_ATOM)
                    && (atomType != SKIP_ATOM)
                    && (atomType != WIDE_ATOM)
                    && (atomType != PICT_ATOM)
                    && (atomType != UUID_ATOM)
                    && (atomType != FTYP_ATOM)) {
                break;
            }

            if (atomSize < 8)
                break;
        }
        if (atomType != MOOV_ATOM) {
            return false;
        }
        moovAtomSize = uint32ToInt(atomSize);
        lastOffset = infile.size() - moovAtomSize; // NOTE: assuming no extra data after moov, as qt-faststart.c
        moovAtom = ByteBuffer.allocate(moovAtomSize).order(ByteOrder.BIG_ENDIAN);
        if (!readAndFill(infile, moovAtom, lastOffset)) {
            throw new MalformedFileException("failed to read moov atom");
        }

        if (moovAtom.getInt(12) == CMOV_ATOM) {
            throw new UnsupportedFileException("this utility does not support compressed moov atoms yet");
        }

        while (moovAtom.remaining() >= 8) {
            int atomHead = moovAtom.position();
            atomType = moovAtom.getInt(atomHead + 4); // representing uint32_t in signed int
            if (!(atomType == STCO_ATOM || atomType == CO64_ATOM)) {
                moovAtom.position(moovAtom.position() + 1);
                continue;
            }
            atomSize = uint32ToLong(moovAtom.getInt(atomHead)); // uint32
            if (atomSize > moovAtom.remaining()) {
                throw new MalformedFileException("bad atom size");
            }
            moovAtom.position(atomHead + 12); // skip size (4 bytes), type (4 bytes), version (1 byte) and flags (3 bytes)
            if (moovAtom.remaining() < 4) {
                throw new MalformedFileException("malformed atom");
            }
            int offsetCount = uint32ToInt(moovAtom.getInt());
            if (atomType == STCO_ATOM) {
                if (moovAtom.remaining() < offsetCount * 4) {
                    throw new MalformedFileException("bad atom size/element count");
                }
                for (int i = 0; i < offsetCount; i++) {
                    int currentOffset = moovAtom.getInt(moovAtom.position());
                    int newOffset = currentOffset + moovAtomSize; // calculate uint32 in int, bitwise addition
                    if (currentOffset < 0 && newOffset >= 0) {
                        throw new UnsupportedFileException("This is bug in original qt-faststart.c: "
                                + "stco atom should be extended to co64 atom as new offset value overflows uint32, "
                                + "but is not implemented.");
                    }
                    moovAtom.putInt(newOffset);
                }
            } else if (atomType == CO64_ATOM) {
                if (moovAtom.remaining() < offsetCount * 8) {
                    throw new MalformedFileException("bad atom size/element count");
                }
                for (int i = 0; i < offsetCount; i++) {
                    long currentOffset = moovAtom.getLong(moovAtom.position());
                    moovAtom.putLong(currentOffset + moovAtomSize); // calculate uint64 in long, bitwise addition
                }
            }
        }
        infile.position(startOffset);

        if (ftypAtom != null) {
            ftypAtom.rewind();
            outfile.write(ftypAtom);
        }

        moovAtom.rewind();
        outfile.write(moovAtom);

        infile.transferTo(startOffset, lastOffset - startOffset, outfile);
        return true;
    }

    public static class Mp4ConverterException extends Exception {
        private Mp4ConverterException(String detailMessage) {
            super(detailMessage);
        }
    }

    public static class MalformedFileException extends Mp4ConverterException{
        private MalformedFileException(String detailMessage) {
            super(detailMessage);
        }
    }

    public static class UnsupportedFileException extends Mp4ConverterException{
        private UnsupportedFileException(String detailMessage) {
            super(detailMessage);
        }
    }
}
