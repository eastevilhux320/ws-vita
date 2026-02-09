# WS-SDK-Framework 核心能力库

`ws-sdk-framework` 是 WSVita 体系下的核心底层 SDK，基于 **MVVM 模式** 与 **组件化协议** 构建。其目标是通过一套清晰的分层架构与 Kotlin 扩展，彻底消除 Android 列表开发与 UI 配置中的模板代码。

---

## 🏗 一、 Adapter 架构原理

框架采用了五层继承关系，将 RecyclerView 的基础逻辑、数据比对、组件化协议和具体业务完全解耦。

### 1. 架构分层职责
* **RecyclerAdapter**: 基础层。封装 `ViewDataBinding` 的自动解析与 `BindingViewHolder` 维护。
* **SDKAdapter**: 异步层。利用 **Kotlin 协程** 在后台线程执行 `DiffUtil` 差分计算，确保在快速滑动刷新时的线程安全与 UI 丝滑。
* **VitaAdapter**: 协议层。通过 **虚拟索引映射 (`validIndices`)** 机制，过滤 `type <= 0` 的非法数据，隔离 Header/Footer 对业务索引的干扰。
* **AppAdapter**: 业务层。**核心自动化点**：自动执行 `binding.setVariable(BR.recyclerIten, item)`。

### 2. 自动化绑定协议
本设计通过 `wsui` 风格的前缀规范和 DataBinding 自动化绑定，极大减少了业务层的模板代码。在 XML 中必须使用以下变量名：
```xml
<data>
    <variable name="recyclerIten" type="com.wsvita.xxx.YourEntity" />
</data>
```

## 🎨 三、 ViewExt (UI 逻辑代码化)

`ext.ViewExt` 是框架提供的 UI 增强工具箱。通过 Kotlin 扩展函数，将繁琐的 `shape.xml` 配置和复杂的资源检索逻辑封装为语义化的链式调用，大幅提升开发效率并降低包体积。

### 1. 动态背景构建 (Drawable Factory)
旨在消灭冗余的 `res/drawable/*.xml` 文件，支持在代码中动态生成高性能背景。

* **核心工厂方法 `createRectangle`**:
  支持设置填充色、统一圆角、四角独立圆角及边框样式。
* **基础矩形 (`createRectDrawable`)**:
  快速创建带统一圆角和边框的背景。
* **复杂圆角矩形 (`createComplexRectDrawable`)**:
  支持对 `lt` (左上), `rt` (右上), `rb` (右下), `lb` (左下) 四个角进行像素级精细控制。
* **圆形/椭圆背景 (`createOvalDrawable`)**:
  根据 View 宽高比自动生成圆形或椭圆背景。

#### 3. 快速边框背景 (`createStrokeDrawable`)
专门用于需要强调轮廓的 UI 场景（如搜索框、镂空按钮）。只需指定边框色和圆角，即可生成背景。

* **调用示例**:
    ```kotlin
    // 创建一个蓝色边框、8dp圆角的透明背景
    view.background = context.createStrokeDrawable(
        strokeColor = Color.BLUE,
        radius = 8f
    )
    ```

#### 💡 进阶：智能水波纹 (`toRippleDrawable`)
这是 `ViewExt` 的设计亮点。它结合了 `GradientDrawable` 与 `RippleDrawable`，只需一行代码即可为 View 增加点击态：
```kotlin
// 自动计算比该背景色深 20% 的颜色作为水波纹反馈
view.background = Color.WHITE.toRippleDrawable(radius = 12f)
```
