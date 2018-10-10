# AndroidDoodle

致力打造最简洁好用的 Android 涂鸦开源库。

### Demo
<div class='row'>
    <img src='http://7xq276.com2.z0.glb.qiniucdn.com/keyboard-demo.gif' width="250px"/>
</div>

[Demo Download](http://7xq276.com2.z0.glb.qiniucdn.com/keyboard_new.apk)

### import

```
dependencies {
    compile 'com.qunhe.android:doodle:1.0.0'
}
```

### 添加绘图层

```
<com.qunhe.sketch.SketchBoardLayout
    android:id="@+id/content"
    android:layout_width="match_parent"
    android:layout_height="match_parent"/>
```

注意添加到图片之上，自己有效按钮之下

### 监听

可以对 SketchBoardLayout 中的 setOnSketchBoardListener 来监听一些操作行为。

```
public interface OnSketchBoardListener {
        /**
         * is any sketch selected
         *
         * @param isSelected is selected
         */
        void onSketchSelected(boolean isSelected);

        /**
         * when text sketch clicked
         *
         * @param text  text
         * @param color color
         */
        void onTextSketchClicked(String text, int color);
    }
```

#### 画线

```
// 切换为画线模式
setSketchType(SketchBoardLayout.LINE_SKETCH);

// 设置颜色和粗细，切换只需要一次，以后直接设置颜色和粗细就行
setLineSketchPaint(int color, float strokeWidth)
```

#### 文字

```
// 加入文字层
addTextSketch(String text, int color);
//重置已有文字层，只重置最上层的文字层，如果最上层非文字层，则无效
resetTextSketch(String text, int color);  
```

```
当文字层被点击后，点击后，该文字层就会被添加到最上层
void onTextSketchClicked(String text, int color);
```

#### 图片

```
addImageSketch(Bitmap bitmap)
```

#### undo

```
undo()
```

#### delete

```
delete() 
// 删除最上层的sketch，因为只有点选的sketch才可以删除，而点选后会到最上层
```

可以通过onSketchSelected监听判断当前是否有选中的sketch，如果无选中的，则无法删除，可以用此方法来判断删除按钮的可点状态。

#### frozen

```
Bitmap frozen(Bitmap)
```

当最后需要倒出图片时，可以把原图的 Bitmap 传入该函数，然后获得一个新的 bitmap，该 bitmap 即为包含所有涂鸦的图片。