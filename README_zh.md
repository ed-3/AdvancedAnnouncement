### AdvancedAnnouncement: 更高级的自动公告

[English](README.md)

[MCBBS](https://www.mcbbs.net/forum.php?mod=viewthread&tid=1420798&page=1&extra=#pid27960648)

#### 目录
+ [公告类型](#公告类型)
+ [指令](#指令)
+ [配置](#配置) 
+ [图片预览](#图片预览)
  + [CHAT 类型](#CHAT-类型)
  + [ACTION_BAR 类型](#ACTION_BAR-类型)
  + [BOSS_BAR 类型](#BOSS_BAR-类型)
  + [TITLE 类型](#TITLE-类型)

#### 公告类型
目前的公告类型如下:

| 公告类型       | 说明 |
| ------------ | --- |
| CHAT         | 公告会被发送到聊天栏上 |
| ACTION_BAR   | 公告会被发送到玩家的物品栏上方 |
| BOSS_BAR     | 公告会以 BOSS 栏的方式显示在玩家屏幕正上方 |
| TITLE        | 公告会以标题的形式发送到玩家的屏幕中间 |
| PRE_ANNOUNCE | 公告会在指定时间到达时被发送出去 |

#### 指令
|指令|权限|描述|
|---|---|---|
|/aa help     | -------------------- | 显示帮助        |
|/aa broadcast| aa.command.broadcast | 广播一个公告     |
|/aa display  | aa.command.display   | 向你展示一个公告  |
|/aa list     | aa.command.list      | 列出所有加载的公告 |
|/aa parse    | aa.command.parse     | 解析一个字符串    |
|/aa reload   | aa.command.reload    | 重新加载插件  |

#### 配置
+ #### config.yml
```yaml
  # 语言
  # zh_CN (简体中文) | en_US (English)
  translation: zh_CN
  
  # 是否打印公告到控制台，该选项只打印 CHAT 类型
  Console-broadCast: true
  
  # 启用 PlaceHolderAPI 支持
  PlaceholderAPI-support: true
  
  # 预公告的时间格式
  date-format: yyyy-MM-dd HH:mm:ss
  
  # 随机发送公告
  random: true
  
  # debug 测试
  debug: false
```
+ #### components.yml
```yaml
# 将会被显示在聊天栏中的 JSON 文本组件
# 只在 CHAT 类型公告下有用。
# 通过 @<example_name>@ 调用

# 名称，不要重复
example:
  # 将会被显示的文本
  text: '&f[&brun_command&f]'
  onClick:
    # 可用的选项:
    # OPEN_URL: 打开一个网页链接
    # OPEN_FILE: 打开玩家本地的一个文件，需要用绝对路径
    # RUN_COMMAND: 让玩家执行一个命令
    # SUGGEST_COMMAND: 填充一段文本到玩家的输入栏中
    action: RUN_COMMAND
    value: '/say hello! this is a example action.'

  # 当玩家鼠标停留在这段文字上，将会显示的内容
  hover-value: |-
    {#f4114a->#20d820}An example hover text.
    &6you can use multiple lines.

    &f&l>>> &b&lClick here &e&lRun command
```
+ #### announcement.yml
```yaml
announcements:
  # 公告名称，不要重复
  chat_announce_type_example:
    # 权限名，没有权限的玩家不会收到这个公告。
    # 如果什么都不写或者去除该项，则表示任何玩家都可收到这个公告
    permission: 'user.example_perm'
    # 下一个公告的延迟
    # 例子:
    # - 60s, 延迟是 60 秒
    # - 1min, 1 分钟
    # - 1h, 1 小时 (确定吗?)
    # - 1d, 1 天 (确定吗?)
    # - 1week, 1 周(¿认真的吗)
    # - 1month, 1 个月(¿认真的吗)
    # - 1year， 1 年 (¿认真的吗)
    delay: 60s
    # 可用的类型:
    # CHAT: 该公告会被发送到聊天栏中
    # ACTION_BAR: 该公告会被发送到玩家的物品栏上方
    # BOSS_BAR: 该公告会以 BOSS 栏的方式发送到玩家的屏幕上方
    # TITLE: 该公告会以标题形式发送到玩家屏幕中间
    # PRE_ANNOUNCE: 指定时间发送的公告
    type: CHAT
    # 公告内容
    # +==+==+==+==+==+==+==+==+==+==+==+==+==
    # 支持 RGB 颜色
    # 需要你的服务器版本为 1.16 及以上，并且玩家版本也需要 1.16 及以上
    #
    # RGB颜色格式: {<Color>[,<arg1>,<arg2>,...]}
    # 例子:
    # 1. {#FFFFFF}RGB颜色文本
    # 2. {#FFFFFF,&l}加粗的RGB颜色文本
    # 3. {#FFFFFF,&l,&n}加粗和下划线的RGB颜色文本
    # +==+==+==+==+==+==+==+==+==+==+==+==+==
    # 支持渐变 & 彩虹渐变
    #
    # 渐变色格式: {<Color1>-><Color2>[,<arg1>,<arg2>,...]}
    # 例子:
    # 1. {#04ff00->#bf0000}渐变文字
    # 2. {#04ff00->#bf0000,&l}加粗的渐变文字
    # 3. {#04ff00->#bf0000,&l,&n}加粗和下划线的渐变文字
    #
    # 彩虹渐变格式: {rainbow[,<arg1>,<arg2>,...]}
    # 例子:
    # 1. {rainbow}彩虹渐变文字
    # 2. {rainbow,&l}加粗彩虹渐变
    # 3. {rainbow,&l,&n}加粗和下划线的彩虹渐变文字
    # +==+==+==+==+==+==+==+==+==+==+==+==+==
    #
    # 注意:
    # 支持 PlaceholderAPI 变量
    # 如果你启用了 'Console-broadCast', 占位符会直接显示它的原始值
    # (如果这个占位符必须要以一个玩家解析的话)
    #
    # 如果你的服务器版本低于了 1.16, 那么推荐使用原版颜色代码。尽管插件对低版本做了兼容
    # (它会自动将 RGB 颜色替换成与原版最接近的颜色), 仍然不建议在低版本服务器中使用 RGB 
    # 颜色代码
    # +==+==+==+==+==+==+==+==+==+==+==+==+==
    content:
      - '{rainbow}==============================='
      - '  {#ffff00}This is an example announcement'
      - '  {#04ff00->#bf0000}gradient color text @example@ '
      - '{rainbow}==============================='
  
  action_bar_type_example:
    delay: 60s
    type: ACTION_BAR
    content:
      # 2s 的停留时间，最长是 10 秒
      # 默认停留时间为 5 秒
      - '{rainbow}this is an action bar announcement{stay:2}'
      # 在显示下一条 Action bar 时，中间会停留 2 秒
      # 默认停留时间为 5 秒
      - 'the next action bar{delay:2}'
      - 'test'

  boss_bar_type_example:
    delay: 60s
    type: BOSS_BAR
    content:
      # 可用的 BOSS 栏颜色:
      # PINK, BLUE, RED, GREEN, YELLOW, PURPLE, WHITE
      # 可用的 BOSS 栏节段: 6, 10, 12, 20
      - '{#4d6ea5->#a7bce5}this is an boss bar announcement{stay:2}{color:GREEN}{segment:20}'
      # 需要 PAPI 拓展变量: Animations
      # 下载指令: /papi ecloud download Animations (如果你启用了 ecloud 功能)
      # 每 0.5 秒更新一次
      - '%animations_<shine start=&9 middle=&4 end=&9 normal=&b size=3>this is an auto-updated boss bar</shine>%{update:0.5}'
      # 在显示下一个 BOSS 栏时，中间会停留 2 秒
      - '&6progressing boss bar{progress:true}{delay:2}'
      # 默认停留时间是 5 秒
      # 默认颜色为 PURPLE
      - '&bthe next boss bar{segment:10}'

  title_type_example:
    delay: 60s
    type: TITLE
    # 第一行作为主标题
    # 第二行作为副标题.
    content:
      - '{#faefae->#00ff66}Example gradient title.'
      - '{rainbow}sub title'

    # 下面的配置节段只在该类型下有效
    # 淡入时间，单位：秒
    fadeIn: 0.2
    # 停留时间, 单位：秒
    stay: 3
    # 淡出时间, 单位：秒
    fadeout: 1

  pre_announce_example:
    delay: 60s
    # 这个公告什么时候会被发送
    # 时间格式可以在 config.yml 中修改
    date: '2024-1-1 00:00:00'
    type: PRE_ANNOUNCE
    # 时间到的时候，这个公告会以什么形式发送出来
    displayType: CHAT
    content:
      - '{rainbow}==============================='
      - ''
      - '{#bf0000->#00bfbf,&l}HAPPY NEW YEAR!'
      - ''
      - '{rainbow}==============================='

```

#### 使用 '/aa parse' 解析字符串
p话少说，直接上图:
![parse.gif](https://s2.loli.net/2023/01/23/8vhblmMaDtGoCSZ.gif)

#### 图片预览
+ #### CHAT 类型
  ![chatType.gif](https://s2.loli.net/2023/01/23/mszgIDpcWUR6TAn.gif)

+ #### ACTION_BAR 类型
  ![actionBarType.gif](https://s2.loli.net/2023/01/23/F2qKZnX1u8GcCx5.gif)

+ #### BOSS_BAR 类型
  ![bossBarType.gif](https://s2.loli.net/2023/01/23/iZnydepk68mOgzM.gif)

+ #### TITLE 类型
  ![titleType.gif](https://s2.loli.net/2023/01/23/gmNuj3ShGzpB5DK.gif)

+ #### PRE_ANNOUNCE 类型
    PRE_ANNOUNCE 类型公告当到达规定时间的时候，**_会以上述四种类型去进行显示_**。