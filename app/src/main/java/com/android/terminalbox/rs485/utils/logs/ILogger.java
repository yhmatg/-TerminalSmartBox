package com.android.terminalbox.rs485.utils.logs;

/**
 * Logger
 *
 * @author 徐诚聪
 * @version 1.0
 * @since 20/8/19 下午5:39
 */
public interface ILogger {
    boolean isShowLog = false;
    boolean isShowStackTrace = false;
    void showLog(boolean isShowLog);

    void showStackTrace(boolean isShowStackTrace);

    void d(String tag, String message);

    void info(String tag, String message);

    void warning(String tag, String message);

    void error(String tag, String message);

    void monitor(String message);

    boolean isMonitorMode();

    String getDefaultTag();
}