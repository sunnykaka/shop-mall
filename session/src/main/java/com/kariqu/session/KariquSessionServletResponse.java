package com.kariqu.session;

import org.apache.commons.lang.time.FastDateFormat;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.Nullable;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.*;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 重新实现HttpServletResponse
 *
 * @author Tiger
 * @version 1.0.0
 * @since 11-5-17 下午6:46
 */
public class KariquSessionServletResponse extends HttpServletResponseWrapper {

    private static Log logger = LogFactory.getLog(KariquSessionServletResponse.class);

    private static final String HTTP_ONLY = "HttpOnly";

    private static final String SET_COOKIE = "Set-Cookie";

    private static final String COOKIE_SEPARATOR = ";";

    private static final String KEY_VALUE_SEPARATOR = "=";

    private static final String EXPIRES = "Expires";

    private static final String PATH = "Path";

    private static final String DOMAIN = "Domain";

    private static final TimeZone GMT_TIME_ZONE = TimeZone.getTimeZone("GMT");

    private static final String COOKIE_DATE_PATTERN = "EEE, dd-MMM-yyyy HH:mm:ss 'GMT'";

    private static final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance(COOKIE_DATE_PATTERN, GMT_TIME_ZONE,
            Locale.US);

    private static final String QUOTATION = "\"";

    /**
     * stream 和 writer不能两个同时不为空，这里单一个不为空的时候，调用另外一个需要进行适配
     */
    private BufferedServletOutputStream stream;

    private PrintWriter streamAdapter;   /*将stream适配到writer*/

    private BufferedServletWriter writer;

    private ServletOutputStream writerAdapter;  /*将writer适配到stream*/

    /**
     * 标记是否框架外面已经调用了flushBuffer，如果是则在提交的时候再一次刷新
     */
    private boolean flushed = false;

    /**
     * 标记是否需要缓存
     */
    private boolean isWriterBuffered = false;


    private KariquSession session;

    /*HTTP 状态码*/
    private int status;

    /*HTTP 重定向URL*/
    private String sendRedirect;

    private SendError sendError;

    /**
     * Constructs a response adaptor wrapping the given response.
     *
     * @param response
     * @throws IllegalArgumentException if the response is null
     */
    public KariquSessionServletResponse(HttpServletResponse response) {
        super(response);
    }


    public void addCookie(KariquCookie cookie) {
        if (cookie.isHttpOnly()) {
            addHeader(SET_COOKIE, buildHttpOnlyCookie(cookie));
        } else {
            super.addCookie(cookie);
        }
    }

    private String buildHttpOnlyCookie(KariquCookie cookie) {
        StringBuilder cookieBuilder = new StringBuilder();
        cookieBuilder.append(cookie.getName()).append(KEY_VALUE_SEPARATOR).append(QUOTATION).append(cookie.getValue()).append(QUOTATION);
        cookieBuilder.append(COOKIE_SEPARATOR);
        if (cookie.getDomain() != null) {
            cookieBuilder.append(DOMAIN).append(KEY_VALUE_SEPARATOR).append(cookie.getDomain());
            cookieBuilder.append(COOKIE_SEPARATOR);
        }
        if (cookie.getPath() != null) {
            cookieBuilder.append(PATH).append(KEY_VALUE_SEPARATOR).append(cookie.getPath());
            cookieBuilder.append(COOKIE_SEPARATOR);
        }
        if (cookie.getMaxAge() >= 0) {
            cookieBuilder.append(EXPIRES).append(KEY_VALUE_SEPARATOR).append(getCookieExpires(cookie));
            cookieBuilder.append(COOKIE_SEPARATOR);
        }
        cookieBuilder.append(HTTP_ONLY);
        return cookieBuilder.toString();

    }

    @Override
    public void sendError(int status) throws IOException {
        sendError(status, null);
    }

    @Override
    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public void sendError(int status, @Nullable String message) throws IOException {
        if ((sendError == null) && (sendRedirect == null)) {
            sendError = new SendError(status, message);
        }
    }

    @Override
    public void sendRedirect(String location) throws IOException {
        if ((sendError == null) && (sendRedirect == null)) {
            sendRedirect = location;
        }
    }

