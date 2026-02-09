# 玄同 (WSVita) 组件化列表框架技术文档

- 玄同


本框架是一套基于 **DataBinding**、**Kotlin 协程** 与 **DiffUtil** 构建的 Android 列表深度封装方案。通过四层 Adapter 架构与实体协议设计，实现了业务逻辑、数据转换与 UI 渲染的完全解耦。

---

## 一、 实体类协议设计 (Entity Protocol)

在组件化开发中，实体类通过实现协议来决定其在列表中的展示行为。



### 1. 协议继承关系
* **`IFrameEntity`**: 框架顶层标记接口。
* **`IRecyclerItem`**: 核心协议。定义了 `recyclerItemId()` 和 `recyclerItemType()`。
* **`RecyclerItemEntity`**: 抽象实现层，用于 core 层统一扩展。
* **`BaseEntity` (业务核心基类)**:
    * **自动绑定**: 默认实现 `recyclerItemType()`，直接返回 `customLayoutId()`。
    * **组件化支持**: 内置 `itemSelect`、`iconUrl`、`detail` 等字段，适配 `wsui` 风格组件。

---

## 二、 Adapter 架构原理 (Architecture)

框架采用职责链模式，每一层解决特定的技术痛点：

### 1. RecyclerAdapter (基础层)
**职责**: DataBinding 基础封装。
* 自动解析布局并创建 `BindingViewHolder`。
* 封装支持 `Payloads` 的局部刷新接口。

### 2. SDKAdapter (异步层)
**职责**: 异步 Diff 处理。
* 利用协程在后台线程执行差分计算，避免 UI 卡顿。
* 通过 `mGeneration` 原子变量确保在快速滑动刷新时的线程安全。

### 3. VitaAdapter (增强层)
**职责**: 虚拟索引映射。
* 通过 `validIndices` 过滤 `type <= 0` 的非法数据。
* 隔离 Header、Footer 和 Empty 布局对业务列表索引的干扰。

### 4. AppAdapter (业务层)
**职责**: 自动化绑定与状态管理。
* **核心机制**: 自动执行 `binding.setVariable(BR.recyclerIten, item)`。

---

## 三、 详细使用指南

### 1. 定义业务实体类
#### 继承 `BaseEntity`，只需指定对应的布局 ID 和唯一 ID。

```kotlin
/**
 * 示例：车牌实体类
 */
class PlateLicenseEntity(val plateNo: String) : BaseEntity() {
    // 1. 指定该实体对应的 XML 布局 ID
    override fun customLayoutId(): Int = R.layout.item_plate_license

    // 2. 提供唯一标识（用于 DiffUtil 局部刷新）
    override fun recyclerItemId(): Long = plateNo.hashCode().toLong()
}
```
### 2. 编写 DataBinding 布局
#### 在 XML 中必须使用 <variable> 声明变量，变量名必须为 recyclerIten。
```layout
/**
 * 示例：车牌实体类
 */
<layout xmlns:android="[http://schemas.android.com/apk/res/android](http://schemas.android.com/apk/res/android)"
    xmlns:app="[http://schemas.android.com/apk/res-auto](http://schemas.android.com/apk/res-auto)">
    <data>
        <variable name="recyclerIten" type="com.wsvita.core.entity.domain.PlateLicenseEntity" />
    </data>

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:padding="12dp">

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@{recyclerIten.plateNo}"
            app:wsui_text_style="normal" />

    </LinearLayout>
</layout>
```

### 3. 实现业务 Adapter
#### 由于 AppAdapter 已自动完成变量绑定，子类通常极其简洁。

```kotlin
/**
 * 示例：车牌业务 Adapter 实现
 */
class PlateAdapter(context: Context) : AppAdapter<PlateLicenseEntity>(context) {
    
    // 如果是纯列表（无 Header/Footer），返回 true
    override fun isPureListData(): Boolean = true

    /**
     * 由于基类 AppAdapter 已自动执行了：
     * binding.setVariable(BR.recyclerIten, item)
     * 所以子类仅需处理点击事件等特殊交互逻辑。
     */
    override fun onBindItemData(binding: ViewDataBinding, item: PlateLicenseEntity, position: Int) {
        binding.root.setOnClickListener {
            // 在此处处理业务点击逻辑
        }
    }
}