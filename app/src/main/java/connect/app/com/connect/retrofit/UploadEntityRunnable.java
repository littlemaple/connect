//package connect.app.com.connect.retrofit;
//
//import android.text.TextUtils;
//
//import com.medzone.framework.Log;
//import com.medzone.framework.data.bean.Account;
//import com.medzone.framework.network.NetworkClientResult;
//import com.medzone.framework.util.Args;
//import com.medzone.mcloud.data.bean.dbtable.UploadEntity;
//import com.medzone.mcloud.data.bean.java.UploadBlockEntity;
//import com.medzone.mcloud.errorcode.CloudStatusCodeProxy.LocalCode;
//import com.medzone.mcloud.network.NetworkClient;
//
//import org.apache.http.entity.InputStreamEntity;
//
//import java.io.File;
//import java.io.IOException;
//
//
//public class UploadEntityRunnable implements Runnable {
//
//    static final String tag = "UploadEntityRunnable";
//    static final int UPLOAD_FAILED_RETRY_TIMES = 3;
//
//    // Constants for indicating the state of the download
//    static final int HTTP_STATE_FAILED = -1;
//    static final int HTTP_STATE_STARTED = 0;
//    static final int HTTP_STATE_COMPLETED = 1;
//
//    final TaskRunnableUploadMethods mUploadTask;
//
//    interface TaskRunnableUploadMethods {
//
//        void setUploadThread(Thread currentThread);
//
//        void handleUploadState(int state);
//
//    }
//
//    public UploadEntityRunnable(TaskRunnableUploadMethods uploadTask) {
//        this.mUploadTask = uploadTask;
//    }
//
//    @Override
//    public void run() {
//
//        mUploadTask.setUploadThread(Thread.currentThread());
//        // Moves the current Thread into the background
//        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
//
//        UploadEntity parentEntity = mUploadTask.getEntity();
//
//        Args.notNull(parentEntity, "entity");
//        final File file = parentEntity.getFile();
//        Args.notNull(file, "file");
//        final String fileName = parentEntity.getFileName();
//        Args.notNull(fileName, "fileName");
//        final long fileSize = file.length();
//        Args.notNull(fileSize, "fileSize");
//
//        UploadBlockEntity childEntity = null;
//        Log.i(tag, "UploadEntity--->fileName:" + parentEntity.getFileName() + ",path:" + parentEntity.getLocalFilePath());
//
//        try {
//            if (Thread.interrupted()) {
//                throw new InterruptedException();
//            }
//            mUploadTask.handleUploadState(HTTP_STATE_STARTED);
//
//            upload:
//            do {
//
//                if (childEntity == null) {
//                    Log.i(tag, "初次请求，在服务端开辟存储空间并获取请求URL");
//                    childEntity = requestBlockEntity(parentEntity);
//                }
//
//                if (Thread.interrupted()) {
//                    throw new InterruptedException();
//                }
//
//                if (!checkBlockEntity(childEntity)) {
//                    throw new InterruptedException("分块不可用，通常是分块为Null或者未获取到分块数据结构");
//                }
//
//                // 标记本地大文件上传已经结束
//                if (parentEntity.getState() == UploadEntity.STATE_ACCEPTED) {
//                    break upload;
//                }
//
//                parentEntity.updateNextBlockEntity(childEntity);
//
//                if (Thread.interrupted()) {
//                    throw new InterruptedException();
//                }
//
//                try {
//                    Log.i(tag, "执行上传本块：" + childEntity.getRequestPath());
//                    childEntity = requestUploadBlockEntity(parentEntity, childEntity);
//                } catch (IOException e) {
//                    Log.w(tag, "分块上传失败");
//                }
//
//                if (null == childEntity) {
//                    throw new InterruptedException("连续三次未获取到下一分块，自动取消本地上传任务。");
//                }
//
//            }
//            while (true);
//
//            Log.v(tag, "大文件已上传完成");
//
//            mUploadTask.handleUploadState(HTTP_STATE_COMPLETED);
//
//        } catch (InterruptedException e) {
//            Log.w(tag, "" + e.getMessage());
//        } finally {
//
//            if (parentEntity.getState() != UploadEntity.STATE_ACCEPTED) {
//                Log.v(tag, "整块文件上传失败！");
//                mUploadTask.handleUploadState(HTTP_STATE_FAILED);
//            } else {
//                Log.v(tag, "整块文件上传成功！");
//            }
//            // Sets the reference to the current Thread to null, releasing its
//            // storage
//            mUploadTask.setUploadThread(null);
//            // Clears the Thread's interrupt flag
//            Thread.interrupted();
//        }
//
//    }
//
//    private UploadBlockEntity requestBlockEntity(UploadEntity parentEntity) throws InterruptedException {
//
//        Args.notNull(parentEntity, "parentEntity");
//        Args.notNull(parentEntity.getFileName(), "filename");
//        Args.notNull(parentEntity.getFile(), "file");
//        Account account = UploadManager.getInstance().getAttachAccount();
//        if (account == null) {
//            throw new InterruptedException();
//        }
//        int retryTime = 0;
//
//        while (true) {
//            retryTime++;
//            Log.i(tag, "requestBlockEntity,net request--->accessToken:" + account.getAccessToken() + ",fileName:" + parentEntity.getFileName() + ",fileSize:" + parentEntity.getFile().length());
//            NetworkClientResult res = (NetworkClientResult) NetworkClient.getInstance().requestStorageSpace(account.getAccessToken(), parentEntity.getFileName(), parentEntity.getFile().length());
//            Log.i(tag, "requestBlockEntity,net response--->code:" + res.getErrorCode() + ",message:" + res.getErrorMessage());
//            switch (res.getErrorCode()) {
//                case LocalCode.CODE_SUCCESS:
//                    UploadBlockEntity blockEntity = UploadBlockEntity.parseBlockEntity(parentEntity, res.getResponseResult());
//                    if (blockEntity.getParentEntity().getState() != UploadEntity.STATE_ACCEPTED) {
//                        Log.i(tag, "初始化下个分块[" + blockEntity.getOffset() + "]！");
//                    }
//                    return blockEntity;
//                default:
//                    if (retryTime >= UPLOAD_FAILED_RETRY_TIMES) {
//                        Log.w(tag, "重试：" + retryTime + "次，无法上传成功。");
//                        return null;
//                    }
//                    Log.i(tag, "重试：" + retryTime + "次");
//                    break;
//            }
//        }
//    }
//
//    private UploadBlockEntity requestUploadBlockEntity(UploadEntity parentEntity, UploadBlockEntity blockEntity) throws IOException {
//
//        Args.notNull(blockEntity, "blockEntity");
//        int retryTime = 0;
//        InputStreamEntity entity = null;
//
//        entity = blockEntity.getBlockEntity();
//
//        while (true) {
//            retryTime++;
//            Log.i(tag, "requestUploadBlockEntity,net request--->blockPath:" + blockEntity.getRequestPath() + ",blockEntity.getMethod():" + blockEntity.getMethod() + ",fileSize:"
//                    + parentEntity.getFile().length());
//            NetworkClientResult res = (NetworkClientResult) NetworkClient.getInstance().requestUploadFile(blockEntity.getRequestPath(), blockEntity.getMethod(), entity);
//            Log.i(tag, "requestUploadBlockEntity,net response--->" + res.getErrorCode() + "," + res.getErrorMessage());
//            switch (res.getErrorCode()) {
//                case LocalCode.CODE_SUCCESS:
//                    Log.i(tag, "分块[" + blockEntity.getOffset() + "]上传成功！");
//                    UploadBlockEntity uploadEntity = UploadBlockEntity.parseBlockEntity(parentEntity, res.getResponseResult());
//                    if (blockEntity.getParentEntity().getState() != UploadEntity.STATE_ACCEPTED) {
//                        Log.i(tag, "初始化下个分块[" + blockEntity.getOffset() + "]！");
//                    }
//                    return uploadEntity;
//                default:
//
//                    if (retryTime >= UPLOAD_FAILED_RETRY_TIMES) {
//                        Log.w(tag, "重试：" + retryTime + "次，无法上传成功。");
//                        return null;
//                    }
//                    Log.i(tag, "重试：" + retryTime + "次");
//                    break;
//            }
//        }
//
//    }
//
//    /**
//     * 检查分块实体是否有效
//     *
//     * @param entity 分块实体
//     * @return
//     */
//    private boolean checkBlockEntity(UploadBlockEntity entity) {
//
//        if (entity == null) return false;
//
//        Args.notNegative(entity.getOffset(), "offset");
//        Args.notNegative(entity.getLength(), "length");
//
//        if (TextUtils.isEmpty(entity.getRequestPath())) {
//            return false;
//        }
//        return true;
//    }
//
//}
