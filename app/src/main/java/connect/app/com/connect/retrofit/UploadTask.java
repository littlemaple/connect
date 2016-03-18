//package connect.app.com.connect.retrofit;
//
//
//public class UploadTask implements TaskRunnableUploadMethods {
//
//    private Runnable mUploadRunnable;
//    private Thread mCurrentThread;
//    private static UploadManager sUploadManager;
//
//    public UploadTask() {
//        mUploadRunnable = new UploadEntityRunnable(this);
//        sUploadManager = UploadManager.getInstance();
//    }
//
//    public void initUploadTask(UploadEntity entity) {
//        mEntity = entity;
//    }
//
//    public void recycle() {
//        mEntity = null;
//    }
//
//    /*
//     * Returns the Thread that this Task is running on. The method must first
//     * get a lock on a static field, in this case the ThreadPool singleton. The
//     * lock is needed because the Thread object reference is stored in the
//     * Thread object itself, and that object can be changed by processes outside
//     * of this app.
//     */
//    public Thread getCurrentThread() {
//        synchronized (sUploadManager) {
//            return mCurrentThread;
//        }
//    }
//
//    public void setCurrentThread(Thread thread) {
//        synchronized (sUploadManager) {
//            mCurrentThread = thread;
//        }
//    }
//
//    @Override
//    public void setUploadThread(Thread currentThread) {
//        setCurrentThread(currentThread);
//    }
//
//    @Override
//    public void handleUploadState(int state) {
//        int outState;
//
//        switch (state) {
//            case UploadEntityRunnable.HTTP_STATE_COMPLETED:
//                outState = UploadManager.UPLOAD_COMPLETE;
//                break;
//            case UploadEntityRunnable.HTTP_STATE_FAILED:
//                outState = UploadManager.UPLOAD_FAILED;
//                break;
//            default:
//                outState = UploadManager.UPLOAD_STARTED;
//                break;
//        }
//        handleState(outState);
//    }
//
//    @Override
//    public UploadEntity getEntity() {
//        return mEntity;
//    }
//
//    // Delegates handling the current state of the task to the PhotoManager
//    // object
//    void handleState(int state) {
//        sUploadManager.handleState(this, state);
//    }
//
//    Runnable getUploadRunnable() {
//        return mUploadRunnable;
//    }
//
//}
