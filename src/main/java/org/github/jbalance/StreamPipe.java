package org.github.jbalance;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

/**
 * User: jjb
 * DateTime: 2013-05-15 06:01
 */
public class StreamPipe {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(StreamPipe.class);

    InputStream sourceIn;
    OutputStream sourceOut;
    InputStream destIn;
    OutputStream destOut;
    private List<StreamPipeFilter> filters;
    private List<StreamPipeListener> listeners;

    private byte[] inBuf,outBuf;
    private static final int BUFFER_SIZE = 8 * 1024;

    public StreamPipe(InputStream sourceIn, OutputStream sourceOut, InputStream destIn, OutputStream destOut) {
        this.sourceIn = sourceIn;
        this.sourceOut = sourceOut;
        this.destIn = destIn;
        this.destOut = destOut;

        this.inBuf = new byte[BUFFER_SIZE];
        this.outBuf = new byte[BUFFER_SIZE];

        this.filters = new LinkedList<StreamPipeFilter>();
        this.listeners = new LinkedList<StreamPipeListener>();
    }

    public void pipe() throws StreamException {
        pipeInput();
        pipeOutput();
    }

    public void pipeInput() throws StreamException {
        doPipe(sourceIn, destOut, true);
    }

    public void pipeOutput() throws StreamException {
        doPipe(destIn, sourceOut, false);
    }

    private void doPipe(InputStream input, OutputStream output, boolean isFromSource ) throws StreamException {
        int size = 0;
        byte[] inBuf = isFromSource? this.inBuf: this.outBuf;

        try {
            size = input.read(inBuf, 0, inBuf.length);
        } catch (IOException e) {
            logger.error("read error", e);
            throw new StreamException("read error", e);
        }

        if( size > 0 ) {
            for(StreamPipeListener listener: listeners){
                logger.trace("try listen input by {}", listener);
                if( isFromSource )
                    listener.handleSourceInput(inBuf, 0, size);
                else
                    listener.handleDestOutput(inBuf, 0, size);
                logger.trace("input listened by {}", listener);
            }

            boolean handled = false;
            for(StreamPipeFilter filter: filters){
                logger.trace("try handle input by {}", filter);
                try{
                    boolean result = isFromSource? filter.handleSourceInput(inBuf, 0, size, input, output)
                            :filter.handleDestOutput(inBuf, 0, size, input, output);
                    if(result){
                        logger.debug("input handled by {}", filter);
                        handled = true;
                        break;
                    }
                } catch (IOException e) {
                    logger.error("handle input error", e);
                }
            }

            if( ! handled )
                logger.debug("no filter handle input, then write by StreamPipe");
            try {
                output.write(inBuf, 0, size);
            } catch (IOException e) {
                logger.error("transfer error", e);
                Utils.flush(output);
                throw new StreamException(e);
            }
        }
    }

    public void addFilter(StreamPipeFilter filter){
        filters.add(filter);
    }

    public void addListener(StreamPipeListener filter){
        listeners.add(filter);
    }

    /**
     * filter and change input & output stream
     */
    public static interface StreamPipeFilter {
        /**
         * handle input stream, if handled then return true, the rest filters will be skipped.
         *
         * @param inBuf
         * @param start
         * @param length
         * @param sourceIn
         * @param destOut
         * @return
         */
        boolean handleSourceInput(byte[] inBuf, int start, int length, InputStream sourceIn, OutputStream destOut) throws IOException;

        /**
         * handle output stream, if handled then return true, the rest filters will be skipped.
         * @param outBuf
         * @param start
         * @param length
         * @param destIn
         * @param sourceOut
         * @return
         */
        boolean handleDestOutput(byte[] outBuf, int start, int length, InputStream destIn, OutputStream sourceOut)throws IOException;
    }

    /**
     * listenning input & output stream
     */
    public static interface StreamPipeListener {
        void handleSourceInput(final byte[] inBuf, int start, int length);
        void handleDestOutput(final byte[] outBuf, int start, int length);
    }

}
