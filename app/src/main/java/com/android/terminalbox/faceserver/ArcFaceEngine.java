package com.android.terminalbox.faceserver;

import android.util.Log;

import com.android.terminalbox.app.BaseApplication;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.enums.DetectFaceOrientPriority;
import com.arcsoft.face.enums.DetectMode;

public class ArcFaceEngine {
    private static final String TAG = "ArcFaceEngine";
    /**
     * 优先打开的摄像头，本界面主要用于单目RGB摄像头设备，因此默认打开前置
     */

    private static final int MAX_DETECT_NUM = 10;
    /**
     * 当FR成功，活体未成功时，FR等待活体的时间
     */
    private static final int WAIT_LIVENESS_INTERVAL = 100;
    /**
     * 失败重试间隔时间（ms）
     */
    private static final long FAIL_RETRY_INTERVAL = 1000;
    /**
     * 出错重试最大次数
     */
    private static final int MAX_RETRY_TIME = 3;

    /**
     * VIDEO模式人脸检测引擎，用于预览帧人脸追踪
     */
    private FaceEngine ftEngine;
    /**
     * 用于特征提取的引擎
     */
    private FaceEngine frEngine;
    /**
     * IMAGE模式活体检测引擎，用于预览帧人脸活体检测
     */
    private FaceEngine flEngine;

    private static ArcFaceEngine arcFaceEngine = null;
    public static ArcFaceEngine getInstance() {
        if (arcFaceEngine == null) {
            synchronized (FaceServer.class) {
                if (arcFaceEngine == null) {
                    arcFaceEngine = new ArcFaceEngine();
                }
            }
        }
        return arcFaceEngine;
    }

    private ArcFaceEngine() {
        ftEngine = new FaceEngine();
        int ftInitCode = ftEngine.init(BaseApplication.getInstance(), DetectMode.ASF_DETECT_MODE_VIDEO, DetectFaceOrientPriority.ASF_OP_0_ONLY,
                16, MAX_DETECT_NUM, FaceEngine.ASF_FACE_DETECT);

        frEngine = new FaceEngine();
        int frInitCode = frEngine.init(BaseApplication.getInstance(), DetectMode.ASF_DETECT_MODE_IMAGE, DetectFaceOrientPriority.ASF_OP_0_ONLY,
                16, MAX_DETECT_NUM, FaceEngine.ASF_FACE_RECOGNITION);

        flEngine = new FaceEngine();
        int flInitCode = flEngine.init(BaseApplication.getInstance(), DetectMode.ASF_DETECT_MODE_IMAGE, DetectFaceOrientPriority.ASF_OP_0_ONLY,
                16, MAX_DETECT_NUM, FaceEngine.ASF_LIVENESS);
        if(ftInitCode!=0 ||frInitCode!=0|| flInitCode!=0){
            Log.d(TAG, "initEngine: fail");
        }
    }

    public FaceEngine getFtEngine() {
        return ftEngine;
    }

    public void setFtEngine(FaceEngine ftEngine) {
        this.ftEngine = ftEngine;
    }

    public FaceEngine getFrEngine() {
        return frEngine;
    }

    public void setFrEngine(FaceEngine frEngine) {
        this.frEngine = frEngine;
    }

    public FaceEngine getFlEngine() {
        return flEngine;
    }

    public void setFlEngine(FaceEngine flEngine) {
        this.flEngine = flEngine;
    }

}
