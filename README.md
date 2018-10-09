README!!😴 OR-> Read The Fucking 👨‍💻‍Source Code //☀️ 🌧 ⚡️ 🌪 🌫 ❄️...

[download_demo_apk]: https://raw.githubusercontent.com/jcodeing/KMedia/release/demo/demo_release.apk  "Click Download KMedia Demo 4.9 MB"

[![KMedia-Logo](/readme/icon/kmedia_logo.svg)][download_demo_apk]

一个为Android打造的应用级媒体框架, 它可以助你快速搭建媒体应用.
内部重新定义Android MediaPlayer API并对其封装, 简化和扩展一些原生API不支持的功能.
其中涵盖了, `AB播放/循环` `位置单元/间隔/循环` `变速播放` `媒体队列管理` `媒体服务/绑定` `音频后台/通知栏控制`
`媒体按键自定义处理` `音频焦点管理` `媒体引擎切换/扩展`... 等功能的快速实现.
以及, 对视频播放实现方面的封装. 其中将视频视图主要分为: 绘制层 控制组 控制层, 三个部分.
从而能够快速并灵活的实现Video相关应用的大部分功能, 包括 `视频浮窗/拖动/调整位置大小` `横竖屏自动切换` `全屏锁定`
`手势调整亮度/音量/进度` `字幕/切换/拖动` `视频段落/间隔复读` `视频续集/列表/循环播放` `动态切换视频控制层`
`控制层分离`... 等功能的快速实现.

## 使用
KMedia框架可以直接从JCenter添加依赖, 或者以子模块的形式添加到工程后再依赖.