    @Override
    public void flushBuffer() throws IOException {
        flushBufferAdapter();
        if (writer != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("session framework flush writer");
            }
            writer.flush();
        }
        if (stream != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("session framework flush stream");
            }
            stream.flush();
        }
        this.flushed = true;
    }

    /**
     * 刷新buffer adapter，确保adapter中的信息被写入buffer中
     */
    private void flushBufferAdapter() {
        if (streamAdapter != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("session framework flush streamAdapter");
            }
            streamAdapter.flush();
        }
        if (writerAdapter != null) {
            try {
                if (logger.isDebugEnabled()) {
                    logger.debug("session framework flush writerAdapter");
                }
                writerAdapter.flush();
            } catch (IOException ignore) {
            }
        }
    }

    private String getCookieExpires(KariquCookie cookie) {
        int maxAge = cookie.getMaxAge();
        if (maxAge > 0) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.SECOND, maxAge);
            return DATE_FORMAT.format(calendar);
        } else { // maxAge == 0
            return DATE_FORMAT.format(0);
        }
    }

    public void commitBuffer() throws IOException {
        if (status > 0) {
            if (logger.isDebugEnabled()) {
                logger.debug("session framework commit : set status,status is : " + status);
            }
            super.setStatus(status);
        }
        if (sendError != null) {
            if (sendError.message == null) {
                if (logger.isDebugEnabled()) {
                    logger.debug("session framework commit : set sendError,error status is : " + sendError.getStatus());
                }
                super.sendError(sendError.status);
            } else {
                if (logger.isDebugEnabled()) {
                    logger.debug("session framework commit : set sendError,error status is : " + sendError.getStatus() + " and the message is ： " + sendError.getMessage());
                }
                super.sendError(sendError.status, sendError.message);
            }
        } else if (sendRedirect != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("session framework commit : set sendRedirect,redirect url is  : " + sendRedirect);
            }
            super.sendRedirect(sendRedirect);
        } else if (stream != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("session framework commit : commit stream to the Servlet output stream");
            }
            flushBufferAdapter();
            super.getOutputStream().write(this.stream.getBytes().toByteArray());
        } else if (writer != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("session framework commit : commit writer to the Servlet output stream");
            }
            flushBufferAdapter();
            super.getWriter().write(this.writer.getBufferedChars().toString());
        }
        /**
         * 如果容器或者MVC框架已经调用了flushBuffer,需要再次刷新
         */
        if (this.flushed) {
            if (logger.isDebugEnabled()) {
                logger.debug("session framework commit : call the servlet container flushBuffer");
            }
            super.flushBuffer();
        }
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (stream != null) {
            return stream;
        }
        if (writer != null) {
            /*如果getWriter方法已经被调用，则将writer转换成OutputStream
             这样做会增加少量额外的内存开销，但标准的servlet engine不会遇到这种情形，只有少数
             servlet engine需要这种做法（resin）*/
            if (writerAdapter != null) {
                return writerAdapter;
            } else {
                logger.warn("attempt to getOutputStream after calling getWriter.  This may cause unnecessary system cost.");
                writerAdapter = new WriterAdapterStream(writer, getCharacterEncoding());
                return writerAdapter;
            }
        }
        if (this.isWriterBuffered) {
            if (logger.isDebugEnabled()) {
                logger.debug("Created new byte buffer");
            }
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            stream = new BufferedServletOutputStream(bytes);
            return stream;
        } else {
            this.getSession().commit();
            return super.getOutputStream();
        }
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (writer != null) {
            return writer;
        }
        if (stream != null) {
            /*如果getOutputStream方法已经被调用，则将stream转换成PrintWriter。
             这样做会增加少量额外的内存开销，但标准的servlet engine不会遇到这种情形， 只有少数
             servlet engine需要这种做法（resin)*/
            if (streamAdapter != null) {
                return streamAdapter;
            } else {
                logger.warn("attempt to getWriter after calling getOutputStream.  This may cause unnecessary system cost.");
                streamAdapter = new PrintWriter(new OutputStreamWriter(stream, getCharacterEncoding()), true);
                return streamAdapter;
            }
        }
        if (this.isWriterBuffered) {
            if (logger.isDebugEnabled()) {
                logger.debug("krqResponse.getWriter(): Created new character buffer");
            }
            StringWriter chars = new StringWriter();
            writer = new BufferedServletWriter(chars);
            return writer;
        } else {
            this.getSession().commit();
            return super.getWriter();
        }
    }


    public void setSession(KariquSession session) {
        this.session = session;
    }

    public KariquSession getSession() {
        return session;
    }

    /**
     * 将outputstream流适配到writer流
     */
    private class WriterAdapterStream extends ServletOutputStream {
        private ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        private Writer writer;
        private String charset;

        public WriterAdapterStream(PrintWriter writer, String characterEncoding) {
            this.writer = writer;
            this.charset = (null == characterEncoding ? "ISO-8859-1" : characterEncoding);
        }

        @Override
        public void write(int b) throws IOException {
            buffer.write((byte) b);
        }

        @Override
        public void write(byte[] b) throws IOException {
            buffer.write(b, 0, b.length);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            buffer.write(b, off, len);
        }

        @Override
        public void flush() throws IOException {
            byte[] bytes = buffer.toByteArray();
            if (bytes.length > 0) {
                ByteArrayInputStream inputBytes = new ByteArrayInputStream(bytes, 0, bytes.length);
                InputStreamReader reader = new InputStreamReader(inputBytes, charset);
                io(reader, writer);
                writer.flush();
                buffer.reset();
            }
        }

        @Override
        public void close() throws IOException {
            this.flush();
        }

        private void io(Reader in, Writer out) throws IOException {
            char[] buffer = new char[8192];
            int amount;
            while ((amount = in.read(buffer)) >= 0) {
                out.write(buffer, 0, amount);
            }
        }
    }

    @Override
    public void resetBuffer() {
        flushBufferAdapter();
        if (stream != null) {
            stream.updateOutputStream(new ByteArrayOutputStream());
        }

        if (writer != null) {
            writer.updateWriter(new StringWriter());
        }
        super.resetBuffer();
    }

    /**
     * 设置isWriterBuffered模式，如果设置成<code>true</code>，表示将所有信息保存在内存中，否则直接输出到原始response中。
     * 此方法必须在<code>getOutputStream</code>和<code>getWriter</code>方法之前执行，否则将抛出<code>IllegalStateException</code>。     *
     *
     * @param isWriterBuffered 是否buffer内容
     * @throws IllegalStateException <code>getOutputStream</code>或<code>getWriter</code>方法已经被执行
     */
    public void setWriterBuffered(boolean isWriterBuffered) {
        if ((stream == null) && (writer == null)) {
            if (this.isWriterBuffered != isWriterBuffered) {
                this.isWriterBuffered = isWriterBuffered;
                if (logger.isDebugEnabled()) {
                    logger.debug("Set WriterBuffered " + (isWriterBuffered ? "on" : "off"));
                }
            }
        } else {
            if (this.isWriterBuffered != isWriterBuffered) {
                throw new IllegalStateException("Unable to change the isWriterBuffered mode since the getOutputStream() or getWriter() method has been called");
            }
        }
    }

    @Override
    public void setContentLength(int length) {
        if (!isWriterBuffered)
            super.setContentLength(length);
    }

    /**
     * 代表一个将内容保存在内存中的<code>PrintWriter</code>。
     */
    private static class BufferedServletWriter extends PrintWriter {
        public BufferedServletWriter(StringWriter chars) {
            super(chars);
        }

        public Writer getBufferedChars() {
            return this.out;
        }

        public void updateWriter(StringWriter chars) {
            this.out = chars;
        }

        public void close() {
            try {
                this.out.close();
            } catch (IOException ignore) {
            }

        }
    }

    private class BufferedServletOutputStream extends ServletOutputStream {

        private ByteArrayOutputStream bytes;

        public BufferedServletOutputStream(ByteArrayOutputStream bytes) {
            this.bytes = bytes;
        }

        public void updateOutputStream(ByteArrayOutputStream bytes) {
            this.bytes = bytes;
        }

        public ByteArrayOutputStream getBytes() {
            return this.bytes;
        }

        @Override
        public void write(int b) throws IOException {
            bytes.write((byte) b);
        }

        @Override
        public void write(byte[] b) throws IOException {
            write(b, 0, b.length);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            bytes.write(b, off, len);
        }

        @Override
        public void flush() throws IOException {
            bytes.flush();
        }

        @Override
        public void close() throws IOException {
            bytes.flush();
            bytes.close();
        }

    }


    private class SendError {
        public final int status;
        public final String message;

        public SendError(int status, String message) {
            this.status = status;
            this.message = message;
        }

        public int getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }
    }
}
