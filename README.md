# kotlin_tips

* 19.12.25: 增加 UpdateUtil。

  > ```kotlin
  >  val updateUtil = UpdateUtil(this@MainActivity, BuildConfig.APPLICATION_ID)
  > 
  >   updateUtil.setOnDownloadListener(object : UpdateUtilOnDownloadListener {
  >                             override fun onStart(downloadId: Long) {
  >                                 Log.i("MainActivity", "onStart: 下载开始")
  >                             }
  > 
  >                             override fun onDownloading(downloadId: Long, progress:Int) {
  >                                 Log.i("MainActivity", "onStart: 下载中：$progress")
  >                             }
  > 
  >                             override fun onSuccess(downloadId: Long) {
  >                                 Log.i("MainActivity", "onStart: 下载完成")
  >                             }
  > 
  >                             override fun onCancel(downloadId: Long) {
  >                                 Log.i("MainActivity", "onStart: 下载取消")
  >                             }
  > 
  >                         })
  > 
  >                         updateUtil.showNormalUpdateDialog(updateInfo)
  > ```



* 19.12.25 日志打印及生成日志文件

  > ```kotlin
  > KtNetLog.init(this,true)
  > KtUiLog.init(this, true)
  > ```