### 从JCenter添加依赖 `快捷方便`
[![KMedia-Core](/readme/icon/kmedia_core_release.svg)](https://bintray.com/jcodeing/kmedia/kmedia-core/_latestVersion) [![KMedia-Uie](/readme/icon/kmedia_uie_release.svg)](https://bintray.com/jcodeing/kmedia/kmedia-uie/_latestVersion) [![KMedia-Exo](/readme/icon/kmedia_exo_release.svg)](https://bintray.com/jcodeing/kmedia/kmedia-exo/_latestVersion)
```gradle
compile 'com.jcodeing:kmedia-core:r1.0.10' //KMedia核心模块
compile 'com.jcodeing:kmedia-uie:r1.0.10' //KMedia界面扩展模块 (可选)
compile 'com.jcodeing:kmedia-exo:r1.0.10' //KMedia媒体引擎扩展模块 (可选)
```

### 添加Submodule到工程后再依赖 `自定义强`
[![KMedia-Core-Fork](/readme/icon/kmedia_core_fork.svg)](https://github.com/jcodeing/KMedia-Core/fork) [![KMedia-Uie-Fork](/readme/icon/kmedia_uie_fork.svg)](https://github.com/jcodeing/KMedia-Uie/fork) [![KMedia-Exo-Fork](/readme/icon/kmedia_exo_fork.svg)](https://github.com/jcodeing/KMedia-Mpe/fork)

#### Step 1: Fork(↑)模块仓库到你的Github.
当然, 你也可以Fork我的仓库到你的私人remote仓库.  
这步主要是为了生成一个remote仓库地址.

#### Step 2: 在你工程的根目录, 用 `git submodule` 命令添加子模块.
```sh
git submodule add "Your KMedia-Core Remote Repositorie Path" kmedia-core
git submodule add "Your KMedia-Uie Remote Repositorie Path" kmedia-uie
git submodule add "Your KMedia-Mpe Remote Repositorie Path" kmedia-mpe
```

#### Step 3: 根据上面所添加的子模块配置你工程的 `settings.gradle` 文件.
```gradle
include ':kmedia-core'
include ':kmedia-uie'
include ':kmedia-exo'
project(':kmedia-exo').projectDir = new File(settingsDir, 'kmedia-mpe/exo')
```

#### Step 4: 经过上面的添加配置步骤后, 你就可以在本地依赖并随时开发自定义KMedia的各个模块.
```gradle
compile project(':kmedia-core')
compile project(':kmedia-uie')
compile project(':kmedia-exo')
```

## 演示
[![Demo-Screen-Record-1](https://raw.githubusercontent.com/jcodeing/KMedia/release/demo/demo_sr_1.gif)][download_demo_apk][![Demo-Ui](https://raw.githubusercontent.com/jcodeing/KMedia/release/demo/demo_ui.gif)][download_demo_apk][![Demo-Screen-Record-2](https://raw.githubusercontent.com/jcodeing/KMedia/release/demo/demo_sr_2.gif)][download_demo_apk]  
[===============================点击下载 KMedia Demo 4.9 MB===============================][download_demo_apk]
```
k, 2017-9-24, PrivateKeyEntry,
证书指纹 (SHA1): 43:79:8C:03:1A:8A:2A:E8:CC:CA:D4:E3:63:9A:0F:70:29:C4:69:9B
```
### Example 1: 简单的视频播放
首先在Layout中添加PlayerView.
```xml
<com.jcodeing.kmedia.video.PlayerView
   android:id="@id/k_player_view"
   android:layout_width="match_parent"
   android:layout_height="200dp"/>
```
然后在Activity中将Player设置给PlayerView即可去播放.
```java
Player player = new Player(context).init(new AndroidMediaPlayer());
((PlayerView) findViewById(R.id.k_player_view)).setPlayer(player);
player.play(Uri.parse("video"));
```
最后当你确定不再播放时, 请调用以下方法去释放资源对象.
```java
playerView.finish();
player.shutdown();
```

### Example 2: 简单的视频浮窗
把Player交给VideoFloatingWindowController去显示即可浮屏播放.
```java
Player player = new Player(context).init(new ExoMediaPlayer(context));
new VideoFloatingWindowController(getApplicationContext()).show(player);
layer.play(Uri.parse("video"));
```
最后当你确定不再播放时, 请调用以下方法去释放资源对象.
```java
player.shutdown();
vFloatingWinControler.hide();
```
温馨提示,请根据你的具体使用需求合理申请WINDOW权限. [点击查看源码片段](https://github.com/jcodeing/KMedia-Core/blob/release/src/main/java/com/jcodeing/kmedia/window/FloatingWindowController.java#L127-L135)
```java
if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT || OS.i().isMIUI()) {
  //<!--Using WindowManager.LayoutParams.TYPE_PHONE For Floating　Window　View-->
  //<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW"/>
  layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;//2002
} else {
  layoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;//2005
}
```

### Example 3: 简单的视频全屏
首先在AndroidManifest文件中, 添加configChanges 到你的Activity.
```xml
<activity
  android:configChanges="orientation|keyboardHidden|screenLayout|screenSize"
</activity>
```
然后在Activity中覆写onConfigurationChanged, 并在方法内处理横竖屏切换时需要隐藏和显示的View.
```java
@Override
public void onConfigurationChanged(Configuration newConfig) {
  super.onConfigurationChanged(newConfig);
  //Dispose view in control layer switch
  if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
    other.setVisibility(View.GONE);
  } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
    other.setVisibility(View.VISIBLE);
  }
}
```
最后调用playerView启用方向助手即可实现简单的横竖屏自动切换.
```java
playerView.setOrientationHelper(this, 1);//enable sensor
```

### Example 4: 简单的使用控制层
ControlLayerView在Layout中的简单使用.
```xml
<com.jcodeing.kmedia.video.PlayerView
  android:id="@id/k_player_view"
  android:layout_width="match_parent"
  android:layout_height="200dp">
  <!--app:use_gesture_detector="true"-->
  <!--可以开启简单的手势,上下滑动调整音量,左右滑动调整进度,双击播放/暂停等-->
  <com.jcodeing.kmedia.video.ControlLayerView
    android:id="@id/k_ctrl_layer_port"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:use_part_top="true">
    <!--使用KID @id/k_ctrl_layer_port 可以标识为竖屏下的默认控制层-->
    <!--使用KID @id/k_ctrl_layer_land 可以标识为横屏下的默认控制层-->

    <!--app:interaction_area_always_visible="true"-->
    <!--可以使各个Part部分一直为显示状态,不会在超时等情况下自动隐藏 -->

    <!--app:part_top_min_height="17dp"-->
    <!--该属性可以直接设置顶部Part的最小高度,另外相对应的还有_bottom -->

    <!--View frame explain:
      |************************|
      |          top           |
      |                        |
      |                        |
      | left    middle   right |
      |                        |
      |                        |
      |         bottom         |
      |************************|-->
     <!--其中bottom,middle都为默认使用-->
     <!--想用其他Part请设置app:use_part_xxx="true"-->

     <!--使用 KID 标识你要添加的各个Part-->
     <!--=========@Top@=========-->
     <!--android:id="@id/k_ctrl_layer_part_top"-->
     <!--=========@Bottom@=========-->
     <!--android:id="@id/k_ctrl_layer_part_bottom"-->
     <!--=========@Xxx@=========-->
     <!--android:id="@id/k_ctrl_layer_part_Xxx"-->
     <XxxxLayout
       android:id="@id/k_ctrl_layer_part_Xxx"
       android:layout_width="match_parent"
       android:layout_height="match_parent">
       <!--XxxxLayout你可以随意替换为FrameLayout,RelativeLayout等-->
       <!--内部你可以随心所欲添加你要的View-->
       <!--其中可以使用一些公共的Id来快速完成不同的需求-->

       <!--使用KID @id/k_play 和 @id/k_pause-->
       <!--内部会自动帮你完成视频播放过程中,播放/暂停控制按钮的各个响应-->

       <!--使用KID @id/k_position_tv 和 @id/k_duration_tv-->
       <!--内部会自动按照你的标识,去显示播放进度和总时长-->

       <!--使用KID @id/k_progress_any 和 @id/k_progress_bar-->
       <!--内部会帮你处理,播放进展 两个id可以一起使用-->
       <!--其中k_progress_any可以为你自定的任何View只要实现ProgressAny这个接口-->

       <!--Tips: 这些 KID 不局限于Part内部使用,只要在控制层内都可以-->
     </XxxxLayout>
     <!--注意: 上面提到的Top,Bottom,Xxx这些方位Part的layout_xx属性
      都是相对于内部方位容器的, 它们在控制层内的位置是固定的-->
     <!--注意: 下面这两个Part的layout_xx属性是相对于这个控制层的-->
     <!--=========@Buffer@=========-->
     <!--android:id="@id/k_ctrl_layer_part_buffer"-->
     <!--用于快速标识该View为播放中视频缓冲时显示-->
     <!--=========@Tips@=========-->
     <!--android:id="@id/k_ctrl_layer_part_tips_tv"-->
     <!--这个TextView用于内部小提示,比如手势改变音量等-->
  </com.jcodeing.kmedia.video.ControlLayerView>
</com.jcodeing.kmedia.video.PlayerView>
```
ControlLayerView在Activity中的简单使用. [点击查看源码片段](/demo/src/main/java/com/jcodeing/kmedia/demo/VideoQueueActivity.java#L72-L114)
```java
// ============================@ControlLaye@============================
// 根据自己添加的控制层id,find到控制层View. 下面简单用控制层KID @id/k_ctrl_layer_port 来演示
ControlLayerView portCtrlLayer = (ControlLayerView) findViewById(R.id.k_ctrl_layer_port);
// 在initPart时,注意确保这个Part在Layout中 已经 app:use_part_xxx="true" (默认Part就不用再设置了)

// Use top title
portTitle = (TextView) portCtrlLayer.initPart(R.id.part_top_tv);
// 自己处理Title, 如果使用了MediaQueue则可以在onCurrentQueueIndexUpdated(.)回调用更新你的Title

//Custom top left iv
portCtrlLayer.initPartIb(R.id.part_top_left_ib,
    R.drawable.ic_go_back, "goBack").setOnClickListener(this);
//Custom bottom right iv
portCtrlLayer.initPartIb(R.id.part_bottom_right_ib,
    R.drawable.ic_go_full_screen, "goFullScreen").setOnClickListener(this);
// 自己处理不同Part按钮的点击事件
// 其中goFullScreen Api调用: playerView.getOrientationHelper().goLandscape();

//Custom remove position
portCtrlLayer.removePart(R.id.k_position_tv);
//Custom change bottom background
portCtrlLayer.findPart(R.id.k_ctrl_layer_part_bottom)
    .setBackgroundResource(android.R.color.transparent);
//Custom change middle background
portCtrlLayer.findPart(R.id.k_ctrl_layer_part_middle)
    .setBackgroundResource(android.R.color.transparent);
//Custom change middle play/pause/buffer/... size
((ImageButton) portCtrlLayer.findPart(R.id.k_play,
    Metrics.dp2px(this, 31f), Metrics.dp2px(this, 31f))).setScaleType(ScaleType.FIT_CENTER);
//最后如果上面的改动中,涉及到SmartView(k_play/pause..) 需要去更新下
portCtrlLayer.updateSmartView();
```

### Example 5: 简单的使用PlayerService/Binding
首先在AndroidManifest文件中, 添加service.
```xml
<service android:name="com.jcodeing.kmedia.service.PlayerService"/>
```
然后在Activity中进行PlayerBinding. [点击查看源码片段](/demo/src/main/java/com/jcodeing/kmedia/demo/AudioQueueActivity.java#L91-L125)
```java
player = new PlayerBinding(this, PlayerService.class, new BindPlayer() {
  @Override
  public IPlayer onBindPlayer() {
    return new Player(getApplicationContext())
        .init(new ExoMediaPlayer(getApplicationContext()));
  }//Player service bind player, call back.(if is bound, not call back)
}, new BindingListener() {
  @Override
  public void onFirstBinding(PlayerService service) {
    //do something player service init operation.
    //可以在这里给服务设置一个Notifier,来实现在通知栏中控制音频播放
    service.setNotifier(new AudioQueueNotifier());
  }//First binding call back.(if first binding finish, not call back)

  @Override
  public void onBindingFinish() {
    player.xxx();
  }//Binding finish. Can play.
});
}
```

### Example X: 更多例子参考

#### [MainActivity](/demo/src/main/java/com/jcodeing/kmedia/demo/MainActivity.java)
* [activity_main](/demo/src/main/res/layout/activity_main.xml) & [ctrl_layer_custom_main](/demo/src/main/res/layout/ctrl_layer_custom_main.xml)  
* [MainPortCtrlLayer](/demo/src/main/java/com/jcodeing/kmedia/demo/MainPortCtrlLayer.java) & [ctrl_layer_port_main](/demo/src/main/res/layout/ctrl_layer_port_main.xml)  
* [MainLandCtrlLayer](/demo/src/main/java/com/jcodeing/kmedia/demo/MainLandCtrlLayer.java) & [ctrl_layer_land_main](/demo/src/main/res/layout/ctrl_layer_land_main.xml)  
* [MainVFloatingView](/demo/src/main/java/com/jcodeing/kmedia/demo/MainVFloatingView.java) & [floating_video_view_main](/demo/src/main/res/layout/floating_video_view_main.xml)

#### [AudioQueueActivity](/demo/src/main/java/com/jcodeing/kmedia/demo/AudioQueueActivity.java)
* [activity_queue_audio](/demo/src/main/res/layout/activity_queue_audio.xml) & [item_audio_queue](/demo/src/main/res/layout/item_audio_queue.xml)  
* [AudioQueueNotifier](/demo/src/main/java/com/jcodeing/kmedia/demo/AudioQueueNotifier.java)

#### [VideoQueueActivity](/demo/src/main/java/com/jcodeing/kmedia/demo/VideoQueueActivity.java)
* [activity_queue_video](/demo/src/main/res/layout/activity_queue_video.xml) & [item_video_queue_port](/demo/src/main/res/layout/item_video_queue_port.xml)  
* [VideoQueueLandCtrlLayer](/demo/src/main/java/com/jcodeing/kmedia/demo/VideoQueueLandCtrlLayer.java) & [ctrl_layer_land_queue](/demo/src/main/res/layout/ctrl_layer_land_queue.xml) &  [item_video_queue_land](/demo/src/main/res/layout/item_video_queue_land.xml)

#### [VideoMultipleActivity](/demo/src/main/java/com/jcodeing/kmedia/demo/VideoMultipleActivity.java)
* [activity_multiple_video](/demo/src/main/res/layout/activity_multiple_video.xml)  
* [VideoMultipleFloatingView](/demo/src/main/java/com/jcodeing/kmedia/demo/VideoMultipleFloatingView.java) & [floating_video_view_multiple](/demo/src/main/res/layout/floating_video_view_multiple.xml)


## 文档
* The [KMedia-Core](https://jcodeing.github.io/KMedia-Core) JavaDoc
* The [KMedia-Uie](https://jcodeing.github.io/KMedia-Uie) JavaDoc

### [API - IPlayer](https://github.com/jcodeing/KMedia-Core/blob/release/src/main/java/com/jcodeing/kmedia/IPlayer.java)
#### Video
```java
void setVideo(SurfaceView surfaceView);
void setVideo(TextureView textureView);
void clearVideo();
```
#### AB
```java
P setAB(long startPos, long endPos);
P setABLoop(int loopMode, int loopInterval);
P setAB(long startPos, long endPos, int loopMode, int loopInterval);
P setClearAB(boolean autoClear);
///////////////////////////////////////////////////////////
player.setAB(abStart, abEnd, abLoop, abLoopInterval).play();
```
#### PlaybackSpeed
```java
boolean setPlaybackSpeed(float speed);
float getPlaybackSpeed();
```
#### SeekTo
```java
boolean seekTo(long ms);
boolean seekTo(long ms, int processingLevel);
void seekToPending(long ms);
long seekToProgress(int progress, int progressMax);
boolean fastForwardRewind(long ms);
```
#### PositionUnit
```java
P setPositionUnitList(IPositionUnitList posUnitList);
int getCurrentPositionUnitIndex();
void setCurrentPositionUnitIndex(int posUnitIndex);
long seekToPositionUnitIndex(int posUnitIndex);
int calibrateCurrentPositionUnitIndex(long position);
P setEnabledPositionUnitLoop(boolean enabled, int loopMode, int loopInterval);
P setPositionUnitLoopIndexList(ArrayList<Integer> posUnitLoopIndexList);
```
### [API - IMediaPlayer](https://github.com/jcodeing/KMedia-Core/blob/release/src/main/java/com/jcodeing/kmedia/IMediaPlayer.java)
如果你想扩展媒体引擎, 可以去实现IMediaPlayer接口.

#### Source
```java
void setDataSource(String path);
void setDataSource(Context context, Uri uri);
void setDataSource(Context context, Uri uri, Map<String, String> headers);
void setDataSource(FileDescriptor fd);
Uri getDataSource();
void prepareAsync();
```

#### Control
```java
boolean start();
boolean pause();
boolean seekTo(long ms);
void stop();
```

#### Control
```java
void setAudioStreamType(int streamtype);
void setVolume(float leftVolume, float rightVolume);
void setDisplay(SurfaceHolder sh);
void setSurface(Surface surface);
void setScreenOnWhilePlaying(boolean screenOn);
void setLooping(boolean looping);
boolean isLooping();
int getVideoWidth();
int getVideoHeight();
int getAudioSessionId();
```

### [Public - KID](https://github.com/jcodeing/KMedia-Core/blob/release/src/main/res/values/ids.xml)
```xml
<!--========================================================-->
<!--=========@Use prefix "k_" to avoid duplication@=========-->
<!--========================================================-->

<!--=========@Player@=========-->
<item name="k_player_view" type="id"/>
<item name="k_content_frame" type="id"/>
<item name="k_shutter" type="id"/>

<!--=========@Control@=========-->
<item name="k_ctrl_group" type="id"/>
<item name="k_ctrl_layer_port" type="id"/>
<item name="k_ctrl_layer_land" type="id"/>
<!--=========@Layer Part-->
<item name="k_ctrl_layer_part_top" type="id"/>
<item name="k_ctrl_layer_part_bottom" type="id"/>
<item name="k_ctrl_layer_part_left" type="id"/>
<item name="k_ctrl_layer_part_right" type="id"/>
<item name="k_ctrl_layer_part_middle" type="id"/>
<item name="k_ctrl_layer_part_buffer" type="id"/>
<item name="k_ctrl_layer_part_tips_tv" type="id"/>
<!--=========@Smart View-->
<item name="k_play" type="id"/>
<item name="k_pause" type="id"/>
<item name="k_prev" type="id"/>
<item name="k_next" type="id"/>
<item name="k_rew" type="id"/>
<item name="k_ffwd" type="id"/>
<item name="k_position_tv" type="id"/>
<item name="k_duration_tv" type="id"/>
<item name="k_progress_bar" type="id"/>
<item name="k_progress_any" type="id"/>
<!--=========@Extend-->
<item name="k_switch_control_layer" type="id"/>

<!--=========@Floating@=========-->
<item name="k_floating_view_close" type="id"/>
<item name="k_floating_view_drag_location" type="id"/>
<item name="k_floating_view_drag_size" type="id"/>

<!--=========@....................................@=========-->
```

### [Public - Attrs](https://github.com/jcodeing/KMedia-Core/blob/release/src/main/res/values/attrs.xml)
```xml
<!--=========@AControlGroupView@=========-->
<attr format="boolean" name="use_gesture_detector"/>
<declare-styleable name="AControlGroupView">
  <attr format="integer" name="show_timeout"/>
  <attr format="integer" name="rewind_increment"/>
  <attr format="integer" name="fast_forward_increment"/>
  <attr format="reference" name="default_control_layer_id"/>
  <attr name="use_gesture_detector"/>
</declare-styleable>

<!--=========@AControlLayerView@=========-->
<declare-styleable name="AControlLayerView">
  <attr format="reference" name="control_layer_layout_id"/>
  <!--=========@Part-->
  <!--=====@Interaction Area-->
  <attr format="boolean" name="interaction_area_always_visible"/>
  <attr format="boolean" name="use_part_top"/>
  <attr format="boolean" name="use_part_bottom"/>
  <attr format="boolean" name="use_part_left"/>
  <attr format="boolean" name="use_part_right"/>
  <attr format="boolean" name="use_part_middle"/>
  <attr format="boolean" name="use_part_view_animation"/>
  <attr format="dimension" name="part_top_min_height"/>
  <attr format="dimension" name="part_bottom_min_height"/>
  <!--=====@Other-->
  <attr format="boolean" name="use_part_buffer"/>
  <attr format="boolean" name="use_part_tips"/>
</declare-styleable>

<!--=========@AspectRatioView@=========-->
<!--Must be kept in sync with AspectRatioView-->
<attr format="enum" name="resize_mode">
  <enum name="fit" value="0"/>
  <enum name="fixed_width" value="1"/>
  <enum name="fixed_height" value="2"/>
  <enum name="fill" value="3"/>
</attr>
<declare-styleable name="AspectRatioView">
  <attr name="resize_mode"/>
</declare-styleable>

<!--=========@APlayerView@=========-->
<!--Must be kept in sync with APlayerView-->
<attr format="enum" name="surface_type">
  <enum name="none" value="0"/>
  <enum name="surface_view" value="1"/>
  <enum name="texture_view" value="2"/>
</attr>
<declare-styleable name="APlayerView">
  <attr name="surface_type"/>
  <attr format="reference" name="player_layout_id"/>
  <attr format="boolean" name="use_control_group"/>
  <!--Other-->
  <attr name="resize_mode"/>
  <attr name="android:layout_height"/>
  <attr name="use_gesture_detector"/>
</declare-styleable>

<!--=========@.................@=========-->
```

## 注意
### Vector低版本兼容
KMedia各个Module中均使用支持库, 来实现Android2.1(API 7)及更高版本中支持VectorDrawable
```gradle
vectorDrawables.useSupportLibrary = true
```
值得注意的是, 如果你的应用需要运行在低于Android 5.0(API 21)的设备上,  
并使用KMedia中ANotifier的createSimpleMediaNotificationBuilder时,  
你就要考虑去兼容下低版本中Vector用在Notification上的情况.  
可以将KMedia中用于Notification的Vector转成png, 放到[Res中.](/demo/src/main/res) 更多[参见.](https://developer.android.com/studio/write/vector-asset-studio.html?hl=zh-cn)

### RequiresPermission权限申请
KMedia各个Module中不会去主动申请任何相关权限,  
对于部分需要权限的API, 会加上注解 `RequiresPermission` 来提醒开发者去申请权限.  
值得注意的是, 在显示浮窗相关的API处, 如果需要做兼容处理时, 需要使用到WINDOW权限.  
此处并未加权限注解, 但在源码中有详细的注释, 上文中也提到了这点.  
因为不是所有用户都需要去做兼容浮窗. 这个就要根据你的具体使用需求合理申请WINDOW权限.  
还有一些特殊的定制系统, 需要手动处理 `显示悬浮窗`.

## 开发
很高兴同大家一起来开发KMedia. 做开源贡献者的一夸克.

### 首先我们做下开发准备工作

#### Step 1: 克隆KMedia仓库的开发分支到本地
```sh
git clone -b develop https://github.com/jcodeing/KMedia.git
```
#### Step 2: 初始化KMedia各个模块
也可以使用 `git submodule` 命令(init & update)来完成
```sh
./init_modules.sh
```
#### Step 3: 添加属于你的远程仓库地址
远程仓库地址, 可以通过Fork我的各个模块得到.
```sh
git remote add fork "Your KMedia Remote Repositorie Path"
cd core
git remote add fork "Your KMedia-Core Remote Repositorie Path"
cd uie
git remote add fork "Your KMedia-Uie Remote Repositorie Path"
cd mpe
git remote add fork "Your KMedia-Mpe Remote Repositorie Path"
```
#### 完成上面的步骤后用IDE(AndroidStudio)打开KMedia

### 其次我们来了解下开发规范

#### Code Style

##### KMedia 代码统一使用 [Google Style](https://github.com/google/styleguide/blob/gh-pages/intellij-java-google-style.xml) 来 Reformat Code
由于KMedia为开源项目, 开发过程中可能会涉及到多种Reformat Code风格,  
为了便于大家阅读和编码, 所以在此统一使用Google Style来Reformat Code.

##### 代码中使用以下注释条来划分具体的功能模块
你可以将下面的注释条, 加入到IDE的Live Templates中, 从而方便使用.
```java
// ============================@Xxx Xxx@============================
// ============================@Xxx
// =========@Xxx Xxx@=========
// =========@Xxx
```
### 最后我们具体谈下从无到有的编码贡献流程.
#### 首先, 你要有一个需求
* 优化KMedia.
* 解决KMedia的Bug,修复它.
* 一拍脑门得到一个Idea, 想加到KMedia中, 让大家一起用.
* 等等...

#### 然后, 你就可以狂甩代码了
甩码过程中, 请遵循KMedia的编码规范.

#### 其次, 注意下, 在编码完成部分后先提交到自己(Fork)的远程仓库
在准备工作中, 我们各自都有一个属于自己的远程仓库.  
当代码还是雏形时, 先提交到自己的仓库进行维护.
同时, 可以随时合并Origin仓库的最新代码.

#### 最后, 当你完成这个需求或者确定要先提交到Origin仓库时
可以到你Fork的Github仓库主页, 点击 `Pull request` 去 Create pull request 给我.  
我看到后, 会第一时间处理你的 request. 从而时KMedia更加完善和强固.

### ===========================感谢你使用KMedia===========================
